package com.leanx.app.api.user.auth;

import java.io.IOException;
import java.sql.SQLException;

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

@WebServlet(name = "authController", value = "/api/auth/*")
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            int authUserId = authService.authenticate(username, password);
            if (authUserId == -1) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed!");
                return;
            }

            // Invalidate the current session and create a new session to prevent session fixation
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);

            // Set the userId and session timeout
            session.setAttribute("userId", authUserId);
            session.setMaxInactiveInterval(SecurityConfig.SESSION_TIMEOUT);

            ApiUtils.sendSuccessResponse(response, "Login successful");
        } catch (FirstLoginException | PasswordExpiredException e) {
            HttpSession session = request.getSession(true);
            try {
                session.setAttribute("userId", new UserService().getUserId(username));
            } catch (SQLException e1) {
                ApiUtils.sendExceptionResponse(response, null, e1);
                return;
            }
            session.setMaxInactiveInterval(SecurityConfig.SESSION_TIMEOUT - 1800); // Session Timeout set to 30 min
            ApiUtils.sendRedirectResponse(response, e.getMessage(), "/change-password");
        } catch (AccountLockedException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        session.invalidate();
        ApiUtils.sendSuccessResponse(response, "Logout successful!");
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");

        try {
            boolean success = authService.changePassword(userId, userId, oldPassword, newPassword);
            if (!success) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update password!");
                return;
            }

            ApiUtils.sendSuccessResponse(response, "Password changed successfully!");
        } catch (IllegalArgumentException e) {
            ApiUtils.sendExceptionResponse(response, e.getMessage(), e);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, null, e);
        } catch (SecurityException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}