package com.j3d.engine.geometry.geo2d.copy;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.ArrayList;

public class CopyPropertiesBuilder {
    private CopyProperties properties;

    protected CopyPropertiesBuilder(ArrayList<GObject> objects) {
        properties = new CopyProperties(objects);
    }

    public CopyPropertiesBuilder severeConnections() {
        properties.setSevereConnections(true);
        return this;
    }

    public CopyProperties build() {
        return properties;
    }
}
