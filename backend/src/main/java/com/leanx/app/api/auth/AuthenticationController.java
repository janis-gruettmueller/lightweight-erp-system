package com.leanx.app.api.auth;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.exceptions.AccountLockedException;
import com.leanx.app.exceptions.FirstLoginException;
import com.leanx.app.exceptions.PasswordExpiredException;
import com.leanx.app.security.SecurityConfig;
import com.leanx.app.service.AuthenticationService;
import com.leanx.app.service.modules.user_admin.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "authController", value = "/api/auth/*")
public class AuthenticationController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

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
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Unknown endpoint\"}");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            int authUserId = authService.authenticate(username, password);
            if (authUserId == -1) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authentication failed.\"}");
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

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Login successful\"}");
        } catch (FirstLoginException | PasswordExpiredException e) {
            HttpSession session = request.getSession(true);
            try {
                session.setAttribute("userId", new UserService().getUserId(username));
            } catch (SQLException e1) {
                logger.log(Level.SEVERE, "Database error while retrieving user: " + e1.getMessage(), e1);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Internal server error! Please try again later.\"}");
            }
            session.setMaxInactiveInterval(SecurityConfig.SESSION_TIMEOUT - 1800); // Session Timeout set to 30 min
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\", \"redirect\": \"/change-password\"}");
        } catch (AccountLockedException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        session.invalidate();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Logout successful!\"}");
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        Integer userId = (Integer) session.getAttribute("userId");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
    
        try {
            boolean success = authService.changePassword(userId, userId, oldPassword, newPassword);
            if (!success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Failed to update password!\"}");
                return;
            }
    
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Password changed successfully!\"}");
        } catch (IllegalArgumentException | SQLException e) {
            logger.log(Level.SEVERE, "Error while updating user: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error! Please try again later.\"}");
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Security Exception: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Failed to authenticate!\"}");
        }
    }
}