package com.j3d.engine;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.*;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.SelectionQuery;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Renderer is a class responsible for the creation of {@link GObject}s and handling of what's going on in the
 * render.
 * @see Graphics2D
 */
public class Renderer {
    /**
     * The dimensions of the window.
     */
    public Dimension screenSize;
    /**
     * An ArrayDeque of Layers.
     */
    public ArrayList<Layer> layers = new ArrayList<>();

    public ArrayDeque<GPoint> points = new ArrayDeque<>();
    /**
     * Factor to scale the {@link CartesianPoint} vs {@link ScreenPoint} units.
     * <p>
     * This is such that the screen space is not used as the default grid. Where (0, 1) and (0, 0) are but a pixel apart.
     * The Scale factor helps by making it such that (if SCALE is set to 10), inputting (0, 1) as a {@link CartesianPoint}, when converted to {@link ScreenPoint} it is multiplied by 10 units.
     */
    public double SCALE = 10.0;

    /**
     * Default Constructor
     * @param dim The dimensions of the screen
     */
    public Renderer(Dimension dim) {
        screenSize = dim;
        Layer bg = new Layer(Layer.backgroundId);
        layers.add(bg); // the default layer
        bg.add(new Thing(this, bg));
        layers.add(new Layer()); // testing layer.
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

    /**
     * Draws the 3D Cartesian axes (X, Y, Z) in the scene, relative to the camera's position.
     * The length of the axes is scaled based on the camera's distance from the origin.
     * @param g The Graphics2D object to draw on.
     * @param camera The camera instance used for perspective transformation.
     */
    public void axis(Graphics2D g, Camera camera) {
        double axisLength = camera.getPosition().magnitude() * 0.9;
        Vector3 origin = new Vector3(0, 0, 0);
        Vector3 offset = new Vector3(0, 0, 0); // avoids collapse

        g.setColor(Color.RED);
        this.drawLine3D(g, origin.add(offset), origin.add(new Vector3(axisLength, 0, 0)), camera);
        this.drawText3D(g, origin.add(new Vector3(axisLength+5, 0, 0)), "X", camera);

        g.setColor(Color.GREEN);
        this.drawLine3D(g, origin.add(offset), origin.add(new Vector3(0, axisLength, 0)), camera);
        this.drawText3D(g, origin.add(new Vector3(0, axisLength+5, 0)), "Y", camera);

        g.setColor(Color.BLUE);
        this.drawLine3D(g, origin.add(offset), origin.add(new Vector3(0, 0, axisLength)), camera);
        this.drawText3D(g, origin.add(new Vector3(0, 0, axisLength+5)), "Z", camera);
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

        g.setColor(new Color(255, 255, 255, 111));
        g.fillRect(p.x, p.y - height, width, height);
        g.setColor(new Color(255, 255, 255, 255));
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
     * Deletes the given GObject.
     * @param obj The object to delete.
     * @return true if the object existed and got removed.
     */
    public boolean delete(GObject obj) {
        for (Layer layer : layers) {
            if (layer.remove(obj)) {
                return true;
            }
        }
        return false;
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
        layers.getFirst().getFirst().getObjects().add(point);
        return point;
    }
    
    /**
     * Draws all objects in all layers to the screen.
     *
     * @param graphics The Graphics2D object to draw on.
     * @param camera The camera instance.
     */
    public void draw(Graphics2D graphics, Camera camera) {
            layers.forEach(layer -> layer.draw(this, graphics, camera));
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
    public GObject findThing(String Id) {
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

    /**
     * Finds a {@link Layer} by its identifier.
     * @param id The identifier of the layer to find.
     * @return The {@link Layer} with the matching identifier, or {@code null} if no such layer is found.
     */
    public Layer findLayer(String id) {
        for (Layer layer : layers) {
            if (layer.getIdentifier().equals(id)) {
                return layer;
            }
        }
        return null;
    }

    public void select(SelectionQuery selectionQuery) {

    }
}