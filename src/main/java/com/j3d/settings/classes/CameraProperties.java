package com.j3d.settings.classes;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.settings.SettingsChild;
import com.j3d.settings.types.DoubleSetting;
import com.j3d.settings.Setting;
import com.j3d.settings.SettingsParent;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class CameraProperties implements SettingsParent {
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
    public DoubleSetting orbitSensitivity = new DoubleSetting(
            "Orbit Sensitivity",
            61.56,
            "Determines how sensitive the camera's orbit control is.",
            1.0,
            100.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    );
    public DoubleSetting focalLength = new DoubleSetting(
            "Focal Length",
            37.0,
            "The focal length of the camera, affecting perspective distortion.",
            1.0,
            200.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    ).onSetValue((Double d) -> {
        Static.camera.setProjectionPlane(new Vector3(0, 0, d));
        Static.mainPanel.repaint();
        return null;
    });

    public CameraProperties() {

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
    public ArrayList<SettingsChild> getAllChildren() {
        return new ArrayList<>() {{
            add(fieldOfView);
            add(movementSpeed);
            add(orbitSensitivity);
            add(focalLength);
        }};
    }

    @Override
    public ArrayList<SettingsParent> getChildSettingsFolder() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Setting<?>> getChildSettings() {
        return new ArrayList<>() {{
            add(fieldOfView);
            add(movementSpeed);
            add(orbitSensitivity);
            add(focalLength);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
