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
        private String sqlString;
        Reason(String str) {
            this.str = str;
        }

        public Reason setSqlString(String s) {
            this.sqlString = s;
            return this;
        }

        public String getSqlString() {
            return sqlString;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    private final Table<?, ?> table;
    private final Reason reason;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ConnectionReason(Table<?, ?> table, Reason reason) {
        this.table = table;
        this.reason = reason;
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
        if (reason.getSqlString() == null) return "";
        int maxLength = 70;
        if (reason.getSqlString().length() <= maxLength) return reason.getSqlString();
        return reason.getSqlString().substring(0, maxLength) + "...";
    }

    @Override
    public String toString() {
        return table.getName() + "\t(" + reason.str + ") | " + truncatedSqlString();
    }
}
