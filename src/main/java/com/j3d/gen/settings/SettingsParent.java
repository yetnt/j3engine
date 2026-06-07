package com.j3d.gen.settings;


import java.util.ArrayList;

/**
 * An interface for a folder of settings.
 * <p>
 * This interface allows for the creation of a hierarchical structure of settings, where each folder can contain
 * other folders or individual settings. This is useful for organizing a large number of settings in a logical way.
 * </p>
 */
public interface SettingsParent extends SettingsChild {
    ArrayList<SettingsChild> getAllChildren();
    ArrayList<SettingsParent> getChildSettingsFolder();
    ArrayList<Setting<?>> getChildSettings();
}