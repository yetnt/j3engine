package com.j3d.engine.geometry.geo2d;

import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.util.List;
import java.util.Objects;

/**
 * Represents a winding of three {@link GPoint}s, typically used to define a triangle or a face in 2D geometry.
 * This record provides utility methods for converting the winding to lists of points or vectors, reversing its order,
 * and generating a string representation.
 * @param first The first point in the winding.
 * @param second The second point in the winding.
 * @param third The third point in the winding.
 * @see GTri
 * @see GPoint
 * @see Vector3
 * @author Lehlogonolo Poole
 */
public record Winding(GPoint first, GPoint second, GPoint third) {

    /**
     * Converts the winding into an ordered list of its constituent {@link GPoint}s.
     * @return A {@link List} containing the first, second, and third points of the winding in order.
     */
    public List<GPoint> toList() {
        return List.of(first, second, third);
    }

    /**
     * Converts the winding into an ordered list of its constituent {@link Vector3} pivots.
     * @return A {@link List} containing the {@link Vector3} pivots of the first, second, and third points of the winding in order.
     */
    public List<Vector3> toVector3List() {
        return List.of(first.getPivot(), second.getPivot(), third.getPivot());
    }

    /**
     * Creates a new {@code Winding} with the order of its points reversed.
     * The new winding will have the third point as its first, the second point remaining as second,
     * and the first point as its third.
     * @return A new {@code Winding} instance with the points in reverse order.
     */
    public Winding reverse() {
        return new Winding(third, second, first);
    }

    public Vector3 normal() {
        Vector3 A = first.getPivot();
        Vector3 B = second.getPivot();
        Vector3 C = third.getPivot();

        Vector3 AB = B.sub(A);
        Vector3 AC = C.sub(A);

        return AB.cross(AC).normalize();
    }

    /**
     * Helper method to get a truncated string representation of a {@link GPoint}'s ID.
     * @param point The {@link GPoint} whose ID is to be truncated.
     * @return A string representing the first three characters of the point's ID.
     */
    private String id(GPoint point) {
        return point.getId().toString().substring(0, 3);
    }


    /**
     * Returns a string representation of the winding, showing the truncated IDs of its constituent points
     * in the format "[first_id -> second_id -> third_id]".
     */
    @Override
    public String toString() {
        return
                "[" + id(first) + " -> " + id(second) + " -> " + id(third) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Winding winding = (Winding) o;
        return Objects.equals(first(), winding.first()) && Objects.equals(second(), winding.second()) && Objects.equals(third(), winding.third());
    }

    @Override
    public int hashCode() {
        return Objects.hash(first(), second(), third());
    }
}
