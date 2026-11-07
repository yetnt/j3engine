package com.j3d.jaiva.packs.getters;

import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GPolygon;
import com.j3d.engine.scene.nodes.geometry.GPolyline;
import com.j3d.jaiva.EngineObject;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.util.ArrayList;

public class PolyGetters extends BaseLibrary {
    public PolyGetters() {

        super();

        GettersPack.putAliases(
                this::addWithAliases, "polygon",
                JDoc.builder()
                        .addDesc("Retrieves the triangulation method used to triangulate this polygon."),
                GPolygon.EngineObjectUtils::getTriangulation,
                "triMethod", "triangulationMethod", "method"
        );
        GettersPack.putAliases(
                this::addWithAliases, "polygon",
                JDoc.builder()
                        .addDesc("Retrieves the winding direction of this polygon"),
                GPolygon.EngineObjectUtils::getWindingDir,
                "windingDir", "windingDirection", "winding"
        );

        GettersPack.putAliases(
                this::addWithAliases, "polygon",
                JDoc.builder()
                        .addDesc("Retrieves the array of point references which belong to this polygon"),
                (t, r) -> {
                    ArrayList<Object> newList = new ArrayList<>();
                    for (EngineObject object : GPolygon.EngineObjectUtils.getObjects(t, r)) {
                        Object obj = GettersPack.referenceTransformer.apply(
                                (_, c) -> c,
                                t, object
                        );
                        newList.add(obj);
                    }
                    return newList;
                },
                "points", "pointList", "gPoints"
        );

        GettersPack.putAliases(
                this::addWithAliases, "polyline",
                JDoc.builder()
                        .addDesc("Retrieves the array of point references which belong to this polyline"),
                (t, r) -> {
                    ArrayList<Object> newList = new ArrayList<>();
                    for (EngineObject object : GPolyline.EngineObjectUtils.getObjects(t, r)) {
                        Object obj = GettersPack.referenceTransformer.apply(
                                (_, c) -> c,
                                t, object
                        );
                        newList.add(obj);
                    }
                    return newList;
                },
                "points", "pointList", "gPoints"
        );
    }
}
