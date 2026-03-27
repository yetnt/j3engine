package com.j3d.storage.db.api;

/**
 * A table in the database.
 */
public interface Table {
    /**
     * Gets the table identity.
     * @return The table identity enum.
     */
    Tables getIdentity();
    /**
     * Gets the table name.
     * @return The table name.
     */
    default String getName() {
        return getIdentity().getTableName();
    }
    /**
     * Gets the table primary key.
     * @return The table primary key.
     */
    default String getPrimaryKey() {
        return getIdentity().getPrimaryKey();
    }
}
