package com.j3d.storage.db.users;

import com.j3d.storage.db.api.TableColumns;

public enum CUsers implements TableColumns {
    IDENTIFIER("userId"),
    THEME_ID("themeId"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    PASSWORD_HASH("passwordHash"),
    PASSWORD_SALT("passwordSalt");

    private final String value;

    CUsers(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
