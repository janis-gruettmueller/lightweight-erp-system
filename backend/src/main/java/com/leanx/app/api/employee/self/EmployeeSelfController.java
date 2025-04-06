package com.leanx.app.api.employee.self;

import java.io.IOException;
import java.sql.SQLException;

import com.leanx.app.model.dto.EmployeeProfile;
import com.leanx.app.service.modules.employee.self.EmployeeSelfService;
import com.leanx.app.utils.ApiUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet controller for handling API requests related to an employee themself
 */
@WebServlet(name = "EmployeeSelfController", urlPatterns = "/api/employee/self/*")
public class EmployeeSelfController extends HttpServlet {

    private final EmployeeSelfService employeeSelfService = new EmployeeSelfService();

    /**
     * Handles GET requests to the `/api/employee/self` endpoint to retrieve
     * the personal employee profile for the currently logged-in user.
     * Requires a valid user session to identify the current user.
     *
     * @param request  The {@code HttpServletRequest} object containing the client's request.
     * @param response The {@code HttpServletResponse} object for sending the response to the client.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        Integer currentUserId = (Integer) session.getAttribute("userId");

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetEmployeeProfile(currentUserId, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    /**
     * Handles the retrieval of the personal employee profile for the user
     * associated with the provided user ID. It delegates the retrieval to the
     * {@link EmployeeSelfService} and sends the {@link EmployeeProfile} as a
     * JSON response. Returns an error response if the profile cannot be fetched.
     *
     * @param userId   The ID of the currently logged-in user whose profile is being requested.
     * @param response The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during response processing.
     */
    private void handleGetEmployeeProfile(Integer userId, HttpServletResponse response) throws IOException {
        try {
            EmployeeProfile employeeProfile = employeeSelfService.getPersonalEmployeeProfile(userId);
            if (employeeProfile == null) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error fetching personal employee profile!");
                return;
            }

            ApiUtils.sendJsonResponse(response, employeeProfile);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, null, e);
        }
    }
}