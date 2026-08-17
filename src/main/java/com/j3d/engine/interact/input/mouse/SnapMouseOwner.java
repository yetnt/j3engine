package com.j3d.engine.interact.input.mouse;

import com.j3d.StaticRefs;
import com.j3d.engine.react.events.payloads.SnapPayload;
import com.j3d.engine.scene.SceneManager;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.scene.nodes.geometry.GLine;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionQuery;
import com.j3d.engine.interact.selection.SelectionType;
import com.j3d.engine.react.events.EventType;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j3d.StaticRefs.getCamera;
import static com.j3d.StaticRefs.getSceneManager;

/**
 * A {@link MouseOwner} which enables child classes which extend it, the capability to snap to
 * {@link GObject}s.
 *  <p>
 *     This class:
 *  </p>
 *     <ul>
 *         <li>Detects nearby {@link GObject}s (points and midpoints of lines) within a small radius of the mouse cursor.</li>
 *         <li>Highlights these potential snap targets visually in the scene.</li>
 *         <li>Broadcasts a {@link EventType#SNAP_TO_OBJ} event when the user right-clicks on a highlighted snap target.</li>
 *     </ul>
 *     Subclasses can extend this to enable snapping behaviour for their specific mouse interactions, such as
 *     transforming objects to align with other objects in the scene.
 * @implNote
 *     <p>
 *         Subclasses do need to call {@link #snapEnabled()} or else no snapping will be performed and the
 *         child class will act as normal.
 *     </p>
 *     <p>
 *         Subclasses also need to call {@link #clear()} such as to clear the snapping overlaps from
 *         the {@link SceneManager} when it is no longer needed.
 *     </p>
 * </p>
 * @author Lehlogonolo Poole
 * @see MouseOwner
 * @see SnapPayload
 * @see EventType#SNAP_TO_OBJ
 */
public class SnapMouseOwner extends MouseOwner {

    /**
     * The radius (in pixels) around the mouse cursor within which {@link GObject}s are considered
     * potential snap targets.
     */
    public static int offset = 4;
    /**
     * A list of {@link GObject}s that are currently within the snapping region around the mouse cursor.
     * These are potential targets for snapping.
     */
    protected ArrayList<GObject> snappingCandidates = new ArrayList<>();
    /**
     * The {@link GObject} that the mouse is currently hovering over within the snapping region.
     * This object will be the target if a snap event is triggered.
     */
    private GObject hoveringOver = null;
    /**
     * A unique identifier for this {@code SnapMouseOwner} instance, used for managing overlaps
     * with the {@link StaticRefs#getSceneManager()}.
     */
    private final UUID uuid;
    /**
     * A flag indicating whether the snapping functionality is currently enabled.
     */
    private boolean enabled = false;
    /**
     * A flag indicating whether the overlap consumer has been scheduled with the scene manager.
     */
    private boolean overlapScheduled = false;
    /** The current screen coordinates of the mouse cursor. */
    private ScreenPoint mouseLoc = new ScreenPoint(0, 0);

    /**
     * Constructs a new {@code SnapMouseOwner}.
     *
     * @param owner The owner of this mouse interaction.
     * @param uuid A unique identifier for this instance, used for managing overlaps with the {@link SceneManager}.
     */
    public SnapMouseOwner(MOwner owner, UUID uuid) {
        super(owner);
        this.uuid = uuid;
    }

    /**
     * Returns a {@code Consumer<Graphics2D>} that handles the visual representation of snapping candidates.
     * This consumer is scheduled with the {@link SceneManager} to draw overlays, identifying and highlighting
     * potential snap targets (points and midpoints of lines) within the snapping region.
     * @return A {@code Consumer} that draws the snapping overlays.
     */
    private Consumer<Graphics2D> consumer() {
        return (g) -> {
            hoveringOver = null;
            // get the first 5 elements
            for (int i = 0; i < Math.min(5, snappingCandidates.size()); i++) {
                GObject obj = snappingCandidates.get(i);
                boolean isLine = obj instanceof GLine;
                Vector3 pos = obj.getPivot(); // if it's a line this is the midpoint.
                ScreenPoint sp = pos.toPoint(getCamera()).toScreen();

                if (isLine) {
                    // draw triangle at sp for midpoint. using Graphics2D
                    g.setColor(java.awt.Color.CYAN);
                    int[] xPoints = {sp.x, sp.x - 5, sp.x + 5};
                    int[] yPoints = {sp.y - 5, sp.y + 5, sp.y + 5};
                    g.fillPolygon(xPoints, yPoints, 3);
                    if (isWithinRegion(sp)) {
                        StaticRefs.getHoverLabel().setText("Snap to Midpoint of " + obj.getId().toString().substring(0, 5));
                        hoveringOver = obj;
                    }
                } else { // It's a GPoint
                    g.setColor(java.awt.Color.MAGENTA);
                    g.fillOval(sp.x - 3, sp.y - 3, 6, 6);
                    if (isWithinRegion(sp)) {
                        StaticRefs.getHoverLabel().setText("Snap to " + obj.getId().toString().substring(0, 5));
                        hoveringOver = obj;
                    }
                }
            }
        };
    }

