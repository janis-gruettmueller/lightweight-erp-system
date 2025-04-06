package com.leanx.app.api;

import java.io.IOException;

import com.leanx.app.utils.ApiUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for providing a health check endpoint for the application.
 * This endpoint can be used by monitoring systems to verify if the application
 * is running and responsive.
 */
@WebServlet(name = "HealthCheck", value = "/api/health")
public class HealthCheckServlet extends HttpServlet {

    /**
     * Handles GET requests to the health check endpoint.
     *
     * @param request  The {@code HttpServletRequest} object containing client's request.
     * @param response The {@code HttpServletResponse} object for sending response to the client.
     * @throws ServletException If any servlet specific error occurs.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // TODO: include checks for database connectivity, external service availability, etc.
            ApiUtils.sendJsonResponse(response, "UP");
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }
}