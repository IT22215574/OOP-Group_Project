package com.primeestate.servlet;

import com.primeestate.util.JsonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/upload/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 10 * 1024 * 1024,
    maxRequestSize    = 15 * 1024 * 1024
)
public class ImageUploadServlet extends HttpServlet {

    private String uploadDir;

    private static final String FRONTEND_IMAGES = "/Applications/XAMPP/xamppfiles/htdocs/OOP-Group_Project/frontend/assets/images";
    private static final String IMAGE_URL_PREFIX = "/OOP-Group_Project/frontend/assets/images";

    @Override
    public void init() throws ServletException {
        uploadDir = FRONTEND_IMAGES;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonResponse.error(res, 401, "Authentication required");
            return;
        }

        Part part = req.getPart("image");
        if (part == null || part.getSize() == 0) {
            JsonResponse.error(res, 400, "No image provided");
            return;
        }

        String originalName = extractFileName(part);
        if (originalName == null || originalName.isEmpty()) {
            JsonResponse.error(res, 400, "Could not determine file name");
            return;
        }

        String lower = originalName.toLowerCase();
        if (!lower.endsWith(".jpg") && !lower.endsWith(".jpeg") &&
            !lower.endsWith(".png") && !lower.endsWith(".gif") && !lower.endsWith(".webp")) {
            JsonResponse.error(res, 400, "Invalid format. Allowed: jpg, jpeg, png, gif, webp");
            return;
        }

        String ext      = originalName.substring(originalName.lastIndexOf('.'));
        String fileName = UUID.randomUUID() + ext.toLowerCase();
        File   saved    = new File(uploadDir, fileName);
        part.write(saved.getAbsolutePath());
        saved.setReadable(true, false);  // chmod o+r so Apache can serve it

        String imageUrl = req.getScheme() + "://" + req.getServerName()
                        + IMAGE_URL_PREFIX + "/" + fileName;
        JsonResponse.send(res, 200, Map.of("success", true, "data", Map.of("imageUrl", imageUrl)));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty()) {
            res.sendError(404, "Image not found");
            return;
        }
        
        String fileName = pathInfo.substring(1);
        if (fileName.contains("/") || fileName.contains("..")) {
            res.sendError(403, "Invalid file path");
            return;
        }
        
        File imageFile = new File(uploadDir, fileName);
        if (!imageFile.exists() || !imageFile.isFile()) {
            res.sendError(404, "Image not found");
            return;
        }
        
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        res.setContentType("image/" + (ext.equals("jpg") ? "jpeg" : ext));
        res.setContentLength((int) imageFile.length());
        
        try (java.io.FileInputStream input = new java.io.FileInputStream(imageFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                res.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
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
