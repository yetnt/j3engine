package com.j3d.storage.db.api;

public class RecordField<T> {
    public final String name;
    private T value;
    private boolean updated = false;
    public final String tblName;

    public RecordField(String name, T value, String tblName) {
        this.name = name;
        this.value = value;
        this.tblName = tblName;
    }

    public T getValue() {
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
