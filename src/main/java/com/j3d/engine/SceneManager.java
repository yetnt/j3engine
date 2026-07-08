package com.j3d.engine;

import com.j3d.Static;
import com.j3d.engine.draw.tris.TriStateArea;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.*;
import com.j3d.engine.geometry.Dim;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionQuery;
import com.j3d.engine.interact.selection.SelectionType;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.layer.LayerList;
import com.j3d.engine.react.history.History;
import com.j3d.gen.properties.HasProperties;
import com.j3d.ui.generic.J3DTheme;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * SceneManager is the class. The main class that handles the rendering of 3D objects onto a 2D screen.
 * @see Graphics2D
 */
public class SceneManager {
    /**
     * The dimensions of the window.
     */
    public Dim screenSize;
    /**
     * An Array of Layers
     */
    public LayerList layers = new LayerList();

    public ArrayDeque<GPoint> points = new ArrayDeque<>();
    private HashSet<HasParents<? extends GObject>> unparented = new HashSet<>();

    /**
     * The current selection made by the user.
     */
    private SelectionManager currentSelection = new SelectionManager();

    /**
     * A list of overlap Runnables to be executed after rendering such that it's on top of everything else.
     * This is not for UI but mainly for debugging purposes.
     */
    private HashMap<UUID, Consumer<Graphics2D>> overlaps = new HashMap<>();

    /**
     * The history manager for undo/redo functionality.
     */
    public static History history = new History();

    /**
     * Default Constructor
     * @param dim The dimensions of the screen
     */
    public SceneManager(Dim dim) {
        screenSize = dim;
        Layer bg = new Layer(Layer.BACKGROUND_ID);
        layers.add(bg); // the default layer
        bg.add(new Thing(this, bg, "bg"));
        layers.add(new Layer()); // testing layer.
    }

    /**
     * Schedules an overlap Runnable to be executed after rendering.
     * @param r The Runnable to execute.
     */
    public void scheduleOverlap(UUID id, Consumer<Graphics2D> r) {
        overlaps.put(id, r);
    }

    /**
     * Create a new GLine from 2 CartesianPoints
     * @param A Point 1
     * @param B Point 2
     * @param l The layer. if null, the default layer is used.
     * @return A new GLine.
     */
    public GLine line(Vector3 A, Vector3 B, Layer l) {
        l = l == null ? layers.get(1) : l;
        GPoint gPointA = findOrCreatePoint(A, l);
        GPoint gPointB = findOrCreatePoint(B, l);
        return new GLine(gPointA, gPointB);
    }

    /**
     * Create a new point from one CartesianPoint
     * @param point Point 1
     * @return A new GPoint
     */
    public GPoint point(Vector3 point) {
        return new GPoint(point);
    }

    /**
     * Draws the Cartesian XY Axis at play.
     */
    public void axis(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw X axis (horizontal)
        graphics.drawLine(0, screenSize.height / 2, screenSize.width,  screenSize.height / 2);

        // Draw Y axis (vertical)
        graphics.drawLine(screenSize.width / 2, 0, screenSize.width / 2, screenSize.height);

        // Optional: draw origin marker
        graphics.setColor(Color.RED);
        graphics.fillOval(screenSize.width / 2 - GPoint.DIAMETER / 2, screenSize.height / 2 - GPoint.DIAMETER / 2, GPoint.DIAMETER, GPoint.DIAMETER);

        graphics.setColor(Color.BLACK);
    }

    public void axisGrid(Graphics2D g, Camera camera) {
        int start = 100;
        int jump = 10;
        int amt = 20;

        Vector3 vXA = new Vector3(-start, 0, -start);
        Vector3 vXB = new Vector3(-start, 0, start);
        Vector3 vZA = new Vector3(-start, 0, -start);
        Vector3 vZB = new Vector3(start, 0, -start);
        g.setColor(
                new Color(
                        J3DTheme.TEXT_PRIMARY.color().getRed(),
                        J3DTheme.TEXT_PRIMARY.color().getBlue(),
                        J3DTheme.TEXT_PRIMARY.color().getGreen(),
                        140
                )
        );
        for (int i = 0; i <= amt; i++) {
            this.drawLine3D(
                    g,
                    vXA.add(new Vector3(i * jump, 0, 0)),
                    vXB.add(new Vector3(i * jump, 0, 0)),
                    camera
            );
            this.drawLine3D(
                    g,
                    vZA.add(new Vector3(0, 0, i * jump)),
                    vZB.add(new Vector3(0, 0, i * jump)),
                    camera
            );
        }
//        double axisLength = camera.getPosition().magnitude() * 0.9;
//        int amt = ((int) camera.getPosition().magnitude());
//        ArrayList<Integer> nums = new ArrayList<>();
//
//        for (int i = -100; i < amt; i+=10) {
//            Color col = new Color(
//                    J3DTheme.TEXT_PRIMARY.color().getRed(),
//                    J3DTheme.TEXT_PRIMARY.color().getGreen(),
//                    J3DTheme.TEXT_PRIMARY.color().getBlue(),
//                    100
//            );
//            g.setColor(col);
//            g.setStroke(new BasicStroke(2));
//
//
//            this.drawLine3D(g,
//                    new Vector3(i, 0, axisLength),
//                    new Vector3(i, 0,
//                            camera.getPosition().getZ() < 0 ?
//                                    Math.clamp(-axisLength, camera.getPosition().getZ(), axisLength) :
//                                    Math.clamp(camera.getPosition().getZ() - 100, 0, axisLength)
//                            ),
//                    camera);
//
//            this.drawLine3D(g,
//                    new Vector3(axisLength, 0, i),
//                    new Vector3(-axisLength, 0, i),
//                    camera);
//        }

    }


