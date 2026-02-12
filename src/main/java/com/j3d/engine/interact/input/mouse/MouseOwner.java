package com.j3d.engine.interact.input.mouse;

import com.j3d.engine.react.events.*;
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
        EventEmitter.genericAttach(registered, event);
    }

    @Override
    public void detach(EventListener event) {
        EventEmitter.genericDetach(registered, event);
    }

    @Override
    public void detachAll() {
        EventEmitter.genericDetachAll(registered);
    }

    @Override
    public <K> void broadcast(EventType eventType, EventBroadcast<K> properties) {
        EventEmitter.genericBroadcast(registered, eventType, properties);
    }
}
