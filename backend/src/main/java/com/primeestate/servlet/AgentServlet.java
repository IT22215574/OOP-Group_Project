package com.primeestate.servlet;

import com.google.gson.Gson;
import com.primeestate.dao.AgentDAO;
import com.primeestate.model.Agent;
import com.primeestate.util.JsonResponse;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * MODULE 1 – Real Estate Agent Management
 * Endpoints:
 *   GET    /api/agents          – list / search agents
 *   GET    /api/agents/{id}     – view agent profile
 *   POST   /api/agents          – register new agent
 *   PUT    /api/agents/{id}     – edit agent details
 *   DELETE /api/agents/{id}     – remove agent profile
 */
@WebServlet("/api/agents/*")
public class AgentServlet extends HttpServlet {

    private final AgentDAO agentDAO = new AgentDAO();
    private final Gson     gson     = new Gson();

    // READ – View / search agent profiles
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Agent agent = agentDAO.readById(Integer.parseInt(pathInfo.substring(1)));
                if (agent == null) { JsonResponse.error(res, 404, "Agent not found"); return; }
                JsonResponse.success(res, agent);
            } else {
                String name           = req.getParameter("name");
                String location       = req.getParameter("location");
                String specialization = req.getParameter("specialization");
                List<Agent> agents = (name != null || location != null || specialization != null)
                    ? agentDAO.search(name, location, specialization)
                    : agentDAO.readAll();
                JsonResponse.success(res, agents);
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // CREATE – Register a new real estate agent
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Agent agent = gson.fromJson(req.getReader(), Agent.class);
            String err = agent.validate();
            if (err != null) { JsonResponse.error(res, 400, err); return; }
            if (agentDAO.emailExists(agent.getEmail())) {
                JsonResponse.error(res, 409, "Agent email already registered"); return;
            }
            int newId = agentDAO.create(agent);
            JsonResponse.send(res, 201, java.util.Map.of("success", true, "data", java.util.Map.of("id", newId)));
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // UPDATE – Edit agent details
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Agent ID required"); return;
        }
        try {
            int   id    = Integer.parseInt(pathInfo.substring(1));
            Agent agent = gson.fromJson(req.getReader(), Agent.class);
            agent.setId(id);
            String err = agent.validate();
            if (err != null) { JsonResponse.error(res, 400, err); return; }
            if (agentDAO.emailExistsForOther(agent.getEmail(), id)) {
                JsonResponse.error(res, 409, "Email already used by another agent"); return;
            }
            boolean updated = agentDAO.update(agent);
            if (updated) JsonResponse.success(res, "Agent updated successfully");
            else         JsonResponse.error(res, 404, "Agent not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // DELETE – Remove agent profile
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Agent ID required"); return;
        }
        try {
            boolean deleted = agentDAO.delete(Integer.parseInt(pathInfo.substring(1)));
            if (deleted) JsonResponse.success(res, "Agent deleted successfully");
            else         JsonResponse.error(res, 404, "Agent not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }
}
