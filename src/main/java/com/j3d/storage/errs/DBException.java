package com.j3d.storage.errs;


import com.j3d.storage.db.ConnectionReason;
import com.j3d.storage.db.DatabaseManager;

/**
 * An exception that occurs during the reading or writing to the database.
 * @see GenericIOException
 * @see DatabaseManager
 * @author Lehlogonolo Poole
 */
public class DBException extends GenericIOException {
    public DBException(String message, ConnectionReason connectionReason) {
        super(message + " {" + connectionReason.toString() + "}");
    }

    public DBException(String message, ConnectionReason connectionReason, Throwable cause) {
        super(message + " {" + connectionReason.toString() + "}", cause);
    }
}
