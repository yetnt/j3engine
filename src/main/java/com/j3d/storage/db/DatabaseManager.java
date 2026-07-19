package com.j3d.storage.db;

import com.j3d.StaticRefs;
import com.j3d.storage.db.themes.ThemesTable;
import com.j3d.storage.db.users.UsersTable;

import java.io.File;
import java.sql.*;

public class DatabaseManager {

    public static ThemesTable tblThemes = new ThemesTable();
    public static UsersTable tblUsers = new UsersTable();

    /**
     * Connects to the database.
     * @return The connection object.
     * @throws SQLException If the connection fails.
     */
    public static Connection connect(ConnectionReason cr) throws SQLException {
        // 1. Get the absolute path to your project folder automatically
        String projectPath = System.getProperty("user.dir");

        // 2. Point to the database file inside that folder
        // Use File.separator so it works on both Windows and Mac/Linux
        String dbFilePath = projectPath + File.separator + "database" + File.separator + "j3db.accdb";

        String url = "jdbc:ucanaccess://" + dbFilePath;

        System.out.println("Connected to: " + dbFilePath);

        StaticRefs.getLog().println("[DB] Connected. " + cr);
        return DriverManager.getConnection(url);
    }

}
