package com.j3d.engine.interact.cmd.base;

import com.j3d.ui.util.SafeJLabel;

public class TaggedArgValue<T> {
    public String taggedArgName;
    public T value;
    public Class type;

    private boolean isEmpty = true;
    private boolean err = false;

    private TaggedArgValue(T v) {
        value = v;
    }

    public TaggedArgValue(Class type) {
        this.type = type;
    }

    public TaggedArgValue<T> setName(String name) {
        isEmpty = false;
        taggedArgName = name;
        return this;
    }

    public TaggedArgValue<T> error() {
        err = true;
        return this;
    }

    public TaggedArgValue<T> setType(Class clazz) {
        type = clazz;
        return this;
    }

    public TaggedArgValue<?> copy(Object val, SafeJLabel label) {
        if (val instanceof Integer i && type == Double.class)
            val = Double.valueOf((double)i);
        if (!type.isInstance(val)) {
            label.setText("Invalid type given to tagged argument: " + val.getClass().getSimpleName().toLowerCase() + ". RequireS: " + type.getSimpleName().toLowerCase());
            return new TaggedArgValue<>(null).error();
        }
        return new TaggedArgValue<>(val).setName(taggedArgName).setType(type);
    }

    public boolean isEmpty() {
        return isEmpty;
    }
    public boolean isErr() {
        return err;
    }

    public String toString() {
        return "{" + taggedArgName + ": " + value.toString()+"}";
    }
}
