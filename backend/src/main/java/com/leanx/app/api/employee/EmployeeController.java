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

@WebServlet(name = "EmployeeController", urlPatterns = "/api/employee/*")
public class EmployeeController extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // POST /employee - Create a new employee
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* to be implemented!
        HttpSession session = request.getSession(false);
        if (!session.getAttribute("roles").contains("HR Ops")) {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Missing Role. Access Denied!");
            return;
        }
        */

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            handleCreateEmployee(request, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    private void handleCreateEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee newEmployee = objectMapper.readValue(request.getReader(), Employee.class);

            if (newEmployee.getFirstName() == null || newEmployee.getLastName() == null || newEmployee.getEmail() == null || newEmployee.getManagerId() == null) {
                ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required employee fields.");
                return;
            }

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

    // PUT /employee/{id} - Update an existing employee
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/")) {
            handleUpdateEmployee(request, response);
        } else {
            ApiUtils.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint!");
        }
    }

    private void handleUpdateEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String employeeIdStr = pathInfo.substring(1); // Remove leading slash
        
        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            Employee updatedEmployee = objectMapper.readValue(request.getReader(), Employee.class);

            // Update employee in the database using the employee service
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

    // GET /employee - Get all employees
    // GET /employee/search - Search employees by name
    // GET /employee/{id} - Get employee by id
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

    private void handleGetAllEmployees(HttpServletResponse response) throws IOException {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            ApiUtils.sendJsonResponse(response, employees);
        } catch (SQLException e) {
            ApiUtils.sendExceptionResponse(response, "Failed to fetch employees.", e);
        }
    }

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