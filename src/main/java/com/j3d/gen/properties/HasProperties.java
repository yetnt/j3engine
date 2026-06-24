package com.j3d.gen.properties;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.ArrayList;

public interface HasProperties {
    ArrayList<Property<?, ?>> getProperties();
}