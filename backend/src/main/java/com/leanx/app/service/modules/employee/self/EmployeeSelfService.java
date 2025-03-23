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

public class EmployeeSelfService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserEmployeeLinkRepository userEmployeeLinkRepository = new UserEmployeeLinkRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    public int getEmployeeId(Integer userId) throws SQLException {
        try {
            return userEmployeeLinkRepository.findEmployeeIdByUserId(userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get employeeId for user with ID: " + userId, e);
            throw e;
        }
    }

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

    public void submitChangeRequest() {
        // TODO
    }
}
