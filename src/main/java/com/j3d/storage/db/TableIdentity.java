package com.j3d.storage.db;

import com.j3d.storage.db.themes.CThemes;
import com.j3d.storage.db.users.CUsers;

/**
 * The tables in the database.
 */
public enum TableIdentity {

    /**
     * The users table.
     */
    USERS("tblUsers", CUsers.IDENTIFIER.getValue()),
    /**
     * The themes table.
     */
    THEMES("tblThemes", CThemes.IDENTIFIER.getValue());

    private final String tableName;
    private final String primaryKey;

    /**
     * Constructor.
     * @param tbName The table name.
     * @param pkName The primary key name.
     */
    TableIdentity(String tbName, String pkName) {
        this.tableName = tbName;
        this.primaryKey = pkName;
    }

    /**
     * Gets the table name.
     * @return The table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the primary key name.
     * @return The primary key name.
     */
    public String getPrimaryKey() {
        return primaryKey;
    }
}
