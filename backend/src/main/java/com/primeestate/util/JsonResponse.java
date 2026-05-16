package com.primeestate.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonResponse {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void send(HttpServletResponse res, int status, Object data) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(GSON.toJson(data));
    }

    public static void success(HttpServletResponse res, Object data) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("data", data);
        send(res, 200, body);
    }

    public static void error(HttpServletResponse res, int status, String message) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", message);
        send(res, status, body);
    }
}
