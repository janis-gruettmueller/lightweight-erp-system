package com.leanx.app.service.modules.employee;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.entity.Employee;
import com.leanx.app.repository.EmployeeRepository;

public class EmployeeService {

    private static final Logger logger = Logger.getLogger(EmployeeService.class.getName());
    
    private final EmployeeRepository employeeRepository = new EmployeeRepository();


    private boolean executeUpdate(Integer employeeId, Map<String, Object> updates) throws SQLException {
        try {
            int affectedRows = employeeRepository.update(employeeId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update records for employee with ID: " + employeeId, e);
            throw e;
        }
    }

    public List<Employee> searchEmployeesByName(String name) throws SQLException {
        try {
            return employeeRepository.findByName(name);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to access the database: {0}", e);
            throw e;
        }

    }

    public List<Employee> getAllEmployees() throws SQLException {
        try {
            return employeeRepository.findAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to access the database: {0}", e);
            throw e;
        }
    }

    public Employee getEmployeeById(Integer employeeId) throws IllegalArgumentException, SQLException {
        try {
            if (employeeId == null || employeeId <= 0) {
                throw new IllegalArgumentException("Illegal argument: employeeId cannot be null!");
            }

            return employeeRepository.read(employeeId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to fetch employee with ID: " + employeeId, e);
            throw e;
        }
    }

    public boolean updateEmployeeRecord(Integer id, Employee employee) throws IllegalArgumentException, SQLException {
        if (employee == null) {
            throw new IllegalArgumentException("Illegal argument: employee object cannot be null!");
        }

        Map<String, Object> updates = new HashMap<>();

        updates.put("first_name", employee.getFirstName());
        updates.put("last_name", employee.getLastName());
        updates.put("email", employee.getEmail());
        updates.put("manager_id", employee.getManagerId());
        updates.put("job_title", employee.getJobTitle());
        updates.put("department", employee.getDepartment());
        updates.put("employment_type", String.valueOf(employee.getEmploymentType()));
        updates.put("employment_status", String.valueOf(employee.getEmploymentStatus()));
        updates.put("hire_date", employee.getHireDate());
        updates.put("start_date", employee.getStartDate());
        updates.put("termination_date", employee.getTerminationDate());
        updates.put("termination_reason", employee.getTerminationReason()); 
        
        return executeUpdate(id, updates);
    }

    public boolean createEmployeeRecord(Employee employee) throws SQLException {
        try {
            return employeeRepository.create(employee) > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create records for employee with ID: " + employee.getId(), e);
            throw e;
        }
    }

    public boolean isEmployeeInTeam(Integer employeeId, Integer managerId) throws IllegalArgumentException, SQLException {
        if (employeeId == null || managerId == null) {
            throw new IllegalArgumentException("Illegal argument: employeeId or managerId cannot be null!");
        }

        try {
            if (employeeRepository.findDirectSubordinates(managerId).contains(employeeId)) {
                return true;
            }
    
            return employeeRepository.findAllSubordinates(managerId).contains(employeeId); 
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to verify if employee with ID: " + employeeId + " is subordinate of employee with ID: " + managerId, e);
            throw e;
        }
    }
}