    /**
     * Draws the 3D Cartesian axes (X, Y, Z) in the scene, relative to the camera's position.
     * The length of the axes is scaled based on the camera's distance from the origin.
     * @param g The Graphics2D object to draw on.
     * @param camera The camera instance used for perspective transformation.
     */
    public void axis(Graphics2D g, Camera camera) {
        double axisLength = camera.getPosition().magnitude() * 0.9;
        Vector3 origin = new Vector3(0, 0, 0);
        Vector3 offset = new Vector3(0, 0, 0);
        ArrayList<Double> nums = new ArrayList<>(List.of(-1.0, 1.0));
        for (double i = 2.0; i < Math.clamp(Math.floor(axisLength), 0, 20); i++) {
            nums.add(i);
        };

        g.setColor(J3DTheme.TEXT_PRIMARY.color());
//        g.setColor(Color.RED);
        this.drawLine3D(g,
                origin.add(offset).sub(new Vector3(2, 0, 0)),
                origin.add(new Vector3(axisLength, 0, 0)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(axisLength+5, 0, 0)), "X", camera);
        nums.forEach(num -> {
            this.drawLine3D(
                    g, new Vector3(num, 0, -1), new Vector3(num, 0, 1), camera
            );
        });

//        g.setColor(Color.GREEN);
        this.drawLine3D(g, origin.add(offset).sub(new Vector3(0, 30, 0)),
                origin.add(new Vector3(0, axisLength, 0)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(0, axisLength+5, 0)), "Y", camera);
        nums.forEach(num -> {
            this.drawLine3D(
                    g, new Vector3(-1, num, 0), new Vector3(1, num, 0), camera
            );
        });

//        g.setColor(Color.BLUE);
        this.drawLine3D(g, origin.add(offset).sub(new Vector3(0, 0, 2)),
                origin.add(new Vector3(0, 0, axisLength)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(0, 0, axisLength+5)), "Z", camera);
        nums.forEach(num -> {
            this.drawLine3D(
                    g, new Vector3(0, -1, num), new Vector3(0, 1, num), camera
            );
        });
    }

