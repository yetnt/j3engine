package com.j3d.storage.db.users;

import com.j3d.storage.db.api.RecordField;

import java.util.Base64;

/**
 * The password of a user within {@link UsersTable}.
 * @param hash The hashed password
 * @param salt The salt used to hash the password
 * @apiNote While this is more used in relation to a {@link User} object. One may need to
 *          define this class before. In that case use {@link this#directHash()} and {@link this#directSalt()} instead.
 */
public record Password(RecordField<String> hash, RecordField<byte[]> salt) {
    /**
     * @param hash The hashed password
     * @param salt The salt used to hash the password
     */
    public Password(String hash, byte[] salt) {
        this(new RecordField<>("passwordHash", hash), new RecordField<>("passwordSalt", salt,
                (s) -> Base64.getEncoder().encodeToString(s)
        ));
    }

    /**
     * Convenience method for direct hash access.
     * @return The hashed password
     */
    public String directHash() {
        return hash.getValue();
    }

    /**
     * Convenience method for direct salt access.
     * @return The salt used to hash the password
     */
    public byte[] directSalt() {
        return salt.getValue();
    }
}
