package com.primeestate.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class CORSFilter implements Filter {

    public static final String FILTER_NAME = "CORSFilter";

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

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
