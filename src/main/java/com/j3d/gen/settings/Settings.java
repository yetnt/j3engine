package com.j3d.gen.settings;

import com.j3d.Static;
import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.gen.settings.classes.EditorProperties;
import com.j3d.gen.settings.classes.SceneProperties;
import com.j3d.gen.settings.types.ComplexSetting;
import com.j3d.storage.db.DatabaseManager;
import com.j3d.storage.files.FilesUtility;
import com.j3d.ui.settings.PreferencesFrame;
import com.j3d.ui.settings.popouts.ThemeChanger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class Settings implements SettingsParent {
    
    public static PreferencesFrame preferencesFrame;
    public static CameraProperties cameraProperties = new CameraProperties();
    public static SceneProperties sceneProperties = new SceneProperties();
    public static EditorProperties editorProperties = new EditorProperties();
    public static SettingsParent debugProperties;
    public static SettingsParent viewProperties;
    public static ThemeChanger themeChanger;
    public static ComplexSetting<String> changeTheme = new ComplexSetting<>(
            "Change Theme",
            DatabaseManager.tblThemes.currentSelectedTheme().themeName.getValue(),
            "Change your theme (only applies on app restart)",
            (e, label) -> {
                if (themeChanger == null) {
                    themeChanger = new ThemeChanger();
                    themeChanger.setVisible(true);
                }
                String str = DatabaseManager.tblThemes.map()
                        .get(themeChanger.getSelectedId())
                        .themeName.getValue();
                label.setText(
                        str
                );
                return str;
            },
            () -> DatabaseManager.tblThemes.currentSelectedTheme().themeName.getValue()
    );
    public static ComplexSetting<File> projectOutputFile = new ComplexSetting<>(
            "Project Output File",
            null,
            "Set the output file.",
            (e, label) -> {
                File file = FilesUtility.fileChooser(
                        (cf) -> {
                            File f2 = Settings.projectOutputFile.getValue();
                            if (f2 == null) {
                                cf.setSelectedFile(new File("new.j3p"));
                            } else {
                                cf.setCurrentDirectory(f2.getParentFile());
                                cf.setSelectedFile(f2);
                            }
                        },
                        Static.mainFrame
                );

                Function<File, File> isDir = f -> new File(f, "new.j3p");

                if (file == null) return null;
                if (!file.exists()) {
                    // Make new file.
                    String fileName = file.getName();
                    if (file.isDirectory()) file = isDir.apply(file);
                    else if (!fileName.endsWith(".j3p")) file = new  File(
                            file.getParent(),
                            fileName + ".j3p"
                    );
                    file.mkdirs();
                    try {
                        if (file.createNewFile()) {
                            label.setText(file.getAbsolutePath());
                            return file;
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (file.isDirectory())
                    file = isDir.apply(file);

                label.setText(file.getAbsolutePath());
                return file;
            },
            () -> {
                File file = Settings.projectOutputFile.getValue();
                if (file == null) return "<not set>";

                return file.getAbsolutePath();
            }
    );

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
            add(projectOutputFile);
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
            add(projectOutputFile);
        }};
    }

    @Override
    public PreferencesFrame panel() {
        if (preferencesFrame == null) preferencesFrame = new PreferencesFrame();
        return preferencesFrame;
    }
}
