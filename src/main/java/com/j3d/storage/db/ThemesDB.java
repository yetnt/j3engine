package com.j3d.storage.db;

import com.j3d.ui.J3DTheme;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class ThemesDB {

    public static HashMap<String, Color> getTheme(int themeId) {
        String sql = "SELECT * FROM tblThemes WHERE themeId = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            HashMap<String, Color> theme = new HashMap<>();

            pstmt.setInt(1, themeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // theme exists.
                    Arrays.stream(J3DTheme.values()).forEach(
                            t -> {
                                String name = t.toDbFieldName();
                                try {
                                    theme.put(t.name(), Color.decode("#" + rs.getString(name)));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                    return theme;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
