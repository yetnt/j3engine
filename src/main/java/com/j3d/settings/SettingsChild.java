package com.j3d.settings;

import java.awt.*;

/**
 * An interface for a child of a settings folder.
 * <p>
 * This interface allows for the creation of a hierarchical structure of settings, where each folder can contain
 * other folders or individual settings. This is useful for organizing a large number of settings in a logical way.
 * </p>
 * <p>
 *     This interface is implemented by {@link SettingsParent} and {@link Setting}.
 * </p>
 */
public interface SettingsChild {
    String getDescription();
    String getName();
    Component panel();
}
