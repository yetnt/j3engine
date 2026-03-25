package com.j3d.utility.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDB {
    /**
     * Registers a new user if the email does not already exist in the database.
     *
     * @param name The name for the new user.
     * @param surname The surname for the new user.
     * @param email The email for the new user. This is used to check for existence.
     * @param hashedPass The user's password
     * @param passwordSalt The user's password salt
     * @return true if the user was successfully registered, false if the user already exists.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean registerUser(String name, String surname, String email, String hashedPass, String passwordSalt) throws SQLException {
        // First, check if the user already exists.
        if (userExists(email)) {
            // User with this email already exists, so we do nothing.
            return false;
        }

        // User does not exist, so proceed with insertion.
        String sql = "INSERT INTO tblUsers (firstName, lastName, email, passwordHash, passwordSalt, themeId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPass);
            pstmt.setString(5, passwordSalt);
            pstmt.setInt(6, 1);

            pstmt.executeUpdate();
        }
        // Registration was successful.
        return true;
    }

    /**
     * Checks if a user with the specified email already exists in the database.
     *
     * @param email The email to check.
     * @return true if a user with the given email exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean userExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tblUsers WHERE email = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If the count is greater than 0, the user exists.
                    return rs.getInt(1) > 0;
                }
            }
        }
        // Default to false if something goes wrong or no result is found.
        return false;
    }
}
