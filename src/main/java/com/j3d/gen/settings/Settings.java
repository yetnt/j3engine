package com.j3d.gen.settings;

import com.j3d.StaticRefs;
import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.gen.settings.classes.EditorProperties;
import com.j3d.gen.settings.classes.SceneProperties;
import com.j3d.gen.settings.types.ComplexSetting;
import com.j3d.storage.files.FilesUtility;
import com.j3d.ui.settings.PreferencesFrame;
import com.j3d.ui.settings.popouts.ThemeChanger;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.theme.ThemeEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

public class Settings implements SettingsParent {

    private static boolean savedPreferences = true;
    public static boolean isPreferencesSaved() {
        return savedPreferences;
    }

    public static void setPreferencesSaved(boolean savedPreferences) {
        Settings.savedPreferences = savedPreferences;
        if (!savedPreferences && preferencesFrame != null) {
            preferencesFrame.onChange();
        }
    }

    public static PreferencesFrame preferencesFrame;
    public static CameraProperties cameraProperties = new CameraProperties();
    public static SceneProperties sceneProperties = new SceneProperties();
    public static EditorProperties editorProperties = new EditorProperties();
    public static ThemeChanger themeChanger;
    public static ComplexSetting<ThemeEntry> changeTheme = new ComplexSetting<>(
            "Theme",
            J3DTheme.getCurrentLoadedTheme(),
            "Change your theme",
            (e, label, panel) -> {
                if (themeChanger == null) {
                    themeChanger = new ThemeChanger(panel);
                    themeChanger.setVisible(true);
                }
                ThemeEntry str = J3DTheme.getCurrentLoadedTheme();
                label.setText(str.getName());
                return str;
            },
            () -> {
                ThemeEntry e = J3DTheme.getCurrentLoadedTheme();
                return e.getName();
            }
    ).serializable(
            (t) -> {
                return
                        (t.getThemeType() == ThemeEntry.ThemeType.USER_LOADED ? "user" : "engine")
                        + ":\"" + t.getName() + "\"";
            },
            (s) -> {
                String s2 = s.split(":")[1];
                String s3 = s2.substring(1, s2.length()-1);
                ThemeEntry e = ThemeChanger.fromTitle(s3, StaticRefs.getEngineFiles().themeFiles::loadAllEntries);
                return e;
            }
    );
    public static ComplexSetting<File> projectOutputFile = new ComplexSetting<>(
            "Project Output File",
            null,
            "Set the output file.",
            (e, label, ignored) -> {
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
                        StaticRefs.getMainFrame()
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
                        StaticRefs.getErrs().handle(
                                new PrefsGenException(
                                        "Could not create new project file: " + file.getAbsolutePath(),
                                        ex
                                ).code(100)
                        );
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
        read();
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

    /**
     * Recursively retrieves all children settings, including those nested within {@link SettingsParent} objects.
     * This method flattens the hierarchy into a single stream of {@link SettingsChild} objects.
     *
     * @param sc The starting {@link SettingsChild} from which to begin the recursive retrieval.
     * @return A {@link Stream} of all {@link SettingsChild} objects found recursively.
     */
    public static Stream<SettingsChild> getChildrenRecursive(SettingsChild sc) {
        if (sc instanceof SettingsParent settingsParent) {
            return settingsParent
                    .getAllChildren()
                    .stream()
                    .flatMap(Settings::getChildrenRecursive);
        } else {
            return Stream.of(sc);
        }
    }

    @Override
    public PreferencesFrame panel() {
        if (preferencesFrame == null) preferencesFrame = new PreferencesFrame();
        return preferencesFrame;
    }

    public void clearState() {
        getChildrenRecursive(this)
                .map(s -> (Setting<?>)s)
                .forEach(s -> {
                    s.detachAll();
                    s.setValue(s.getDefaultValue());
                });
    }

    public void toDefault() {
        getChildrenRecursive(this)
                .map(s -> (Setting<?>)s)
                .forEach(s -> s.setValue(s.getDefaultValue()));
    }

    public void write() {
        StaticRefs.getEngineFiles().preferencesFile.write(
                serialize()
        );
        setPreferencesSaved(true);
    }

    public void read() {
        deserialize(StaticRefs.getEngineFiles().preferencesFile.read());
        setPreferencesSaved(true);
    }
}
