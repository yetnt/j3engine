package com.j3d.engine.scene.nodes.geometry;

import com.j3d.engine.geometry.Pure;
import com.j3d.engine.geometry.Triangle;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.IdempotentEventListener;
import com.j3d.engine.react.events.payloads.GPointMovedEvent;
import com.j3d.engine.scene.draw.RenderState;
import com.j3d.engine.scene.nodes.geometry.base.DecomposeWhenDrawn;
import com.j3d.engine.scene.nodes.geometry.base.WindingDir;
import com.j3d.jaiva.EngineObject;

import java.util.ArrayList;

public final class GPolygon extends GObject implements
        IdempotentEventListener<GPointMovedEvent, Vector3>, DecomposeWhenDrawn<Triangle> {

    private ArrayList<GLine> lineSegments = new ArrayList<>();
    private WindingDir windingDir = WindingDir.CLOCKWISE;
    private ArrayList<RenderState<Triangle, GObject>> triangles = new ArrayList<>();

    public GPolygon(ArrayList<GPoint> points) {

    }

    @Override
    public Vector3 getDupeObjectToCheck() {
        return null;
    }

    @Override
    public void handlePossibleDuplicates(EventType type, GPointMovedEvent payload) {

    }

    @Override
    public EngineObject.Type getEngineObjectType() {
        return null;
    }

    @Override
    public void decompose() {

    }

    @Override
    public ArrayList<RenderState<Triangle, GObject>> getDecomposeList() {
        return null;
    }
}
