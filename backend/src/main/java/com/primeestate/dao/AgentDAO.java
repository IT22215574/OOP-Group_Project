package com.primeestate.dao;

import com.primeestate.model.Agent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 1 – Real Estate Agent CRUD
 * OOP - Inheritance: extends BaseDAO<Agent> (inherits getConnection(), mapRow() contract)
 * OOP - Polymorphism: overrides mapRow() to build Agent from ResultSet
 */
public class AgentDAO extends BaseDAO<Agent> {

    // CREATE – Register a new real estate agent
    @Override
    public int create(Agent agent) throws SQLException {
        String sql = "INSERT INTO agents (full_name, contact_number, email, location, experience_years, property_specialization, availability_status) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, agent.getFullName());
            ps.setString(2, agent.getContactNumber());
            ps.setString(3, agent.getEmail());
            ps.setString(4, agent.getLocation());
            ps.setInt(5, agent.getExperienceYears());
            ps.setString(6, agent.getPropertySpecialization());
            ps.setString(7, agent.getAvailabilityStatus() != null ? agent.getAvailabilityStatus() : "available");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    // READ – View agent profile by ID
    @Override
    public Agent readById(int id) throws SQLException {
        String sql = "SELECT * FROM agents WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // READ – View all agent profiles
    @Override
    public List<Agent> readAll() throws SQLException {
        String sql = "SELECT * FROM agents ORDER BY full_name ASC";
        List<Agent> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // READ – Search agents by name, location, or specialization
    public List<Agent> search(String name, String location, String specialization) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM agents WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + name + "%");
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location + "%");
        }
        if (specialization != null && !specialization.isEmpty()) {
            sql.append(" AND property_specialization = ?");
            params.add(specialization);
        }
        sql.append(" ORDER BY full_name ASC");

        List<Agent> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // UPDATE – Edit agent details
    @Override
    public boolean update(Agent agent) throws SQLException {
        String sql = "UPDATE agents SET full_name=?, contact_number=?, email=?, location=?, experience_years=?, property_specialization=?, availability_status=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, agent.getFullName());
            ps.setString(2, agent.getContactNumber());
            ps.setString(3, agent.getEmail());
            ps.setString(4, agent.getLocation());
            ps.setInt(5, agent.getExperienceYears());
            ps.setString(6, agent.getPropertySpecialization());
            ps.setString(7, agent.getAvailabilityStatus());
            ps.setInt(8, agent.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE – Remove agent profile
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM agents WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Agent findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM agents WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agents WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public boolean emailExistsForOther(String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agents WHERE email = ? AND id != ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    @Override
    protected Agent mapRow(ResultSet rs) throws SQLException {
        Agent a = new Agent();
        a.setId(rs.getInt("id"));
        a.setFullName(rs.getString("full_name"));
        a.setContactNumber(rs.getString("contact_number"));
        a.setEmail(rs.getString("email"));
        a.setLocation(rs.getString("location"));
        a.setExperienceYears(rs.getInt("experience_years"));
        a.setPropertySpecialization(rs.getString("property_specialization"));
        a.setAvailabilityStatus(rs.getString("availability_status"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
