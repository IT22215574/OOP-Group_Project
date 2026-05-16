package com.primeestate.config;

import com.primeestate.util.CORSFilter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import java.util.EnumSet;

/**
 * Programmatic fallback for CORS filter registration.
 *
 * web.xml is the primary registration path. If the container processes
 * web.xml first (standard behaviour), ctx.addFilter returns null here and
 * we do nothing.  If web.xml is skipped for any reason (e.g. metadata-
 * scanning quirks in the Jetty maven plugin), this listener registers the
 * filter so that preflight OPTIONS responses still carry CORS headers.
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        // Returns null when a filter named CORSFilter is already registered
        // (i.e. web.xml did its job). Only proceeds when it hasn't been set up yet.
        FilterRegistration.Dynamic cors =
                ctx.addFilter(CORSFilter.FILTER_NAME, CORSFilter.class);

        if (cors != null) {
            cors.setAsyncSupported(true);
            cors.addMappingForUrlPatterns(
                    EnumSet.of(
                            DispatcherType.REQUEST,
                            DispatcherType.FORWARD,
                            DispatcherType.INCLUDE,
                            DispatcherType.ERROR,
                            DispatcherType.ASYNC),
                    false,   // isMatchAfter=false → run before any other filter
                    "/api/*"
            );
        }
    }
}
