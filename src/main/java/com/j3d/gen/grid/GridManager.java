package com.j3d.gen.grid;

import com.j3d.StaticRefs;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.Dim;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.convert.ConversionWithOffset;
import com.j3d.engine.math.convert.Offset;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.ui.engine.floating.grid2d.Grid;
import com.j3d.ui.engine.floating.grid2d.Grid2DPanel;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generic.tuple.SamePair;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.j3d.StaticRefs.*;
import static com.j3d.ui.engine.floating.grid2d.Grid2DPanel.mousePosInPanel;

public class GridManager {

    private final ArrayList<GridObject<?>> objects = new ArrayList<>();
    private final int ovalRadius = 10;
    /**
     * Temporary consumers which {@link Grid2DPanel} might want. One being the deletion line drawn atop of everything.
     */
    private final HashMap<UUID, Consumer<Graphics2D>> temporaryDrawConsumers = new HashMap<>();

    private final Grid2DPanel panel;

    private boolean deleteMode = false;

    /**
     * Grid lines. Except here the lines are represented as 2 screen points to join in a SamePair.
     */
    private final ArrayList<SamePair<ScreenPoint>> gridlinesPoints = new ArrayList<>();

    public GridManager(Grid2DPanel panel) {
        this.panel = panel;
        init();
    }

    public ArrayList<GridObject<?>> getObjects() {
        return objects;
    }

