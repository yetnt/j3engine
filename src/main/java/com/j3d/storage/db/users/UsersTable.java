package com.j3d.storage.db.users;

import com.j3d.StaticRefs;
import com.j3d.errors.ErrorHandler;
import com.j3d.storage.db.ConnectionReason;
import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.api.SQLOperator;
import com.j3d.storage.db.api.Table;
import com.j3d.storage.db.api.TableColumns;
import com.j3d.storage.errs.DBException;
import com.j3d.utility.generic.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UsersTable implements Table<User, CUsers> {

    @Override
    public String getName() {
        return "tblUsers";
    }

    @Override
    public CUsers getPrimaryKey() {
        return CUsers.IDENTIFIER;
    }

    /**
     * Finds and creates a new user object.
     * @param id The id of the user to find.
     * @return The user object or null if the user does not exist.
     * @see DatabaseManager#tblUsers
     * @see Table#findById(int)
     */
    public static User getUser(int id) {
        return DatabaseManager.tblUsers.findById(id);
    }

    /**
     * Checks if a user with the specified email already exists in the database.
     *
     * @param email The email to check.
     * @return the id of the user if the user with the given email exists, -1 otherwise.
     * @see DatabaseManager#tblUsers
     * @see Table#findWhere(TableColumns, SQLOperator, Object) 
     */
    public static int userExists(String email) {
        ArrayList<User> users = DatabaseManager.tblUsers.findWhere(
                CUsers.EMAIL, SQLOperator.EQUALS, email
        );
        if (users.isEmpty()) return -1;
        else return users.getFirst().getRecordId();
        //TODO: If multiple users exist throw new custom error.
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
    public static Pair<Boolean, User> newOrExisting(String name, String surname, String email, Password pass) {
        // First, check if the user already exists.
        int userId = userExists(email);
        if (userId != -1)
            // User with this email already exists, so we do nothing.
            return new Pair<>(false, getUser(userId));

        // User does not exist, so proceed with insertion.
        String sql = "INSERT INTO tblUsers (firstName, lastName, email, passwordHash, passwordSalt, themeId) VALUES (?, ?, ?, ?, ?, ?)";

        ConnectionReason cr = new ConnectionReason(
                DatabaseManager.tblUsers,
                ConnectionReason.Reason.INSERT.setSqlString(sql)
        );

        try (Connection conn = DatabaseManager.connect(cr);
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, email);
            pstmt.setString(4, pass.hash().getValue());
            pstmt.setString(5, (String)pass.salt().getDbValue());
            pstmt.setInt(6, 1);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Registration was successful.
                    return new Pair<>(true,
                            new User(
                                    rs.getInt(1),
                                    1,
                                    name,
                                    surname,
                                    email,
                                    pass
                            )
                    );
                }
            } catch (SQLException e) {
                StaticRefs.getErrs().handle(
                        new DBException(
                                "An SQL exception was encountered whilst trying to insert a new user",
                                cr, e
                        )
                );
            }
        } catch (SQLException e) {
            StaticRefs.getErrs().handle(
                    new DBException(
                            "An SQL exception was encountered whilst trying to insert or find a new user",
                            cr, e
                    )
            );
        }

        return new Pair<>(false, null);

    }

    @Override
    public User of(Object... values) {
        return new User(
                (int) values[0],
                (int) values[1],
                (String) values[3],
                (String) values[4],
                (String) values[2],
                new Password((String) values[5], Base64.getDecoder().decode((String)values[6]))
        );
    }

    @Override
    public ArrayList<CUsers> getColumns() {
        return new ArrayList<>(
                List.of(CUsers.values())
        );
    }
}
