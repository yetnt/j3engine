package com.j3d.storage.db.themes;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.api.SQLOperator;
import com.j3d.storage.db.api.Table;
import com.j3d.storage.db.api.TableColumns;
import com.j3d.storage.db.users.CUsers;
import com.j3d.storage.db.users.User;

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

    /**
     * Finds and creates a new theme object.
     * @param id The id of the theme to find.
     * @return The theme object or null if the theme does not exist.
     * @see DatabaseManager#tblThemes
     * @see Table#findById(int)
     */
    public static Theme getTheme(int id) {
        return DatabaseManager.tblThemes.findById(id);
    }

    /**
     * Checks if a theme with the specified name already exists in the database.
     * @param name The name to check.
     * @return The id of the theme if it exists, -1 otherwise.
     * @see DatabaseManager#tblThemes
     * @see Table#findWhere(TableColumns, SQLOperator, Object) 
     */
    public static int themeExists(String name) {
        ArrayList<Theme> themes = DatabaseManager.tblThemes.findWhere(
                CThemes.THEME_NAME, SQLOperator.EQUALS, name
        );
        if (themes.isEmpty()) return -1;
        else return themes.getFirst().getRecordId();
        //TODO: If multiple themes exist throw new custom error. (similar todo fom tblusers)
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
