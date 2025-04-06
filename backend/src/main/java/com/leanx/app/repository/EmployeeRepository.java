package com.leanx.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.entity.Employee;
import com.leanx.app.model.entity.Employee.EmploymentStatus;
import com.leanx.app.model.entity.Employee.EmploymentType;
import com.leanx.app.repository.base.CrudRepository;
import com.leanx.app.utils.DatabaseUtils;

/**
 * Repository class for performing CRUD (Create, Read, Update, Delete) operations
 * and other queries on {@link Employee} entities in the database.
 */
public class EmployeeRepository implements CrudRepository<Employee> {

    private static final Logger logger = Logger.getLogger(EmployeeRepository.class.getName());

    /**
     * Creates a new employee record in the database.
     *
     * @param employee The {@link Employee} object containing the data for the new record.
     * @return The number of rows affected by the insert operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code employee} object is null.
     * @throws SQLException             If a database access error occurs during the creation.
     */
    @Override
    public int create(Employee employee) throws IllegalArgumentException, SQLException {
        if (employee == null) {
            throw new IllegalArgumentException("Invalid employee object.");
        }

        String sql = "INSERT INTO employees (first_name, last_name, email, job_title, department, " +
                "employment_type, employment_status, hire_date, start_date, created_by, manager_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement SQLStatement = c.prepareStatement(sql)) {
            SQLStatement.setString(1, employee.getFirstName());
            SQLStatement.setString(2, employee.getLastName());
            SQLStatement.setString(3, employee.getEmail());
            SQLStatement.setString(4, employee.getJobTitle());
            SQLStatement.setString(5, employee.getDepartment());
            SQLStatement.setString(6, String.valueOf(employee.getEmploymentType()));
            SQLStatement.setString(7, String.valueOf(employee.getEmploymentStatus()));
            SQLStatement.setDate(8, employee.getHireDate());
            SQLStatement.setDate(9, employee.getStartDate());
            SQLStatement.setInt(10, employee.getCreatedBy());
            SQLStatement.setInt(11, employee.getManagerId());

            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error creating new employee", e);
        }
    }

    /**
     * Retrieves an employee record from the database based on its unique ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return An {@link Employee} object representing the retrieved record, or {@code null}
     * if no employee with the given ID exists.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    @Override
    public Employee read(Integer id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);
            try (ResultSet rs = SQLStatement.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setEmail(rs.getString("email"));
                    employee.setManagerId(rs.getInt("manager_id"));
                    employee.setJobTitle(rs.getString("job_title"));
                    employee.setDepartment(rs.getString("department"));

                    try {
                        employee.setEmploymentType(EmploymentType.valueOf(rs.getString("employment_type")));
                        employee.setEmploymentStatus(EmploymentStatus.valueOf(rs.getString("employment_status")));
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "IllegalArgumentException: " + e.getMessage(), e);
                    }

                    employee.setHireDate(rs.getDate("hire_date"));
                    employee.setStartDate(rs.getDate("start_date"));
                    employee.setTerminationDate(rs.getDate("termination_date"));
                    employee.setTerminationReason(rs.getString("termination_reason"));
                    employee.setRetentionEndDate(rs.getDate("retention_end_date"));
                    employee.setCreatedAt(rs.getTimestamp("created_at"));
                    employee.setCreatedBy(rs.getInt("created_by"));
                    employee.setLastUpdatedBy(rs.getInt("last_updated_by"));
                    employee.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));

                    return employee;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving employee with ID " + id, e);
        }
    }

    /**
     * Updates specific fields of an existing employee record in the database.
     *
     * @param id      The ID of the employee record to update.
     * @param updates A {@code Map} where the keys are the column names to update
     * and the values are the new values for those columns.
     * @return The number of rows affected by the update operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code updates} map is null or empty.
     * @throws SQLException             If a database access error occurs during the update.
     */
    @Override
    public int update(Integer id, Map<String, Object> updates) throws IllegalArgumentException, SQLException {
        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        StringBuilder sql = new StringBuilder("UPDATE employees SET ");
        for (String field : updates.keySet()) {
            sql.append(field).append(" = ?, ");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement SQLStatement = c.prepareStatement(sql.toString())) {
            int index = 1;

            for (Object value : updates.values()) {
                SQLStatement.setObject(index++, value);
            }

            SQLStatement.setInt(index, id);
            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating employee with ID " + id, e);
        }
    }

    /**
     * Deletes an employee record from the database based on its unique ID.
     *
     * @param id The ID of the employee record to delete.
     * @return The number of rows affected by the delete operation (should be 1 on success).
     * @throws IllegalArgumentException If the provided {@code id} is null or not positive.
     * @throws SQLException             If a database access error occurs during the deletion.
     */
    @Override
    public int delete(Integer id) throws IllegalArgumentException, SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);
            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting employee with ID " + id, e);
        }
    }

    /**
     * Retrieves all employee records from the database.
     *
     * @return A {@code List} containing all {@link Employee} objects in the database.
     * Returns an empty list if no employees are found.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    @Override
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id FROM employees"; // Only fetch IDs for efficiency

        try (Connection connection = DatabaseUtils.getMySQLConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Employee employee = read(rs.getInt("id"));
                if (employee != null) {
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    /**
     * Retrieves a list of employees whose start date is the current date.
     *
     * @return A {@code List} of {@link Employee} objects who are scheduled to start today.
     * Returns an empty list if no employees are starting today.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<Employee> findStartingToday() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM employees WHERE start_date = CURDATE()";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setFirstName(rs.getString("first_name"));
                employee.setLastName(rs.getString("last_name"));
                employee.setEmail(rs.getString("email"));
                employees.add(employee);
            }
        }

        return employees;
    }

    /**
     * Searches for employees whose first or last name contains the given name.
     * The search is case-insensitive and uses a "LIKE %value%" pattern.
     *
     * @param name The name (or part of a name) to search for.
     * @return A {@code List} of {@link Employee} objects whose first or last name
     * contains the search term. Returns an empty list if no matches are found.
     * @throws SQLException If a database access error occurs during the search.
     */
    public List<Employee> findByName(String name) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM employees WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?)";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            stmt.setString(2, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setEmail(rs.getString("email"));
                    employees.add(employee);
                }
            }
        }
        return employees;
    }

    /**
     * Retrieves a basic employee information (ID, first name, last name, email)
     * based on the employee's ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return An {@link Employee} object containing basic information, or {@code null}
     * if no employee with the given ID is found.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public Employee findById(Integer id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setFirstName(rs.getString("first_name"));
                    employee.setLastName(rs.getString("last_name"));
                    employee.setEmail(rs.getString("email"));
                    return employee;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the first and last name of an employee based on their ID.
     *
     * @param id The ID of the employee.
     * @return A {@code List} containing two elements: the first name at index 0
     * and the last name at index 1. Returns an empty list if no employee
     * with the given ID is found.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<String> findNameById(Integer id) throws SQLException {
        List<String> name = new ArrayList<>();
        String sql = "SELECT first_name, last_name FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    name.add(rs.getString("first_name"));
                    name.add(rs.getString("last_name"));
                }
            }
        }
        return name;
    }

    /**
     * Retrieves a list of IDs of employees who directly report to the given manager.
     *
     * @param managerId The ID of the manager.
     * @return A {@code List} of employee IDs who are direct subordinates of the manager.
     * Returns an empty list if the manager has no direct reports or if the
     * manager ID is invalid.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<Integer> findDirectSubordinates(Integer managerId) throws SQLException {
        List<Integer> subordinates = new ArrayList<>();
        String sql = "SELECT id FROM employees WHERE manager_id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subordinates.add(rs.getInt("id"));
                }
            }
        }
        return subordinates;
    }

    /**
     * Retrieves a list of IDs of all employees who are direct or indirect
     * subordinates of the given manager using a recursive common table expression (CTE).
     *
     * @param managerId The ID of the top-level manager.
     * @return A {@code List} of employee IDs who are subordinates (direct or indirect)
     * of the manager. Returns an empty list if the manager has no subordinates
     * or if the manager ID is invalid.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public List<Integer> findAllSubordinates(Integer managerId) throws SQLException {
        List<Integer> subordinates = new ArrayList<>();
        // Recursive CTE to get all indirect subordinates
        String sql = "WITH RECURSIVE Subordinates AS (" +
                    "    SELECT id FROM employees WHERE manager_id = ?" +
                    "    UNION ALL" +
                    "    SELECT e.id FROM employees e INNER JOIN Subordinates s ON s.id = e.manager_id" +
                    ")" +
                    "SELECT id FROM Subordinates";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subordinates.add(rs.getInt("id"));
                }
            }
        }
        return subordinates;
    }
}