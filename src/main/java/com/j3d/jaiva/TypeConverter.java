package com.j3d.jaiva;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GObjectRegistry;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.j3d.jaiva.packs.getters.J3DGetterException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TypeConverter {
    private static final ArrayList<Class<?>> allowedClasses = new ArrayList<>(
            List.of(
                    UUID.class, Vector3.class, Color.class

            )
    );

    static {
        allowedClasses.addAll(GObjectRegistry.getClasses());
    }

    public static ArrayList<Class<?>> getAllowedClasses() {
        return allowedClasses;
    }

    public TypeConverter() {}

    public static ArrayList<?> expectArr(Object arr) {
        if (!(arr instanceof ArrayList<?> a))
            throw new IllegalArgumentException(
                    "Expected an array. Instead got " + arr.getClass()
            );
        return a;
    }

    public static <T> T expectType(Object a, Class<T> expected) {
        if (!(expected.isInstance(a)))
            throw new IllegalArgumentException(
                    "Expected " + expected.getName() + " instead of " + a.getClass()
            );
        return expected.cast(a);
    }

    public static EngineObject toObject(Object object) {
        // rigorously, check
        return switch (object) {
            case GObject gObject -> gObject.toObject();
            case UUID id -> convertUUID(id);
            case Color color -> convertColor(color);
            case Vector3 vector3 -> vector3.toObject();
            default -> throw new IllegalArgumentException("Unknown object type: " + object);
        };
    }

    public static void expectObjectType(EngineObject object, EngineObject.Type type, GettersPack.CallProperties callProperties) throws J3DGetterException {
        if (object.getType() == type) return;
        throw new J3DGetterException(
                callProperties.scope(),
                type,
                object.getType(),
                callProperties.call().lineNumber
        );
    }

    public static EngineObject fromArr(ArrayList<?> objects) {
        if (objects.size() != 2) throw  new IllegalArgumentException(
                "Cannot convert " + objects.size() + " objects."
        );
        if (objects.getFirst() instanceof ArrayList<?> s) {
            if (s.size() != 2) throw new IllegalArgumentException("Cannot convert " + s.size() + " objects.");

            return EngineObject.from(objects);
        }

        throw new IllegalArgumentException("Cannot convert " + objects.size() + " objects.");
    }

    public static Object from(EngineObject engineObject) {
        return switch (EngineObject.Type.valueOf(engineObject.getEngineProperties().getLast())) {
            case VECTOR3 -> Vector3.fromObject(null, engineObject);
            case COLOUR -> colorFromObject(null, engineObject);
            case UUID -> UUIDfromObject(engineObject);
            case GPOINT, GLINE, GTRI, GCURVE -> getGObject(engineObject);
            case GREF -> getReference(engineObject);
        };
    }

    public static GObject getGObject(EngineObject object) {
        // Just find by ID
        if (object == null) return null;
        if (!(object.getProperties().getFirst() instanceof ArrayList<?> uuidProperties))
            throw new IllegalArgumentException("Cannot convert " + object.getEngineProperties().getLast() + " objects.");
        Object id = from(fromArr(uuidProperties));
        if (!(id instanceof UUID uuid))
            throw new IllegalArgumentException("Cannot convert " + object.getEngineProperties().getLast() + " objects.");

        return StaticRefs.getSceneManager().finder().findFirst(
                GObject.class, Finder.idQuery(), uuid
        ).getgObject();

    }

    public static GObject getReference(EngineObject object) {
        // Just find by ID
        if (object == null) return null;
        if (!(object.getProperties().getLast() instanceof String uuidString))
            throw new IllegalArgumentException("Cannot convert " + object.getEngineProperties().getLast() + " objects.");

        UUID uuid = UUID.fromString(uuidString);

        return StaticRefs.getSceneManager().finder().findFirst(
                GObject.class, Finder.idQuery(), uuid
        ).getgObject();
    }

    public static Object toJaivaReadable(Object o) {
        return switch (o) {
            case GObject g -> toObject(g);
            case UUID id -> toObject(id);
            case Color c -> toObject(c);
            case Vector3 vector3 -> toObject(vector3);
            case String s -> s;         // Jaiva allows strings
            case ArrayList<?> a -> a;   // Jaiva allows arraylist
            case Boolean b -> b;        // Jaiva allows booleans
            case Double d -> d;         // Jaiva allows doubles
            case Integer i -> i;        // Jaiva allows ints
            default ->  throw new IllegalArgumentException("Unknown object type: " + o);
        };
    }

    public static EngineObject convertUUID(UUID id) {
        return new EngineObject(EngineObject.Type.UUID)
                .addProperty(id.toString());
    }

    public static UUID UUIDfromObject(EngineObject uuid) {
        return  UUID.fromString(
                (String) uuid.getProperties().getFirst()
        );
    }

    public static EngineObject convertColor(Color color) {
        return new EngineObject(EngineObject.Type.COLOUR)
                .addProperty(color.getRed())
                .addProperty(color.getGreen())
                .addProperty(color.getBlue())
                .addProperty(color.getAlpha());
    }

    public static Color colorFromObject(GettersPack.CallProperties cp, EngineObject color) {
        return new Color(
                (int)color.getProperties().getFirst(),
                (int)color.getProperties().get(1),
                (int)color.getProperties().get(2),
                (int)color.getProperties().getLast()
        );
    }
}
