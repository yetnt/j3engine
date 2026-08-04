package com.j3d.engine.scene.nodes.geometry;

/**
 * Defines the main registry for checking for GObjects, primarily used by these subsystems:
 * <ul>
 *     <li>{@link com.j3d.engine.interact.cmd.CommandParser}/{@link com.j3d.engine.interact.cmd.args.TaggedArgUtil}
 *     /{@link com.j3d.engine.interact.cmd.base.Command}/{@link com.j3d.engine.interact.cmd.complete.TypingHints}
 *     for command parsing, usage parsing, typed hints and etc
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties.PropertiesUI} for showing properties of these objects
 *     </li>
 *     <li>
 *         And overall just decentralisation of making these objects.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
public enum GObjectRegistry {
    /**
     * a point.
     */
    POINT(GPoint.class, "point"),
    /**
     * a curve
     */
    LINE(GLine.class, "line"),
    /**
     * a triangle
     */
    TRI(GTri.class, "tri"),
    /**
     * a curve
     */
    CURVE(GCurve.class, "curve");

    /**
     * The actual class
     */
    private final Class<? extends GObject> clazz;
    /**
     * The simple name, usually the exact same name used in command usage strings and by the properties
     * menu.
     */
    private final String simpleName;

    GObjectRegistry(Class<? extends GObject> clazz, String simpleName) {
        this.clazz = clazz;
        this.simpleName = simpleName;
    }

    public Class<? extends GObject> getClazz() {
        return clazz;
    }

    /**
     * Returns the simple name of the GObject, typically used in command usage strings and property menus.
     * @return The simple name of the GObject.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Retrieves a {@code GObjectRegistry} entry based on its associated {@link GObject} class.
     * @param clazz The class to look up.
     * @return The corresponding {@code GObjectRegistry} entry, or {@code null} if not found.
     */
    public static GObjectRegistry fromClass(Class<?> clazz) {
        for (GObjectRegistry registry : values()) {
            if (registry.getClazz().equals(clazz)) {
                return registry;
            }
        }
        return null;
    }

    /**
     * Checks if the given class is one of the registered {@link GObject} types.
     * @param clazz The class to check.
     * @return {@code true} if the class is a registered GObject, {@code false} otherwise.
     */
    public static boolean isGObject(Class<?> clazz) {
        for (GObjectRegistry registry : values()) {
            if (registry.getClazz().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the simple name for a given {@link GObject} class.
     * @param clazz The class for which to get the simple name.
     * @return The simple name string, or {@code null} if the class is not a registered GObject.
     */
    public static String getSimpleName(Class<?> clazz) {
        for (GObjectRegistry registry : values()) {
            if (registry.getClazz().equals(clazz)) {
                return registry.getSimpleName();
            }
        }
        return null;
    }

    /**
     * Provides a type-safe way to return a specific value based on the runtime type of a {@link GObject} instance.
     * This method acts as a type-dispatch mechanism for registered GObjects.
     * @param obj The object to check its type.
     * @param defaultReturn The value to return if {@code obj} does not match any known GObject type.
     * @param point The value to return if {@code obj} is a {@link GPoint}.
     * @param line The value to return if {@code obj} is a {@link GLine}.
     * @param tri The value to return if {@code obj} is a {@link GTri}.
     * @param curve The value to return if {@code obj} is a {@link GCurve}.
     * @param <T> The type of the return value.
     * @return The value corresponding to the type of {@code obj}, or {@code defaultReturn} if no match.
     */
    public static <T> T expectedObjs(Object obj, T defaultReturn, T point, T line, T tri, T curve) {
        return switch (obj) {
            case GPoint p -> point;
            case GLine l -> line;
            case GTri t -> tri;
            case GCurve c -> curve;
            default -> defaultReturn;
        };
    }

    /**
     * Performs a fuzzy match to check if the input string contains the simple name of any registered GObject.
     * @param input The string to check against registered GObject simple names.
     * @return {@code true} if the input string contains any GObject's simple name, {@code false} otherwise.
     */
    public static boolean fuzzyMatch(String input) {
        for (GObjectRegistry registry : values()) {
            if (input.contains(registry.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given string matches a specific pattern derived from the simple name of a registered GObject.
     * The pattern is formed by repeating the first two characters of the simple name twice (e.g., "curve" -> "cucu", "line" -> "lili").
     * This is primarily used for typing hints in command line interfaces.
     * @param letters The string to match.
     * @return {@code true} if the string matches the pattern for any registered GObject, {@code false} otherwise.
     * @see com.j3d.engine.interact.cmd.complete.TypingHints
     */
    public static boolean tHintsUsageStringTypeMatch(String letters) {
        for (GObjectRegistry registry : values()) {
            // input like "curve" would be "cucu" or "line" "lili"
            if (registry.getSimpleName().substring(0, 2).repeat(2).equals(letters)) {
                return true;
            }
        }
        return false;
    }
}
