package com.primeestate.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = "/api/*", asyncSupported = true)
public class CORSFilter implements Filter {

    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost",
            "http://localhost:3000",
            "http://localhost:5173"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest)  request;
        HttpServletResponse res = (HttpServletResponse) response;

        String origin = req.getHeader("Origin");

        // Vary must be set on every response so shared caches don't serve
        // a cached response with a wrong (or missing) Allow-Origin header.
        res.addHeader("Vary", "Origin");

        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            res.setHeader("Access-Control-Allow-Origin",      origin);
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            res.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, Authorization, X-Requested-With, Accept, Accept-Language, Content-Language");
            res.setHeader("Access-Control-Expose-Headers",
                    "Content-Type, Authorization");
            res.setHeader("Access-Control-Max-Age", "3600");
        }

        // Respond immediately to preflight; the browser checks these headers
        // before sending the real request.
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        chain.doFilter(request, response);
    }
}
