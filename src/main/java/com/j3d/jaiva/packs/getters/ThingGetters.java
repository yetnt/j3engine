package com.j3d.jaiva.packs.getters;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.jaiva.EngineObject;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.util.ArrayList;

public class ThingGetters extends BaseLibrary {

    public ThingGetters() {
        super();

        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves the ID of the thing"),
                Thing.EngineObjectUtils::getUuid,
                "id", "uuid"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves the centroid of the thing."),
                Thing.EngineObjectUtils::getCentroid,
                "pivot", "centre", "center"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves the name of the thing."),
                Thing.EngineObjectUtils::getName,
                "name"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves whether this thing is a solid"),
                Thing.EngineObjectUtils::getIsSolid,
                "solid", "solidified", "isSolid"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves whether this thing is hidden from the scene"),
                Thing.EngineObjectUtils::getIsHidden,
                "hidden", "isHidden"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves whether this thing has been scheduled for deletion."),
                Thing.EngineObjectUtils::getIsForDeletion,
                "deleted", "isDeleted", "isForDeletion"
        );
        GettersPack.putAliases(
                this::addWithAliases, "thing",
                JDoc.builder()
                        .addDesc("Retrieves the array of object references in which belong to this thing"),
                (t, r) -> {
                    ArrayList<Object> newList = new ArrayList<>();
                    for (EngineObject object : Thing.EngineObjectUtils.getObjects(t, r)) {
                        Object obj = GettersPack.referenceTransformer.apply(
                                (_, c) -> c,
                                t, object
                        );
                        newList.add(obj);
                    }
                    return newList;
                },
                "objects", "objs", "objectList", "gObjects"
        );
    }
}
