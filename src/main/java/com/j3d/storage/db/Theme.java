package com.j3d.storage.db;

import com.j3d.storage.db.api.DBRecord;
import com.j3d.storage.db.api.RecordField;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Theme implements DBRecord {

    public int themeId;
    public RecordField<String> themeName;
    public ColorField textPrimary;
    public ColorField textSecondary;
    public ColorField accentPrimary;
    public ColorField accentSecondary;
    public ColorField uiSurface;
    public ColorField background;
    private ArrayList<RecordField<?>> fields = new ArrayList<>();

    private Theme(int themeId, String themeName, Color textPrimary, Color textSecondary, Color accentPrimary, Color accentSecondary, Color uiSurface, Color background) {
        this.themeId = themeId;
        this.themeName = new RecordField<>("themeName", themeName, "tblThemes");
        this.textPrimary = new ColorField("textPrimary", textPrimary, "tblThemes");
        this.textSecondary = new ColorField("textSecondary", textSecondary, "tblThemes");
        this.accentPrimary = new ColorField("accentPrimary", accentPrimary, "tblThemes");
        this.accentSecondary = new ColorField("accentSecondary", accentSecondary, "tblThemes");
        this.uiSurface = new ColorField("uiSurface", uiSurface, "tblThemes");
        this.background = new ColorField("background", background, "tblThemes");
        this.fields.add(this.themeName);
        this.fields.add(this.textPrimary);
        this.fields.add(this.textSecondary);
        this.fields.add(this.accentPrimary);
        this.fields.add(this.accentSecondary);
        this.fields.add(this.uiSurface);
        this.fields.add(this.background);
    }

    @Override
    public String getPrimaryKey() {
        return "themeId";
    }

    @Override
    public String getTableName() {
        return "tblThemes";
    }

    @Override
    public int getRecordId() {
        return themeId;
    }

    @Override
    public ArrayList<RecordField<?>> getFields() {
        return fields;
    }

    public HashMap<String, Color> toColorHashMap() {
        HashMap<String, Color> theme = new HashMap<>();
        theme.put("textPrimary", textPrimary.getValue());
        theme.put("textSecondary", textSecondary.getValue());
        theme.put("accentPrimary", accentPrimary.getValue());
        theme.put("accentSecondary", accentSecondary.getValue());
        theme.put("uiSurface", uiSurface.getValue());
        theme.put("background", background.getValue());
        return theme;
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
}
