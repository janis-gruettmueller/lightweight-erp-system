package com.leanx.app.service.modules.employee;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.entity.Employee;
import com.leanx.app.repository.EmployeeRepository;

/**
 * Service class for managing employee-related operations.
 * Provides functionalities for searching, retrieving, updating, and creating
 * employee records, as well as checking team hierarchies.
 */
public class EmployeeService {

    private static final Logger logger = Logger.getLogger(EmployeeService.class.getName());

    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    /**
     * Executes an update operation on an employee record in the database.
     *
     * @param employeeId The ID of the employee to update.
     * @param updates    A map containing the fields to update and their new values.
     * The keys of the map should correspond to the database column names.
     * @return {@code true} if the update was successful (at least one row was affected),
     * {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the update.
     */
    private boolean executeUpdate(Integer employeeId, Map<String, Object> updates) throws SQLException {
        try {
            int affectedRows = employeeRepository.update(employeeId, updates);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update records for employee with ID: " + employeeId, e);
            throw e;
        }
    }

    /**
     * Searches for employees whose first or last name contains the given name.
     * The search is case-insensitive and can match partial names.
     *
     * @param name The name (or part of a name) to search for.
     * @return A list of {@link Employee} objects matching the search criteria.
     * Returns an empty list if no employees are found.
     * @throws SQLException If a database access error occurs during the search.
     */
    public List<Employee> searchEmployeesByName(String name) throws SQLException {
        try {
            return employeeRepository.findByName(name);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to access the database: {0}", e);
            throw e;
        }

    }

    /**
     * Retrieves all employee records from the database.
     *
     * @return A list containing all {@link Employee} objects in the system.
     * Returns an empty list if no employees exist.
     * @throws SQLException If a database access error occurs while fetching the employees.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        try {
            return employeeRepository.findAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to access the database: {0}", e);
            throw e;
        }
    }

    /**
     * Retrieves a specific employee record based on their unique ID.
     *
     * @param employeeId The ID of the employee to retrieve.
     * @return The {@link Employee} object with the given ID.
     * @throws IllegalArgumentException If the provided {@code employeeId} is null or not positive.
     * @throws SQLException             If a database access error occurs while fetching the employee.
     */
    public Employee getEmployeeById(Integer employeeId) throws IllegalArgumentException, SQLException {
        try {
            if (employeeId == null || employeeId <= 0) {
                throw new IllegalArgumentException("Illegal argument: employeeId cannot be null or non-positive!");
            }

            return employeeRepository.read(employeeId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to fetch employee with ID: " + employeeId, e);
            throw e;
        }
    }

    /**
     * Updates an existing employee record with the information provided in the
     * {@link Employee} object. Only the non-null fields of the provided employee
     * object will be used to update the corresponding record in the database.
     *
     * @param id       The ID of the employee record to update.
     * @param employee An {@link Employee} object containing the updated information.
     * Null values in the employee object will result in no change
     * to the corresponding field in the database.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws IllegalArgumentException If the provided {@code employee} object is null.
     * @throws SQLException             If a database access error occurs during the update.
     */
    public boolean updateEmployeeRecord(Integer id, Employee employee) throws IllegalArgumentException, SQLException {
        if (employee == null) {
            throw new IllegalArgumentException("Illegal argument: employee object cannot be null!");
        }

        Map<String, Object> updates = new HashMap<>();

        if (employee.getFirstName() != null) {
            updates.put("first_name", employee.getFirstName());
        }
        if (employee.getLastName() != null) {
            updates.put("last_name", employee.getLastName());
        }
        if (employee.getEmail() != null) {
            updates.put("email", employee.getEmail());
        }
        if (employee.getManagerId() != null) {
            updates.put("manager_id", employee.getManagerId());
        }
        if (employee.getJobTitle() != null) {
            updates.put("job_title", employee.getJobTitle());
        }
        if (employee.getDepartment() != null) {
            updates.put("department", employee.getDepartment());
        }
        if (employee.getEmploymentType() != null) {
            updates.put("employment_type", String.valueOf(employee.getEmploymentType()));
        }
        if (employee.getEmploymentStatus() != null) {
            updates.put("employment_status", String.valueOf(employee.getEmploymentStatus()));
        }
        if (employee.getHireDate() != null) {
            updates.put("hire_date", employee.getHireDate());
        }
        if (employee.getStartDate() != null) {
            updates.put("start_date", employee.getStartDate());
        }
        if (employee.getTerminationDate() != null) {
            updates.put("termination_date", employee.getTerminationDate());
        }
        if (employee.getTerminationReason() != null) {
            updates.put("termination_reason", employee.getTerminationReason());
        }

        return executeUpdate(id, updates);
    }

    /**
     * Creates a new employee record in the database using the information
     * provided in the {@link Employee} object.
     *
     * @param employee The {@link Employee} object containing the details of the
     * new employee to be created.
     * @return {@code true} if the creation was successful, {@code false} otherwise.
     * @throws SQLException If a database access error occurs during the creation.
     */
    public boolean createEmployeeRecord(Employee employee) throws SQLException {
        try {
            return employeeRepository.create(employee) > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create records for employee with ID: " + employee.getId(), e);
            throw e;
        }
    }

    /**
     * Checks if a given employee is a direct or indirect subordinate of a specified manager.
     * This method queries the database to determine the reporting hierarchy.
     *
     * @param employeeId The ID of the employee to check.
     * @param managerId  The ID of the manager to check against.
     * @return {@code true} if the employee is a subordinate of the manager, either directly
     * or indirectly through the reporting structure, {@code false} otherwise.
     * @throws IllegalArgumentException If either {@code employeeId} or {@code managerId} is null.
     * @throws SQLException             If a database access error occurs while querying the hierarchy.
     */
    public boolean isEmployeeInTeam(Integer employeeId, Integer managerId) throws IllegalArgumentException, SQLException {
        if (employeeId == null || managerId == null) {
            throw new IllegalArgumentException("Illegal argument: employeeId or managerId cannot be null!");
        }

        try {
            List<Integer> directSubordinates = employeeRepository.findDirectSubordinates(managerId);
            if (directSubordinates.contains(employeeId)) {
                return true;
            }

            return employeeRepository.findAllSubordinates(managerId).contains(employeeId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to verify if employee with ID: " + employeeId + " is subordinate of employee with ID: " + managerId, e);
            throw e;
        }
    }
}