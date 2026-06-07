package com.j3d.gen.settings;

import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.gen.settings.classes.EditorProperties;
import com.j3d.gen.settings.classes.SceneProperties;
import com.j3d.gen.settings.types.ComplexSetting;
import com.j3d.gen.settings.types.StringSetting;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.settings.PreferencesFrame;
import com.j3d.ui.settings.popouts.ThemeChanger;

import java.util.ArrayList;

public class Settings implements SettingsParent {
    
    public static PreferencesFrame preferencesFrame;
    public static CameraProperties cameraProperties = new CameraProperties();
    public static SceneProperties sceneProperties = new SceneProperties();
    public static EditorProperties editorProperties = new EditorProperties();
    public static SettingsParent debugProperties;
    public static SettingsParent viewProperties;
    public static ThemeChanger themeChanger;
    public static ComplexSetting<?> changeTheme = new ComplexSetting<>(
            "Change Theme",
            J3DTheme.BACKGROUND,
            "Change your theme (only applies on app restart)",
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
            add(editorProperties);
            add(sceneProperties);
//            add(debugProperties);
//            add(viewProperties);
        }};
    }

    @Override
    public ArrayList<SettingsParent> getChildSettingsFolder() {
        return new ArrayList<>() {{
            add(cameraProperties);
            add(editorProperties);
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
    public PreferencesFrame panel() {
        if (preferencesFrame == null) preferencesFrame = new PreferencesFrame();
        return preferencesFrame;
    }
}
