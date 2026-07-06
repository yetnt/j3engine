package com.j3d.engine.interact.cmd;

public class CmdToken {

    private final String input;
    private Type type;
    private Object parsedValue;

    public CmdToken(String input) {
        this.input = input;
    }

    public void parsedAs(Object o, Type t) {
        this.parsedValue = o;
        this.type = t;
    }

    public Object getParsedValue() {
        return parsedValue;
    }

    public String getInput() {
        return input;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CMD_NAME,
        VECTOR3,
        ID_POINT,
        ID_LINE,
        ID_TRI,
        ID_THING,
        COLOUR,
        STRING,
        INT,
        DOUBLE,
        BOOL,
        TAGGED;
    }
}
