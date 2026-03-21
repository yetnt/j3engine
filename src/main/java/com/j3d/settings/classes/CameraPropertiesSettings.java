package com.j3d.settings.classes;

import com.j3d.settings.types.DoubleSetting;
import com.j3d.settings.Setting;
import com.j3d.settings.SettingsParent;
import com.j3d.ui.settings.SettingsParentPanel;

public class CameraPropertiesSettings implements SettingsParent {
    public DoubleSetting fieldOfView = new DoubleSetting(
            "Field of View",
            2.0,
            "The field of view of the camera, in degrees.",
            1.0,
            200.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
            );
    public DoubleSetting movementSpeed = new DoubleSetting(
            "Movement Speed",
            0.5,
            "Factor by which the camera moves through the world.",
            0.01,
            5.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    );

    public CameraPropertiesSettings() {

    }

    @Override
    public String getDescription() {
        return "Properties related to the camera's behavior and perspective.";
    }

    @Override
    public String getName() {
        return "Camera Properties";
    }

    @Override
    public java.util.ArrayList<com.j3d.settings.SettingsChild> getAllChildren() {
        return new java.util.ArrayList<>() {{
            add(fieldOfView);
            add(movementSpeed);
        }};
    }

    @Override
    public java.util.ArrayList<SettingsParent> getChildSettingsFolder() {
        return new java.util.ArrayList<>();
    }

    @Override
    public java.util.ArrayList<Setting<?>> getChildSettings() {
        return new java.util.ArrayList<>() {{
            add(fieldOfView);
            add(movementSpeed);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
