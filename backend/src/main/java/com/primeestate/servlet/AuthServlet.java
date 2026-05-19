package com.primeestate.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primeestate.dao.AgentDAO;
import com.primeestate.dao.UserDAO;
import com.primeestate.model.Agent;
import com.primeestate.model.User;
import com.primeestate.util.JsonResponse;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication – login, logout, session check.
 * User registration is also here for convenience; full user CRUD is in UserServlet.
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserDAO  userDAO  = new UserDAO();
    private final AgentDAO agentDAO = new AgentDAO();
    private final Gson     gson     = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if      ("/register".equals(pathInfo))       handleRegister(req, res);
        else if ("/register-agent".equals(pathInfo)) handleRegisterAgent(req, res);
        else if ("/login".equals(pathInfo))          handleLogin(req, res);
        else if ("/logout".equals(pathInfo))         handleLogout(req, res);
        else    JsonResponse.error(res, 404, "Endpoint not found");
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject body     = gson.fromJson(req.getReader(), JsonObject.class);
            String     fullName = body.get("fullName").getAsString();
            String     email    = body.get("email").getAsString();
            String     password = body.get("password").getAsString();
            String     phone    = body.has("phone") ? body.get("phone").getAsString() : null;
            String     location = body.has("preferredLocation") ? body.get("preferredLocation").getAsString() : null;

            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                JsonResponse.error(res, 400, "Full name, email and password are required");
                return;
            }
            if (userDAO.emailExists(email)) {
                JsonResponse.error(res, 409, "Email already registered");
                return;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
            user.setPhone(phone);
            user.setPreferredLocation(location);
            user.setAccountStatus("active");
            user.setRole("buyer");

            int newId = userDAO.create(user);
            Map<String, Object> result = new HashMap<>();
            result.put("id", newId);
            result.put("fullName", fullName);
            result.put("email", email);
            result.put("role", "buyer");
            JsonResponse.send(res, 201, Map.of("success", true, "data", result));

        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleRegisterAgent(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject body     = gson.fromJson(req.getReader(), JsonObject.class);
            String     fullName = body.get("fullName").getAsString();
            String     email    = body.get("email").getAsString();
            String     password = body.get("password").getAsString();
            String     phone    = body.has("phone") ? body.get("phone").getAsString() : null;
            String     location = body.has("location") ? body.get("location").getAsString() : null;
            String     contactNumber       = body.has("contactNumber") ? body.get("contactNumber").getAsString() : phone;
            int        experienceYears     = body.has("experienceYears") ? body.get("experienceYears").getAsInt() : 0;
            String     specialization      = body.has("specialization") ? body.get("specialization").getAsString() : null;

            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                JsonResponse.error(res, 400, "Full name, email and password are required");
                return;
            }
            if (userDAO.emailExists(email)) {
                JsonResponse.error(res, 409, "Email already registered");
                return;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
            user.setPhone(phone);
            user.setPreferredLocation(location);
            user.setAccountStatus("active");
            user.setRole("agent");

            int newUserId = userDAO.create(user);

            Agent agent = new Agent();
            agent.setFullName(fullName);
            agent.setEmail(email);
            agent.setContactNumber(contactNumber);
            agent.setLocation(location);
            agent.setExperienceYears(experienceYears);
            agent.setPropertySpecialization(specialization);
            agent.setAvailabilityStatus("available");

            int newAgentId = agentDAO.create(agent);

            Map<String, Object> result = new HashMap<>();
            result.put("id",      newUserId);
            result.put("agentId", newAgentId);
            result.put("fullName", fullName);
            result.put("email",    email);
            result.put("role",     "agent");
            JsonResponse.send(res, 201, Map.of("success", true, "data", result));

        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            JsonObject body     = gson.fromJson(req.getReader(), JsonObject.class);
            String     email    = body.get("email").getAsString();
            String     password = body.get("password").getAsString();

            User user = userDAO.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                JsonResponse.error(res, 401, "Invalid email or password");
                return;
            }
            if ("suspended".equals(user.getAccountStatus()) || "inactive".equals(user.getAccountStatus())) {
                JsonResponse.error(res, 403, "Account is not active");
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("userId",   user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(3600);

            Map<String, Object> result = new HashMap<>();
            result.put("id",       user.getId());
            result.put("fullName", user.getFullName());
            result.put("email",    user.getEmail());
            result.put("role",     user.getRole());
            if ("agent".equals(user.getRole())) {
                Agent agent = agentDAO.findByEmail(user.getEmail());
                if (agent != null) result.put("agentId", agent.getId());
            }
            JsonResponse.success(res, result);

        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        JsonResponse.success(res, "Logged out successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // GET /api/auth/me — return current session user
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonResponse.error(res, 401, "Not authenticated");
            return;
        }
        try {
            int  userId = (int) session.getAttribute("userId");
            User user   = userDAO.findById(userId);
            if (user == null) { JsonResponse.error(res, 404, "User not found"); return; }

            Map<String, Object> result = new HashMap<>();
            result.put("id",               user.getId());
            result.put("fullName",         user.getFullName());
            result.put("email",            user.getEmail());
            result.put("role",             user.getRole());
            result.put("phone",            user.getPhone());
            result.put("preferredLocation",user.getPreferredLocation());
            result.put("accountStatus",    user.getAccountStatus());
            JsonResponse.success(res, result);
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }
}
