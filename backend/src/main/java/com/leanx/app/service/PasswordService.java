package main.java.com.leanx.app.service;

import main.java.com.leanx.app.utils.PasswordUtils;

public class PasswordService {
    private PasswordUtils utils = new PasswordUtils();

    /**
     * Validates if the password meets the required format.
     * @param password The password to be validated.
     * @return True if valid, false otherwise.
     */
    boolean isValidPassword(String password);
    
}
