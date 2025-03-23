package com.leanx.app.utils;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;

import com.leanx.app.service.modules.system.configs.SecurityConfig;

public class PasswordUtils {

    private final Map<String, String> PASSWORD_SETTINGS = SecurityConfig.PASSWORD_SETTINGS;

    private final int maxNumFailedAttempts;
    private final int historySize;
    private final int minLength;
    private final int maxLength;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireNumber;
    private final boolean requireSpecialCharacter;

    public PasswordUtils() {
        this.maxNumFailedAttempts = Integer.parseInt(PASSWORD_SETTINGS.get("password.num_failed_attempts_before_lockout"));
        this.historySize = Integer.parseInt(PASSWORD_SETTINGS.get("password.history_size"));
        this.minLength = Integer.parseInt(PASSWORD_SETTINGS.get("password.min_length"));
        this.maxLength = Integer.parseInt(PASSWORD_SETTINGS.get("password.max_length"));
        this.requireUppercase = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_uppercase"));
        this.requireLowercase = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_lowercase"));
        this.requireNumber = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_numbers"));
        this.requireSpecialCharacter = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_special_characters"));
    }

    public int getMaxNumFailedAttempts() { return maxNumFailedAttempts; }
    public int getHistorySize() { return historySize; }

    /**
     * Validates if the given password meets the required password policy.
     * The password must meet the specified criteria like minimum length, maximum length, required uppercase letters,
     * lowercase letters, numbers, and special characters.
     *
     * @param newPassword The new password to be validated.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     * @throws SQLException If a database error occurs while checking the password history.
     */
    public boolean isValidPassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        if (newPassword.length() < this.minLength || newPassword.length() > this.maxLength) {
            return false;
        }

        if (this.requireUppercase && !newPassword.matches(".*[A-Z].*")) {
            return false;
        }

        if (this.requireLowercase && !newPassword.matches(".*[a-z].*")) {
            return false;
        }

        if (this.requireNumber && !newPassword.matches(".*\\d.*")) {
            return false;
        }

        if (this.requireSpecialCharacter && !newPassword.matches(".*[!@#$%&*.].*")) {
            return false;
        }

        return true;
    }

    public String generatePassword() {
        String password;
        do {
            password = generateRandomString();
        } while (!isValidPassword(password));

        return password;
    }

    private String generateRandomString() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%&*.";

        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();

        String allChars = "";
        if (this.requireUppercase) {
            allChars += upper;
        }
        if (this.requireLowercase) {
            allChars += lower;
        }
        if (this.requireNumber) {
            allChars += digits;
        }
        if (this.requireSpecialCharacter) {
            allChars += specialChars;
        }

        if (allChars.isEmpty()) {
            return null;
        }

        for (int i = 0; i <= this.minLength; i++) {
            int randomIndex = random.nextInt(allChars.length());
            sb.append(allChars.charAt(randomIndex));
        }

        return sb.toString();
    }

    /**
     * Hashes the plain text password using BCrypt.
     *
     * @param plainTextPassword The plain text password.
     * @return The hashed password.
     */
    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if the provided plain text password matches the hashed password.
     *
     * @param plainTextPassword The plain text password.
     * @param hashedPassword    The password hash of the user object.
     * @return {@code true} if passwords match, {@code false} otherwise.
     */
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
