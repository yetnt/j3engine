package com.j3d.engine.interact.input.mouse;

import com.j3d.engine.react.events.EventBroadcast;
import com.j3d.engine.react.events.EventEmitterInterface;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class MouseOwner extends MouseAdapter implements EventEmitterInterface {
    /**
     * All registered EventListeners
     */
    protected ArrayList<EventListener> registered = new ArrayList<>();
    private final MOwner owner;

    public MouseOwner(MOwner owner) {
        this.owner = owner;
    }

    public void requestOwnership() {
        EngineFrame.setMouseOwner(owner);
    }

    protected boolean isNotOwner() {
        return EngineFrame.getMouseOwner() != owner;
    }

    @Override
    public void attach(EventListener event) {
        registered.add(event);
    }

    @Override
    public void detach(EventListener event) {
        registered.remove(event);
    }

    @Override
    public void detachAll() {
        registered.clear();
    }

    @Override
    public <K> void broadcast(EventType eventType, EventBroadcast<K> properties) {
        registered.forEach(event -> event.onEvent(eventType, properties));
    }
}
