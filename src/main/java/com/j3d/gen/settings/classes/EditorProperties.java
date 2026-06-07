package com.j3d.gen.settings.classes;

import com.j3d.gen.settings.SettingsChild;
import com.j3d.gen.settings.types.DoubleSetting;
import com.j3d.gen.settings.Setting;
import com.j3d.gen.settings.SettingsParent;
import com.j3d.gen.settings.types.IntSetting;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class EditorProperties implements SettingsParent {

    public IntSetting handleSize = new IntSetting(
            "Transform Handle Size",
            10,
            "The size of the handle for transform commands",
            1,
            100
    );
    public DoubleSetting handleDist = new DoubleSetting(
            "Transform Handle Distance",
            10.0,
            "How far away the handles are from the transform origin",
            1,
            100
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    );


    public EditorProperties() {

    }

    @Override
    public String getDescription() {
        return "Properties related to the camera's behavior and perspective.";
    }

    @Override
    public String getName() {
        return "Editor Properties";
    }

    @Override
    public ArrayList<SettingsChild> getAllChildren() {
        return new ArrayList<>() {{
            add(handleSize);
            add(handleDist);
        }};
    }

    @Override
    public ArrayList<SettingsParent> getChildSettingsFolder() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Setting<?>> getChildSettings() {
        return new ArrayList<>() {{
            add(handleSize);
            add(handleDist);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
