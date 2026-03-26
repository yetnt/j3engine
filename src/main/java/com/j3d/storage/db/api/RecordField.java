package com.j3d.storage.db.api;

import java.util.function.Function;

public class RecordField<T> {
    public final String name;
    private T value;
    private boolean updated = false;
    public final String tblName;
    private final Function<T, String> converter;

    public RecordField(String name, T value, String tblName) {
        this.name = name;
        this.value = value;
        this.tblName = tblName;
        this.converter = null;
    }

    public RecordField(String name, T value, String tblName, Function<T, String> converter) {
        this.name = name;
        this.value = value;
        this.tblName = tblName;
        this.converter = converter;
    }

    public T getValue() {
        return value;
    }

    public Object getDbValue() {
        if (converter != null)
            return converter.apply(value);
        return value;
    }

    public boolean isUpdated() {
        if (this.updated) {
            this.updated = false;
            return true;
        }
        return false;
    }

    public void setValue(T value) {
        this.value = value;
        this.updated = true;
    }
}
