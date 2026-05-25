package com.j3d.storage.db.api;

import com.j3d.errors.ErrorHandler;
import com.j3d.storage.db.ConnectionReason;
import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.themes.ThemesTable;
import com.j3d.storage.db.users.UsersTable;
import com.j3d.storage.errs.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A table in the database.
 * @param <T> The type of record this table produces
 * @param <C> The column definitions.
 */
public interface Table<T extends DBRecord<?>, C extends TableColumns> {

    /**
     * Gets the table name.
     * @return The table name.
     */
    String getName();

    /**
     * Gets the table columns enum.
     * @return The table columns enum.
     */
    ArrayList<C> getColumns();
    /**
     * Gets the table primary key. (Enum)
     * @return The table primary key.
     */
    C getPrimaryKey();

    /**
     * Creates a record for the table to use within
     * @param values The values of the record in order.
     * @return The record.
     * @implNote Nothing outside any implementors of {@link Table} should use this method. Rather use the
     * methods provided by the specific table classes to create a record. e.g. {@link ThemesTable#getTheme(int)} or {@link UsersTable#getUser(int)}
     */
    T of(Object... values);

    /**
     * Finds all records in the table where the given column matches the given value via the given operator.
     * @param columnName The column to match.
     * @param op The operator to use.
     * @param value The value to match.
     * @return The list of records.
     * @param <V> The type of the value.
     * @see #findById(int)
     */
    default <V> ArrayList<T> findWhere(C columnName, SQLOperator op, V value) {

        ArrayList<T>  list = new ArrayList<>();

        if (!getColumns().contains(columnName))
            return list; // TODO: Throw a custom error here for clarity.

        String sql = "SELECT * FROM " +  getName() + " WHERE " + columnName.getValue() + " " + op.getValue() + " ?";

        ConnectionReason cr = new ConnectionReason(
                this,
                ConnectionReason.Reason.QUERY.setSqlString(sql)
        );

        try (Connection con = DatabaseManager.connect(cr); PreparedStatement psmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            switch (value) {
                case Integer i -> psmt.setInt(1, i);
                case String s -> {
                    if (op == SQLOperator.LIKE)
                        psmt.setString(1, SQLOperator.toDriverLIKESyntax(s));
                    else
                        psmt.setString(1, s);
                }
                case Boolean b -> psmt.setBoolean(1, b);
                case Double d -> psmt.setDouble(1, d);
                // If we don't know, just hope prepared statement does.
                case null, default -> psmt.setObject(1, value);
            }

            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    list.add(of(
                            getColumns().stream().map(
                                    (s) -> {
                                        try {
                                            return rs.getObject(s.getValue());
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            ).toArray()
                    ));
                }

                return list;
            }


        } catch (SQLException e) {
            ErrorHandler.handle(
                    new DBException(
                            "An SQL exception was encountered!",
                            cr,
                            e
                    )
            );
        }
        return list;
    }

    /**
     * Finds a record by its primary key.
     * @param id The id of the record.
     * @return The record.
     * @see #findWhere(TableColumns, SQLOperator, Object) 
     */
    default T findById(int id) {
        ArrayList<T> results = findWhere(getPrimaryKey(), SQLOperator.EQUALS, id);
        if (results.isEmpty()) return null;
        return results.getFirst();
    }
}
