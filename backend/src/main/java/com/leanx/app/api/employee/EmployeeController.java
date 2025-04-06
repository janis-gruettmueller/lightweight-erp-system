package com.leanx.app.api.employee;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leanx.app.model.entity.Employee;
import com.leanx.app.service.modules.employee.EmployeeService;
import com.leanx.app.utils.ApiUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet controller for handling employee-related API requests.
 * This servlet manages operations such as creating, updating, retrieving,
 * and searching for employee records.
 */
@WebServlet(name = "EmployeeController", urlPatterns = "/api/employee/*")
public class EmployeeController extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles POST requests for the `/api/employee` endpoint to create a new employee.
     * Requires a valid user session and potentially specific roles.
     * Expects the employee data in JSON format in the request body.
     *
     * @param request  The {@code HttpServletRequest} object containing the client's request.
     * @param response The {@code HttpServletResponse} object for sending the response to the client.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* TODO: Implement Role checks
        HttpSession session = request.getSession(false);
        if (!session.getAttribute("roles").contains("HR Ops")) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Missing Role. Access Denied!");
            return;
        }
        */
        HttpSession session = request.getSession(false);
        Integer currUser = (Integer) session.getAttribute("userId");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleCreateEmployee(currUser, request, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    /**
     * Handles the creation of a new employee record. Reads the employee data from
     * the request body (JSON), sets the `createdBy` field with the current user ID,
     * and delegates the creation to the {@link EmployeeService}.
     *
     * @param currentUserId The ID of the user performing the create operation.
     * @param request       The {@code HttpServletRequest} object.
     * @param response      The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during request processing.
     */
    private void handleCreateEmployee(Integer currentUserId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee newEmployee = objectMapper.readValue(request.getReader(), Employee.class);

            if (newEmployee.getFirstName() == null || newEmployee.getLastName() == null || newEmployee.getEmail() == null || newEmployee.getManagerId() == null) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required employee fields.");
                return;
            }

            newEmployee.setCreatedBy(currentUserId);
            boolean created = employeeService.createEmployeeRecord(newEmployee);
            if (!created) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error creating the employee record!");
                return;
            }

            ApiUtils.sendJsonResponse(response, newEmployee);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, null, e);
        }
    }

    /**
     * Handles PUT requests for the `/api/employee/{id}` endpoint to update an
     * existing employee. Requires a valid user session. Expects the updated
     * employee data in JSON format in the request body and the employee ID in
     * the path.
     *
     * @param request  The {@code HttpServletRequest} object containing the client's request.
     * @param response The {@code HttpServletResponse} object for sending the response to the client.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        Integer currUser = (Integer) session.getAttribute("userId");

        if (pathInfo != null && pathInfo.startsWith("/")) {
            handleUpdateEmployee(currUser, request, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    /**
     * Handles the update of an existing employee record. Extracts the employee ID
     * from the path, reads the updated employee data from the request body (JSON),
     * sets the `lastUpdatedBy` field with the current user ID, and delegates the
     * update to the {@link EmployeeService}.
     *
     * @param currentUserId The ID of the user performing the update operation.
     * @param request       The {@code HttpServletRequest} object.
     * @param response      The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during request processing.
     */
    private void handleUpdateEmployee(Integer currentUserId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String employeeIdStr = pathInfo.substring(1); // Remove leading slash

        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            Employee updatedEmployee = objectMapper.readValue(request.getReader(), Employee.class);

            // Update employee in the database using the employee service
            updatedEmployee.setLastUpdatedBy(currentUserId);
            boolean updated = employeeService.updateEmployeeRecord(employeeId, updatedEmployee);
            if (!updated) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Employee not found.");
                return;
            }

            ApiUtils.sendJsonResponse(response, updatedEmployee);
        } catch (NumberFormatException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID.");
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, null, e);
        }
    }

    /**
     * Handles GET requests for various employee retrieval endpoints:
     * - `/api/employee`: Gets all employees.
     * - `/api/employee/search?name=...`: Searches employees by name.
     * - `/api/employee/{id}`: Gets a specific employee by ID.
     *
     * @param request  The {@code HttpServletRequest} object containing the client's request.
     * @param response The {@code HttpServletResponse} object for sending the response to the client.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs while handling the request.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAllEmployees(response);
        } else if (pathInfo.startsWith("/search")) {
            handleSearchEmployees(request, response);
        } else if (pathInfo.matches("^/\\d+$")) {
            handleGetEmployeeById(pathInfo, response);
        }  else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    /**
     * Handles the retrieval of all employee records. Delegates the call to the
     * {@link EmployeeService} and sends the list of employees as a JSON response.
     *
     * @param response The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during response processing.
     */
    private void handleGetAllEmployees(HttpServletResponse response) throws IOException {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            ApiUtils.sendJsonResponse(response, employees);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, "Failed to fetch employees.", e);
        }
    }

    /**
     * Handles the search for employees by name. Extracts the `name` query parameter
     * from the request, delegates the search to the {@link EmployeeService}, and
     * sends the list of matching employees as a JSON response.
     *
     * @param request  The {@code HttpServletRequest} object.
     * @param response The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during request or response processing.
     */
    private void handleSearchEmployees(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String nameQuery = request.getParameter("name");

            if (nameQuery == null || nameQuery.trim().isEmpty()) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing search query.");
                return;
            }


            List<Employee> employees = employeeService.searchEmployeesByName(nameQuery);
            ApiUtils.sendJsonResponse(response, employees);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, "Database error while searching employees.", e);
        }
    }

    /**
     * Handles the retrieval of a specific employee record by their ID. Extracts the
     * employee ID from the path, delegates the retrieval to the
     * {@link EmployeeService}, and sends the employee data as a JSON response.
     * Returns a 404 error if the employee is not found.
     *
     * @param pathInfo The path information containing the employee ID.
     * @param response The {@code HttpServletResponse} object.
     * @throws IOException If an I/O error occurs during response processing.
     */
    private void handleGetEmployeeById(String pathInfo, HttpServletResponse response) throws IOException {
        try {
            int employeeId = Integer.parseInt(pathInfo.substring(1)); // Extract ID from URL
            Employee employee = employeeService.getEmployeeById(employeeId);

            if (employee == null) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Employee not found.");
                return;
            }

            ApiUtils.sendJsonResponse(response, employee);
        } catch (NumberFormatException e) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID format.");
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, "Database error while fetching employee.", e);
        }
    }
}