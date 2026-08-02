package com.j3d.engine.draw.methods.utils;

import com.j3d.StaticRefs;
import com.j3d.engine.draw.methods.VisibleSort;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.StaticConfig;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * ZDepthIdBuffer is a high-performance software rasterizer used for object picking.
 * It renders a scene into an off-screen buffer, storing the UUID of the topmost object
 * at each pixel, along with its depth. This allows for fast determination of which
 * objects are visible from the camera's perspective without drawing them to the screen.
 *
 * The primary optimisations include:
 * - Integer-based triangle rasterisation using edge functions.
 * - Incremental depth calculation.
 * - Efficient collection of unique visible object IDs using a HashSet.
 *
 * @author Gemini IntelliJ Plugin
 * @see VisibleSort
 * @implNote This was generated with Gemini along with {@link VisibleSort}. I take no authorship
 * and was purely trying to see what an implementation of an z-buffer would "look" like
 */
public class ZDepthIdBuffer {
    private final UUID[][] buffer;
    private final float[][] depthBuffer;
    private final int width;
    private final int height;

    // Default constructor: allocates new buffers
    public ZDepthIdBuffer() {
        this(StaticConfig.screenSize.width, StaticConfig.screenSize.height);
    }

    // Constructor for specific dimensions
    public ZDepthIdBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new UUID[height][width];
        this.depthBuffer = new float[height][width];
    }

    // Constructor for using existing buffers (for advanced use cases like buffer sharing)
    public ZDepthIdBuffer(UUID[][] buffer, float[][] depthBuffer) {
        this.buffer = buffer;
        this.depthBuffer = depthBuffer;
        this.height = buffer.length;
        this.width = buffer[0].length;
    }

    // Resets the buffers to their initial state for a new frame
    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = null;
                depthBuffer[y][x] = Float.POSITIVE_INFINITY;
            }
        }
    }

    // Rasterizes a line using Bresenham's algorithm with depth interpolation
    public void line(GLine line) {
        Point p1 = line.getA().getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();
        Point p2 = line.getB().getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();
        float z1 = (float) line.getA().getPivot().getZ();
        float z2 = (float) line.getB().getPivot().getZ();

        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        float totalDistance = (float) Math.sqrt(dx * dx + dy * dy);

        while (true) {
            if (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) {
                float currentDistance = (float) Math.sqrt(Math.pow(x1 - p1.x, 2) + Math.pow(y1 - p1.y, 2));
                float t = (totalDistance == 0) ? 0 : currentDistance / totalDistance;
                float z = z1 * (1 - t) + z2 * t;

                if (z < depthBuffer[y1][x1]) {
                    depthBuffer[y1][x1] = z;
                    buffer[y1][x1] = line.getId();
                }
            }
            if (x1 == x2 && y1 == y2) break;
            int err2 = err * 2;
            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    // Rasterizes a single point
    public void point(GPoint pt) {
        Point screenPt = pt.getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();
        int x = screenPt.x;
        int y = screenPt.y;
        float z = (float) pt.getPivot().getZ();

        if (x >= 0 && x < width && y >= 0 && y < height) {
            if (z < depthBuffer[y][x]) {
                depthBuffer[y][x] = z;
                buffer[y][x] = pt.getId();
            }
        }
    }

    /**
     * Rasterizes a triangle using an efficient, integer-based edge-function algorithm.
     * This avoids costly floating-point operations per pixel.
     */
    public void tri(GTri triangle) {
        Point p1 = triangle.getLegA().getA().getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();
        Point p2 = triangle.getLegB().getA().getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();
        Point p3 = triangle.getLegC().getA().getPivot().toPoint(StaticRefs.getCamera()).toScreen(StaticRefs.getSceneManager()).toSwingPoint();

        float z1 = (float) triangle.getLegA().getA().getPivot().getZ();
        float z2 = (float) triangle.getLegB().getA().getPivot().getZ();
        float z3 = (float) triangle.getLegC().getA().getPivot().getZ();

        // Bounding box for the triangle, clamped to screen dimensions
        int minX = Math.max(0, Math.min(p1.x, Math.min(p2.x, p3.x)));
        int maxX = Math.min(width - 1, Math.max(p1.x, Math.max(p2.x, p3.x)));
        int minY = Math.max(0, Math.min(p1.y, Math.min(p2.y, p3.y)));
        int maxY = Math.min(height - 1, Math.max(p1.y, Math.max(p2.y, p3.y)));

        // Edge functions
        int dx12 = p1.x - p2.x;
        int dy12 = p1.y - p2.y;
        int dx23 = p2.x - p3.x;
        int dy23 = p2.y - p3.y;
        int dx31 = p3.x - p1.x;
        int dy31 = p3.y - p1.y;

        // Area of the triangle multiplied by 2
        int area = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
        if (area == 0) return; // Degenerate or line triangle

        // Barycentric coordinates at the starting point of the loop (minX, minY)
        int w1_row = (minX - p2.x) * dy23 - (minY - p2.y) * dx23;
        int w2_row = (minX - p3.x) * dy31 - (minY - p3.y) * dx31;
        int w3_row = (minX - p1.x) * dy12 - (minY - p1.y) * dx12;

        // Rasterize
        for (int y = minY; y <= maxY; y++) {
            int w1 = w1_row;
            int w2 = w2_row;
            int w3 = w3_row;

            for (int x = minX; x <= maxX; x++) {
                // Check if the pixel is inside the triangle
                if ((w1 | w2 | w3) >= 0) {
                    float alpha = (float) w1 / area;
                    float beta = (float) w2 / area;
                    float gamma = (float) w3 / area;

                    // Interpolate depth
                    float z = alpha * z3 + beta * z1 + gamma * z2;

                    if (z < depthBuffer[y][x]) {
                        depthBuffer[y][x] = z;
                        buffer[y][x] = triangle.getId();
                    }
                }
                // Incrementally update barycentric coordinates
                w1 += dy23;
                w2 += dy31;
                w3 += dy12;
            }
            // Update for the next row
            w1_row -= dx23;
            w2_row -= dx31;
            w3_row -= dx12;
        }
    }

    /**
     * Processes all layers and returns a list of unique, visible object UUIDs.
     * This uses a HashSet for efficient collection of unique IDs.
     */
    public ArrayList<UUID> draw(ArrayList<Layer> layers) {
        // Rasterize all objects into the buffer following the Layer -> Thing -> GObject hierarchy
        for (Layer layer : layers) {
            for (Thing thing : layer) {
                // It's often best to draw 3D objects from back to front for transparency, 
                // but for an ID buffer, the order of objects within a Thing doesn't matter.
                for (GObject subObject : thing.getObjects()) {
                    rasterizeObject(subObject);
                }
            }
        }

        // Collect unique IDs efficiently
        HashSet<UUID> ids = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                UUID id = buffer[y][x];
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return new ArrayList<>(ids);
    }
    
    private void rasterizeObject(GObject obj) {
        if (obj instanceof GTri tri) {
            tri(tri);
        } else if (obj instanceof GLine line) {
            line(line);
        } else if (obj instanceof GPoint pt) {
            point(pt);
        }
    }

    // Getters for buffer access if needed
    public UUID[][] getBuffer() {
        return buffer;
    }

    public float[][] getDepthBuffer() {
        return depthBuffer;
    }
}
