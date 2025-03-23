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

public class EmployeeRepository implements CrudRepository<Employee> {

    private static final Logger logger = Logger.getLogger(EmployeeRepository.class.getName());

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

    @Override
    public int delete(Integer id) throws IllegalArgumentException, SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement SQLStatement = c.prepareStatement(sql)) {

            SQLStatement.setInt(1, id);
            return SQLStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting employee with ID " + id, e);
        }
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection connection = DatabaseUtils.getMySQLConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(read(rs.getInt("id")));
                }
            }
        }
        return employees;
    }

    public List<Employee> findStartingToday() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE start_date = CURDATE()";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                ));
            }
        }

        return employees;
    }

    public List<Employee> findByName(String name) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM employees WHERE first_name LIKE ? OR last_name LIKE ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            stmt.setString(2, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                    ));
                }
            }
        }
        return employees;
    }

    public Employee findById(Integer id) throws SQLException {
        Employee employee;
        String sql = "SELECT id, first_name, last_name, email FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                 employee = new Employee(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"));
            }
        }
        return employee;
    }

    public List<String> findNameById(Integer id) throws SQLException {
        List<String> name = new ArrayList<>();
        String sql = "SELECT first_name, last_name FROM employees WHERE id = ?";

        try (Connection c = DatabaseUtils.getMySQLConnection();
            PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                name.add(rs.getString("first_name"));
                name.add(rs.getString("last_name"));
            }
        }
        return name;
    }

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

    public List<Integer> findAllSubordinates(Integer managerId) throws SQLException {
        List<Integer> subordinates = new ArrayList<>();  
        // recursive call to get all indirect subordinates
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