package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.engine.geometry.geo2d.DecomposeWhenDrawn;
import com.j3d.engine.geometry.geo2d.graphics.pure.Segment;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.gen.properties.Property;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class GCurve extends GObject implements IdempotentEventListener<GPoint.GPointMovedEvent, Vector3>, DecomposeWhenDrawn<Segment<GCurve>> {

    private GPoint start;
    private GPoint controlPoint;
    private GPoint end;
    public static final int AMOUNT = 50;

    public static GCurve fromRaw(String id, GPoint st, GPoint cp, GPoint en) {
        GCurve gp = new GCurve(st, cp, en);
        gp.setId(UUID.fromString(id));
        return gp;
    }

    public GCurve(GPoint start, GPoint controlPoint, GPoint end) {

        this.start = start;
        this.controlPoint = controlPoint;
        this.end = end;

        setPivot(controlPoint.getPivot()); // set the pivot to the control point

        start.addParent(this);
        controlPoint.addParent(this);
        end.addParent(this);
        addProps();
    }

    public void addProps() {
        properties.addAll(List.of(
                new Property<>(
                        "Start Point",
                        this::getStart,
                        GCurve.class
                ),
                new Property<>(
                        "Control Point",
                        this::getControlPoint,
                        GCurve.class
                ),
                new Property<>(
                        "End Point",
                        this::getEnd,
                        GCurve.class
                )
        ));
        pivotProperty.constant(); // the pivot cannot be edited.
    }

    public Vector3 point(double t) {
        double u = 1 - t;

        return start.getPivot().mult(u*u)
                .add(controlPoint.getPivot().mult(2 * u * t))
                .add(end.getPivot().mult(t*t));
    }

    public Stream<GPoint> getPointStream() {
        return Stream.of(start, controlPoint, end);
    }

    public GPoint getStart() {
        return start;
    }

    public GPoint getEnd() {
        return end;
    }

    public GPoint getControlPoint() {
        return controlPoint;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        // decompose.
    }

    @Override
    public void drawSelected(Graphics2D graphics2D) {
        // decompose.
    }

    ArrayList<Segment<GCurve>> segments = new ArrayList<>();

    @Override
    public void decompose() {
        segments.clear();
        Vector3 prev = point(0);

        for (int i = 0; i < AMOUNT; i++) {
            double t = (double) i / AMOUNT;
            Vector3 next = point(t);

            segments.add(new Segment<>(prev, next, this));

            prev = next;
        }
    }

    @Override
    public ArrayList<Segment<GCurve>> getDecomposeList() {
        return segments;
    }

    public void swingDraw(Graphics2D graphics2D) {
//        graphics2D.draw
    }

    @Override
    public Vector3 getDupeObjectToCheck() {
        return getPivot();
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPoint.GPointMovedEvent payload) {
        Vector3 newPiv = controlPoint.getPivot();
        if (!newPiv.equals(getDupeObjectToCheck())) setPivot(newPiv);
    }
}
