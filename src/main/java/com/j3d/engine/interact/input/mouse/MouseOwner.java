package com.j3d.engine.interact.input.mouse;

import com.j3d.J3DSettings;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.react.events.*;
import com.j3d.ui.engine.EngineFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * MouseOwner is a class which represents an entity that can own the mouse input in the sceneManager. It extends {@link MouseAdapter}
 * to allow it to handle mouse events and implements {@link EventEmitterInterface}
 * to allow it to broadcast events to registered listeners.
 * @see MouseAdapter
 * @see EventEmitterInterface
 * @see EngineFrame
 * @author Lehlogonolo Poole
 * @implSpec Implementors are meant to check whether they have ownership or not using {@link #isNotOwner()},
 * since all MouseOwners are registered within {@link EngineFrame} and all get called. This does mean one
 * who does not check whether they are the owner or not, can execute regardless. (system of trust typa stuff)
 */
public class MouseOwner extends MouseAdapter implements EventEmitterInterface {

    /**
     * The threshold for the click delay, used to differentiate between a click and a drag.
     */
    private int clickDelayThreshold = 10;
    /**
     * The current click delay, incremented on mouse drag and reset on mouse release.
     */
    private int clickDelay = 0;
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
     * Creates a new MouseOwner with the given owner and click delay threshold.
     * @param owner The owner of the mouse input.
     * @param clickDelayThreshold The threshold for the click delay.
     */
    public MouseOwner(MOwner owner, int clickDelayThreshold) {
        this.owner = owner;
        this.clickDelayThreshold = clickDelayThreshold;
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
    public boolean isNotOwner() {
        return EngineFrame.getMouseOwner() != owner;
    }

    protected ScreenPoint getMouseLoc(MouseEvent e) {
        return new ScreenPoint(
                e.getX() - 4,
                e.getY() - ((2 * J3DSettings.jMenuBarOffsetY)) - 10);
    }

    protected ScreenPoint getSelectionMouseLoc(MouseEvent e) {
        return new ScreenPoint(
                e.getX() - 4,
                e.getY() - 2) ;
    }

    public void clear() {

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

    @Override
    public boolean isAttached(EventListener e) {
        return registered.contains(e);
    }

    /**
     * This method is called when a mouse drag event occurs, but only after the {@link MouseOwner#clickDelayThreshold}
     * has been exceeded. This is used to differentiate between a click and a drag.
     * @implNote Only if this differentiation is required to be made like in the case of {@link SelectionMouseOwner}
     * trying to avoid accidental selections, then you also need to override this. Otherwise simply overriding
     * {@link MouseOwner#mouseDragged(MouseEvent)} is perfectly fine.
     * @param e The mouse event.
     */
    public void mouseDraggedUsingClickDelay(MouseEvent e) {
        // do whatever the hell.
    }

    /**
     * Handles the mouse dragged event
     * @implNote This does some extra logic for determining the difference between an actual drag and an accidental
     * drag (because human click never perfect) by making use of {@link MouseOwner#clickDelayThreshold} and
     * {@link MouseOwner#clickDelay}. Implementors which requires dragging and needs differentiating should
     * instead override {@link MouseOwner#mouseDraggedUsingClickDelay(MouseEvent)} and use the
     * {@link MouseOwner#MouseOwner(MOwner, int)} to define the {@code clickDelayThreshold}. otherwise this method
     * can be overridden directly.
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        clickDelay++;
//        System.out.println(clickDelay);
        if (clickDelay > clickDelayThreshold)
            mouseDraggedUsingClickDelay(e);

    }

    /**
     * Handles the mouse pressed event.
     * @implNote Implementors making use of {@link MouseOwner#mouseDraggedUsingClickDelay(MouseEvent)} and {@link MouseOwner#clickDelayThreshold}
     * need to call {@code super.mouseReleased()} when overriding this such that it works. Also in the event that the threshold
     * was not met, the same mouse event is instead passed into {@link MouseOwner#mousePressed(MouseEvent)} instead as if it were
     * a click.
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (clickDelay < clickDelayThreshold)
            mousePressed(e);

        clickDelay = 0;
    }
}
