package com.j3d.engine.math.convert;

import com.j3d.StaticRefs;
import com.j3d.engine.math.*;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.engine.EngineFrame;

/**
 * Generic conversion record which stores the conversion required to turn a
 * {@link CartesianPoint} into a {@link ScreenPoint} and vice versa.
 * <p>
 *     This is the simplified record which does not have an offset. See {@link ConversionWithOffset}
 * </p>
 * @param scale The scale factor such that a unit length is not a pixel. e.g. a value of 10.0 means a
 *              CartesianPoint unit length of 1, is about 10 pixels long.
 * @param size The dimension of the screen which the points belong to. This is used to determine the centre for
 *             converting to and between {@link CartesianPoint} and knowing the maximum height and length
 *             of a {@link ScreenPoint}
 * @see ScreenPoint
 * @see CartesianPoint
 * @see ConversionWithOffset
 * @author Lehlogonolo Poole
 */
public record Conversion(
        double scale,
        Dim size
) {
    /**
     * The global conversion properties specifically for the main draw panel {@link EngineFrame#getDrawPanel()}
     * <p>
     *     These are defined as:
     *     <ul>
     *         <li>The setting the user defines as the scale within {@link Settings#sceneProperties#scale}</li>
     *         <li>The actual size of {@link EngineFrame#getDrawPanel()}</li>
     *     </ul>
     * </p>
     * @return The Converison properties of the main draw panel.
     */
    public static Conversion global() {
        return new Conversion(
                Settings.sceneProperties.scale.getValue(),
                StaticRefs.getSceneManager().screenSize
        );
    }
}
