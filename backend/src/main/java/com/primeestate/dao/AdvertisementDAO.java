package com.primeestate.dao;

import com.primeestate.model.Advertisement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 4 – Property Advertisement CRUD
 * OOP - Inheritance: extends BaseDAO<Advertisement>
 * OOP - Polymorphism: overrides mapRow() to build Advertisement from ResultSet
 * Connection: JOINs agents (Module 1); manages advertisement_images (up to 3 per ad)
 */
public class AdvertisementDAO extends BaseDAO<Advertisement> {

    private static final String SELECT_WITH_JOIN =
        "SELECT a.*, ag.full_name AS agent_name " +
        "FROM advertisements a " +
        "LEFT JOIN agents ag ON a.agent_id = ag.id";

    // CREATE – Post new property advertisement
    @Override
    public int create(Advertisement ad) throws SQLException {
        String sql = "INSERT INTO advertisements (agent_id, property_title, property_type, price, location, description, availability_status) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ad.getAgentId());
            ps.setString(2, ad.getPropertyTitle());
            ps.setString(3, ad.getPropertyType());
            ps.setBigDecimal(4, ad.getPrice());
            ps.setString(5, ad.getLocation());
            ps.setString(6, ad.getDescription());
            ps.setString(7, ad.getAvailabilityStatus() != null ? ad.getAvailabilityStatus() : "available");
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                saveImages(newId, ad.getImagePaths());
                return newId;
            }
        }
        return -1;
    }

    // READ – View advertisement by ID (includes images)
    @Override
    public Advertisement readById(int id) throws SQLException {
        String sql = SELECT_WITH_JOIN + " WHERE a.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Advertisement ad = mapRow(rs);
                ad.setImagePaths(fetchImages(id));
                return ad;
            }
        }
        return null;
    }

    // READ – View all advertisements (includes images)
    @Override
    public List<Advertisement> readAll() throws SQLException {
        String sql = SELECT_WITH_JOIN + " ORDER BY a.created_at DESC";
        List<Advertisement> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Advertisement ad = mapRow(rs);
                ad.setImagePaths(fetchImages(ad.getId()));
                list.add(ad);
            }
        }
        return list;
    }

    // READ – Search/filter advertisements
    public List<Advertisement> search(String title, String location, String propertyType, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_WITH_JOIN + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            sql.append(" AND a.property_title LIKE ?"); params.add("%" + title + "%");
        }
        if (location != null && !location.isEmpty()) {
            sql.append(" AND a.location LIKE ?"); params.add("%" + location + "%");
        }
        if (propertyType != null && !propertyType.isEmpty()) {
            sql.append(" AND a.property_type = ?"); params.add(propertyType);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND a.availability_status = ?"); params.add(status);
        }
        sql.append(" ORDER BY a.created_at DESC");

        List<Advertisement> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Advertisement ad = mapRow(rs);
                ad.setImagePaths(fetchImages(ad.getId()));
                list.add(ad);
            }
        }
        return list;
    }

    // UPDATE – Edit advertisement details
    @Override
    public boolean update(Advertisement ad) throws SQLException {
        String sql = "UPDATE advertisements SET agent_id=?, property_title=?, property_type=?, price=?, location=?, description=?, availability_status=? WHERE id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, ad.getAgentId());
            ps.setString(2, ad.getPropertyTitle());
            ps.setString(3, ad.getPropertyType());
            ps.setBigDecimal(4, ad.getPrice());
            ps.setString(5, ad.getLocation());
            ps.setString(6, ad.getDescription());
            ps.setString(7, ad.getAvailabilityStatus());
            ps.setInt(8, ad.getId());
            boolean updated = ps.executeUpdate() > 0;
            if (updated && ad.getImagePaths() != null && !ad.getImagePaths().isEmpty()) {
                deleteImages(ad.getId());
                saveImages(ad.getId(), ad.getImagePaths());
            }
            return updated;
        }
    }

    // DELETE – Remove advertisement (images cascade via FK)
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM advertisements WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void saveImages(int advertisementId, List<String> paths) throws SQLException {
        if (paths == null || paths.isEmpty()) return;
        String sql = "INSERT INTO advertisement_images (advertisement_id, image_path, image_order) VALUES (?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            int order = 1;
            for (String path : paths) {
                if (order > 3) break;
                ps.setInt(1, advertisementId);
                ps.setString(2, path);
                ps.setInt(3, order++);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteImages(int advertisementId) throws SQLException {
        String sql = "DELETE FROM advertisement_images WHERE advertisement_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, advertisementId);
            ps.executeUpdate();
        }
    }

    private List<String> fetchImages(int advertisementId) throws SQLException {
        String sql = "SELECT image_path FROM advertisement_images WHERE advertisement_id = ? ORDER BY image_order ASC";
        List<String> paths = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, advertisementId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) paths.add(rs.getString("image_path"));
        }
        return paths;
    }

    @Override
    protected Advertisement mapRow(ResultSet rs) throws SQLException {
        Advertisement ad = new Advertisement();
        ad.setId(rs.getInt("id"));
        ad.setAgentId(rs.getInt("agent_id"));
        ad.setPropertyTitle(rs.getString("property_title"));
        ad.setPropertyType(rs.getString("property_type"));
        ad.setPrice(rs.getBigDecimal("price"));
        ad.setLocation(rs.getString("location"));
        ad.setDescription(rs.getString("description"));
        ad.setAvailabilityStatus(rs.getString("availability_status"));
        ad.setCreatedAt(rs.getTimestamp("created_at"));
        try { ad.setAgentName(rs.getString("agent_name")); } catch (SQLException ignored) {}
        return ad;
    }
}
