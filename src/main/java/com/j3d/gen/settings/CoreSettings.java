package com.j3d.gen.settings;

import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.users.User;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * While apart of settings, core settings are the modernied version of {@link com.j3d.J3DSettings}.
 * Settings who the user should not be able to access and thus is not available on the UI.
 */
public abstract class CoreSettings {
    public static User user;
    public static boolean hasSaved = false;
    /**
     * Generic lock flag for when the user has CAPS LOCK enabled.
     */
    public static boolean lock;

    static {
        user = DatabaseManager.tblUsers.findById(1);
        try {
            CoreSettings.lock =
                    Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        } catch (UnsupportedOperationException ue) {
            CoreSettings.lock = false;
        }
    }
}
