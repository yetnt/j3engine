package com.j3d.gen.grid;

import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.convert.ConversionWithOffset;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.math.plane.NormalPlane;
import com.j3d.engine.react.events.*;
import com.j3d.engine.react.events.payloads.ReactivePointInvalidatedPayload;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.ui.engine.floating.grid2d.Grid2DPanel;
import com.j3d.ui.theme.J3DTheme;
import com.yetnt.utils.qol.Colours;
import jdk.jfr.FlightRecorder;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReactivePoint extends Point implements EventListener, EventEmitterInterface {

    GPoint gp;
    Supplier<AxisPlane> axisPlaneSuppler;
    Function<CartesianPoint, CartesianPoint> pointRounder;
    ArrayList<EventListener> events = new ArrayList<>();
    boolean invalid = false;

    public ReactivePoint(GPoint gPoint, Supplier<AxisPlane> axisPlaneSupplier, Function<CartesianPoint, CartesianPoint> pointRounder) {
        this.gp = gPoint;
        this.pointRounder = pointRounder;
        gPoint.attachListener(this);
        this.axisPlaneSuppler = axisPlaneSupplier;
    }

    @Override
    public CartesianPoint getPoint(ConversionWithOffset props) {
        AxisPlane axisPlane = axisPlaneSuppler.get();
        if (!axisPlane.toNormalPlane().onPlane(gp.getPivot())) {
            invalid = true;
            broadcast(EventType.REACTIVE_POINT_INVALIDATED, new ReactivePointInvalidatedPayload(this));
        }
        return pointRounder.apply(axisPlane.fromWorld(gp.getPivot()));
    }

    @Override
    public void draw(Graphics2D graphics2D, ConversionWithOffset props) {
        // same logic as super but different colour lol
        // draw as circle.
        ScreenPoint sp = getPoint(props).toScreen(props);
        int size = 10;

        Stroke original = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(Colours.alphaChannel(J3DTheme.TEXT_SECONDARY.color().darker(), 160));
        graphics2D.fillOval(sp.x - size / 2, sp.y - size / 2, size, size);
        graphics2D.setColor(Color.black);
        graphics2D.setStroke(original);
    }

    @Override
    public void drawWorld(Graphics2D graphics2D, AxisPlane axisPlane) {
        // no logic.
    }

    @Override
    public GPoint render(AxisPlane plane, ArrayList<GObject> objects) {
        return gp;
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.GPOINT_RECALC_PIVOT) {
            boolean onPlane = axisPlaneSuppler.get()
                    .toNormalPlane()
                    .onPlane(gp.getPivot());
            if (!onPlane) {
                invalid = true;
                broadcast(EventType.REACTIVE_POINT_INVALIDATED, new EventPayload<>(this) {
                });
            }
        }
    }

    @Override
    public void attachListener(EventListener event) {
        EventEmitter.genericAttach(events, event);
    }

    @Override
    public void detachListener(EventListener event) {
        EventEmitter.genericDetach(events, event);
    }

    @Override
    public void detachAll() {
        EventEmitter.genericDetachAll(events);
    }

    @Override
    public <K> void broadcast(EventType eventType, EventPayload<K> properties) {
        EventEmitter.genericBroadcast(events, eventType, properties);
    }

    @Override
    public boolean isAttached(EventListener e) {
        return events.contains(e);
    }

    public GPoint getGPoint() {
        return gp;
    }
}
