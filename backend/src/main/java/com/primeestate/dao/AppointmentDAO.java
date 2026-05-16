package com.primeestate.dao;

import com.primeestate.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 3 – Appointment CRUD
 * OOP - Inheritance: extends BaseDAO<Appointment>
 * OOP - Polymorphism: overrides mapRow() to build Appointment from ResultSet
 * Connection: JOINs users (Module 2) and agents (Module 1) to enrich appointment data
 */
public class AppointmentDAO extends BaseDAO<Appointment> {

    private static final String SELECT_WITH_JOINS =
        "SELECT a.*, u.full_name AS user_name, ag.full_name AS agent_name " +
        "FROM appointments a " +
        "LEFT JOIN users  u  ON a.user_id  = u.id " +
        "LEFT JOIN agents ag ON a.agent_id = ag.id";

    // CREATE – Book appointment
    @Override
    public int create(Appointment appt) throws SQLException {
        String sql = "INSERT INTO appointments (user_id, agent_id, appointment_date, appointment_time, property_type, appointment_status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, appt.getUserId());
            ps.setInt(2, appt.getAgentId());
            ps.setDate(3, appt.getAppointmentDate());
            ps.setTime(4, appt.getAppointmentTime());
            ps.setString(5, appt.getPropertyType());
            ps.setString(6, appt.getAppointmentStatus() != null ? appt.getAppointmentStatus() : "pending");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    // READ – View appointment by ID (with user and agent names)
    @Override
    public Appointment readById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + " WHERE a.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // READ – View all appointments
    @Override
    public List<Appointment> readAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + " ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // READ – Search/filter appointments
    public List<Appointment> search(Integer userId, Integer agentId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_WITH_JOINS + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) { sql.append(" AND a.user_id = ?");             params.add(userId); }
        if (agentId != null){ sql.append(" AND a.agent_id = ?");            params.add(agentId); }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND a.appointment_status = ?"); params.add(status);
        }
        sql.append(" ORDER BY a.appointment_date DESC, a.appointment_time DESC");

        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // UPDATE – Reschedule appointment (date, time, status)
    @Override
    public boolean update(Appointment appt) throws SQLException {
        String sql = "UPDATE appointments SET user_id=?, agent_id=?, appointment_date=?, appointment_time=?, property_type=?, appointment_status=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, appt.getUserId());
            ps.setInt(2, appt.getAgentId());
            ps.setDate(3, appt.getAppointmentDate());
            ps.setTime(4, appt.getAppointmentTime());
            ps.setString(5, appt.getPropertyType());
            ps.setString(6, appt.getAppointmentStatus());
            ps.setInt(7, appt.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE – Cancel appointment
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    protected Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAgentId(rs.getInt("agent_id"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setAppointmentTime(rs.getTime("appointment_time"));
        a.setPropertyType(rs.getString("property_type"));
        a.setAppointmentStatus(rs.getString("appointment_status"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        try { a.setUserName(rs.getString("user_name")); } catch (SQLException ignored) {}
        try { a.setAgentName(rs.getString("agent_name")); } catch (SQLException ignored) {}
        return a;
    }
}
