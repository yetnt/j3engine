package com.j3d.engine.geometry.constraints.concrete;

import com.j3d.engine.geometry.constraints.ConstraintIntent;
import com.j3d.engine.geometry.constraints.ConstraintMirror;
import com.j3d.engine.geometry.constraints.ConstraintOn;
import com.j3d.engine.geometry.geo2d.constraints.CPoint;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MidpointConstraint implements ConstraintOn<GPoint> {

    private final Runnable applier;
    private final GPoint parent;
    private final GLine line;

    public MidpointConstraint(GPoint parent, GLine line) {
        this.parent = parent;
        this.line = line;
        this.applier = () -> {
            parent.setPivot(line.getPivot());
        };
        MidpointConstraintEventListener mdpl = new MidpointConstraintEventListener(applier);

        line.getStart().attach(mdpl);
        line.getEnd().attach(mdpl);

    }

    @Override
    public String name() {
        return "Midpoint Constraint";
    }

    @Override
    public String description() {
        return "Enforces that the given point stays at the geometric centre of a line.";
    }

    @Override
    public GPoint getParent() {
        return parent;
    }

    @Override
    public boolean satisfiesConstraint(ConstraintIntent intent) {
        intent.consume(); // apply the proposed change
        HashMap<UUID, ConstraintMirror> changedObjects = intent.map();
        changedObjects.values().forEach(ConstraintMirror::dispose);

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

    @Override
    public Set<Class<?>> incompatibleWith() {
        return Set.of(FixedPointConstraint.class);
    }

    public static class MidpointConstraintEventListener implements EventListener {
        private final Runnable applier;
        public MidpointConstraintEventListener(Runnable applier) {
            this.applier = applier;
        }
        @Override
        public <K> void onEvent(EventType event, EventPayload<K> properties) {
            if (event == EventType.GPOINT_RECALC_PIVOT)
                applier.run();
        }
    }
}
