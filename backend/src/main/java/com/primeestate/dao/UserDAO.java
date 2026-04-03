package com.primeestate.dao;
/**
 * MODULE 2 – User CRUD
 * OOP - Inheritance: extends BaseDAO<User> (inherits getConnection(), mapRow() contract)
 * OOP - Polymorphism: overrides mapRow() to build User from ResultSet
 */
import com.primeestate.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO<User> {

    // CREATE – Register a new user
    @Override
    public int create(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password, phone, preferred_location, account_status, role) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getPreferredLocation());
            ps.setString(6, user.getAccountStatus() != null ? user.getAccountStatus() : "active");
            ps.setString(7, user.getRole()          != null ? user.getRole()          : "buyer");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    // READ – View user profile by ID
    @Override
    public User readById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // READ – View all user profiles
    @Override
    public List<User> readAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY full_name ASC";
        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // READ – Search users by name, email, or location
    public List<User> search(String name, String email, String location) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email + "%");
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND preferred_location LIKE ?");
            params.add("%" + location + "%");
        }
        sql.append(" ORDER BY full_name ASC");

        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // READ – Find by email (used by AuthServlet)
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // UPDATE – Update user information
    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, preferred_location=?, account_status=?, role=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPreferredLocation());
            ps.setString(5, user.getAccountStatus());
            ps.setString(6, user.getRole());
            ps.setInt(7, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // UPDATE – Change user password
    public boolean updatePassword(int id, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE – Delete user account
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public boolean emailExistsForOther(String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    // Legacy alias used by AuthServlet
    public int save(User user) throws SQLException { return create(user); }
    public User findById(int id) throws SQLException { return readById(id); }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setPhone(rs.getString("phone"));
        u.setPreferredLocation(rs.getString("preferred_location"));
        u.setAccountStatus(rs.getString("account_status"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
