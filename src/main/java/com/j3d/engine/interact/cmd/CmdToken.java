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

public class CmdToken {

    private final String input;
    private Type type;
    private Object parsedValue;

    public CmdToken(String input) {
        this.input = input;
    }

    public static String toStr(ArrayList<CmdToken> tokens) {
        StringBuilder sb = new StringBuilder();
        for (CmdToken token : tokens) {
            sb.append(token.getInput()).append(" ");
        }
        // remove last space
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void parsedAs(Object o, Type t) {
        this.parsedValue = o;
        this.type = t;
    }

    public Object getParsedValue() {
        return parsedValue;
    }

    public String getInput() {
        return input;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CMD_NAME(Void.class, ""),
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

        Type(Class<?> clazz, String usage) {
            this.clazz = clazz;
            this.usage = usage;
        }

        private Class<?> clazz;
        private String usage;

        public Class<?> getTypeClass() {
            return clazz;
        }

        public String toUsage() {
            return usage;
        }
    }

    @Override
    public String toString() {
        return input;
    }
}
