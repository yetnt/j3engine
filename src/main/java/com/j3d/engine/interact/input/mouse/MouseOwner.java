package com.j3d.engine.interact.input.mouse;

import com.j3d.engine.react.events.*;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;

/**
 * MouseOwner is a class which represents an entity that can own the mouse input in the sceneManager. It extends MouseAdapter
 * to allow it to handle mouse events and implements EventEmitterInterface
 * to allow it to broadcast events to registered listeners.
 */
public class MouseOwner extends MouseAdapter implements EventEmitterInterface {
    /**
     * All registered EventListeners
     */
    protected ArrayList<EventListener> registered = new ArrayList<>();
    /**
     * The owner of the mouse input, used to determine if the current MouseOwner is the owner of the mouse input in the sceneManager.
     */
    private final MOwner owner;

    /**
     * Creates a new MouseOwner with the given owner.
     * @param owner The owner of the mouse input.
     */
    public MouseOwner(MOwner owner) {
        this.owner = owner;
    }

    /**
     * Requests ownership of the mouse input in the sceneManager.
     * This will set the mouse owner in the EngineFrame to this MouseOwner's owner.
     */
    public void requestOwnership() {
        EngineFrame.setMouseOwner(owner);
    }

    /**
     * Checks if this MouseOwner is not the owner of the mouse input in the sceneManager.
     * @return True if this MouseOwner is not the owner of the mouse input in the sceneManager, false otherwise.
     * @implNote This is flipped from the more intuitive isOwner() method to allow for easier use in mouse event methods,
     *           where we want to return early if this MouseOwner is not the owner of the mouse input in the sceneManager.
     */
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
    public <K> void broadcast(EventType eventType, EventPayload<K> properties) {
        EventEmitter.genericBroadcast(registered, eventType, properties);
    }
}
