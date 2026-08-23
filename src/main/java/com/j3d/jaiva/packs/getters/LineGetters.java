package com.j3d.jaiva.packs.getters;

import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.tokenizer.jdoc.JDoc;

public class LineGetters extends BaseLibrary {

    public LineGetters() {
        super(LibraryType.CONTAINER);

        GettersPack.putAliases(
                vfs, "line",
                JDoc.builder()
                        .addDesc("Retrieves the start point of the line"),
                (t, r) -> GettersPack.referenceTransformer.apply(GLine.EngineObjectUtils::getPointA, t, r),
                "start", "pointA"
        );
        GettersPack.putAliases(
                vfs, "line",
                JDoc.builder()
                        .addDesc("Retrieves the control point of the line"),
                (t, r) -> GettersPack.referenceTransformer.apply(GLine.EngineObjectUtils::getPointB, t, r),
                "end", "pointB"
        );
    }
}
