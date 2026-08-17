package com.j3d.storage.errs;


import com.j3d.errors.BaseErrorCodes;
import com.j3d.errors.J3DError;
import com.j3d.errors.severity.J3DFatal;
import com.j3d.errors.severity.J3DWarning;
import com.j3d.storage.db.ConnectionReason;
import com.j3d.storage.db.DatabaseManager;

/**
 * An exception that occurs during the reading or writing to the database.
 * @see J3DError
 * @see J3DFatal
 * @see DatabaseManager
 * @author Lehlogonolo Poole
 */
public class DBException extends J3DError implements J3DWarning {
    public DBException(String message, ConnectionReason connectionReason) {
        super(message + " {" + connectionReason.toString() + "}", BaseErrorCodes.DATABASE.getBaseCode());
    }

    public DBException(String message, ConnectionReason connectionReason, Throwable cause) {
        super(message + " {" + connectionReason.toString() + "}", cause, BaseErrorCodes.DATABASE.getBaseCode());
    }


    public DBException(String message) {
        super(message, BaseErrorCodes.DATABASE.getBaseCode());
    }
    public DBException(String message, Throwable throwable) {
        super(message, throwable, BaseErrorCodes.DATABASE.getBaseCode());
    }

    public static J3DError sqlException(Throwable throwable, ConnectionReason cr) {
        return new DBException("An SQL exception occurred...", cr, throwable)
                .code(100);
    }
}
