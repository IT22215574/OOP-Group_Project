package com.primeestate.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primeestate.dao.UserDAO;
import com.primeestate.model.User;
import com.primeestate.util.JsonResponse;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * MODULE 2 – User Management
 * Endpoints:
 *   GET    /api/users           – list / search users
 *   GET    /api/users/{id}      – view user profile
 *   POST   /api/users           – register new user
 *   PUT    /api/users/{id}      – update user information
 *   DELETE /api/users/{id}      – delete user account
 *
 * Note: Login/logout/session handled by AuthServlet (/api/auth/*)
 */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final Gson    gson    = new Gson();

    // READ – View / search user profiles
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                User user = userDAO.readById(Integer.parseInt(pathInfo.substring(1)));
                if (user == null) { JsonResponse.error(res, 404, "User not found"); return; }
                safeUser(user);
                JsonResponse.success(res, user);
            } else {
                String name     = req.getParameter("name");
                String email    = req.getParameter("email");
                String location = req.getParameter("location");
                List<User> users = (name != null || email != null || location != null)
                    ? userDAO.search(name, email, location)
                    : userDAO.readAll();
                users.forEach(this::safeUser);
                JsonResponse.success(res, users);
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // CREATE – Register new user
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);
            User user = new User();
            user.setFullName(getString(body, "fullName"));
            user.setEmail(getString(body, "email"));
            user.setPhone(getString(body, "phone"));
            user.setPreferredLocation(getString(body, "preferredLocation"));
            user.setAccountStatus("active");
            user.setRole(body.has("role") ? body.get("role").getAsString() : "buyer");

            String rawPassword = getString(body, "password");
            if (rawPassword == null || rawPassword.isBlank()) {
                JsonResponse.error(res, 400, "Password is required"); return;
            }
            user.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt(12)));

            String err = user.validate();
            if (err != null) { JsonResponse.error(res, 400, err); return; }
            if (userDAO.emailExists(user.getEmail())) {
                JsonResponse.error(res, 409, "Email already registered"); return;
            }

            int newId = userDAO.create(user);
            JsonResponse.send(res, 201, java.util.Map.of("success", true,
                "data", java.util.Map.of("id", newId, "fullName", user.getFullName(), "email", user.getEmail())));
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // UPDATE – Update user information
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "User ID required"); return;
        }
        try {
            int        id   = Integer.parseInt(pathInfo.substring(1));
            JsonObject body = gson.fromJson(req.getReader(), JsonObject.class);

            User existing = userDAO.readById(id);
            if (existing == null) { JsonResponse.error(res, 404, "User not found"); return; }

            existing.setFullName(body.has("fullName")          ? body.get("fullName").getAsString()          : existing.getFullName());
            existing.setEmail(body.has("email")                ? body.get("email").getAsString()              : existing.getEmail());
            existing.setPhone(body.has("phone")                ? body.get("phone").getAsString()              : existing.getPhone());
            existing.setPreferredLocation(body.has("preferredLocation") ? body.get("preferredLocation").getAsString() : existing.getPreferredLocation());
            existing.setAccountStatus(body.has("accountStatus")? body.get("accountStatus").getAsString()     : existing.getAccountStatus());
            existing.setRole(body.has("role")                  ? body.get("role").getAsString()               : existing.getRole());

            if (body.has("password") && !body.get("password").getAsString().isBlank()) {
                userDAO.updatePassword(id, BCrypt.hashpw(body.get("password").getAsString(), BCrypt.gensalt(12)));
            }

            if (userDAO.emailExistsForOther(existing.getEmail(), id)) {
                JsonResponse.error(res, 409, "Email already used by another user"); return;
            }

            boolean updated = userDAO.update(existing);
            if (updated) JsonResponse.success(res, "User updated successfully");
            else         JsonResponse.error(res, 404, "User not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // DELETE – Delete user account
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "User ID required"); return;
        }
        try {
            boolean deleted = userDAO.delete(Integer.parseInt(pathInfo.substring(1)));
            if (deleted) JsonResponse.success(res, "User deleted successfully");
            else         JsonResponse.error(res, 404, "User not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // Strip password before sending user to client
    private void safeUser(User u) { u.setPassword(null); }

    private String getString(JsonObject body, String key) {
        return body.has(key) && !body.get(key).isJsonNull() ? body.get(key).getAsString() : null;
    }
}
