package com.j3d.engine.interact.cmd.base;

import com.j3d.StaticRefs;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.events.CapableEventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.EmittedErrorEventPayload;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.errors.J3DError;
import com.yetnt.utils.functional.consumer.ThrowableConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PointReferenceConstraintChecker implements CapableEventListener<Boolean> {
    private final ArrayList<Vector3> referenceCopy;
    private final Class<? extends J3DError> err;
    private final Consumer<ArrayList<Vector3>> applier;
    private boolean errorCaught = false;

    public PointReferenceConstraintChecker(
            Command cmd, Class<? extends J3DError> err,
            ArrayList<Vector3> original, Consumer<ArrayList<Vector3>> applier) {
        this.referenceCopy = new ArrayList<>(original);
        this.err = err;
        this.applier = applier;
        StaticRefs.getErrs().attachListener(this);
    }

    public void finish() {
        StaticRefs.getErrs().detachListener(this);
    }

    public boolean isErrorCaught() {
        return errorCaught;
    }

    public void setErrorCaught(boolean errorCaught) {
        this.errorCaught = errorCaught;
    }

    @Override
    public <K> Boolean capableOnEvent(EventType event, EventPayload<K> properties) {

        if (errorCaught) return true;

        if (event != EventType.EMITTED_ERROR ||  properties == null)
            return false;

        EmittedErrorEventPayload payload = (EmittedErrorEventPayload) properties;

        if (payload.getError().getClass() != err) return false;

        // tell the command to do their shit

        errorCaught = true;

        applier.accept(referenceCopy);

        return true;

    }

    public ArrayList<Vector3> getReferenceCopy() {
        return referenceCopy;
    }

    public void update(ArrayList<Vector3> original) {
        this.referenceCopy.clear();
        this.referenceCopy.addAll(original);
    }

    public void updateCopy(@NotNull ArrayList<GPoint> original) {
        update(original.stream().map(GPoint::getPivot).collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
    }
}
