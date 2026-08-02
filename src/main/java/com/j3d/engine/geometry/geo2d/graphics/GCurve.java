package com.j3d.engine.geometry.geo2d.graphics;

import com.j3d.engine.draw.RenderState;
import com.j3d.engine.geometry.geo2d.DecomposeWhenDrawn;
import com.j3d.engine.geometry.geo2d.copy.CopyProperties;
import com.j3d.engine.geometry.geo2d.copy.InvalidCopyException;
import com.j3d.engine.geometry.geo2d.graphics.pure.Segment;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.gen.properties.Property;
import com.j3d.utility.generic.SamePair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class GCurve extends GObject implements IdempotentEventListener<GPoint.GPointMovedEvent, SamePair<Double>>, DecomposeWhenDrawn<Segment> {

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

        start.attachListener(this);
//        start.addParent(this);
        controlPoint.attachListener(this);
//        controlPoint.addParent(this);
        end.attachListener(this);
//        end.addParent(this);
        addProps();
        decompose();
    }

    public void addProps() {
        properties.addAll(List.of(
                new Property<>(
                        "Start Point",
                        this::getStart,
                        GCurve.class
                ).holds(GPoint.class).setDescription("Start Point").constant(),
                new Property<>(
                        "Control Point",
                        this::getControlPoint,
                        GCurve.class
                ).holds(GPoint.class).setDescription("Control Point").constant(),
                new Property<>(
                        "End Point",
                        this::getEnd,
                        GCurve.class
                ).holds(GPoint.class).setDescription("End Point").constant()
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

    ArrayList<RenderState<Segment, GObject>> segments = new ArrayList<>();

    @Override
    public void decompose() {
        segments.forEach(RenderState::invalidate);
        segments.clear();
        Vector3 prev = point(0);

        for (int i = 0; i < AMOUNT; i++) {
            double t = (double) i / AMOUNT;
            Vector3 next = point(t);

            segments.add(new Segment(prev, next).toRenderState(this));

            prev = next;
        }
    }

    @Override
    public ArrayList<RenderState<Segment, GObject>> getDecomposeList() {
        return segments;
    }

    @Override
    public ArrayList genericRenderStateList() {
        decompose();
        return getDecomposeList();
    }

    double lengthA = 0.00;
    double lengthB = 0.00;
    double EPSILON = 1e-6;

    @Override
    public void onEvent(EventType event, EventPayload properties) {
        if (event == EventType.GPOINT_RECALC_PIVOT && properties instanceof GPoint.GPointMovedEvent p)
            handlePossibleDuplicates(event, p);
    }

    @Override
    public SamePair<Double> getDupeObjectToCheck() {
        return new SamePair<>(lengthA, lengthB);
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPoint.GPointMovedEvent payload) {
        setPivot(controlPoint.getPivot());
        decompose();
//        double reLengthA = start.getPivot().distance(controlPoint.getPivot());
//        double reLengthB = end.getPivot().distance(controlPoint.getPivot());
//        if (Math.abs(reLengthA - lengthA) > EPSILON || Math.abs(reLengthB - lengthB) > EPSILON) {
//            lengthA = reLengthA;
//            lengthB = reLengthB;
//            setPivot(payload.emitter.getPivot());
//            decompose();
//        }
    }

    public void copy(CopyProperties props) throws InvalidCopyException {
        GPoint a = props.existsOrElse(start.getId(), start::copySelf);
        GPoint control = props.existsOrElse(controlPoint.getId(), controlPoint::copySelf);
        GPoint b = props.existsOrElse(end.getId(), end::copySelf);
        props.add(getId(), copy(a, control, b));
    }

    protected GCurve copy(GPoint copyA, GPoint control, GPoint copyB) {
        GCurve curve = new GCurve(
                copyA,
                control,
                copyB
        );
        curve.setColour(getColour());
        return curve;
    }
}
