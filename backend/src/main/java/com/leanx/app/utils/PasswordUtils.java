package main.java.com.leanx.app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Hashes the plain text password using BCrypt.
     * @param plainTextPassword The plain text password.
     * @return The hashed password.
     */
    public String hashPassword(String plainTextPassword) {

    }

    /**
     * Checks if the provided plain text password matches the hashed password.
     * @param plainTextPassword The plain text password.
     * @param hashedPassword The password hash of user object.
     * @return True if passwords match, false otherwise.
     */
    boolean checkPassword(String plainTextPassword, String hashedPassword);

}
