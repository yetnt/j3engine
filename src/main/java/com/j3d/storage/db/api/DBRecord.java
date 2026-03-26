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
 */
public interface DBRecord {
    String getTableName();
    int getRecordId();
    ArrayList<RecordField<?>> getFields();
    /**
     * Updates the record in the database.
     */
    default void update() {
        ArrayList<RecordField<?>> updatedFields = getFields().stream().filter(
                RecordField::isUpdated
        ).collect(Collectors.toCollection(ArrayList::new));
        if (updatedFields.isEmpty()) return;
        StringBuilder setString = new StringBuilder();
        updatedFields.forEach(
                f -> {
                    setString.append(f.name).append(" = ?,");
                }
        );
        setString.deleteCharAt(setString.length() - 1);
        String sql = "UPDATE " + getTableName() + " SET " + setString + " WHERE "+getPrimaryKey()+" = ?";

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

    String getPrimaryKey();
}
