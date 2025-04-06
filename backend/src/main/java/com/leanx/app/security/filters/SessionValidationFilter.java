package com.leanx.app.security.filters;

import java.io.IOException;

import com.leanx.app.utils.ApiUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet filter that intercepts all requests to the "/api/*" path to validate
 * the user's session. It checks if a valid HTTP session exists and if the
 * 'userId' attribute is present in the session. Requests to specific endpoints
 * like "/login", "/logout", and "/change-password" are excluded from this
 * session validation. If the session is invalid or the 'userId' is missing,
 * an unauthorized error response (401) is sent back to the client.
 */
@WebFilter("/api/*")
public class SessionValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional initialization logic can be placed here if needed.
    }

    /**
     * Intercepts incoming requests and outgoing responses for resources under the
     * "/api/*" path. It performs session validation by checking for the existence
     * of a valid HTTP session and the presence of the 'userId' attribute.
     * Requests to the "/login", "/logout", and "/change-password" endpoints are
     * allowed to bypass this validation.
     *
     * @param request  The {@code ServletRequest} object representing the client's request.
     * @param response The {@code ServletResponse} object representing the server's response.
     * @param chain    The {@code FilterChain} object that allows the request to proceed to the next filter 
     * in the chain or to the target servlet.
     * @throws IOException      If an I/O error occurs during the processing of the filter.
     * @throws ServletException If a servlet-specific error occurs during the processing of the filter.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        // Allow requests to login, logout, and change password without session validation
        if (path.endsWith("/login") || path.endsWith("/logout") || path.endsWith("/change-password")) {
            chain.doFilter(request, response);
            return;
        }

        // Get the session; do not create one if it doesn't exist
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            ApiUtils.sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "invalid or inactive session!");
            return;
        }

        // Check if the userId attribute exists in the session
        if (session.getAttribute("userId") == null) {
            ApiUtils.sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized session!");
            return;
        }

        // If session is valid and userId exists, continue the request processing
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Optional cleanup logic can be placed here if needed.
    }
}