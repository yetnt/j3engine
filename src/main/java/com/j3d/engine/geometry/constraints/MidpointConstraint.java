package com.j3d.engine.geometry.constraints;

import com.j3d.engine.geometry.geo2d.constraints.CObject;
import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.utility.Pair;

import java.util.HashMap;
import java.util.UUID;

public class MidpointConstraint implements Constraint {

    private Runnable applier;
    private GPoint parent;
    private GLine line;

    public MidpointConstraint(GPoint parent, GLine line) {
        this.parent = parent;
        this.line = line;
        this.applier = () -> {
            parent.setPivot(line.getPivot());
        };

        // TODO: Event listener where applier is applied to midpoint when one of the edges changes,
    }

    @Override
    public GPoint getParent() {
        return parent;
    }

    @Override
    public boolean satisfiesConstraint(ConstraintIntent intent) {
        intent.consume(); // apply the proposed change
        HashMap<UUID, ConstraintMirror> changedObjects = intent.map();


        CPoint changedParent =
                changedObjects.get(parent.getId()) ==  null
                        ? null : (CPoint) changedObjects.get(parent.getId());
        CPoint point1 =
                changedObjects.get(line.getStart().getId()) == null
                        ? null : (CPoint) changedObjects.get(line.getStart().getId());

        CPoint point2 =
                changedObjects.get(line.getEnd().getId()) == null
                        ? null : (CPoint) changedObjects.get(line.getEnd().getId());

        if ((point1 == null || point2 == null) && changedParent == null) {
            return true; // the midpoint was not moved. constraint satisfied.
        } else if (point1 != null && point2 != null && changedParent != null) {
            // Check if the midpoint is still the centre.
            Vector3 expected = point1.getPivot().add(point2.getPivot()).div(2);
            Vector3 actual = changedParent.getPivot();
            double epsilon = 1e-6d;
            Vector3 diff = actual.sub(expected);
            return diff.dot(diff) < epsilon * epsilon;
        } else if (changedParent != null) {
            // if its still the centre allow through. otherwise this fails.
            // even though most cases we can hard code this to false, anything can happen
            // hence this is a safe guard.
            return parent.getPivot().equals(changedParent.getPivot());
        }

        // by now, there is no lines moved.
        return true;
    }

    @Override
    public Runnable getConstraintApplier() {
        return applier;
    }
}
