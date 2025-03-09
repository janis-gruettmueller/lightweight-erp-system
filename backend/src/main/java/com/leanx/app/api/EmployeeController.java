package com.leanx.app.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import com.leanx.app.service.EmployeeService;
import com.leanx.app.service.UserService;


@WebServlet("/api/employee/*")
public class EmployeeController extends HttpServlet {
    private EmployeeService employeeService = new EmployeeService();
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action required");
                return;
            }

            switch (action) {
                case "viewPersonalInfo":
                    handleViewPersonalInfo(request, response);
                    break;
                case "viewPayroll":
                    handleViewPayroll(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                    break;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action required");
                return;
            }

            switch (action) {
                case "requestLeave":
                    handleRequestLeave(request, response);
                    break;
                case "requestChange":
                    handleRequestChange(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                    break;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleViewPersonalInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // needs current user as input -> handeled via JWT probably
        if (!userService.hasRole("Employee")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new JSONObject().put("error", "Access denied").toString());
            return;
        }

        int employeeId = Integer.parseInt(request.getParameter("employeeId"));
        Employee employee = employeeService.getEmployeeById(employeeId);

        if (employee != null) {
            JSONObject json = new JSONObject()
                    .put("id", employee.getId())
                    .put("name", employee.getName())
                    .put("email", employee.getEmail())
                    .put("phoneNumber", employee.getPhoneNumber());

            response.getWriter().write(json.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(new JSONObject().put("error", "Employee not found").toString());
        }
    }

    private void handleViewPayroll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.hasRole("Employee")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new JSONObject().put("error", "Access denied").toString());
            return;
        }

        int employeeId = Integer.parseInt(request.getParameter("employeeId"));
        List<Payroll> payrolls = employeeService.getPayrollsByEmployeeId(employeeId);

        if (payrolls != null && !payrolls.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (Payroll payroll : payrolls) {
                JSONObject json = new JSONObject()
                        .put("id", payroll.getId())
                        .put("employeeId", payroll.getEmployeeId())
                        .put("salary", payroll.getSalary())
                        .put("date", payroll.getDate().toString());
                jsonArray.put(json);
            }
            response.getWriter().write(jsonArray.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(new JSONObject().put("error", "No payroll data found").toString());
        }
    }

    private void handleRequestLeave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.hasRole("Employee")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new JSONObject().put("error", "Access denied").toString());
            return;
        }

        String leaveType = request.getParameter("leaveType");
        String leaveStartDate = request.getParameter("startDate");
        String leaveEndDate = request.getParameter("endDate");

        boolean success = employeeService.requestLeave(leaveType, leaveStartDate, leaveEndDate);

        if (success) {
            response.getWriter().write(new JSONObject().put("message", "Leave request submitted successfully").toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Failed to submit leave request").toString());
        }
    }

    private void handleRequestChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.hasRole("Employee")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(new JSONObject().put("error", "Access denied").toString());
            return;
        }

        String newEmail = request.getParameter("newEmail");
        String newPhoneNumber = request.getParameter("newPhoneNumber");

        boolean success = employeeService.updateEmployeeInfo(newEmail, newPhoneNumber);

        if (success) {
            response.getWriter().write(new JSONObject().put("message", "Personal information updated successfully").toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Failed to update personal information").toString());
        }
    }
}