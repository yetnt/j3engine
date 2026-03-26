package com.j3d.storage.db;

import com.j3d.storage.db.api.RecordField;

import java.util.Base64;

/**
 * Password
 */
public final class Password {
    private final RecordField<String> hash;
    private final RecordField<byte[]> salt;

    /**
     * @param hash The hashed password
     * @param salt The salt used to hash the password
     */
    public Password(String hash, byte[] salt) {
        this.hash = new RecordField<>("passwordHash", hash, "tblUsers");
        this.salt = new RecordField<>("passwordSalt", salt, "tblUsers",
                (t) -> Base64.getEncoder().encodeToString(t)
        );
    }

    public RecordField<byte[]> getSalt() {
        return salt;
    }

    public RecordField<String> getHash() {
        return hash;
    }
}
