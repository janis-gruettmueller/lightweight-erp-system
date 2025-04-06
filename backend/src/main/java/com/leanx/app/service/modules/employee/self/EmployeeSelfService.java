package com.leanx.app.service.modules.employee.self;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leanx.app.model.dto.EmployeeProfile;
import com.leanx.app.model.entity.Employee;
import com.leanx.app.repository.EmployeeRepository;
import com.leanx.app.repository.UserEmployeeLinkRepository;
import com.leanx.app.service.modules.user.admin.UserService;

/**
 * Service class for handling self-service operations related to employees.
 * Provides functionalities for retrieving employee-specific information
 * based on the logged-in user.
 */
public class EmployeeSelfService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserEmployeeLinkRepository userEmployeeLinkRepository = new UserEmployeeLinkRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    /**
     * Retrieves the employee ID associated with a given user ID.
     * This is typically used to link a user account to their corresponding
     * employee record.
     *
     * @param userId The ID of the user.
     * @return The ID of the employee linked to the user.
     * @throws SQLException If a database access error occurs during the retrieval.
     */
    public int getEmployeeId(Integer userId) throws SQLException {
        try {
            return userEmployeeLinkRepository.findEmployeeIdByUserId(userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get employeeId for user with ID: " + userId, e);
            throw e;
        }
    }

    /**
     * Retrieves the personal employee profile information for the user with the given ID.
     * This method fetches the employee details and their manager's name to construct
     * an {@link EmployeeProfile} DTO.
     *
     * @param userId The ID of the user whose employee profile is to be retrieved.
     * @return An {@link EmployeeProfile} object containing the employee's personal information.
     * @throws SQLException If a database access error occurs while fetching the employee
     * details or the manager's name.
     */
    public EmployeeProfile getPersonalEmployeeProfile(Integer userId) throws SQLException {
        try {
            Employee employee = employeeRepository.read(getEmployeeId(userId));
            List<String> managerName = employeeRepository.findNameById(employee.getManagerId());

            EmployeeProfile employeeProfile = new EmployeeProfile(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                managerName.get(0),
                managerName.get(1),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getEmploymentType(),
                employee.getEmploymentStatus(),
                employee.getStartDate()
            );

            return employeeProfile;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get employee profile for user with ID: " + userId, e);
            throw e;
        }
    }

    /**
     * Allows an employee to submit a change request for their personal information.
     * This method is currently a placeholder and its implementation will be added
     * in future development.
     */
    public void submitChangeRequest() {
        // TODO: Implement the logic for submitting employee change requests.
    }
}