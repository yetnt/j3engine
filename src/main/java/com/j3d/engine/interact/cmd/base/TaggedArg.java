package com.j3d.engine.interact.cmd.base;

import java.util.HashMap;
import java.util.Map;

/**
 * TaggedArg is a class that represents an argument with specific tags.
 * Each tag has a predefined type, and the accepted tags are defined in the acceptedTags map.
 * This class implements the Argument interface.
 * <p>
 *     Unlike other arguments, where arguments are defined by their type (e.g., String, Integer),
 *     TaggedArg uses a set of predefined tags, each associated with a specific type.
 *     A user can specify one or more of these tags when using a command that accepts TaggedArg.
 *     For example, a command might accept a TaggedArg with tags "type" (String) and "x" (Double).
 *     The user would then provide values for these tags when invoking the command:
 *     <pre> command type:"some String" x:10.5 </pre>
 * </p>
 * <p>
 *     The accepted tags and their types are as follows:
 *     <ul>
 *         <li>{@code type: String}: Any type primitive such as "point" "vector3" "line" ...etc</li>
 *         <li>{@code layer: String}: The layer the object belongs to, e.g., "default", "background", "foreground"</li>
 *         <li>{@code thing: String}: The thing's ID the object is associated with. </li>
 *         <li>{@code id: String}: A unique identifier for the object, e.g., "object123"</li>
 *         <li>{@code x: Double}: The x-coordinate of the object in 3D space</li>
 *         <li>{@code y: Double}: The y-coordinate of the object in 3D space</li>
 *         <li>{@code z: Double}: The z-coordinate of the object in 3D space</li>
 *         <li>{@code custom: String}: A custom tag for any additional information</li>
 *         <li>{@code behindCam: Boolean}: Whether the object is behind the camera (true/false)</li>
 *         <li>{@code rand: Boolean}: Idk what it does. It's random. Set it to true and find out (true/false)</li>
 *    </ul>
 *</p>
 *
 */
public class TaggedArg implements Argument {
    public static final HashMap<String, Class> acceptedTags = new HashMap<>(Map.of(
            "type", String.class,
            "layer", String.class,
            "thing", String.class,
            "id", String.class,
            "x", Double.class,
            "y", Double.class,
            "z", Double.class,
            "custom", String.class,
            "behindCam", Boolean.class,
            "rand", Boolean.class
            ));

    private final String argName;
    private final String description;
    private final boolean optional;

    public TaggedArg(String argName, String description, boolean optional) {
        this.argName = argName;
        this.description = description;
        this.optional = optional;
    }

    @Override
    public String getName() {
        return argName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