    public boolean isDeleteMode() {
        return deleteMode;
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public HashMap<UUID, Consumer<Graphics2D>> getTemporaryDrawConsumers() {
        return temporaryDrawConsumers;
    }

    public void repaint() {
        gridlinesPoints.clear();
        grid();
        panel.getDrawPanel().repaint();
    }

    /**
     * initialises all the drawing shenanigans we need to do being:
     * <ul>
     *     <li>The origin point</li>
     *     <li>The grid lines</li>
     *     <li>Temporary consumers</li>
     * </ul>
     */
    private void init() {
        ((Grid)panel.getDrawPanel()).setConsumer((g) -> {
            // Draw the origin point
            ConversionWithOffset props = new ConversionWithOffset(
                    panel.getScale(),
                    new Dim(panel.getDrawPanel().getWidth(), panel.getDrawPanel().getHeight()),
                    fromMut()
            );
            CartesianPoint cp = new CartesianPoint(0, 0);
            ScreenPoint sp =
                    cp.toScreen(
                            props
                    );
            Color original = J3DTheme.TEXT_PRIMARY.color();
            Color col = new Color(
                    original.getRed(),
                    original.getGreen(),
                    original.getBlue(),
                    panel.getScale() >= 20 ? 255 : (int)((panel.getScale()/20) * 255)
                    );
            g.setColor(col);

            // Draw all the grid lines
            gridlinesPoints.forEach(p -> {
                g.drawLine(
                        p.first.x,
                        p.first.y,
                        p.second.x,
                        p.second.y
                );
            });
            g.setColor(original);
            g.fillOval(sp.x - ovalRadius /2, sp.y - ovalRadius /2, ovalRadius, ovalRadius);
            objects.forEach(
                    gr -> gr.draw(g, props)
            );
            temporaryDrawConsumers.forEach((i, s) -> {
                s.accept(g);
            });
        });
        getSceneManager().scheduleOverlap(
                panel.getOverlapId(),
                (g) -> {
                    if (panel.floatingPanel.isHidden()) return;
                    AxisPlane p = new AxisPlane(panel.getOrigin(), panel.getV1(), panel.getV2());
                    drawMouse(g, mousePosInPanel, p);
                    drawDirArr(g, new CartesianPoint(0, 10), p, false);
                    drawDirArr(g, new CartesianPoint(10, 0), p, true);
                    Vector3 point = new AxisPlane(panel.getOrigin(), panel.getV1(), panel.getV2())
                            .toWorld(mousePosInPanel);
                    ScreenPoint sp =
                            point.toPoint(getCamera())
                                    .toScreen();
                    g.setColor(Color.cyan);
                    g.drawOval(sp.x - ovalRadius, sp.y - ovalRadius, ovalRadius *2, ovalRadius *2);
                    g.setColor(Color.white);
                    getMainPanel().repaint();
                    objects.forEach(gr -> gr.drawWorld(g, new AxisPlane(panel.getOrigin(), panel.getV1(), panel.getV2())));
                }
        );
        panel.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        repaint();
                    }
                }
        );
        grid();
    }

    /**
     * Register a grid line to draw on the grid
     * @param p Point A
     * @param s Point B
     */
    private void regInGrid(CartesianPoint p, CartesianPoint s) {
        Dim dim = panel.getGrid().sizeDim();
        ConversionWithOffset conversionProperties = new ConversionWithOffset(panel.getScale(), dim, fromMut());
        gridlinesPoints.add(
                new SamePair<>(
                        p.toScreen(conversionProperties),
                        s.toScreen(conversionProperties)
                )
        );
    }

    /**
     * Draws the grid with all scaling applied.
     */
    private void grid() {
        Dim panelSize = panel.getGrid().sizeDim();
        if (panelSize.width == 0 || panelSize.height == 0) return;

        // Convert screen corners to Cartesian points to find the bounds
        ConversionWithOffset conversionProperties = new ConversionWithOffset(panel.getScale(), panelSize, fromMut());
        CartesianPoint topLeft = new ScreenPoint(0, 0).toPoint(conversionProperties);
        CartesianPoint bottomRight = new ScreenPoint(panelSize.width, panelSize.height).toPoint(conversionProperties);

        double startX = Math.floor(topLeft.x);
        double endX = Math.ceil(bottomRight.x);
        double startY = Math.floor(bottomRight.y);
        double endY = Math.ceil(topLeft.y);

        // Draw vertical lines
        for (double x = startX; x <= endX; x++) {
            regInGrid(new CartesianPoint(x, startY), new CartesianPoint(x, endY));
        }

        // Draw horizontal lines
        for (double y = startY; y <= endY; y++) {
            regInGrid(new CartesianPoint(startX, y), new CartesianPoint(endX, y));
        }
    }

    /**
     * If the given point's coordinates dont already exist within {@link #objects}, add it.
     * @param p The point supplier (why the fuck is this a supplier)
     */
    public void existing(Supplier<Point> p) {
        Point o = p.get();
        Point p2 = objects
                .stream()
                .filter(
                        o2 -> o2 instanceof Point
                )
                .map(
                        o2 -> (Point)o2
                )
                .filter(
                        point -> point.getPoint().equals(o.getPoint())
                )
                .findFirst()
                .orElse(null);
        if (p2 == null) {
            objects.add(o);
        }
    }

    /**
     * Delete a point at the given coordinates.
     * @param cartesianPoint The position of the point to delete at.
     */
    public void delete(CartesianPoint cartesianPoint) {
        // look for a point at the current position, if any matches delete said points.
        new ArrayList<>(objects).stream()
                .filter(
                        o -> o instanceof Point
                )
                .map(
                        o -> (Point) o
                )
                .filter(
                        point -> point.getPoint().equals(cartesianPoint)
                )
                .forEach(objects::remove);
    }

    /**
     * Delete any point or line which intersects with the given line
     * @param l The query line. usually drawn by the user.
     */
    public void delete(Line l) {
        // if this line intersects with any other line in this list, delete that line
        new ArrayList<>(objects)
                .stream()
                .peek(o -> {
                    if (o instanceof Point p)
                        if (p.getPoint().equals(l.getP1()) || p.getPoint().equals(l.getP2()))
                            objects.remove(p);
                })
                .filter(
                        lw -> lw instanceof Line
                )
                .map(
                        lw -> (Line) lw
                )
                .filter(
                        lw ->
                                (lw.getP1().equals(l.getP1()) && lw.getP2().equals(l.getP2()))
                                        || (lw.getP1().equals(l.getP2()) && lw.getP2().equals(l.getP1()))
                                        || Line.intersects(l, lw)
                )
                .forEach(objects::remove);
    }

    /**
     * Draws the mouse in the world on the actual 3D plane
     * @param g Graphics bro.
     * @param mousePos The position of the mouse in cartesian terms according to the plane
     * @param plane The plane in question
     */
    public static void drawMouse(Graphics2D g, CartesianPoint mousePos, AxisPlane plane) {
        int arrowSize = 1;
        double sideLength = 0.25;
        // not really the side length since this is more the offset to make the diagonal
        // which is the actual line we care about.
        // the actual side length will be sqrt(4)
        double x = mousePos.x;
        double y = mousePos.y;
        ArrayList<CartesianPoint> points = new ArrayList<>(List.of(mousePos));

        // tip is the mouse position (first element)
        points.add(new CartesianPoint(x, y - 3*arrowSize)); // tail
        points.add(new CartesianPoint(x - sideLength, y - sideLength)); // Bottom-left
        points.add(new CartesianPoint(x + sideLength, y - sideLength)); // Bottom-right

        ArrayList<Vector3> points2 = points
                .stream().map(plane::toWorld)
                .collect(Collectors.toCollection(ArrayList::new));

        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(1), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(2), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(3), getCamera()
        );

        getSceneManager().drawText3D(
                g, points2.getFirst(),
                mousePos.friendlyString() +" -> " + points2.getFirst().toCommandPaletteString() + " | " + StaticRefs.getGrid2DPanel().getScale(),
                getCamera(),
                J3DTheme.UI_SURFACE.color(),
                J3DTheme.TEXT_PRIMARY.color()
        );
    }

    public static void drawDirArr(Graphics2D g, CartesianPoint pos, AxisPlane plane, boolean xDir) {
        double x = pos.x;
        double y = pos.y;
        ArrayList<CartesianPoint> points = new ArrayList<>(List.of(pos));

        if (xDir) {
            // tip is the mouse position (first element)
            points.add(new CartesianPoint(x-1, y)); // tail
            points.add(new CartesianPoint(x - 1, y - 1)); // Bottom-left
            points.add(new CartesianPoint(x - 1, y + 1)); // Bottom-right
        } else {
            // tip is the mouse position (first element)
            points.add(new CartesianPoint(x, y - 1)); // tail
            points.add(new CartesianPoint(x - 1, y - 1)); // Bottom-left
            points.add(new CartesianPoint(x + 1, y - 1)); // Bottom-right
        }

        ArrayList<Vector3> points2 = points
                .stream().map(plane::toWorld)
                .collect(Collectors.toCollection(ArrayList::new));

        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(1), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(2), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(3), getCamera()
        );

//        getSceneManager().drawText3D(
//                g, points2.getFirst(),
//                pos.friendlyString() +" -> " + points2.getFirst().toCommandPaletteString() + " | " + StaticRefs.getGrid2DPanel().getScale(),
//                getCamera(),
//                J3DTheme.UI_SURFACE.color(),
//                J3DTheme.TEXT_PRIMARY.color()
//        );
    }

    public static Offset fromMut() {
        return Offset.fromMutablePair(Grid2DPanel.offset);
    }
}
