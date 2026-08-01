package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class CopyProperties {
    private boolean severeConnections = false;
    private HashSet<Copy> copies = new HashSet<>();
    private ArrayList<GObject> objects;

    protected CopyProperties(ArrayList<GObject> objects) {
        this.objects = objects;
    }

    protected void setSevereConnections(boolean severeConnections) {
        this.severeConnections = severeConnections;
    }

    public boolean shouldSevereConnections() {
        return severeConnections;
    }

    public void add(UUID original, GObject copy) {
        copies.add(new Copy(original, copy));
    }

    public boolean exists(UUID original) {
        return
                copies.stream()
                .anyMatch(c -> c.is(original));
    }

    public HashSet<Copy> getCopies() {
        return copies;
    }

    public ArrayList<GObject> getCopiesAsObjects() {
        return
                copies.stream()
                .map(Copy::copy)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static CopyPropertiesBuilder builder(ArrayList<GObject> objects) {
        return new CopyPropertiesBuilder(objects);
    }
}
