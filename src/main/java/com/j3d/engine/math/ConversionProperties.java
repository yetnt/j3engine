package com.j3d.engine.math;

import com.j3d.StaticRefs;
import com.j3d.gen.settings.Settings;

public record ConversionProperties(
        double scale,
        Dim size
) {
    public static ConversionProperties global() {
        return new ConversionProperties(
                Settings.sceneProperties.scale.getValue(),
                StaticRefs.getSceneManager().screenSize
        );
    }
}
