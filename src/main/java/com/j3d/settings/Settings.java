package com.j3d.settings;

import com.j3d.settings.classes.CameraPropertiesSettings;
import com.j3d.settings.classes.ScenePropertiesSettings;
import com.j3d.settings.types.StringSetting;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class Settings implements SettingsParent {

    public CameraPropertiesSettings cameraProperties = new CameraPropertiesSettings();
    public ScenePropertiesSettings sceneProperties = new ScenePropertiesSettings();
    public SettingsParent debugProperties;
    public SettingsParent viewProperties;
    public StringSetting projectFile = new StringSetting("Project File", "", "The path to the project file.");


    public Settings() {

    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public String getDescription() {
        return ""; // Main Settings object, no description.
    }

    @Override
    public ArrayList<SettingsChild> getAllChildren() {
        return new ArrayList<>() {{
            add(projectFile);
            add(cameraProperties);
            add(sceneProperties);
//            add(debugProperties);
//            add(viewProperties);
        }};
    }

    @Override
    public ArrayList<SettingsParent> getChildSettingsFolder() {
        return new ArrayList<>() {{
            add(cameraProperties);
            add(sceneProperties);
//            add(debugProperties);
//            add(viewProperties);
        }};
    }

    @Override
    public ArrayList<Setting<?>> getChildSettings() {
        return new ArrayList<>() {{
            add(projectFile);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return null; // Main Settings object, no panel.
    }
}
