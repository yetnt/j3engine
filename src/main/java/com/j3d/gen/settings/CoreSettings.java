package com.j3d.gen.settings;

import com.j3d.engine.interact.selection.SelectionFilter;
import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.db.users.User;

/**
 * While apart of settings, core settings are the modernied version of {@link com.j3d.J3DSettings}.
 * Settings who the user should not be able to access and thus is not available on the UI.
 */
public abstract class CoreSettings {
    public static User user;
    public static boolean hasSaved = false;
    static {
        user = DatabaseManager.tblUsers.findById(1);
    }
    public static SelectionFilter selectionFilter = SelectionFilter.TRI;
}
