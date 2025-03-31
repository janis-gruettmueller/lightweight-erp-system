package com.leanx.app.service.jobs;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.leanx.app.model.entity.Employee;
import com.leanx.app.repository.EmployeeRepository;
import com.leanx.app.repository.base.StoredProceduresRepository;
import com.leanx.app.service.modules.system.EmailService;
import com.leanx.app.service.modules.user.admin.UserService;
import com.leanx.app.utils.PasswordUtils;

public class OnboardingJob implements Job {

    private static final Logger logger = Logger.getLogger(OnboardingJob.class.getName());

    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final StoredProceduresRepository storedProceduresRepository = new StoredProceduresRepository();
    private final PasswordUtils passwordUtils = new PasswordUtils();
    private final UserService userService = new UserService();
    private final EmailService emailService = new EmailService();

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        logger.log(Level.INFO, "Executing Onboarding Job...");
        try {
            List<Employee> newEmployees = employeeRepository.findStartingToday();
            if (newEmployees.isEmpty()) {
                logger.log(Level.INFO, "Successfully Executed Onboarding Job. No new users were created!");
                return;
            }
            
            boolean isEmailSent = true;
            for (Employee employee : newEmployees) {
                String username = userService.generateUsername(employee.getFirstName(), employee.getLastName());
                String password = passwordUtils.generatePassword();
                String passwordHash = passwordUtils.hashPassword(password);

                storedProceduresRepository.callCreateNewUserAccount(username, passwordHash, employee.getId());

                int sendResult = emailService.attemptSendCredentialsEmail(employee.getEmail(), username, password);
                if (sendResult != 0) {
                    logger.log(Level.WARNING, "Failed to send credentials email to {0} after multiple retries.", employee.getEmail());
                    isEmailSent = false;
                }
            }

            if (!isEmailSent) {
                logger.log(Level.WARNING, "Executed Onboarding Job with errors. {0} new users were created!", newEmployees.size());
                return;
            }
            logger.log(Level.INFO, "Successfully Executed Onboarding Job. {0} new users were created!", newEmployees.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to execute the Onboarding Job. Error accessing the database: {0}", e);
        }
    }
}
