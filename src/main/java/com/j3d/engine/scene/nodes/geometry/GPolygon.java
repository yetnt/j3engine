package com.j3d.engine.scene.nodes.geometry;

import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.payloads.GPointMovedEvent;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.Winding;
import com.j3d.engine.scene.nodes.geometry.base.WindingDir;
import com.j3d.engine.scene.nodes.util.tri.Triangulation;
import com.j3d.engine.scene.nodes.util.tri.TriangulationMethod;
import com.j3d.gen.properties.Property;
import com.j3d.gen.settings.Settings;
import com.j3d.jaiva.EngineObject;
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

    public GPolygon(ArrayList<GPoint> points) {
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

    private void triangulate(ArrayList<GPoint> points) {
        triangles = Settings.sceneProperties.polygonTriangulateMethod.getValue().getTriangulationMethod().apply(points);
        renderState.forEach(RenderState::invalidate);
        renderState.clear();
        triangles.tris().forEach(tri -> {
            if (windingDir == WindingDir.COUNTERCLOCKWISE)
                tri.flip();
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

        return lines;
    }
}
