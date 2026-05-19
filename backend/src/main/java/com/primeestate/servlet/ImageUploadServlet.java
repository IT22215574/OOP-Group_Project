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

    private static final String UPLOAD_DIR = "/Applications/XAMPP/xamppfiles/htdocs/OOP-Group_Project/frontend/assets/images";
    private static final String WEB_PATH   = "/OOP-Group_Project/frontend/assets/images";

    @Override
    public void init() throws ServletException {
        File dir = new File(UPLOAD_DIR);
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
        File   saved    = new File(UPLOAD_DIR, fileName);
        part.write(saved.getAbsolutePath());
        saved.setReadable(true, false);  // chmod o+r so Apache can serve it

        String imageUrl = "http://localhost" + WEB_PATH + "/" + fileName;
        JsonResponse.send(res, 200, Map.of("success", true, "data", Map.of("imageUrl", imageUrl)));
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
