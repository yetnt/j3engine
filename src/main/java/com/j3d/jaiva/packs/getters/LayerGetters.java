package com.j3d.jaiva.packs.getters;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.jaiva.EngineObject;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.util.ArrayList;

public class LayerGetters extends BaseLibrary {

    public LayerGetters() {
        super();

        GettersPack.putAliases(
                this::addWithAliases, "layer",
                JDoc.builder()
                        .addDesc("Retrieves the ID of the layer. ")
                        .addNote("Layuers don't actually have a queryable id, in J3D, they are identified by name. So the ID returned by this" +
                                " is just the id it got assigned at construction time. It's useless."),
                Layer.EngineObjectUtils::getUuid,
                "id", "uuid"
        );
        GettersPack.putAliases(
                this::addWithAliases, "layer",
                JDoc.builder()
                        .addDesc("Retrieves the name of the layer."),
                Layer.EngineObjectUtils::getName,
                "name"
        );
        GettersPack.putAliases(
                this::addWithAliases, "layer",
                JDoc.builder()
                        .addDesc("Retrieves whether this layer is hidden from the scene"),
                Layer.EngineObjectUtils::getIsHidden,
                "hidden", "isHidden"
        );
        GettersPack.putAliases(
                this::addWithAliases, "layer",
                JDoc.builder()
                        .addDesc("Retrieves whether this layer has been scheduled for deletion."),
                Layer.EngineObjectUtils::getIsForDeletion,
                "deleted", "isDeleted", "isForDeletion"
        );
        GettersPack.putAliases(
                this::addWithAliases, "layer",
                JDoc.builder()
                        .addDesc("Retrieves the array of thing references in which belong to this layer"),
                (t, r) -> {
                    ArrayList<Object> newList = new ArrayList<>();
                    for (EngineObject thing : Layer.EngineObjectUtils.getObjects(t, r)) {
                        Object obj = GettersPack.referenceTransformer.apply(
                                (_, c) -> c,
                                t, thing
                        );
                        newList.add(obj);
                    }
                    return newList;
                },
                "things", "thingList"
        );
    }
}
