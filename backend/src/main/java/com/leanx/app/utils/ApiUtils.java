package com.leanx.app.utils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for handling API responses in JSON format.
 * Provides methods to send successful data, errors, and exceptions as JSON responses.
 */
public class ApiUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(ApiUtils.class.getName());

    /**
     * Sends a successful JSON response to the client.
     * Sets the content type to "application/json", character encoding to "UTF-8",
     * and the status code to {@link HttpServletResponse#SC_OK} (200 OK).
     * If the provided data is a String, it will be wrapped in a JSON object with a "status" key.
     *
     * @param response The {@link HttpServletResponse} object to write the response to.
     * @param data     The data to be sent in the JSON response. Can be any Java object.
     * @throws IOException If an I/O error occurs while writing to the response.
     */
    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Object formattedData;
        if (data instanceof String) {
            formattedData = Map.of("status", data);
        } else {
            formattedData = data;
        }

        String jsonResponse = objectMapper.writeValueAsString(formattedData);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        // logger.log(Level.INFO, "JSON response sent successfully: {0}", jsonResponse);
    }

    /**
     * Sends an error JSON response to the client with a specific status code and message.
     * Sets the content type to "application/json", character encoding to "UTF-8",
     * and the status code to the provided {@code statusCode}.
     * The response body will be a JSON object with "error" and "details" keys.
     *
     * @param response   The {@link HttpServletResponse} object to write the response to.
     * @param statusCode The HTTP status code to set for the error response (e.g., 400, 404, 500).
     * @param message    The error message to be included in the JSON response.
     * @throws IOException If an I/O error occurs while writing to the response.
     */
    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        String jsonResponse = objectMapper.writeValueAsString(new ErrorResponse(message, null));
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        // logger.log(Level.WARNING, "Error response sent (Status: {0}): {1}", new Object[]{statusCode, message});
    }

    /**
     * Sends an exception JSON response to the client, typically with an internal server error status.
     * Sets the content type to "application/json", character encoding to "UTF-8",
     * and the status code to {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR} (500 Internal Server Error).
     * The response body will be a JSON object with "error" and "details" keys,
     * including the provided message and the exception's message.
     * The exception is also logged at the SEVERE level.
     *
     * @param response The {@link HttpServletResponse} object to write the response to.
     * @param message  A custom error message to be included in the JSON response. If null, a default message is used.
     * @param e        The {@link Exception} that occurred. Its message will be included in the response details.
     * @throws IOException If an I/O error occurs while writing to the response.
     */
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

    /**
     * Sends a JSON response indicating a redirect to a different URL.
     * Sets the content type to "application/json", character encoding to "UTF-8",
     * and the status code to {@link HttpServletResponse#SC_FOUND} (302 Found).
     * The response body will be a JSON object containing a "message" and a "redirectUrl".
     *
     * @param response    The {@link HttpServletResponse} object to write the response to.
     * @param message     A message to be included in the JSON response, providing context for the redirect.
     * @param redirectUrl The URL to which the client should be redirected.
     * @throws IOException If an I/O error occurs while writing to the response.
     */
    public static void sendRedirectResponse(HttpServletResponse response, String message, String redirectUrl) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 Found / Redirect

        RedirectResponse redirectResponse = new RedirectResponse(message, redirectUrl);
        objectMapper.writeValue(response.getWriter(), redirectResponse);

        // logger.log(Level.INFO, "Redirect response sent: {0} -> {1}", new Object[]{message, redirectUrl});
    }

    /**
     * Private static inner class representing the structure of a redirect response.
     */
    private static class RedirectResponse {
        @JsonProperty
        public String message;
        @JsonProperty
        public String redirectUrl;

        /**
         * Constructs a new {@code RedirectResponse}.
         *
         * @param message     The message associated with the redirect.
         * @param redirectUrl The URL to redirect to.
         */
        public RedirectResponse(String message, String redirectUrl) {
            this.message = message;
            this.redirectUrl = redirectUrl;
        }
    }

    /**
     * Private static inner class representing the structure of an error response.
     */
    private static class ErrorResponse {
        @JsonProperty
        public String error;
        @JsonProperty
        public String details;

        /**
         * Constructs a new {@code ErrorResponse}.
         *
         * @param error   The main error message.
         * @param details Additional details about the error (can be null).
         */
        public ErrorResponse(String error, String details) {
            this.error = error;
            this.details = details;
        }
    }
}