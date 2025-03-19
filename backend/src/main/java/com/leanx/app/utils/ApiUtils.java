package com.leanx.app.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;

public class ApiUtils {

    private static final Logger logger = Logger.getLogger(ApiUtils.class.getName());

    public static void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }

    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    public static void sendExceptionResponse(HttpServletResponse response, String defaultErrorMessage, Exception e) throws IOException {
        logger.log(Level.SEVERE, "Exception occurred: " + e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (defaultErrorMessage != null) {
            response.getWriter().write("{\"error\": \"" + defaultErrorMessage + "\"}");
            return;
        }

        response.getWriter().write("{\"error\":  Internal Server Problem! Please try again later\"}");
    }

    public static void sendRedirectResponse(HttpServletResponse response, String message, String redirectUrl) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"" + message + "\", \"redirect\": \"" + redirectUrl + "\"}");
    }
}
