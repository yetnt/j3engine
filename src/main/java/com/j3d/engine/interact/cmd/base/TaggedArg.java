package com.j3d.engine.interact.cmd.base;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.jaiva.utils.Find;
import com.jaiva.utils.Pair;
import com.jaiva.utils.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaggedArg is an argument that expects a single key-value pair.
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
 *         <li>{@code v: Vector3}: A Vector3 string converted to object e.g. (0, 0, 1) </li>
 *    </ul>
 *</p>
 *
 */
public class TaggedArg extends TypedArg {
    public static final HashMap<String, TaggedArgValue<?>> acceptedTags = new HashMap<>();
    static {
        acceptedTags.put("type", new TaggedArgValue<String>(String.class).setName("type"));
        acceptedTags.put("layer", new TaggedArgValue<String>(String.class).setName("layer"));
        acceptedTags.put("thing", new TaggedArgValue<String>(String.class).setName("thing"));
        acceptedTags.put("id", new TaggedArgValue<String>(String.class).setName("id"));
        acceptedTags.put("x", new TaggedArgValue<Double>(Double.class).setName("x"));
        acceptedTags.put("y", new TaggedArgValue<Double>(Double.class).setName("y"));
        acceptedTags.put("z", new TaggedArgValue<Double>(Double.class).setName("z"));
        acceptedTags.put("custom", new TaggedArgValue<String>(String.class).setName("custom"));
        acceptedTags.put("behindCam", new TaggedArgValue<Boolean>(Boolean.class).setName("behindCam"));
        acceptedTags.put("rand", new TaggedArgValue<Boolean>(Boolean.class).setName("rand"));
        acceptedTags.put("v", new TaggedArgValue<Vector3>(Vector3.class).setName("v"));
    }

    public TaggedArg(String argName, String description, boolean optional) {
        super(argName, description, optional, TaggedArgValue.class);
    }

    public static TaggedArgValue<?> parse(String accumulator, SafeJLabel label) {
        accumulator = accumulator.trim();
        TaggedArgValue<Void> taggedArgValue = new TaggedArgValue<>(null);
        ArrayList<Character> disallowed = new ArrayList<>(List.of(
                '(', '[', '"', '\'', ']', ')', '#', ':', '='
        ));
        if (disallowed.contains(accumulator.charAt(0))) return taggedArgValue; // return empty.

        StringBuilder tagName = new StringBuilder();
        StringBuilder tagValue = new StringBuilder();
        boolean encounteredSeparator = false;

        for (char c : accumulator.toCharArray()) {
            if ((c == ':' || c == '=') && !encounteredSeparator) {
                encounteredSeparator = true;
                continue;
            }
            if (encounteredSeparator) tagValue.append(c);
            else tagName.append(c);
        }

        String name = tagName.toString();
        String value = tagValue.toString();

        if (!encounteredSeparator) return taggedArgValue; // Never encountered separator. Return
        if (name.isEmpty() || value.isEmpty()) {
            label.setText("Improper tagged argument syntax. No name or value?");
            return taggedArgValue.error();
        };
        if (!acceptedTags.containsKey(name)) {
            label.setText("Invalid tagged argument used: " + name);
            return taggedArgValue.error();
        }

        Object valueObject = null;

        ArrayList<Pair<Integer>> valueQuotePairs = Find.quotationPairs(value);
        Tuple2<ArrayList<Pair<Integer>>, ArrayList<Tuple2<Integer, Character>>> braceQuotePairs = Find.bracePairs(value);

        if (valueQuotePairs.isEmpty() && braceQuotePairs.first.isEmpty()) {
            try {
                valueObject = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                    valueObject = Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    switch (value.toLowerCase()) {
                        case "yebo", "yes", "y", "true", "t" -> valueObject = true;
                        case "aowa", "no", "n", "false", "f" -> valueObject = false;
                        default -> {
                            label.setText("Invalid tagged argument value: " + value + " (If text, wrap in quotes.)");
                            return taggedArgValue.error();
                        }
                    }
                }
            }
        } else if (valueQuotePairs.size() == 1) {

            valueObject = value.substring(1, value.length() - 1);

        } else if (braceQuotePairs.first.size() == 1) {

            if (!value.contains(",")) {
                label.setText("Invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) is required when using braces)");
                return taggedArgValue.error();
            }

            String valuesStr = value.substring(1, value.length() - 1);
            String[] values = valuesStr.split(",");

            if (values.length != 3) {
                label.setText("Invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) is required when using braces)");
                return taggedArgValue.error();
            }

            try {
                double x = Double.parseDouble(values[0].trim());
                double y = Double.parseDouble(values[1].trim());
                double z = Double.parseDouble(values[2].trim());
                valueObject = new Vector3(x, y, z);
            } catch (NumberFormatException e) {
                label.setText("invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) where x, y, and z are numbers is required)");
                return taggedArgValue.error();
            }

        } else {
            // Both arrays contain something or more than 1 thing
            label.setText("Malformed tagged argument value: " + value);
        }

        return acceptedTags.get(name).copy(valueObject, label);
    }
}
