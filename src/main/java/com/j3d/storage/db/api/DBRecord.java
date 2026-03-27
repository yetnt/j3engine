package com.j3d.storage.db.api;

import com.j3d.storage.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A singular record within a table.
 * @param <I> The table identity.
 */
public interface DBRecord<I extends Table> {

    /**
     * Gets the table identity.
     * @return The table identity.
     */
    I getTableIdentity();

    /**
     * Gets the record id. (An int because both table's primary keys are of type AutoNumber in the database)
     * @return The record id.
     */
    int getRecordId();

    /**
     * Gets the fields of the record.
     * @return The fields of the record.
     */
    ArrayList<RecordField<?>> getFields();

    /**
     * Saves the record to the database.
     * @implNote This method make use of the updated fields to generate an SQL query that only
     *           updates the affected fields.
     */
    default void save() {
        ArrayList<RecordField<?>> updatedFields = getFields().stream().filter(
                RecordField::consumeUpdated
        ).collect(Collectors.toCollection(ArrayList::new));
        if (updatedFields.isEmpty()) return;
        StringBuilder setString = new StringBuilder();
        updatedFields.forEach(
                f -> {
                    setString.append(f.name).append(" = ?,");
                }
        );
        setString.deleteCharAt(setString.length() - 1);
        String sql = "UPDATE " + getTableIdentity().getName() + " SET " + setString + " WHERE " + getTableIdentity().getPrimaryKey() + " = ?";

        try (Connection conn = DatabaseManager.connect(); PreparedStatement psmt = conn.prepareStatement(sql)) {
            AtomicInteger i = new AtomicInteger(1);
            updatedFields.forEach(
                    f -> {
                        try {
                            psmt.setObject(i.get(), f.getDbValue());
                            i.incrementAndGet();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            psmt.setInt(i.get(), getRecordId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
