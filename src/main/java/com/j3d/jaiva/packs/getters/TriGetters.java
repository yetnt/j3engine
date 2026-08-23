package com.j3d.jaiva.packs.getters;

import com.j3d.engine.scene.nodes.geometry.GTri;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryType;
import com.jaiva.tokenizer.jdoc.JDoc;

public class TriGetters extends BaseLibrary {

    public TriGetters() {
        super(LibraryType.CONTAINER);

        GettersPack.put(
                vfs, "tri", "getLegA",
                JDoc.builder()
                        .addDesc("Retrieves the first leg of the triangle"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getLegA, t, r)
        );
        GettersPack.put(
                vfs, "tri", "getLegB",
                JDoc.builder()
                        .addDesc("Retrieves the second leg of the triangle"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getLegB, t, r)
        );
        GettersPack.put(
                vfs, "tri", "getLegC",
                JDoc.builder()
                        .addDesc("Retrieves the third leg of the triangle"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getLegC, t, r)
        );

        GettersPack.put(
                vfs, "tri", "getWindingA",
                JDoc.builder()
                        .addDesc("Retrieves the first point of the triangle's winding"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getWindingA, t, r)
        );
        GettersPack.put(
                vfs, "tri", "getWindingB",
                JDoc.builder()
                        .addDesc("Retrieves the second point of the triangle's winding"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getWindingB, t, r)
        );
        GettersPack.put(
                vfs, "tri", "getWindingC",
                JDoc.builder()
                        .addDesc("Retrieves the third point of the triangle's winding"),
                (t, r) -> GettersPack.referenceTransformer.apply(GTri.EngineObjectUtils::getWindingC, t, r)
        );

        GettersPack.put(
                vfs, "tri", "getDoubleSided",
                JDoc.builder()
                        .addDesc("Retrieves the double sided property"),
                GTri.EngineObjectUtils::getIsDoubleSided
        );
    }
}
