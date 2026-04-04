package com.j3d.storage.db.themes;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.api.Table;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThemesTable implements Table<Theme, CThemes> {

    @Override
    public String getName() {
        return "tblThemes";
    }

    @Override
    public CThemes getPrimaryKey() {
        return CThemes.IDENTIFIER;
    }

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
    public Theme of(Object... values) {
        return new Theme(
                (int) values[0],                                // themeId
                (String) values[1],                             // themeName
                Color.decode("#" + ((String) values[2])),   // textPrimary
                Color.decode("#" + ((String) values[3])),   // textSecondary
                Color.decode("#" + ((String) values[4])),   // accentPrimary
                Color.decode("#" + ((String) values[5])),   // accentSecondary
                Color.decode("#" + ((String) values[6])),   // uiSurface
                Color.decode("#" + ((String) values[7]))    // background
        );
    }

    @Override
    public ArrayList<CThemes> getColumns() {
        return new ArrayList<>(
                List.of(CThemes.values())
        );
    }
}
