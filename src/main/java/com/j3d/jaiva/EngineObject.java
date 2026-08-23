package com.j3d.jaiva;

import java.util.ArrayList;
import java.util.List;

public class EngineObject extends ArrayList<Object> {
    private final ArrayList<String> engineProperties;
    private final ArrayList<Object> objects;

    public EngineObject(Type type) {
        super();
        engineProperties = new ArrayList<>(List.of("J3Engine"));
        this.add(engineProperties);
        engineProperties.add(type.toString());
        objects = new ArrayList<>();
        this.add(objects);
    }

    private EngineObject(ArrayList<?> objects) {
        if (objects.size() != 2) throw  new IllegalArgumentException("Cannot convert " + objects.size() + " objects.");
        if (!(objects.getFirst() instanceof ArrayList<?> engineProps))
            throw new IllegalArgumentException("Cannot convert " + objects.size() + " objects.");
        if (!(objects.getLast() instanceof ArrayList<?> objectProps))
            throw new IllegalArgumentException("Cannot convert " + objects.size() + " objects.");
        this.engineProperties = (ArrayList<String>) engineProps;
        this.objects = (ArrayList<Object>) objectProps;
        this.addAll(objects);
    }

    public static EngineObject from(ArrayList<?> objects) {
        return new EngineObject(objects);
    }

    public EngineObject addProperty(Object property) {
        this.objects.add(property);
        return this;
    }

    public ArrayList<Object> getProperties() {
        return objects;
    }

    public ArrayList<String> getEngineProperties() {
        return engineProperties;
    }

    public EngineObject.Type getType() {
        return Type.valueOf(engineProperties.getLast());
    }

    public enum Type {
        VECTOR3, COLOUR, UUID, GPOINT, GLINE, GTRI, GCURVE, GREF;
    }
}
