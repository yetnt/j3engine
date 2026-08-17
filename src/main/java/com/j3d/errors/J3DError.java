package com.j3d.errors;

import com.j3d.errors.severity.J3ErrSeverity;

/**
 * J3Engine Exceptions
 * <p>
 *     It extends {@link RuntimeException} and provides structured error codes
 *     consisting of a base code and a specific error code, along with a formatted
 *     string representation of the error.
 * </p>
 * @see J3ErrSeverity
 * @see ErrorHandler
 * @see BaseErrorCodes
 * @author Lehlogonolo Poole
 */
public class J3DError extends RuntimeException {
    /**
     * A specific error code within the {@link #baseCode} category.
     * This code is typically set via the {@link #code(int)} method.
     */
    int code = 0;
    /**
     * The base category code for this error.
     * This helps categorize errors into broader groups.
     */
    private int baseCode = 0;

    /**
     * Constructs a new {@code J3DError} with the specified detail message and base code.
     * The cause is not initialized, and the specific error code defaults to 0.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param baseCode The base category code for this error.
     */
    public J3DError(String message, int baseCode) {
        super(message);
        this.baseCode = baseCode;
    }

    /**
     * Constructs a new {@code J3DError} with the specified detail message, cause, and base code.
     * The specific error code defaults to 0.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param baseCode The base category code for this error.
     */
    public J3DError(String message, Throwable cause, int baseCode) {
        super(message, cause);
        this.baseCode = baseCode;
    }

    /**
     * Sets the specific error code for this {@code J3DError}.
     * This method allows for fluent API chaining.
     *
     * @param value The specific error code.
     * @return This {@code J3DError} instance, allowing method chaining.
     */
    public J3DError code(int value) {
        this.code = value;
        return this;
    }

    /**
     * Returns the specific error code associated with this {@code J3DError}.
     *
     * @return The specific error code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the base category code associated with this {@code J3DError}.
     *
     * @return The base category code.
     */
    public int getBaseCode() {
        return baseCode;
    }

    /**
     * Builds and returns a combined integer error code by multiplying the
     * {@link #baseCode} by 1000 and adding the {@link #code}.
     * This creates a unique numerical identifier for the error.
     *
     * @return The combined integer error code.
     */
    public int buildCode() {
        return baseCode * 1000 + code;
    }

    /**
     * Returns a formatted string representation of the error code.
     * The format is "J" followed by the {@link #buildCode()} padded to 5 digits with leading zeros.
     * For example, if {@code baseCode} is 1 and {@code code} is 23, {@code buildCode()} would be 1023,
     * and {@code errorCode()} would return "J01023".
     *
     * @return A string representing the formatted error code.
     */
    public String errorCode() {
        return "J" + String.format("%05d", buildCode());
    }

}
