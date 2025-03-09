package com.leanx.app.api;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter(urlPatterns = {"/api/*"}, filterName = "sessionValidationFilter", order = 2) // Adjust URL patterns
public class SessionValidationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SessionValidationFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic (if needed)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            session.setMaxInactiveInterval(60 * 60); // 1 hour
        }

        if (session == null || session.getAttribute("currentUser") == null) {
            logger.warning("Session expired or invalid for: " + httpRequest.getRequestURI());
            // httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?message=Session expired, please login again.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic (if needed)
    }
}