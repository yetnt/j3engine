package com.j3d.storage.db;

/**
 * Password record
 * @param hash The hashed password
 * @param salt The salt used to hash the password
 */
public record Password(String hash, byte[] salt) {
    public String hash() {
        return hash;
    }

    public byte[] salt() {
        return salt;
    }
    
    public String hashFieldName() {
        return "passwordHash";
    }

    public String saltFieldName() {
        return "passwordSalt";
    }
}
