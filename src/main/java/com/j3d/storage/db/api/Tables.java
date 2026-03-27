package com.j3d.storage.db.api;

/**
 * The tables in the database.
 */
public enum Tables {

    /**
     * The users table.
     */
    USERS("tblUsers", "userId"),
    /**
     * The themes table.
     */
    THEMES("tblThemes", "themeId");

    private final String tableName;
    private final String primaryKey;

    /**
     * Constructor.
     * @param tbName The table name.
     * @param pkName The primary key name.
     */
    Tables(String tbName, String pkName) {
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
