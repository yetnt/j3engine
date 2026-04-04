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

    /**
     * Turns text like {@code *a*} into {@code %a%}.
     * @param statement The statement to convert.
     * @return The converted statement.
     * @implSpec Even though we work with an Access DB,
     * i noticed that normal access syntax for LIKE clause does not work.
     * but MySQL does. So this converter makes the conversion which just turns all
     * "*" into "%" and "?" into "_".
     */
    public static String toDriverLIKESyntax(String statement) {
        // Turn Access LIKE Syntax into MySQL Syntax
        return statement.replace('*', '%').replace('?', '_');
    }
}
