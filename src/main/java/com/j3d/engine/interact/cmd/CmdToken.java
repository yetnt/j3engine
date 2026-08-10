package com.j3d.engine.interact.cmd;

import com.j3d.engine.scene.nodes.geometry.GCurve;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a token parsed from a command string.
 * Each token holds its original string input and, once parsed,
 * its interpreted type and value.
 * @see CommandParser
 * @see com.j3d.engine.interact.cmd.complete.TypingHints
 * @author Lehlogonolo Poole
 */
public class CmdToken {

    private final String input;
    private Type type;
    private Object parsedValue;

    /**
     * Constructs a new CmdToken with the given input string.
     *
     * @param input The raw string input for this token.
     */
    public CmdToken(String input) {
        this.input = input;
    }

    /**
     * Converts a list of CmdToken objects back into a single space-separated string.
     *
     * @param tokens An ArrayList of CmdToken objects.
     * @return A string representation of the tokens, joined by spaces.
     */
    public static String toStr(ArrayList<CmdToken> tokens) {
        StringBuilder sb = new StringBuilder();
        for (CmdToken token : tokens) {
            sb.append(token.getInput()).append(" ");
        }
        // remove last space
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Sets the parsed value and type of this token.
     * This method is typically called after the token's input string has been
     * successfully interpreted into a specific data type.
     *
     * @param o The parsed object value.
     * @param t The type of the parsed value.
     */
    public void parsedAs(Object o, Type t) {
        this.parsedValue = o;
        this.type = t;
    }

    /**
     * Returns the parsed value of this token.
     * The caller is responsible for casting this Object to the correct type
     * based on {@link #getType()}.
     *
     * @return The parsed value as an Object, or null if not yet parsed.
     */
    public Object getParsedValue() {
        return parsedValue;
    }

    /**
     * Returns the original raw string input of this token.
     *
     * @return The original input string.
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the interpreted type of this token.
     *
     * @return The {@link Type} enum constant representing the token's type, or null if not yet parsed.
     */
    public Type getType() {
        return type;
    }

    /**
     * Defines the possible types a command token can represent.
     * Each type is associated with a Java class and a usage string for help messages.
     */
    public enum Type {
        /** Represents the command name itself. */
        CMD_NAME(Void.class, ""),
        /** Represents a 3D vector. */
        VECTOR3(Vector3.class, "vector3"),
        ID_POINT(GPoint.class, "point"),
        ID_LINE(GLine.class, "line"),
        ID_TRI(GTri.class, "tri"),
        ID_THING(Thing.class, "thing"),
        ID_CURVE(GCurve.class, "curve"),
        COLOUR(Color.class, "#col"),
        STRING(String.class, "string"),
        INT(Integer.class, "num"),
        DOUBLE(Double.class, "num"),
        BOOL(Boolean.class, "bool"),
        TAGGED(TaggedArgValue.class, "");

        /**
         * Constructs a Type enum constant.
         *
         * @param clazz The Java class associated with this token type.
         * @param usage A short string describing the usage of this type, often used in help messages.
         */
        Type(Class<?> clazz, String usage) {
            this.clazz = clazz;
            this.usage = usage;
        }

        /** The Java class that this token type maps to. */
        private Class<?> clazz;
        /** A short string for usage hints. */
        private String usage;

        /**
         * Returns the Java class associated with this token type.
         *
         * @return The Class object.
         */
        public Class<?> getTypeClass() {
            return clazz;
        }

        /**
         * Returns a short usage string for this token type.
         *
         * @return The usage string.
         */
        public String toUsage() {
            return usage;
        }
    }

    /**
     * Returns the original input string of this token.
     * This is a convenience method, equivalent to {@link #getInput()}.
     *
     * @return The original input string.
     */
    @Override
    public String toString() {
        return input;
    }
}
