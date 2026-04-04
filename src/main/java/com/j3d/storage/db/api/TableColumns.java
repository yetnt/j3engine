package com.j3d.storage.db.api;

/**
 * The table's columns. (implmentors should be enums.)
 */
public interface TableColumns {
    /**
     * Gets the column name of the specified enum.
     * @return The column name.
     */
    String getValue();
}
