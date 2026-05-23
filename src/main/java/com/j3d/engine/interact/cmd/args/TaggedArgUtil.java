package com.j3d.engine.interact.cmd.args;

import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.util.SafeJLabel;
import com.jaiva.utils.Find;
import com.jaiva.utils.Pair;
import com.jaiva.utils.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A utility class for parsing and managing "tagged arguments" within the command system.
 * <p>
 *      Tagged arguments are a special type of key-value pair (e.g., {@code x:10.5}, {@code type:"point"})
 *      that provide optional, out-of-order parameters to commands. This class defines the globally
 *      accepted tags and provides the logic to parse them from a raw string.
 * </p>
 *
 * <h3>The Role of Tagged Arguments</h3>
 * Unlike standard positional arguments, tagged arguments are not defined as part of a
 * command's formal argument list. Instead, the {@link CommandParser} identifies and extracts
 * them from the user's input string, providing them to the command in a separate collection.
 * <p>
 *      This means tagged arguments can appear anywhere in the command string after the command name,
 *      interspersed with normal arguments, without affecting the order of the normal arguments.
 *      For example, the following two inputs are parsed identically:
 * </p>
 * <pre>{@code
 * > point at x:4 10 y:5
 * > point y:5 at x:4 10
 *
 * // Both result in:
 * Command: "point"
 * Arguments: ["at", 10]
 * TaggedArgs: [x:4, y:5]
 * }</pre>
 * By default, all commands can accept a variadic number of tagged arguments, though a command's
 * specific {@code run} method determines which ones it actually uses.
 *
 * <h3>Accepted Tags</h3>
 * This utility defines a static map of all valid tags and their expected data types:
 * <ul>
 *     <li>{@code type: String} - e.g., "point", "line"</li>
 *     <li>{@code layer: String} - e.g., "default", "background"</li>
 *     <li>{@code x, y, z: Double} - Coordinates</li>
 *     <li>{@code v: Vector3} - e.g., (0, 0, 1)</li>
 *     <li>...and others for IDs, booleans, and comparisons.</li>
 * </ul>
 *
 * @author Lehlogonolo Poole
 * @see Command
 * @see Argument
 * @see TaggedArgValue
 * @see CommandParser
 * @see Command#run(SafeJLabel, String, Object[], ArrayList)
 */
public class TaggedArgUtil {
    /**
     * A map defining all globally accepted tags and their corresponding typed {@link TaggedArgValue} shells.
     */
    public static final HashMap<String, TaggedArgValue<?>> acceptedTags = new HashMap<>();
    static {
        acceptedTags.put("type", new TaggedArgValue<String>(String.class).setName("type"));
        acceptedTags.put("layer", new TaggedArgValue<String>(String.class).setName("layer"));
        acceptedTags.put("thing", new TaggedArgValue<String>(String.class).setName("thing"));
        acceptedTags.put("id", new TaggedArgValue<UUID>(UUID.class).setName("id"));
        acceptedTags.put("x", new TaggedArgValue<Double>(Double.class).setName("x"));
        acceptedTags.put("y", new TaggedArgValue<Double>(Double.class).setName("y"));
        acceptedTags.put("z", new TaggedArgValue<Double>(Double.class).setName("z"));
        acceptedTags.put("custom", new TaggedArgValue<String>(String.class).setName("custom"));
        acceptedTags.put("behindCam", new TaggedArgValue<Boolean>(Boolean.class).setName("behindCam"));
        acceptedTags.put("rand", new TaggedArgValue<Boolean>(Boolean.class).setName("rand"));
        acceptedTags.put("v", new TaggedArgValue<Vector3>(Vector3.class).setName("v"));
        acceptedTags.put("a", new TaggedArgValue<Vector3>(Vector3.class).setName("a"));
        acceptedTags.put("b", new TaggedArgValue<Vector3>(Vector3.class).setName("b"));
        acceptedTags.put("lessThan", new TaggedArgValue<Double>(Double.class).setName("lessThan"));
        acceptedTags.put("greaterThan", new TaggedArgValue<Double>(Double.class).setName("greaterThan"));
        acceptedTags.put("equalTo", new TaggedArgValue<Double>(Double.class).setName("equal"));
        acceptedTags.put("notEqualTo", new TaggedArgValue<Double>(Double.class).setName("notEqual"));
    }

    /**
     * Parses a single string segment to identify and create a tagged argument.
     * <p>
     * This method attempts to parse a string in the format "key:value" or "key=value".
     * It validates the key against the {@link #acceptedTags} map and attempts to parse
     * the value into the expected type (e.g., number, boolean, quoted string, Vector3).
     *
     * @param accumulator The raw string segment to parse.
     * @param errorToLabel A boolean to log errors to the SafeJLabel
     * @param label       A {@link SafeJLabel} for providing user feedback on parsing errors. If {@code errorToLabel} is false, then this
     *                    can have a null input.
     * @return A populated {@link TaggedArgValue} instance. If parsing fails, the instance
     *         will be marked as an error or empty.
     */
    public static TaggedArgValue<?> parse(String accumulator, boolean errorToLabel, SafeJLabel label) {
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
            if (errorToLabel) label.setText("Improper tagged argument syntax. No name or value?");
            return taggedArgValue.error();
        };
        if (!acceptedTags.containsKey(name)) {
            if (errorToLabel) label.setText("Invalid tagged argument used: " + name);
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
                            try {
                                valueObject = UUID.fromString(value);
                            } catch (IllegalArgumentException l) {
                                if (errorToLabel) label.setText("Invalid tagged argument value: " + value + " (If text, wrap in quotes.)");
                                return taggedArgValue.error();
                            }
                        }
                    }
                }
            }
        } else if (valueQuotePairs.size() == 1) {

            valueObject = value.substring(1, value.length() - 1);

        } else if (braceQuotePairs.first.size() == 1) {

            if (!value.contains(",")) {
                if (errorToLabel) label.setText("Invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) is required when using braces)");
                return taggedArgValue.error();
            }

            String valuesStr = value.substring(1, value.length() - 1);
            String[] values = valuesStr.split(",");

            if (values.length != 3) {
                if (errorToLabel) label.setText("Invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) is required when using braces)");
                return taggedArgValue.error();
            }

            try {
                double x = Double.parseDouble(values[0].trim());
                double y = Double.parseDouble(values[1].trim());
                double z = Double.parseDouble(values[2].trim());
                valueObject = new Vector3(x, y, z);
            } catch (NumberFormatException e) {
                if (errorToLabel) label.setText("invalid tagged argument value: " + value + " (A Vector3 definition (x, y, z) where x, y, and z are numbers is required)");
                return taggedArgValue.error();
            }

        } else {
            // Both arrays contain something or more than 1 thing
            if (errorToLabel) label.setText("Malformed tagged argument value: " + value);
        }

        return acceptedTags.get(name).copy(valueObject, errorToLabel, label);
    }
}
