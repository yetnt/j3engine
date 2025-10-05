package com.j3d.engine.interact.cmd.base;

import java.util.ArrayList;

/**
 * TypedArg is a generic class that can hold a value of any type.
 * It is used to represent an argument with a specific type.
 */
public class TypedArg implements Argument {
    private ArrayList<Class> type = new ArrayList<>();
    private String name;
    private String description;
    private boolean optional;

    public TypedArg(String n, String description, boolean optional, Class ...type) {
        this.name = n;
        this.type.addAll(java.util.Arrays.asList(type));
        this.description = description;
        this.optional = optional;
    }

    public ArrayList<Class> getType() {
        return type;
    }
}
