package com.j3d.engine.geometry.constraints;

import com.j3d.engine.geometry.geo2d.graphics.GObject;

import java.util.ArrayList;

public class ConstraintUtils {

    public static ArrayList<ConstraintMirror> converter(ArrayList<GObject> objects) {
        return objects.stream().map(GObject::toConstraintObject).map(ConstraintMirror.class::cast).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

}
