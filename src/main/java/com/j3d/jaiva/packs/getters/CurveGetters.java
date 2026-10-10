package com.j3d.jaiva.packs.getters;

import com.j3d.engine.scene.nodes.geometry.GCurve;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.tokenizer.jdoc.JDoc;

public class CurveGetters extends BaseLibrary {

    public CurveGetters() {
        super();

        GettersPack.putAliases(
                this::addWithAliases, "curve",
                JDoc.builder()
                        .addDesc("Retrieves the start point of the curve"),
                (t, r) -> GettersPack.referenceTransformer.apply(GCurve.EngineObjectUtils::getStart, t, r),
                "start", "pointA"
        );
        GettersPack.putAliases(
                this::addWithAliases, "curve",
                JDoc.builder()
                        .addDesc("Retrieves the control point of the curve"),
                (t, r) -> GettersPack.referenceTransformer.apply(GCurve.EngineObjectUtils::getControlPoint, t, r),
                "control", "pointB"
        );
        GettersPack.putAliases(
                this::addWithAliases, "curve",
                JDoc.builder()
                        .addDesc("Retrieves the end point of the curve"),
                (t, r) -> GettersPack.referenceTransformer.apply(GCurve.EngineObjectUtils::getEnd, t, r),
                "end", "pointC"
        );

        GettersPack.put(
                this::addWithAliases, "curve", "amount",
                JDoc.builder()
                        .addDesc("Retrives the amount of lines tht this curve will decompose into."),
                GCurve.EngineObjectUtils::getAmount
        );
    }
}
