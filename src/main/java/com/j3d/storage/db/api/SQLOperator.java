package com.j3d.storage.db.api;

public enum SQLOperator {
    LIKE("LIKE"),
    EQUALS("="),
    IN("IN"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN_OR_EQUAL_TO("<="),
    NOT_EQUAL("<>");

    private final String value;

    SQLOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
