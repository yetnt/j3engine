package com.j3d.storage.db;

import com.j3d.storage.db.api.Table;

import java.time.LocalDateTime;

public class ConnectionReason {

    public enum Reason {
        DELETE("D"),
        INSERT("I"),
        QUERY("Q"),
        UPDATE("U");

        private final String str;
        Reason(String str) {
            this.str = str;
        }

        public String sql(String s) {
            return s;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    private final Table<?, ?> table;
    private final Reason reason;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String sqlString;

    public ConnectionReason(Table<?, ?> table, Reason reason, String sql) {
        this.table = table;
        this.reason = reason;
        this.sqlString = sql;
    }

    public Table<?, ?> getTable() {
        return table;
    }

    public Reason getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    private String truncatedSqlString() {
        if (sqlString == null) return "";
        int maxLength = 70;
        if (sqlString.length() <= maxLength) return sqlString;
        return sqlString.substring(0, maxLength) + "...";
    }

    @Override
    public String toString() {
        return table.getName() + "\t(" + reason.str + ") | " + truncatedSqlString();
    }
}
