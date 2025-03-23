package com.leanx.app.service.modules.system;

import java.util.logging.Level;
import java.util.logging.Logger;


public class EmailService {
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    public void sendCredentialsEmail(String to, String username, String password) {
        try {
            // Simulate sending email (Replace with actual SMTP logic)
            logger.log(Level.INFO, "Sending email to: {0}", to);
            logger.info("Subject: Your New ERP Account");
            logger.log(Level.INFO, "Body: \nWelcome to the company! \nYour username: {0}\nYour password: {1}", new Object[]{username, password});

            // TODO: Replace with actual email sending logic (SMTP or JavaMail API)

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email to " + to, e);
        }
    }
}
