package com.leanx.app.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

public class ApiUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(ApiUtils.class.getName());

    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = objectMapper.writeValueAsString(data);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        logger.log(Level.INFO, "JSON response sent successfully: {0}", jsonResponse);
    }

    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        String jsonResponse = objectMapper.writeValueAsString(new ErrorResponse(message, null));
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        logger.log(Level.WARNING, "Error response sent (Status: {0}): {1}", new Object[]{statusCode, message});
    }

    public static void sendExceptionResponse(HttpServletResponse response, String message, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String errorMessage = (message != null) ? message : "Internal Server Error! Please Try Again Later.";
        String jsonResponse = objectMapper.writeValueAsString(new ErrorResponse(errorMessage, e.getMessage()));
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        logger.log(Level.SEVERE, "Exception response sent: " + errorMessage, e);
    }

    public static void sendRedirectResponse(HttpServletResponse response, String message, String redirectUrl) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 Found / Redirect
        
        RedirectResponse redirectResponse = new RedirectResponse(message, redirectUrl);
        objectMapper.writeValue(response.getWriter(), redirectResponse);

        logger.log(Level.INFO, "Redirect response sent: {0} -> {1}", new Object[]{message, redirectUrl});
    }

    private static class RedirectResponse {
        @JsonProperty
        public String message;
        @JsonProperty
        public String redirectUrl;

        public RedirectResponse(String message, String redirectUrl) {
            this.message = message;
            this.redirectUrl = redirectUrl;
        }
    }

    private static class ErrorResponse {
        @JsonProperty
        public String error;
        @JsonProperty
        public String details;

        public ErrorResponse(String error, String details) {
            this.error = error;
            this.details = details;
        }
    }
}