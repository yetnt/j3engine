package com.j3d.engine.math.convert;

import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.utility.generic.tuple.MutablePair;
import com.j3d.utility.generic.tuple.SamePair;

/**
 * A simple immutable class which stores an {@code X} and {@code Y} offset when converting
 * between {@link ScreenPoint} and {@link CartesianPoint} using their offset overloads.
 * @see ConversionWithOffset
 * @see ScreenPoint#toPoint(ConversionWithOffset)
 * @see CartesianPoint#toScreen(ConversionWithOffset)
 * @author Lehlogonolo Poole
 */
public class Offset extends SamePair<Integer> {

    /**
     * A constant identical to instantiating the class with 0 as both values.
     */
    public static final Offset ZERO = new Offset(0, 0);

    /**
     * Default Constructor
     * @param x The X offset
     * @param y The Y offset
     */
    public Offset(int x, int y) {
        super(x, y);
    }

    /**
     * Constructor from a same pair of integers
     * @param samePair The same pair
     */
    public Offset(SamePair<Integer> samePair) {
        super(samePair.first, samePair.second);
    }

    /**
     * Returns the X offset
     * @return The X offset component
     */
    public int getXOffset() {
        return first;
    }

    /**
     * Returns the Y offset
     * @return The Y offset component
     */
    public int getYOffset() {
        return second;
    }

    /**
     * Creates an {@link Offset} immutable class from a {@link MutablePair} who's types are both {@link Integer}
     * @param mutablePair The mutable pair of integers
     * @return A new immutable {@link Offset} instance
     * @param <T> The type held by the MutablePair, in this case it has to be an {@link Integer}
     */
    public static <T extends Integer> Offset fromMutablePair(MutablePair<T, T> mutablePair) {
        return new Offset(mutablePair.getFirst(), mutablePair.getSecond());
    }
}
