package com.j3d.engine.scene.nodes.geometry;

import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.payloads.GPointMovedEvent;
import com.j3d.engine.scene.copy.CopyProperties;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.WindingDir;
import com.j3d.engine.scene.nodes.util.tri.Triangulation;
import com.j3d.engine.scene.nodes.util.tri.TriangulationMethod;
import com.j3d.gen.properties.Property;
import com.j3d.gen.settings.Settings;
import com.j3d.jaiva.EngineObject;
import com.j3d.jaiva.TypeConverter;
import com.j3d.jaiva.packs.getters.GettersPack;
import com.j3d.jaiva.packs.getters.J3DGetterException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GPolygon extends GObject implements
        IdempotentEventListener<GPointMovedEvent, Vector3>, DecomposeWhenDrawn<Triangle> {

    private final ArrayList<GPoint> points = new ArrayList<>();
    private WindingDir windingDir = WindingDir.CLOCKWISE;

    private TriangulationMethod.Triangulated triangles;
    private ArrayList<RenderState<Triangle, GObject>> renderState = new ArrayList<>();

    private Triangulation triangulation = Settings.sceneProperties.polygonTriangulateMethod.getValue();
    private boolean deletedState = false;

    @NotNull
    @Contract("_, _, _, _, _ -> new")
    public static GPolygon fromRaw(String id, Color col, ArrayList<GPoint> points, Triangulation triangulation, WindingDir windingDir) {
        GPolygon gt = new GPolygon(points);
        gt.setId(UUID.fromString(id));
        gt.setColour(col);
        gt.setTriangulation(triangulation);
        gt.setWindingDir(windingDir);
        return gt;
    }

    public GPolygon(ArrayList<GPoint> points) throws TriangulationMethod.InvalidTriangulationException {
        super(Color.BLACK);
        triangulate(points);
        this.points.addAll(points);
        points.forEach(point -> {
            point.attachListener(this);
            point.addParent(this);
        });
        setPivot(Vector3.reduceToVector3(points.stream().map(GPoint::getPivot).toList(), Vector3::add).div(points.size()));
        addProps();
    }

    private void triangulate(ArrayList<GPoint> points) throws TriangulationMethod.InvalidTriangulationException {
        triangles = Settings.sceneProperties.polygonTriangulateMethod.getValue().getTriangulationMethod().apply(points);
        renderState.forEach(RenderState::invalidate);
        renderState.clear();
        triangles.tris().forEach(tri -> {
            if (windingDir == WindingDir.COUNTERCLOCKWISE)
                tri = tri.flip();
            RenderState<Triangle, GObject> r = tri.toTriangle().toRenderState(this);
            r.getPure().setDoubleSided(false); // polygons always experience back-face cullin.
            renderState.add(r);
        });
    }

    private void addProps() {
        properties.addAll(List.of(
                new Property<>("Winding Direction", this::getWindingDir, GPolygon.class)
                        .setNewValueConsumer(this::setWindingDir)
                        .holds(WindingDir.class)
                        .setDescription("The direction which the triangles are wound in. Clockwise is front facing"),
                new Property<>("Triangulation Method", this::getTriangulation, GPolygon.class)
                        .setNewValueConsumer(this::setTriangulation)
                        .holds(Triangulation.class)
                        .setDescription("The method to use for triangulation. This should preferably not be messed with"),
                new Property<>("Triangle Amount", this.getTriangles().tris()::size, GPolygon.class)
                        .constant()
                        .holds(Integer.class)
                        .setDescription("The amount of triangles this polygon is made up off"),
                new Property<>("Produced Points", this.getTriangles().newPoints()::size, GPolygon.class)
                        .constant()
                        .holds(Integer.class)
                        .setDescription("The amount of points that this polygon introduced as a side effect.")
        ));
        pivotProperty // provided by GObject so override its default.
                .setDescription("The geometric centre of this polygon")
                .constant(); // cannot be edited.
    }

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
        return EngineObject.Type.GPOLYGON;
    }

    @Override
    public void decompose() {
        triangulate(points);
    }

    @Override
    public ArrayList<RenderState<Triangle, GObject>> getDecomposeList() {
        return renderState;
    }

    @Override
    public ArrayList genericRenderStateList() {
        decompose();
        return getDecomposeList();
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

    public ArrayList<GPoint> getPoints() {
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

    public GPolygon copy(ArrayList<GPoint> newPoints) {
        GPolygon pgon = new GPolygon(newPoints);
        pgon.setColour(getColour());
        pgon.setTriangulation(triangulation);
        pgon.setWindingDir(windingDir);
        return pgon;
    }

    public WindingDir getWindingDir() {
        return windingDir;
    }

    public void setWindingDir(WindingDir windingDir) {
        this.windingDir = windingDir;
    }

    public TriangulationMethod.Triangulated getTriangles() {
        return triangles;
    }

    public ArrayList<GPoint> generatedPoints() {
        return points;
    }

    public ArrayList<Triangle> getTrianglesForSelectionPurposes() {
        return triangles.tris().stream().map(TriangulationMethod.Tri::toTriangle).collect(Collectors.toCollection(ArrayList::new));
    }

    public void setTriangulation(Triangulation triangulation) {
        this.triangulation = triangulation;
        decompose();
    }

    public Triangulation getTriangulation() {
        return triangulation;
    }

    @NotNull
    @Contract("->new")
    public HashSet<GLine> generateOutline() {

        HashSet<GLine> lines = new HashSet<>();
        getTriangles().tris().forEach(tri -> {
            GLine l1 = GLine.getInstance(lines, tri.A(), tri.B());
            GLine l2 = GLine.getInstance(lines, tri.B(), tri.C());
            GLine l3 = GLine.getInstance(lines, tri.C(), tri.A());
            lines.add(l1);
            lines.add(l2);
            lines.add(l3);
        });
        lines.forEach(line -> line.tag("volatile", getClass(), this));
        return lines;
    }

    @Override
    public EngineObject toObject() {
        return super.toObject()
                .addProperty(windingDir.name())
                .addProperty(triangulation.name())
                .addProperty(points.stream().map(GPoint::asReference).collect(Collectors.toCollection(ArrayList::new)));
    }

    public static class EngineObjectUtils {
        public static String getWindingDir(GettersPack.WrappedProperties call, EngineObject object) throws J3DGetterException {
            TypeConverter.expectObjectType(object, EngineObject.Type.GPOLYGON, call);
            return TypeConverter.expectType(object.getProperties().get(3), String.class);
        }
        public static String getTriangulation(GettersPack.WrappedProperties call, EngineObject object) throws J3DGetterException {
            TypeConverter.expectObjectType(object, EngineObject.Type.GPOLYGON, call);
            return TypeConverter.expectType(object.getProperties().get(4), String.class);
        }
        @NotNull
        public static ArrayList<EngineObject> getObjects(GettersPack.WrappedProperties c, EngineObject object) throws
                J3DGetterException {
            TypeConverter.expectObjectType(object, EngineObject.Type.GPOLYGON, c);
            ArrayList<?> arr = TypeConverter.expectArr(object.getProperties().get(5));
            return TypeConverter.fromArrOfArr(arr);
        }
    }
}
