package com.j3d.engine.scene.nodes.geometry;

import com.j3d.engine.geometry.Segment;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.payloads.GPointMovedEvent;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.gen.properties.Property;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.j3d.jaiva.packs.getters.J3DGetterException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GPolyline extends GObject implements IdempotentEventListener<GPointMovedEvent, Vector3>, DecomposeWhenDrawn<Segment> {

    ArrayList<GPoint> points;
    private boolean deletedState = false;

    @NotNull
    public static GPolyline fromRaw(String id, Color col, ArrayList<GPoint> points) {
        GPolyline gl = new GPolyline(points);
        gl.setId(UUID.fromString(id));
        gl.setColour(col);
        return gl;
    }

    public GPolyline(@NotNull ArrayList<GPoint> points) {
        if (points.size() < 2)
            throw new IllegalArgumentException("less than 2 points");

        this.points = points;
        points.forEach(point -> {
            point.attachListener(this);
            point.addParent(this);
        });
        setPivot(Vector3.reduceToVector3(points.stream().map(GPoint::getPivot).toList(), Vector3::add).div(points.size()));
        addProps();
    }

    public void addProps() {
        properties.add(
                new Property<>(
                        "Point Amount",
                        points::size,
                        GPolyline.class
                ).holds(Integer.class).setDescription(
                        "The amount of points this poly line owns"
                ).constant()
        );
        pivotProperty.constant();
    }

    public ArrayList<GPoint> explode() {
        deletedState = true;
        points.forEach(point -> {
            if (point != null) {
                point.explode(this);
                detachListener(point);
                point.detachListener(this);
            }
        });
        return points;
    }

    public void copy(CopyProperties props) throws InvalidCopyException {
        ArrayList<GPoint> copies = new ArrayList<>();
        for (GPoint gPoint : points) {
            GPoint point = props.existsOrElse(gPoint.getId(), gPoint::copySelf);
            copies.add(point);
        }
        props.add(getId(), copy(copies));
    }

    public GPolyline copy(ArrayList<GPoint> newPoints) {
        GPolyline polyline = new GPolyline(newPoints);
        polyline.setColour(getColour());
        return polyline;
    }

    ArrayList<RenderState<Segment, GObject>> segments = new ArrayList<>();

    @Override
    public Vector3 getDupeObjectToCheck() {
        return getPivot();
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPointMovedEvent payload) {
        Vector3 newPiv = Vector3.reduceToVector3(points.stream().map(GPoint::getPivot).toList(), Vector3::add).div(points.size());
        if (!newPiv.equals(getDupeObjectToCheck())) {
            setPivot(newPiv);
            invalidateAll();
            decompose();
        }
    }

    @Override
    public EngineObject.Type getEngineObjectType() {
        return EngineObject.Type.GPOLYLINE;
    }

    @NotNull
    public ArrayList<Segment> toSegments() {
        ArrayList<Segment> seg = new ArrayList<>();
        for (int i = 0; i < points.size()-1; i++) {
            seg.add(
                    new Segment(points.get(i).getPivot(), points.get(i+1).getPivot())
            );
        }
        return seg;
    }

    @Override
    public void decompose() {
        segments.forEach(RenderState::invalidate);
        segments.clear();

        segments.addAll(
                toSegments().stream().map(a -> a.toRenderState(this))
                        .toList()
        );
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

    @Override
    public EngineObject toObject() {
        return super.toObject()
                .addProperty(points.stream().map(GPoint::asReference).collect(Collectors.toCollection(ArrayList::new)));
    }

    public static class EngineObjectUtils {
        @NotNull
        public static ArrayList<EngineObject> getObjects(GettersPack.WrappedProperties c, EngineObject object) throws
        J3DGetterException {
            TypeConverter.expectObjectType(object, EngineObject.Type.GPOLYLINE, c);
            ArrayList<?> arr = TypeConverter.expectArr(object.getProperties().get(3));
            return TypeConverter.fromArrOfArr(arr);
        }
    }

}
