package com.j3d.engine.scene;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.draw.SceneRenderer;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.Dim;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GPoint;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionQuery;
import com.j3d.engine.interact.selection.SelectionType;
import com.j3d.engine.scene.nodes.geometry.base.HasParents;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.engine.scene.nodes.layer.LayerList;
import com.j3d.engine.react.history.History;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.theme.J3DTheme;

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
    private ArrayList<GObject> clipboard = new ArrayList<>();
    private final Finder finder = new Finder(() -> layers);
    public Finder finder() {
        return finder;
    }
    private SceneRenderer renderer = new SceneRenderer();
    public SceneRenderer getRenderer() {
        return renderer;
    }

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
    public static final History history = new History();

    /**
     * Default Constructor
     * @param dim The dimensions of the screen
     */
    public SceneManager(Dim dim) {
        screenSize = dim;
        Layer bg = new Layer(Layer.BACKGROUND_ID);
        layers.add(bg); // the default layer
        bg.add(new Thing(bg, "bg"));
        layers.add(new Layer()); // testing layer.
    }

    private final String usable = "Usable";
    public Layer usableLayer() {
        boolean l = layers.stream()
                .anyMatch(la -> la.getName().equals(usable));
        if (l) {
            return layers.stream()
                    .filter(layer -> layer.getName().equals(usable))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Usable layer not found despite anyMatch returning true"));
        } else {
            Layer newUsableLayer = new Layer(usable);
            layers.add(newUsableLayer);
            return newUsableLayer;
        }

    }

    public void axisGrid(Graphics2D g, Camera camera) {
        double mult = Settings.sceneProperties.axisLength.getValue();

        int start = (int) (50*mult);
        int jump = 10;
        int amt = (int) (10*mult);

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
                    vXA.add(Vector3.X(i * jump)),
                    vXB.add(Vector3.X(i * jump)),
                    camera
            );
            this.drawLine3D(
                    g,
                    vZA.add(Vector3.Z(i * jump)),
                    vZB.add(Vector3.Z(i * jump)),
                    camera
            );
        }
    }


    /**
     * Draws the 3D Cartesian axes (X, Y, Z) in the scene, relative to the camera's position.
     * The length of the axes is scaled based on the camera's distance from the origin.
     * @param g The Graphics2D object to draw on.
     * @param camera The camera instance used for perspective transformation.
     */
    public void axis(Graphics2D g, Camera camera) {
//        double axisLength = camera.getPosition().magnitude() * 0.9;
        double axisLength = 20;
        int axisOffset = 2;
        Stroke s = g.getStroke();
        Vector3 origin = new Vector3(0, 0, 0);
        Vector3 offset = new Vector3(0, 0, 0);
//        ArrayList<Double> nums = new ArrayList<>(List.of(-1.0, 1.0));
//        for (double i = 2.0; i < Math.clamp(Math.floor(axisLength), 0, 20); i++) {
//            nums.add(i);
//        }

        g.setStroke(new BasicStroke(2));
        g.setColor(J3DTheme.TEXT_PRIMARY.color());
        g.setColor(Color.RED);
        this.drawLine3D(g,
                origin.add(offset).sub(new Vector3(2, 0, 0)),
                origin.add(new Vector3(axisLength, 0, 0)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(axisLength+axisOffset, 0, 0)), "X+", camera);
//        nums.forEach(num -> {
//            this.drawLine3D(
//                    g, new Vector3(num, 0, -1), new Vector3(num, 0, 1), camera
//            );
//        });

        g.setColor(Color.GREEN);
        this.drawLine3D(g, origin.add(offset).sub(new Vector3(0, 30, 0)),
                origin.add(new Vector3(0, axisLength, 0)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(0, axisLength+axisOffset, 0)), "Y+", camera);
//        nums.forEach(num -> {
//            this.drawLine3D(
//                    g, new Vector3(-1, num, 0), new Vector3(1, num, 0), camera
//            );
//        });

        g.setColor(Color.BLUE);
        this.drawLine3D(g, origin.add(offset).sub(new Vector3(0, 0, 2)),
                origin.add(new Vector3(0, 0, axisLength)),
                camera);
        this.drawText3D(g, origin.add(new Vector3(0, 0, axisLength+axisOffset)), "Z+", camera);
//        nums.forEach(num -> {
//            this.drawLine3D(
//                    g, new Vector3(0, -1, num), new Vector3(0, 1, num), camera
//            );
//        });
        g.setStroke(s);
    }

    public void drawLine3D(Graphics2D g, Vector3 start, Vector3 end, Camera cam) {
        ScreenPoint p1 = start.toPoint(cam).toScreen();
        ScreenPoint p2 = end.toPoint(cam).toScreen();
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
        ScreenPoint p = location.toPoint(cam).toScreen();
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
        ScreenPoint p = location.toPoint(cam).toScreen();
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();

        g.setColor(bgColor);
        g.fillRect(p.x, p.y - height, width, height);
        g.setColor(fgColor);
        g.drawString(text, p.x, p.y - 2);
    }

    public void drawPoly3D(Graphics2D g, ArrayList<Vector3> points) {
        int nPoints = points.size();
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        for (int i = 0; i < nPoints; i++) {
            Vector3 v = points.get(i);
            ScreenPoint sp = v
                    .toPoint(StaticRefs.getCamera())
                    .toScreen();

            xPoints[i] = sp.x;
            yPoints[i] = sp.y;
        }

        g.fillPolygon(xPoints, yPoints, nPoints);
    }

    /**
     * "Clears" the screen by drawing a white box over it.
     */
    public void clear(Graphics2D graphics) {
//        graphics.setColor(Color.WHITE); // or whatever your background is
        graphics.clearRect(0, 0, screenSize.width, screenSize.height);
    }


    /**
     * Draws all objects in all layers to the screen.
     *
     * @param graphics The Graphics2D object to draw on.
     */
    public void draw(Graphics2D graphics) {
//        ArrayList<UUID> visibleObjects = buff.draw(layers);
//        ArrayList<UUID> visibleObjects = new ArrayList<>();
        layers.forEach(layer -> layer.draw(graphics));
        getRenderer().draw(graphics);
        // Draw overlaps
        for (Consumer<Graphics2D> r : overlaps.values()) {
            r.accept(graphics);
        }
    }


    /**
     * Schedules an overlap Runnable to be executed after rendering.
     * @param r The Runnable to execute.
     */
    public void scheduleOverlap(UUID id, Consumer<Graphics2D> r) {
        overlaps.put(id, r);
    }
    public void removeOverlap(UUID id) {
        overlaps.remove(id);
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

    public SelectionManager select(ArrayList<GObject> objects) {
        objects.stream()
                .filter(o -> !currentSelection.getSelected().contains(o))
                .forEach(currentSelection.getSelected()::add);
        return currentSelection;
    }

    public void selectAll() {
        layers.objectStream()
                .filter(o -> !currentSelection.getSelected().contains(o))
                .forEach(currentSelection.getSelected()::add);
    }

    public HashSet<GObject> getSelected() {
        return currentSelection.getSelected();
    }

    public void deselectAll() {
        currentSelection.clear();
    }

    public void select(GObject gobject) {
        currentSelection.getSelected().add(gobject);
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

    public void setClipboard(ArrayList<GObject> clipboard) {
        this.clipboard = clipboard;
    }

    public ArrayList<GObject> getClipboard() {
        return new ArrayList<>(clipboard);
    }

    public void clearClipboard() {
        clipboard.clear();
    }

    /**
     * Finds a {@link Thing} within the scene by its name.
     * @implNote Convenience over using {@link #finder} with a {@link Finder#nameQuery()} to find a
     * {@link Thing}
     *
     * @param name The name of the {@link Thing} to find.
     * @return The {@link Thing} with the specified name.
     */
    public Thing findThing(String name) {
        return finder
                .findFirst(Thing.class, Finder.nameQuery(), name)
                .getThing();
    }

    /**
     * Finds the parent {@link Thing} of a given {@link GObject}.
     * @implNote Convenience over using {@link #finder} with a {@link Finder#instanceQuery()} to
     * find a {@link GObject} but return the {@link Thing} that it was found in.
     *
     * @param o The {@link GObject} whose parent {@link Thing} is to be found.
     * @return The {@link Thing} that contains the specified {@link GObject}.
     */
    public Thing findObjectParent(GObject o) {
        return finder
                .findFirst(GObject.class, Finder.instanceQuery(), o)
                .getThing();
    }

    /**
     * Finds the {@link Layer} that contains a given {@link Thing}.
     * @implNote Convenience over using {@link #finder} with a {@link Finder#instanceQuery()} to
     * find a {@link Thing} but return the {@link Layer} that it was found in.
     * @param objectParent The {@link Thing} whose containing {@link Layer} is to be found.
     * @return The {@link Layer} that contains the specified {@link Thing}.
     */
    public Layer findThingLayer(Thing objectParent) {
        return finder
                .findFirst(Thing.class, Finder.instanceQuery(), objectParent)
                .getLayer();
    }

    /**
     * Resets the entire scene by clearing all layers, selections, and unregistering all triangles from the {@link SceneRenderer}.
     * This effectively returns the sceneManager to an empty state, ready for a new project or scene.
     */
    public void resetScene() {
        getSelected().clear();
        layers.forEach(layer -> {
            if (layer.getTreeNode() == null) return;
            StaticRefs.getLayerTree().removeNode(layer.getTreeNode());
        });
        layers.stream()
                .flatMap(Collection::stream)
                .forEach(thing -> {
                    if (thing.getTreeNode() != null)
                        StaticRefs.getLayerTree().removeNode(thing.getTreeNode());
                    thing.getObjects().stream()
                            .filter(o -> o instanceof GTri)
                            .map(GTri.class::cast)
                            .forEach(GTri::deleteSelf);
                    thing.getObjects().clear();
                });
        getRenderer().clearQueue();
        layers.clear();
        points.clear();
        overlaps.clear();
        currentSelection.clear();
        unparented.clear();
        history.clear(); // also clears backup.
        StaticRefs.getMainPanel().repaint();
    }

    public void drawTriangle3D(Graphics2D graphics2D, Vector3 pos1, Vector3 pos2, Vector3 pos3) {
        ArrayList<Vector3> points = new ArrayList<>(List.of(pos1, pos2, pos3));
        drawPoly3D(graphics2D, points);
    }

    public void removeFromParent(GObject o, Thing t) {
        Thing thing = StaticRefs.getSceneManager().findObjectParent(o);
        if (thing.equals(t)) return;
        thing.remove(o);
    }
}