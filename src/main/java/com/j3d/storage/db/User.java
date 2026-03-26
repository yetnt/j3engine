package com.j3d.storage.db;

import com.j3d.storage.db.api.DBRecord;
import com.j3d.storage.db.api.RecordField;
import com.j3d.utility.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class User implements DBRecord {

    public final int id;
    public final RecordField<Integer> themeId;
    public final RecordField<String> firstName;
    public final RecordField<String> lastName;
    public final RecordField<String> email;
    public final Password password;
    public final ArrayList<RecordField<?>> fields = new ArrayList<>();

    private User(int id, int themeId, String firstName, String lastName, String email, Password password) {
        this.id = id;
        this.themeId = new RecordField<>("themeId", themeId, "tblUsers");
        this.firstName = new RecordField<>("firstName", firstName, "tblUsers");
        this.lastName = new RecordField<>("lastName", lastName, "tblUsers");
        this.email = new RecordField<>("email", email, "tblUsers");
        this.password = password;
        this.fields.add(this.themeId);
        this.fields.add(this.firstName);
        this.fields.add(this.lastName);
        this.fields.add(this.email);
        this.fields.add(this.password.getHash());
        this.fields.add(this.password.getSalt());
    }

    @Override
    public String getTableName() {
        return "tblUsers";
    }

    @Override
    public int getRecordId() {
        return id;
    }

    @Override
    public ArrayList<RecordField<?>> getFields() {
        return fields;
    }

    /**
     * Finds and creates a new user object.
     * @param id The id of the user to find.
     * @return The user object or null if the user does not exist.
     */
    public static User getUser(int id) {
        String sql = "SELECT * FROM tblUsers WHERE userId = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // user exists.
                    return new User(
                            id,
                            rs.getInt("themeId"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            new Password(
                                    rs.getString("passwordHash"),
                                    Base64.getDecoder().decode(rs.getString("passwordSalt"))
                            )
                    );
                } else {
                    // user does not exist.
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user with the specified email already exists in the database.
     *
     * @param email The email to check.
     * @return true if a user with the given email exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static int userExists(String email) throws SQLException {
        String sql = "SELECT * FROM tblUsers WHERE email = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // If the count is greater than 0, the user exists.
                    return rs.getInt("userId");
                }
            }
        }
        // Default to false if something goes wrong or no result is found.
        return -1;
    }

    /**
     * Registers a new user if the email does not already exist in the database.
     *
     * @param name The name for the new user.
     * @param surname The surname for the new user.
     * @param email The email for the new user. This is used to check for existence.
     * @param pass The password for the new user.
     * @return A Pair where the first element is a boolean indicating whether the registration was successful (true if the
     * user was created, false if the user already exists), and the second element is the User object. (either the existing
     * user or the newly created user)
     * @throws SQLException if a database access error occurs.
     */
    public static Pair<Boolean, User> newOrExisting(String name, String surname, String email, Password pass) throws SQLException {
        // First, check if the user already exists.
        int userId = userExists(email);
        if (userId != -1)
            // User with this email already exists, so we do nothing.
            return new Pair<>(false, getUser(userId));

        // User does not exist, so proceed with insertion.
        String sql = "INSERT INTO tblUsers (firstName, lastName, email, passwordHash, passwordSalt, themeId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, pass.getHash().getValue());
            pstmt.setString(5, (String)pass.getSalt().getDbValue());
            pstmt.setInt(6, 1);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Registration was successful.
                    return new Pair<>(true,
                            new User(
                                    rs.getInt("userId"),
                                    1,
                                    name,
                                    surname,
                                    email,
                                    pass
                            )
                    );
                }
            }
        }

        return new Pair<>(false, null);

    }
}
