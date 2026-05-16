package com.primeestate.dao;

import com.primeestate.config.DBConnection;
import com.primeestate.model.Property;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {

    private Connection getConn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    public List<Property> findAll(String type, String city, String category, int limit, int offset) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM properties WHERE status = 'available'"
        );
        List<Object> params = new ArrayList<>();

        if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (city != null && !city.isEmpty()) {
            sql.append(" AND city LIKE ?");
            params.add("%" + city + "%");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Property> properties = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                properties.add(mapRow(rs));
            }
        }
        return properties;
    }

    public Property findById(int id) throws SQLException {
        String sql = "SELECT * FROM properties WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public int countAll(String type, String city, String category) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM properties WHERE status = 'available'");
        List<Object> params = new ArrayList<>();

        if (type     != null && !type.isEmpty())     { sql.append(" AND type = ?");         params.add(type); }
        if (city     != null && !city.isEmpty())     { sql.append(" AND city LIKE ?");      params.add("%" + city + "%"); }
        if (category != null && !category.isEmpty()) { sql.append(" AND category = ?");     params.add(category); }

        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int save(Property p) throws SQLException {
        String sql = "INSERT INTO properties (title, description, price, type, category, status, bedrooms, bathrooms, area_sqft, address, city, state, zip_code, agent_id, image_url) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setString(4, p.getType());
            ps.setString(5, p.getCategory());
            ps.setString(6, p.getStatus() != null ? p.getStatus() : "available");
            ps.setInt(7, p.getBedrooms());
            ps.setInt(8, p.getBathrooms());
            ps.setDouble(9, p.getAreaSqft());
            ps.setString(10, p.getAddress());
            ps.setString(11, p.getCity());
            ps.setString(12, p.getState());
            ps.setString(13, p.getZipCode());
            ps.setInt(14, p.getAgentId());
            ps.setString(15, p.getImageUrl());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean update(Property p) throws SQLException {
        String sql = "UPDATE properties SET title=?, description=?, price=?, type=?, category=?, status=?, bedrooms=?, bathrooms=?, area_sqft=?, address=?, city=?, state=?, zip_code=?, image_url=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setString(4, p.getType());
            ps.setString(5, p.getCategory());
            ps.setString(6, p.getStatus());
            ps.setInt(7, p.getBedrooms());
            ps.setInt(8, p.getBathrooms());
            ps.setDouble(9, p.getAreaSqft());
            ps.setString(10, p.getAddress());
            ps.setString(11, p.getCity());
            ps.setString(12, p.getState());
            ps.setString(13, p.getZipCode());
            ps.setString(14, p.getImageUrl());
            ps.setInt(15, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM properties WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Property mapRow(ResultSet rs) throws SQLException {
        Property p = new Property();
        p.setId(rs.getInt("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setType(rs.getString("type"));
        p.setCategory(rs.getString("category"));
        p.setStatus(rs.getString("status"));
        p.setBedrooms(rs.getInt("bedrooms"));
        p.setBathrooms(rs.getInt("bathrooms"));
        p.setAreaSqft(rs.getDouble("area_sqft"));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setState(rs.getString("state"));
        p.setZipCode(rs.getString("zip_code"));
        p.setAgentId(rs.getInt("agent_id"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
