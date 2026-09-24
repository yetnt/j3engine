package com.j3d.engine.math.convert;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.Dim;
import com.j3d.engine.math.ScreenPoint;

/**
 * A specialisation of the {@link Conversion} record which stores the conversion required to turn a
 * {@link CartesianPoint} into a {@link ScreenPoint} and vice versa.
 * <p>
 *     Unlike {@link Conversion}, this stores an extra field, an {@link Offset} field such as to offset
 *     all coordinates by a given delta.
 * </p>
 * @param scale The scale factor such that a unit length is not a pixel. e.g. a value of 10.0 means a
 *              CartesianPoint unit length of 1, is about 10 pixels long.
 * @param size The dimension of the screen which the points belong to. This is used to determine the centre for
 *             converting to and between {@link CartesianPoint} and knowing the maximum height and length
 *             of a {@link ScreenPoint}
 * @param offset The offset X and Y components to offset all points by. Generally speaking no conversion requires
 *               using offsetting unless they want panning the 2d view.
 * @see ScreenPoint
 * @see CartesianPoint
 * @see Conversion
 * @author Lehlogonolo Poole
 */
public record ConversionWithOffset(
        double scale,
        Dim size,
        Offset offset
) {

    /**
     * Converts the given {@link Conversion} object into a {@link ConversionWithOffset} instance
     * by passing an offset value of {@link Offset#ZERO}, which is identical to it's original class
     * as no offset is applied.
     * @param conversion The Conversion properties to convert
     * @return A ConversioNWithOffset which is functionally identical to the original
     */
    public static ConversionWithOffset from(Conversion conversion) {
        return new ConversionWithOffset(
                conversion.scale(),
                conversion.size(),
                Offset.ZERO
        );
    }
}
