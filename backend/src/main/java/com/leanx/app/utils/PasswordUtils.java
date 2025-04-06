package com.leanx.app.utils;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;

import com.leanx.app.service.modules.system.configs.SecurityConfig;

/**
 * Utility class for handling password-related operations, including validation,
 * generation, hashing, and checking against a password policy defined in
 * the {@link SecurityConfig}.
 */
public class PasswordUtils {

    private final Map<String, String> PASSWORD_SETTINGS = SecurityConfig.PASSWORD_SETTINGS;

    private final int maxNumFailedAttempts;
    private final int historySize;
    private final int minLength;
    private final int maxLength;
    private final int lockoutDuration;
    private final boolean requireUppercase;
    private final boolean requireLowercase;
    private final boolean requireNumber;
    private final boolean requireSpecialCharacter;

    /**
     * Constructs a new {@code PasswordUtils} instance, initializing password
     * policy settings from the {@link SecurityConfig#PASSWORD_SETTINGS} map.
     */
    public PasswordUtils() {
        this.maxNumFailedAttempts = Integer.parseInt(PASSWORD_SETTINGS.get("password.num_failed_attempts_before_lockout"));
        this.historySize = Integer.parseInt(PASSWORD_SETTINGS.get("password.history_size"));
        this.minLength = Integer.parseInt(PASSWORD_SETTINGS.get("password.min_length"));
        this.maxLength = Integer.parseInt(PASSWORD_SETTINGS.get("password.max_length"));
        this.requireUppercase = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_uppercase"));
        this.requireLowercase = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_lowercase"));
        this.requireNumber = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_numbers"));
        this.requireSpecialCharacter = Boolean.parseBoolean(PASSWORD_SETTINGS.get("password.require_special_characters"));
        this.lockoutDuration = Integer.parseInt(PASSWORD_SETTINGS.get("password.lockout_duration"));
    }

    /**
     * Returns the maximum number of failed login attempts allowed before lockout.
     * This value is configured by the 'password.num_failed_attempts_before_lockout' setting.
     *
     * @return The maximum number of failed attempts.
     */
    public int getMaxNumFailedAttempts() { return maxNumFailedAttempts; }

    /**
     * Returns the size of the password history to maintain for preventing reuse.
     * This value is configured by the 'password.history_size' setting.
     *
     * @return The password history size.
     */
    public int getHistorySize() { return historySize; }

    /**
     * Returns the minimum required length for a password.
     * This value is configured by the 'password.min_length' setting.
     *
     * @return The minimum password length.
     */
    public int getMinLength() { return minLength; }

    /**
     * Returns the maximum allowed length for a password.
     * This value is configured by the 'password.max_length' setting.
     *
     * @return The maximum password length.
     */
    public int getMaxLength() { return maxLength; }

    /**
     * Returns the duration in minutes for which an account is locked out
     * after exceeding the maximum number of failed login attempts.
     * This value is configured by the 'password.lockout_duration' setting.
     *
     * @return The lockout duration.
     */
    public int getLockoutDuration() { return lockoutDuration; }

    /**
     * Indicates whether the password policy requires at least one uppercase letter.
     * This value is configured by the 'password.require_uppercase' setting.
     *
     * @return {@code true} if uppercase letters are required, {@code false} otherwise.
     */
    public boolean isRequireUppercase() { return requireUppercase; }

    /**
     * Indicates whether the password policy requires at least one lowercase letter.
     * This value is configured by the 'password.require_lowercase' setting.
     *
     * @return {@code true} if lowercase letters are required, {@code false} otherwise.
     */
    public boolean isRequireLowercase() { return requireLowercase; }

    /**
     * Indicates whether the password policy requires at least one digit (number).
     * This value is configured by the 'password.require_numbers' setting.
     *
     * @return {@code true} if numbers are required, {@code false} otherwise.
     */
    public boolean isRequireNumber() { return requireNumber; }

    /**
     * Indicates whether the password policy requires at least one special character
     * (e.g., !@#$%&*.).
     * This value is configured by the 'password.require_special_characters' setting.
     *
     * @return {@code true} if special characters are required, {@code false} otherwise.
     */
    public boolean isRequireSpecialCharacter() { return requireSpecialCharacter; }


    /**
     * Validates if the given password meets the configured password policy.
     * The password must satisfy constraints such as minimum and maximum length,
     * and the presence of required character types (uppercase, lowercase, numbers,
     * and special characters) as defined in the security configuration.
     *
     * @param newPassword The password to be validated.
     * @return {@code true} if the password meets all the policy requirements,
     * {@code false} otherwise.
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

        return !(this.requireSpecialCharacter && !newPassword.matches(".*[!@#$%&*.].*"));
    }

    /**
     * Generates a random password that complies with the configured password policy.
     * It repeatedly generates random strings until one satisfies the criteria defined
     * by {@link #isValidPassword(String)}.
     *
     * @return A randomly generated password that meets the password policy requirements.
     * Returns {@code null} if the configured policy results in an empty set of allowed characters.
     */
    public String generatePassword() {
        String password;
        do {
            password = generateRandomString();
        } while (!isValidPassword(password));

        return password;
    }

    /**
     * Generates a random string based on the enabled password requirements
     * (uppercase, lowercase, numbers, special characters) and the minimum required length.
     *
     * @return A randomly generated string containing characters based on the password policy.
     * Returns {@code null} if no character types are required by the policy.
     */
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

        for (int i = 0; i < this.minLength; i++) {
            int randomIndex = random.nextInt(allChars.length());
            sb.append(allChars.charAt(randomIndex));
        }

        return sb.toString();
    }

    /**
     * Hashes the provided plain text password using the BCrypt hashing algorithm.
     * BCrypt is a strong hashing function that includes salting to protect against
     * rainbow table attacks.
     *
     * @param plainTextPassword The password to be hashed.
     * @return The BCrypt hashed version of the password.
     */
    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Verifies if the given plain text password matches the provided BCrypt hashed password.
     * This method uses the BCrypt library's built-in checking function, which handles
     * the salt and hashing process internally.
     *
     * @param plainTextPassword The plain text password to check.
     * @param hashedPassword    The BCrypt hashed password to compare against.
     * @return {@code true} if the plain text password matches the hash, {@code false} otherwise.
     */
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

}