package com.j3d.settings.classes;

import com.j3d.engine.draw.tris.TriangleSortMethod;
import com.j3d.settings.Setting;
import com.j3d.settings.SettingsChild;
import com.j3d.settings.SettingsParent;
import com.j3d.settings.types.DoubleSetting;
import com.j3d.settings.types.EnumSetting;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class ScenePropertiesSettings implements SettingsParent {
    DoubleSetting scale = new DoubleSetting(
            "Axis Scale Factor",
            10.0,
            "Factor to scale each 3d point's projected 2d axis.",
            1.0,
            100.0
    ).setValues(
            Double::intValue,
            i -> i * 1.0,
            1
    );
    EnumSetting<TriangleSortMethod> triangleSortMethod = new EnumSetting<>(
            "Triangle Sort Method",
            TriangleSortMethod.CAMDISTSORT,
            "The method the renderer should make use of to sort triangles.",
            TriangleSortMethod.values()
    );

    public ScenePropertiesSettings() {

    }

    @Override
    public String getDescription() {
        return "Scene properties";
    }

    @Override
    public String getName() {
        return "Scene Properties";
    }

    @Override
    public ArrayList<Setting<?>> getChildSettings() {
        return new ArrayList<>() {{
            add(scale);
            add(triangleSortMethod);
        }};
    }

    @Override
    public ArrayList<SettingsParent> getChildSettingsFolder() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<SettingsChild> getAllChildren() {
        return new ArrayList<>() {{
            add(scale);
            add(triangleSortMethod);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
