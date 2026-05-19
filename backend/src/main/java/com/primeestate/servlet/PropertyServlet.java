package com.primeestate.servlet;

import com.google.gson.Gson;
import com.primeestate.dao.PropertyDAO;
import com.primeestate.model.Property;
import com.primeestate.util.JsonResponse;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/properties/*")
public class PropertyServlet extends HttpServlet {

    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final Gson        gson        = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            // GET /api/properties/{id}
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Property property = propertyDAO.findById(id);
                if (property == null) {
                    JsonResponse.error(res, 404, "Property not found");
                } else {
                    JsonResponse.success(res, property);
                }
                return;
            }

            // GET /api/properties?type=sale&city=Colombo&category=house&page=1
            String type     = req.getParameter("type");
            String city     = req.getParameter("city");
            String category = req.getParameter("category");
            int    page     = parseIntParam(req.getParameter("page"), 1);
            int    limit    = parseIntParam(req.getParameter("limit"), 9);
            int    offset   = (page - 1) * limit;

            List<Property> properties = propertyDAO.findAll(type, city, category, limit, offset);
            int            total      = propertyDAO.countAll(type, city, category);

            Map<String, Object> response = new HashMap<>();
            response.put("properties", properties);
            response.put("total", total);
            response.put("page", page);
            response.put("pages", (int) Math.ceil((double) total / limit));

            JsonResponse.success(res, response);

        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthenticated(req, res)) return;
        try {
            Property property = gson.fromJson(req.getReader(), Property.class);
            if (property.getTitle() == null || property.getPrice() == null) {
                JsonResponse.error(res, 400, "Title and price are required");
                return;
            }
            HttpSession session = req.getSession(false);
            if (property.getAgentId() == 0) {
                property.setAgentId((int) session.getAttribute("userId"));
            }
            int newId = propertyDAO.save(property);

            // Persist all images to property_images table
            List<String> allImages = new ArrayList<>();
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty())
                allImages.add(property.getImageUrl());
            if (property.getAdditionalImages() != null)
                allImages.addAll(property.getAdditionalImages());
            if (!allImages.isEmpty())
                propertyDAO.savePropertyImages(newId, allImages);

            Map<String, Object> result = new HashMap<>();
            result.put("id", newId);
            JsonResponse.send(res, 201, Map.of("success", true, "data", result));
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthenticated(req, res)) return;
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Property ID required");
            return;
        }
        try {
            int      id       = Integer.parseInt(pathInfo.substring(1));
            Property property = gson.fromJson(req.getReader(), Property.class);
            property.setId(id);
            boolean updated = propertyDAO.update(property);
            if (updated) {
                JsonResponse.success(res, "Property updated");
            } else {
                JsonResponse.error(res, 404, "Property not found");
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthenticated(req, res)) return;
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Property ID required");
            return;
        }
        try {
            int     id      = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = propertyDAO.delete(id);
            if (deleted) {
                JsonResponse.success(res, "Property deleted");
            } else {
                JsonResponse.error(res, 404, "Property not found");
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    private boolean isAuthenticated(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonResponse.error(res, 401, "Authentication required");
            return false;
        }
        return true;
    }

    private int parseIntParam(String value, int defaultVal) {
        try { return value != null ? Integer.parseInt(value) : defaultVal; }
        catch (NumberFormatException e) { return defaultVal; }
    }
}
