package com.j3d.engine.interact.input.mouse;

import com.j3d.StaticRefs;
import com.j3d.engine.EngineException;
import com.j3d.engine.interact.InteractionException;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.react.events.*;
import com.j3d.StaticConfig;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.generators.JLabelRichText;

import java.awt.*;
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

    private Point old = new Point(0 ,0);
    private final Mouse physicalMouse = new Mouse(0, 0);
    private Robot robot;

    /**
     * Creates a new MouseOwner with the given owner.
     * @param owner The owner of the mouse input.
     */
    public MouseOwner(MOwner owner) {
        this.owner = owner;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            StaticRefs.getErrs().handle(
                    new InteractionException("Robot could not be instantiated", e).code(101)
            );
        }
    }

    /**
     * Creates a new MouseOwner with the given owner and click delay threshold.
     * @param owner The owner of the mouse input.
     * @param clickDelayThreshold The threshold for the click delay.
     */
    public MouseOwner(MOwner owner, int clickDelayThreshold) {
        this(owner);
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


    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }

    /**
     * Calculates the adjusted screen point from a given {@link MouseEvent}.
     * This method applies specific offsets (e.g., for menu bar) to the raw mouse coordinates.
     *
     * @param e The {@link MouseEvent} containing the raw mouse coordinates.
     * @return A {@link ScreenPoint} representing the adjusted mouse location on the screen.
     */
    protected ScreenPoint getMouseLoc(MouseEvent e) {
        return new ScreenPoint(
                e.getX() - 4,
                e.getY() - ((2 * StaticConfig.jMenuBarOffsetY)) - 10);
    }

    /**
     * Calculates the adjusted screen point from the current physical mouse coordinates.
     * This method uses the internal {@link #physicalMouse} state and applies specific offsets.
     *
     * @return A {@link ScreenPoint} representing the adjusted physical mouse location on the screen.
     */
    protected ScreenPoint getMouseLocFromPhysical() {
        return new ScreenPoint(
                physicalMouse.getX() - 4,
                physicalMouse.getY() - ((2 * StaticConfig.jMenuBarOffsetY)) - 10);
    }

    /**
     * Calculates the adjusted screen point for selection purposes from a given {@link MouseEvent}.
     * This method applies different offsets compared to {@link #getMouseLoc(MouseEvent)}.
     *
     * @param e The {@link MouseEvent} containing the raw mouse coordinates.
     * @return A {@link ScreenPoint} representing the adjusted mouse location specifically for selection.
     */
    protected ScreenPoint getSelectionMouseLoc(MouseEvent e) {
        return new ScreenPoint(
                e.getX() - 4,
                e.getY() - 2) ;
    }

    /**
     * Resets the state of the {@link #physicalMouse} to its default (0,0) position.
     */
    public void clear() {
        physicalMouse.reset();
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

    /**
     * Moves the physical mouse cursor to the center of the main application window.
     * This method uses a {@link Robot} to programmatically move the mouse and updates
     * the internal {@link #physicalMouse} state to reflect the new position.
     */
    public void sendMouseToCentre() {
        EngineFrame frame = StaticRefs.getMainFrame();
        int centerX = frame.getX() + frame.getWidth() / 2;
        int centerY = frame.getY() + frame.getHeight() / 2;
        robot.mouseMove(centerX, centerY);
        physicalMouse.moveAndReset(centerX, centerY);
    }

    /**
     * Implements mouse wrapping functionality, allowing the mouse cursor to seamlessly
     * move from one edge of the screen to the opposite edge.
     * When the mouse moves past a certain threshold near the left or right edge,
     * it is repositioned to the corresponding opposite edge.
     * @param e The {@link MouseEvent} containing the current mouse coordinates.
     */
    public void wrap(MouseEvent e) {
        if (e.getX() + 10 > StaticRefs.getSceneManager().screenSize.width) {
            // move the mouse to the oppsite side
            robot.mouseMove(
                    StaticRefs.getMainFrame().getLocationOnScreen().x -
                            (StaticRefs.getSceneManager().screenSize.width + 5),
                    e.getY() /* +
                            (StaticRefs.getSceneManager().screenSize.height - e.getY() )*/
            );
            physicalMouse.addX(5);
            old = new Point(
                    5,
                    e.getPoint().y
            );
        } else if (e.getX() - 8 <= 0 /*&& physicalMouse.getX() - StaticRefs.getSceneManager().screenSize.width > 0*/) {
            // move the mouse to the opposite side
            robot.mouseMove(
                    StaticRefs.getMainFrame().getLocationOnScreen().x +
                            (StaticRefs.getSceneManager().screenSize.width - 2),
                    e.getY() /* +
                            (StaticRefs.getSceneManager().screenSize.height - e.getY() )*/
            );
            physicalMouse.addX(-2);
            old = new Point(
                    StaticRefs.getSceneManager().screenSize.width - 2,
                    e.getPoint().y
            );
        } else {
            physicalMouse.addX(e.getPoint().x - old.x).addY(e.getPoint().y - old.y);
            old = e.getPoint();
        }

        StaticRefs.getHoverLabel().setText(
                new JLabelRichText("PHYS="+physicalMouse)
                        .addLn("MOUSE="+e.getPoint())
                        .wrapHTML()
        );
    }

    /**
     * Retrieves the internal {@link Mouse} object representing the physical mouse state.
     * @return The {@link Mouse} object.
     */
    protected Mouse getPhysicalMouse() {
        return physicalMouse;
    }

    /**
     * Sets the 'old' point, which is used to calculate mouse movement deltas
     * for the {@link #wrap(MouseEvent)} method.
     * @param point The new {@link Point} to set as the 'old' point.
     * @see #wrap(MouseEvent)
     */
    protected void setOldPoint(Point point) {
        old = point;
    }

    @Override
    public void attachListener(EventListener event) {
        EventEmitter.genericAttach(registered, event);
    }

    @Override
    public void detachListener(EventListener event) {
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
}
