package com.primeestate.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primeestate.dao.AppointmentDAO;
import com.primeestate.model.Appointment;
import com.primeestate.util.JsonResponse;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

/**
 * MODULE 3 – Appointment Management
 * Endpoints:
 *   GET    /api/appointments              – list / filter appointments
 *   GET    /api/appointments/{id}         – view appointment
 *   POST   /api/appointments              – book appointment
 *   PUT    /api/appointments/{id}         – reschedule appointment
 *   DELETE /api/appointments/{id}         – cancel appointment
 *
 * Cross-module: userId links Module 2 (Users), agentId links Module 1 (Agents)
 */
@WebServlet("/api/appointments/*")
public class AppointmentServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final Gson           gson           = new Gson();

    // READ – View appointments (optionally filtered by userId, agentId, status)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Appointment appt = appointmentDAO.readById(Integer.parseInt(pathInfo.substring(1)));
                if (appt == null) { JsonResponse.error(res, 404, "Appointment not found"); return; }
                JsonResponse.success(res, appt);
            } else {
                Integer userId  = parseIntParam(req.getParameter("userId"));
                Integer agentId = parseIntParam(req.getParameter("agentId"));
                String  status  = req.getParameter("status");
                List<Appointment> appts = (userId != null || agentId != null || status != null)
                    ? appointmentDAO.search(userId, agentId, status)
                    : appointmentDAO.readAll();
                JsonResponse.success(res, appts);
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // CREATE – Book appointment
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            Appointment appt = buildFromJson(body);
            String err = appt.validate();
            if (err != null) { JsonResponse.error(res, 400, err); return; }
            int newId = appointmentDAO.create(appt);
            JsonResponse.send(res, 201, java.util.Map.of("success", true, "data", java.util.Map.of("id", newId)));
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // UPDATE – Reschedule appointment
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Appointment ID required"); return;
        }
        try {
            int        id   = Integer.parseInt(pathInfo.substring(1));
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);

            Appointment existing = appointmentDAO.readById(id);
            if (existing == null) { JsonResponse.error(res, 404, "Appointment not found"); return; }

            if (body.has("userId"))            existing.setUserId(body.get("userId").getAsInt());
            if (body.has("agentId"))           existing.setAgentId(body.get("agentId").getAsInt());
            if (body.has("appointmentDate"))   existing.setAppointmentDate(Date.valueOf(body.get("appointmentDate").getAsString()));
            if (body.has("appointmentTime"))   existing.setAppointmentTime(Time.valueOf(body.get("appointmentTime").getAsString()));
            if (body.has("propertyType"))      existing.setPropertyType(body.get("propertyType").getAsString());
            if (body.has("appointmentStatus")) existing.setAppointmentStatus(body.get("appointmentStatus").getAsString());
            if (body.has("agentMessage"))      existing.setAgentMessage(body.get("agentMessage").isJsonNull() ? null : body.get("agentMessage").getAsString());

            boolean updated = appointmentDAO.update(existing);
            if (updated) JsonResponse.success(res, "Appointment updated successfully");
            else         JsonResponse.error(res, 404, "Appointment not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // DELETE – Cancel appointment
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Appointment ID required"); return;
        }
        try {
            boolean deleted = appointmentDAO.delete(Integer.parseInt(pathInfo.substring(1)));
            if (deleted) JsonResponse.success(res, "Appointment cancelled successfully");
            else         JsonResponse.error(res, 404, "Appointment not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    private Appointment buildFromJson(JsonObject body) {
        Appointment a = new Appointment();
        if (body.has("userId"))          a.setUserId(body.get("userId").getAsInt());
        if (body.has("agentId"))         a.setAgentId(body.get("agentId").getAsInt());
        if (body.has("appointmentDate")) a.setAppointmentDate(Date.valueOf(body.get("appointmentDate").getAsString()));
        if (body.has("appointmentTime")) a.setAppointmentTime(Time.valueOf(body.get("appointmentTime").getAsString()));
        if (body.has("propertyType"))      a.setPropertyType(body.get("propertyType").getAsString());
        if (body.has("appointmentStatus")) a.setAppointmentStatus(body.get("appointmentStatus").getAsString());
        if (body.has("agentMessage"))      a.setAgentMessage(body.get("agentMessage").isJsonNull() ? null : body.get("agentMessage").getAsString());
        return a;
    }

    private Integer parseIntParam(String value) {
        try { return value != null ? Integer.parseInt(value) : null; }
        catch (NumberFormatException e) { return null; }
    }
}
