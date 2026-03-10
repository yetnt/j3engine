package com.j3d.files;

import java.io.File;
import java.sql.*;

public class DatabaseManager {

    public static Connection connect() throws SQLException {
        // 1. Get the absolute path to your project folder automatically
        String projectPath = System.getProperty("user.dir");

        // 2. Point to the database file inside that folder
        // Use File.separator so it works on both Windows and Mac/Linux
        String dbFilePath = projectPath + File.separator + "database" + File.separator + "j3db.accdb";

        String url = "jdbc:ucanaccess://" + dbFilePath;

        System.out.println("Connected to: " + dbFilePath);
        Connection con = DriverManager.getConnection(url);
        return con;
    }
}
