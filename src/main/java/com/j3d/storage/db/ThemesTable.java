package com.j3d.storage.db;

import com.j3d.storage.db.api.Table;
import com.j3d.storage.db.api.Tables;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThemesTable implements Table {

    public static Theme getTheme(int id) {
        String sql = "SELECT * FROM tblThemes WHERE themeId = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Theme(
                            id,
                            rs.getString("themeName"),
                            Color.decode("#" + rs.getString("textPrimary")),
                            Color.decode("#" + rs.getString("textSecondary")),
                            Color.decode("#" + rs.getString("accentPrimary")),
                            Color.decode("#" + rs.getString("accentSecondary")),
                            Color.decode("#" + rs.getString("uiSurface")),
                            Color.decode("#" + rs.getString("background"))
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static int themeExists(String name) {
        String sql = "SELECT * FROM tblThemes WHERE themeName = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("themeId");
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tables getIdentity() {
        return Tables.THEMES;
    }
}
