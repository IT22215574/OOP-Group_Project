package com.primeestate.servlet;

import com.primeestate.dao.AdvertisementDAO;
import com.primeestate.model.Advertisement;
import com.primeestate.util.JsonResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MODULE 4 – Property Advertisement Management
 * Endpoints:
 *   GET    /api/advertisements             – list / search advertisements
 *   GET    /api/advertisements/{id}        – view advertisement (with images)
 *   POST   /api/advertisements             – post new advertisement (multipart, up to 3 images)
 *   PUT    /api/advertisements/{id}        – edit advertisement (multipart, up to 3 images)
 *   DELETE /api/advertisements/{id}        – remove advertisement
 *
 * Cross-module: agentId links Module 1 (Agents)
 * Image upload: multipart/form-data, fields image1/image2/image3 (max 5MB each)
 */
@WebServlet("/api/advertisements/*")
    // Configured for handling multipart property images (max 5MB per file, 20MB total)
    @MultipartConfig(
        fileSizeThreshold = 1024 * 1024,       // 1MB before writing to disk
        maxFileSize       = 5 * 1024 * 1024,   // 5MB per image
        maxRequestSize    = 20 * 1024 * 1024   // 20MB total
    )
public class AdvertisementServlet extends HttpServlet {

    private final AdvertisementDAO advertisementDAO = new AdvertisementDAO();

    private String uploadDir;

    @Override
    public void init() throws ServletException {
        uploadDir = getServletContext().getRealPath("/") + "uploads" + File.separator + "advertisements";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    // READ – View / search advertisements
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Advertisement ad = advertisementDAO.readById(Integer.parseInt(pathInfo.substring(1)));
                if (ad == null) { JsonResponse.error(res, 404, "Advertisement not found"); return; }
                JsonResponse.success(res, ad);
            } else {
                String title        = req.getParameter("title");
                String location     = req.getParameter("location");
                String propertyType = req.getParameter("propertyType");
                String status       = req.getParameter("status");
                List<Advertisement> ads = (title != null || location != null || propertyType != null || status != null)
                    ? advertisementDAO.search(title, location, propertyType, status)
                    : advertisementDAO.readAll();
                JsonResponse.success(res, ads);
            }
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // CREATE – Post new property advertisement (multipart/form-data with up to 3 images)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            Advertisement ad = buildFromRequest(req);
            String err = ad.validate();
            if (err != null) { JsonResponse.error(res, 400, err); return; }

            List<String> savedPaths = saveUploadedImages(req, 0);
            ad.setImagePaths(savedPaths);

            int newId = advertisementDAO.create(ad);
            JsonResponse.send(res, 201, Map.of("success", true, "data",
                Map.of("id", newId, "imageCount", savedPaths.size())));
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // UPDATE – Edit advertisement details (replace images if new ones provided)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Advertisement ID required"); return;
        }
        try {
            int           id       = Integer.parseInt(pathInfo.substring(1));
            Advertisement existing = advertisementDAO.readById(id);
            if (existing == null) { JsonResponse.error(res, 404, "Advertisement not found"); return; }

            Advertisement updated = buildFromRequest(req);
            updated.setId(id);

            List<String> newImages = saveUploadedImages(req, 0);
            if (!newImages.isEmpty()) updated.setImagePaths(newImages);

            boolean ok = advertisementDAO.update(updated);
            if (ok) JsonResponse.success(res, "Advertisement updated successfully");
            else    JsonResponse.error(res, 404, "Advertisement not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // DELETE – Remove advertisement
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            JsonResponse.error(res, 400, "Advertisement ID required"); return;
        }
        try {
            boolean deleted = advertisementDAO.delete(Integer.parseInt(pathInfo.substring(1)));
            if (deleted) JsonResponse.success(res, "Advertisement deleted successfully");
            else         JsonResponse.error(res, 404, "Advertisement not found");
        } catch (SQLException e) {
            JsonResponse.error(res, 500, "Database error: " + e.getMessage());
        }
    }

    // Build Advertisement from multipart form fields
    private Advertisement buildFromRequest(HttpServletRequest req) throws IOException, ServletException {
        Advertisement ad = new Advertisement();
        String agentIdStr = req.getParameter("agentId");
        if (agentIdStr != null) ad.setAgentId(Integer.parseInt(agentIdStr));
        ad.setPropertyTitle(req.getParameter("propertyTitle"));
        ad.setPropertyType(req.getParameter("propertyType"));
        String priceStr = req.getParameter("price");
        if (priceStr != null && !priceStr.isEmpty()) ad.setPrice(new BigDecimal(priceStr));
        ad.setLocation(req.getParameter("location"));
        ad.setDescription(req.getParameter("description"));
        String status = req.getParameter("availabilityStatus");
        ad.setAvailabilityStatus(status != null ? status : "available");
        return ad;
    }

    // Save up to 3 uploaded image files; returns list of web-accessible paths
    private List<String> saveUploadedImages(HttpServletRequest req, int advertisementId) throws IOException, ServletException {
        List<String> paths = new ArrayList<>();
        String[] fields = {"image1", "image2", "image3"};
        for (String field : fields) {
            Part part = req.getPart(field);
            if (part == null || part.getSize() == 0) continue;
            String originalName = extractFileName(part);
            if (originalName == null || originalName.isEmpty()) continue;

            String ext      = originalName.substring(originalName.lastIndexOf('.'));
            String fileName = UUID.randomUUID() + ext;
            String filePath = uploadDir + File.separator + fileName;
            part.write(filePath);
            paths.add("uploads/advertisements/" + fileName);
        }
        return paths;
    }

    private String extractFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
