package com.j3d.gen.settings.classes;

import com.j3d.StaticRefs;
import com.j3d.gen.settings.SettingsChild;
import com.j3d.gen.settings.types.DoubleSetting;
import com.j3d.gen.settings.SettingsParent;
import com.j3d.gen.settings.types.IntSetting;
import com.j3d.ui.settings.SettingsParentPanel;

import java.util.ArrayList;

public class CameraProperties implements SettingsParent {
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
            40.0,
            "Determines how sensitive the camera's orbit control is.",
            1.0,
            100.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    );
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
    public DoubleSetting focalLength = new DoubleSetting(
            "Focal Length",
            37.0,
            "The focal length of the camera, affecting perspective distortion.",
            0.001,
            200.0
    ).setValues(
            d -> (int)(d * 100),
            i -> i/100.0,
            0.01
    ).onSetValue((Double d) -> {
        StaticRefs.getCamera().setFocalLength(d);
        StaticRefs.getMainPanel().repaint();
        return null;
    });
    public IntSetting nearZeroProjectionPower = new IntSetting(
            "Near Zero Projection Power",
            18,
            "When the dz of a point approaches less than 10^-x, it's clamped to 10^-6",
            6,
            30
    );

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
            add(nearZeroProjectionPower);
        }};
    }

    @Override
    public SettingsParentPanel panel() {
        return new SettingsParentPanel(this).addChildren(getAllChildren());
    }
}
