package com.j3d.storage.db.users;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.api.DBRecord;
import com.j3d.storage.db.api.RecordField;

import java.util.ArrayList;

public class User implements DBRecord<UsersTable> {

    public final int id;
    public final RecordField<Integer> themeId;
    public final RecordField<String> firstName;
    public final RecordField<String> lastName;
    public final RecordField<String> email;
    public final Password password;
    public final ArrayList<RecordField<?>> fields = new ArrayList<>();

    protected User(int id, int themeId, String firstName, String lastName, String email, Password password) {
        this.id = id;
        this.themeId = new RecordField<>("themeId", themeId);
        this.firstName = new RecordField<>("firstName", firstName);
        this.lastName = new RecordField<>("lastName", lastName);
        this.email = new RecordField<>("email", email);
        this.password = password;
        this.fields.add(this.themeId);
        this.fields.add(this.firstName);
        this.fields.add(this.lastName);
        this.fields.add(this.email);
        this.fields.add(this.password.hash());
        this.fields.add(this.password.salt());
    }

    @Override
    public UsersTable getTable() {
        return DatabaseManager.tblUsers;
    }

    @Override
    public int getRecordId() {
        return id;
    }

    @Override
    public ArrayList<RecordField<?>> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "User {" + firstName.getValue() + " " + lastName.getValue() + ", id=" + id + "}";
    }
}
