package com.j3d.gen.guide;

import java.awt.*;

/**
 * Positioning bitmasks for {@link GuideInfo} and {@link GuidePanelAdapter}.
 * <p>
 *     These are just quick positioning rather than having to do coordinate calculations yourself.
 *     Used in {@link GuideInfo#addCompAt(GuidePanelAdapter, Component, int)} and
 *     {@link GuideInfo#addCompAt(GuidePanelAdapter, Component, int, int, int)} since offsets might still
 *     be needed.
 * </p>
 * @see GuideInfo
 * @see GuidePanelAdapter
 * @author Lehlogonolo Poole
 */
public class Anchor {
    /**
     * Represents the center anchor position.
     */
    public static final int CENTRE = 0b10000;
    /**
     * Represents the north (top) anchor position.
     */
    public static final int NORTH  = 0b01000;
    /**
     * Represents the south (bottom) anchor position.
     */
    public static final int SOUTH  = 0b00100;
    /**
     * Represents the east (right) anchor position.
     */
    public static final int EAST   = 0b00010;
    /**
     * Represents the west (left) anchor position.
     */
    public static final int WEST   = 0b00001;

    /**
     * Checks if a given combined anchor value contains a specific anchor bit.
     * This is useful for determining if a particular direction or center is part of the
     * specified positioning.
     *
     * @param value The combined anchor value (e.g., {@code NORTH | EAST}).
     * @param bit   The specific anchor bit to check for (e.g., {@code NORTH}).
     * @return {@code true} if the {@code value} contains the {@code bit}, {@code false} otherwise.
     */
    public static boolean has(int value, int bit) {
        return (value & bit) != 0;
    }

    /**
     * Checks if the given combined anchor value includes either the {@link #NORTH}
     * or {@link #SOUTH} anchor bits.
     *
     * @param value The combined anchor value.
     * @return {@code true} if {@link #NORTH} or {@link #SOUTH} is present, {@code false} otherwise.
     */
    public static boolean hasVertical(int value) {
        return has(value, NORTH) || has(value, SOUTH);
    }

    /**
     * Checks if the given combined anchor value includes either the {@link #EAST}
     * or {@link #WEST} anchor bits.
     *
     * @param value The combined anchor value.
     * @return {@code true} if {@link #EAST} or {@link #WEST} is present, {@code false} otherwise.
     */
    public static boolean hasHorizontal(int value) {
        return has(value, EAST) ||  has(value, WEST);
    }
}