    /**
     * Handles mouse click events. If a right-click occurs on a highlighted snap target,
     * it broadcasts a {@link EventType#SNAP_TO_OBJ} event with the {@code hoveringOver} object.
     * @param e The {@code MouseEvent} generated by the click.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (isNotOwner() || !enabled) return;
        // only use right click.
        if (e.getButton() != MouseEvent.BUTTON3) return;
        // only double click
        if (e.getClickCount() != 2) return;
        if (hoveringOver == null) return;

        broadcast(
                EventType.SNAP_TO_OBJ,
                new SnapPayload(this, hoveringOver)
        );
    }

    /**
     * Checks if a given {@link ScreenPoint} is within the current snapping region defined by {@code mouseLoc} and {@code offset}.
     * @param target The {@link ScreenPoint} to check.
     * @return {@code true} if the target is within the region, {@code false} otherwise.
     */
    private boolean isWithinRegion(ScreenPoint target) {
        return target.x >= mouseLoc.x - offset &&
               target.x <= mouseLoc.x + offset &&
               target.y >= mouseLoc.y - offset &&
               target.y <= mouseLoc.y + offset;
    }

    /**
     * Enables the snapping functionality for this {@code SnapMouseOwner}.
     * Subclasses must call this method to activate snapping.
     */
    public void snapEnabled() {
        enabled = true;
    }

    public ScreenPoint getMouseLocation() {
        return mouseLoc;
    }

    /**
     * Handles mouse movement events. When the mouse moves, it updates the {@code mouseLoc}, clears previous snapping candidates,
     * and queries the {@link SelectionManager} for new potential snap targets within the snapping region. It also schedules the overlap consumer if not already scheduled.
     * @param e The {@code MouseEvent} generated by the mouse movement.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mouseLoc = getMouseLoc(e);
        if (isNotOwner() || !enabled) return;
        if (!overlapScheduled) {
            getSceneManager().scheduleOverlap(uuid, consumer());
            overlapScheduled = true;
        }
        snappingCandidates.clear();
        SelectionQuery sq = new SelectionQuery(
                new ScreenPoint(mouseLoc.x-offset, mouseLoc.y-offset),
                new ScreenPoint(mouseLoc.x+offset, mouseLoc.y+offset),
                SelectionType.BOUNDS_SOFT
        );
        SelectionManager sm = new SelectionManager(getSceneManager().layers, sq);
        sm.getSelected()
                .stream()
                .filter(t -> !(t instanceof GTri))
                .sorted((a, b) -> {
                    Vector3 A = a.getPivot();
                    Vector3 B = b.getPivot();
                    Vector3 camera = StaticRefs.getCamera().getPosition();
                    // shortest distance to camera
                    double Ad = A.distance(camera);
                    double Bd = B.distance(camera);
                    return Double.compare(Ad, Bd);
                })
                .forEach(snappingCandidates::add);
    }

    /**
     * Clears all snapping-related state, including removing the overlap consumer from the {@link SceneManager},
     * clearing snapping candidates, and resetting the {@code hoveringOver} object. This should be called when snapping is no longer needed.
     */
    @Override
    public void clear() {
        super.clear();
        getSceneManager().removeOverlap(uuid);
        overlapScheduled = false;
        snappingCandidates.clear();
        hoveringOver = null;
    }

    /**
     * Returns the list of {@link GObject}s that are currently considered potential snapping candidates.
     * @return An {@code ArrayList} of {@link GObject}s.
     */
    public ArrayList<GObject> getSnappingCandidates() {
        return snappingCandidates;
    }
}
