package com.j3d.engine.geometry.constraints.concrete;

import com.j3d.engine.geometry.constraints.ConstraintIntent;
import com.j3d.engine.geometry.constraints.ConstraintMirror;
import com.j3d.engine.geometry.constraints.ConstraintOn;
import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;

import java.util.HashSet;
import java.util.Set;

public class FixedPointConstraint implements ConstraintOn<GPoint> {

    private final Runnable applier;
    private final GPoint parent;
    private final Vector3 pos;

    public FixedPointConstraint(GPoint parent, Vector3 pos) {
        this.parent = parent;
        this.pos = pos;
        this.applier = () -> parent.setPivot(pos);
    }
    @Override
    public String name() {
        return "Fixed Point Constraint";
    }

    @Override
    public String description() {
        return
                "Enforces that the given point must stay at a specific Vector3 coordinate.";
    }

    @Override
    public GPoint getParent() {
        return parent;
    }

    @Override
    public boolean satisfiesConstraint(ConstraintIntent intent) {
        intent.consume(); // apply the proposed change
        intent.map().values().forEach(ConstraintMirror::dispose);

        ConstraintMirror thisMirror = intent.map().get(parent.getId());
        if (thisMirror == null) return true;
        if (thisMirror instanceof CPoint m)
            return m.getPivot().equals(pos);

        return true;
    }

    @Override
    public Runnable getConstraintApplier() {
        return applier;
    }

    @Override
    public Set<Class<?>> incompatibleWith() {
        return new HashSet<>() {{
            add(MidpointConstraint.class);
        }};
    }
}
