package com.j3d.settings;

import com.j3d.settings.classes.CameraPropertiesSettings;
import com.j3d.settings.classes.ScenePropertiesSettings;
import com.j3d.settings.types.ComplexSetting;
import com.j3d.settings.types.StringSetting;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.settings.SettingsFrame;
import com.j3d.ui.settings.panels.PopoutSPanel;
import com.j3d.ui.settings.popouts.ThemeChanger;

import java.util.ArrayList;

public class Settings implements SettingsParent {
    
    public static SettingsFrame settingsFrame;
    public static CameraPropertiesSettings cameraProperties = new CameraPropertiesSettings();
    public static ScenePropertiesSettings sceneProperties = new ScenePropertiesSettings();
    public static SettingsParent debugProperties;
    public static SettingsParent viewProperties;
    public static ThemeChanger themeChanger;
    public static ComplexSetting<?> changeTheme = new ComplexSetting<>(
            "Change Theme",
            J3DTheme.BACKGROUND,
            "",
            e -> {
                if (themeChanger == null) {
                    themeChanger = new ThemeChanger();
                    themeChanger.setVisible(true);
                }
            }
    );
    public static StringSetting projectFile = new StringSetting("Project File", "", "The path to the project file.");

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
            add(changeTheme);
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
            add(changeTheme);
            add(projectFile);
        }};
    }

    @Override
    public SettingsFrame panel() {
        if (settingsFrame == null) settingsFrame = new SettingsFrame();
        return settingsFrame;
    }
}
