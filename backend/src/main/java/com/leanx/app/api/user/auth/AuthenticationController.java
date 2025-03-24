package com.leanx.app.api.user.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leanx.app.service.modules.system.configs.SecurityConfig;
import com.leanx.app.service.modules.user.admin.UserService;
import com.leanx.app.service.modules.user.auth.AuthenticationService;
import com.leanx.app.service.modules.user.auth.exceptions.AccountLockedException;
import com.leanx.app.service.modules.user.auth.exceptions.FirstLoginException;
import com.leanx.app.service.modules.user.auth.exceptions.PasswordExpiredException;
import com.leanx.app.utils.ApiUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthController", value = "/api/auth/*")
public class AuthenticationController extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.endsWith("/login")) {
            handleLogin(request, response);
        } else if (path.endsWith("/logout")) {
            handleLogout(request, response);
        } else if (path.endsWith("/change-password")) {
            handleChangePassword(request, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username;
        String password;

        // handle JSON requests
        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            // Parse JSON Data
            try (BufferedReader reader = request.getReader()) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(json.toString());

                // Extract Username and Password
                username = jsonNode.get("username").asText();
                password = jsonNode.get("password").asText();
            } catch (Exception e) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data.");
                return;
            }
        } else {
            // Handle x-www-form-urlencoded
            username = request.getParameter("username");
            password = request.getParameter("password");
        }

        try {
            // Invalidate the current session and create a new session to prevent session fixation
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);

            int authUserId = authService.authenticate(username, password);
            if (authUserId == -1) {
                session.invalidate();
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed!");
                return;
            }

            // to be implemented!
            // List<Roles> roles = userRoleViewRepository.loadUserRoles(authUserId);
            // session.setAttribute("roles", roles);

            session.setAttribute("userId", authUserId);
            session.setMaxInactiveInterval(SecurityConfig.SESSION_TIMEOUT);

            ApiUtils.sendJsonResponse(response, "Login successful");
        } catch (FirstLoginException | PasswordExpiredException e) {
            HttpSession session = request.getSession(true);
            try {
                session.setAttribute("userId", new UserService().getUserId(username));
            } catch (SQLException e1) {
                ApiUtils.sendExceptionResponse(response, null, e1);
                session.invalidate();
                return;
            }
            session.setMaxInactiveInterval(3600); // Session Timeout set to 1 hour
            ApiUtils.sendRedirectResponse(response, e.getMessage(), "/api/auth/change-password");
        } catch (AccountLockedException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        session.invalidate();
        ApiUtils.sendJsonResponse(response, "Logout successful!");
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");
        String oldPassword;
        String newPassword;

        // handle JSON requests
        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            // Parse JSON Data
            try (BufferedReader reader = request.getReader()) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(json.toString());

                oldPassword = jsonNode.get("oldPassword").asText();
                newPassword = jsonNode.get("newPassword").asText();
            } catch (Exception e) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data.");
                return;
            }
        } else {
            // Handle x-www-form-urlencoded
            oldPassword = request.getParameter("oldPassword");
            newPassword = request.getParameter("newPassword");
        }

        try {
            boolean success = authService.changePassword(userId, userId, oldPassword, newPassword);
            if (!success) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update password!");
                return;
            }

            ApiUtils.sendJsonResponse(response, "Password changed successfully!");
        } catch (IllegalArgumentException e) {
            ApiUtils.sendExceptionResponse(response, e.getMessage(), e);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, null, e);
        } catch (SecurityException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}