    public void drawLine3D(Graphics2D g, Vector3 start, Vector3 end, Camera cam) {
        ScreenPoint p1 = start.toPoint(cam).toScreen(this);
        ScreenPoint p2 = end.toPoint(cam).toScreen(this);
        if (p1 != null && p2 != null) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    /**
     * Draws 3D text at a specified location in the scene.
     *
     * @param g The Graphics2D object to draw on.
     * @param location The 3D {@link Vector3} coordinates where the text should be drawn.
     * @param text The string to be drawn.
     * @param cam The {@link Camera} instance used for perspective transformation.
     */
    public void drawText3D(Graphics2D g, Vector3 location, String text, Camera cam) {
        ScreenPoint p = location.toPoint(cam).toScreen(this);
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();

        g.setColor(new Color(39, 36, 36, 111));
        g.fillRect(p.x, p.y - height, width, height);
        g.setColor(new Color(255, 255, 255, 255));
        g.drawString(text, p.x, p.y - 2);
    }

    /**
     * Draws 3D text at a specified location in the scene with custom background and foreground colors.
     *
     * @param g The Graphics2D object to draw on.
     * @param location The 3D {@link Vector3} coordinates where the text should be drawn.
     * @param text The string to be drawn.
     * @param cam The {@link Camera} instance used for perspective transformation.
     * @param bgColor The background color for the text box.
     * @param fgColor The foreground color for the text.
     */
    public void drawText3D(Graphics2D g, Vector3 location, String text, Camera cam, Color bgColor, Color fgColor) {
        ScreenPoint p = location.toPoint(cam).toScreen(this);
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();

        g.setColor(bgColor);
        g.fillRect(p.x, p.y - height, width, height);
        g.setColor(fgColor);
        g.drawString(text, p.x, p.y - 2);
    }

    /**
     * "Clears" the screen by drawing a white box over it.
     */
    public void clear(Graphics2D graphics) {
//        graphics.setColor(Color.WHITE); // or whatever your background is
        graphics.clearRect(0, 0, screenSize.width, screenSize.height);
    }


    /**
     * Finds an existing {@link GPoint} in the specified layer that matches the target {@link CartesianPoint}.
     * If no such point exists, a new {@link GPoint} is created and returned.
     * @param target The {@link CartesianPoint} to search for or create.
     * @param l The {@link Layer} to search within. If {@code null}, the first layer is used.
     * @return An existing or newly created {@link GPoint} corresponding to the target {@link CartesianPoint}.
     */
    public GPoint findOrCreatePoint(Vector3 target, Layer l) {
        // Iterate through existing objects to find a matching point
        for (Thing t : l == null ? layers.getFirst() : l) {
            for (GObject obj : t.getObjects()) {
                if (obj instanceof GPoint gp && gp.getPivot().equals(target)) {
                    // Found an existing point, return it.
                    return gp;
                }
            }
        }
        GPoint point = new GPoint(target);
        // parent it to the first Thing
        layers.getFirst().getFirst().addObjs(point);
        return point;
    }
    
    /**
     * Draws all objects in all layers to the screen.
     *
     * @param graphics The Graphics2D object to draw on.
     * @param camera The camera instance.
     */
    public void draw(Graphics2D graphics, Camera camera) {
//        ArrayList<UUID> visibleObjects = buff.draw(layers);
//        ArrayList<UUID> visibleObjects = new ArrayList<>();
        layers.forEach(layer -> layer.draw(graphics));
        TriStateArea.draw(graphics);
        // Draw overlaps
        for (Consumer<Graphics2D> r : overlaps.values()) {
            r.accept(graphics);
        }
    }

    /**
     * Finds a {@link GPoint} near the given cursor position within a specified snap radius.
     * @param mousePos The current position of the mouse cursor in Cartesian coordinates.
     * @param snapRadius The maximum distance from the cursor for a point to be considered "near".
     * @return The {@link GPoint} found near the cursor, or {@code null} if no point is within the snap radius.
     */
    public GPoint findPointNearCursor(CartesianPoint mousePos, double snapRadius) {
        double snapRadiusSquared = snapRadius * snapRadius;
        for (Layer layer : layers) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects()) {
                    if (obj instanceof GPoint point) {
                        double distanceSq = point.getPivot().distanceSquaredTo(mousePos);
                        if (distanceSq <= snapRadiusSquared) {
                            return point; // Found a point to drag!
                        }
                    }
                }
            }
        }
        return null; // No point found in snap radius
    }

    /**
     * Moves a given GPoint to a new Cartesian position.
     *
     * @param point The GPoint to move.
     * @param newPosition The new CartesianPoint position for the GPoint.
     */
    public void movePointTo(GPoint point, Vector3 newPosition) {
        point.setPivot(newPosition);
    }

    /**
     * Moves a {@link Thing} from its current {@link Layer} to a {@code differentLayer}.
     * @param obj The {@link Thing} to move.
     * @param differentLayer The target {@link Layer} to move the object to.
     * @return {@code true} if the object was successfully moved, {@code false} otherwise (e.g., if the object was not found in any layer).
     */
    public boolean moveObjTo(Thing obj, Layer differentLayer) {
        for (Layer layer : layers) {
            if (layer.remove(obj)) {
                differentLayer.add(obj);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a {@link GObject} by its unique ID across all layers.
     * @param Id The unique ID of the object to find.
     * @return The {@link GObject} with the matching ID, or {@code null} if no such object is found.
     */
    public GObject findGObject(String Id) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects()) {
                    if (obj.getId().equals(Id)) {
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    public Thing findThing(String name) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                if (t.getName().equals(name))
                    return t;
            }
        }
        return null;
    }

    /**
     * Updates the current selection based on the provided {@link SelectionQuery}.
     * <p>
     * This method modifies the current selection according to the type specified in the {@code selectionQuery}:
     * <ul>
     *     <li>{@link SelectionType#ALL}: Selects all objects within the defined area.</li>
     *     <li>{@link SelectionType#BOUNDS_STRICT}: Selects objects fully contained within the selection boundaries.</li>
     *     <li>{@link SelectionType#BOUNDS_SOFT}: Selects objects that intersect with or are partially within the selection boundaries.</li>
     *     <li>{@link SelectionType#SUBTRACT}: Inverts the current selection, selecting unselected objects and deselecting selected ones.</li>
     *     <li>{@link SelectionType#UNION}: Adds objects from the new selection to the existing selection.</li>
     *     <li>{@link SelectionType#INCLUDE}: Adds objects from the new selection to the existing selection.</li>
     *     <li>{@link SelectionType#EXCLUDE}: Removes objects from the existing selection that are present in the new selection.</li>
     * </ul>
     * </p>
     * @param selectionQuery The {@link SelectionQuery} defining the selection criteria and type.
     */
    public SelectionManager select(SelectionQuery selectionQuery) {
        // first handle if an existing selection was made.
        if (currentSelection.isNothingSelected())
            currentSelection = new SelectionManager(layers, selectionQuery);
        // check if the type is of EXCLUDE or INCLUDE
        SelectionManager newSelection = new SelectionManager(layers, selectionQuery);
        switch (selectionQuery.type) {
            case EXCLUDE -> currentSelection.exclude(newSelection);
            case INCLUDE -> currentSelection.include(newSelection);
            case ALL, BOUNDS_STRICT, BOUNDS_SOFT, SUBTRACT, UNION -> currentSelection = newSelection;
        }
        return currentSelection;
    }

    public SelectionManager select(Thing[]... things) {
        Arrays.stream(things)
                .flatMap(Arrays::stream)
                .map(Thing::getObjects)
                .flatMap(ArrayList::stream)
                .filter(o -> !currentSelection.getSelected().contains(o))
                .forEach(currentSelection.getSelected()::add);
        return currentSelection;
    }

    public SelectionManager select(Thing thing) {
        thing.getObjects()
                .stream().filter(o -> !currentSelection.getSelected().contains(o))
                .forEach(currentSelection.getSelected()::add);
        return currentSelection;
    }

    public ArrayList<GObject> getSelected() {
        return currentSelection.getSelected();
    }

    /**
     * Finds a {@link GObject} by its UUID across all layers.
     * @param id The UUID of the object to find.
     * @return The {@link GObject} with the matching UUID, or {@code null} if no such object is found.
     */
    public GObject findObjectByUUID(UUID id) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects()) {
                    if (obj.getId().equals(id)) {
                        return obj;
                    }
                }
            }
        }
        return null;
    }

    public Thing findThingByUUID(UUID id) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                if (t.getId().equals(id)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Finds the parent {@link Thing} of a given {@link GObject}.
     * @param o The {@link GObject} whose parent {@link Thing} is to be found.
     * @return The parent {@link Thing} containing the specified {@link GObject}, or {@code null} if no such parent is found.
     */
    public Thing findObjectParent(GObject o) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                if (t.getObjects().contains(o)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Removes a {@link Thing} from its containing {@link Layer}.
     * @param thing The {@link Thing} to be removed.
     */
    public void removeThing(Thing thing) {
        for (Layer layer : layers) {
            if (layer.remove(thing)) {
                return;
            }
        }
    }

    /**
     * Resets the entire scene by clearing all layers, selections, and unregistering all triangles from the {@link TriStateArea}.
     * This effectively returns the sceneManager to an empty state, ready for a new project or scene.
     */
    public void resetScene() {
        getSelected().clear();
        layers.forEach(layer -> {
            if (layer.getTreeNode() == null) return;
            Static.getLayerTree().removeNode(layer.getTreeNode());
        });
        layers.stream()
                .flatMap(Collection::stream)
                .forEach(thing -> {
                    if (thing.getTreeNode() != null)
                        Static.getLayerTree().removeNode(thing.getTreeNode());
                    thing.getObjects().stream()
                            .filter(o -> o instanceof GTri)
                            .map(GTri.class::cast)
                            .forEach(GTri::deleteSelf);
                    thing.getObjects().clear();
                });
        TriStateArea.clearQueue();
        TriStateArea.clearRegistered();
        layers.clear();
        points.clear();
        overlaps.clear();
        currentSelection.clear();
        unparented.clear();
        history.clear(); // also clears backup.
        Static.mainPanel.repaint();
    }

    public void removeOverlap(UUID id) {
        overlaps.remove(id);
    }

    public void deselectAll() {
        currentSelection.clear();
    }

    public HashSet<HasParents<? extends GObject>> getUnparented() {
        return unparented;
    }

    public void hasNoParent(HasParents<? extends GObject> g) {
        unparented.add(g);
    }

    public void hasParent(HasParents<? extends GObject> gObject) {
        unparented.remove(gObject);
    }

    public Thing findParentThing(GObject g) {
        for (Layer layer : layers) {
            for (Thing t : layer) {
                if (t.getObjects().contains(g))
                    return t;
            }
        }
        return null;
    }

    public Layer findThingLayer(Thing objectParent) {
        for (Layer l : layers) {
            for (Thing t : l) {
                if (objectParent.getId().equals(t.getId()))
                    return l;
            }
        }
        return null;
    }
}