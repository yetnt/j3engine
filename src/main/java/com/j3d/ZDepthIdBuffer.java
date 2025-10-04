package com.j3d;

import com.j3d.Main;
import com.j3d.engine.Layer;
import com.j3d.engine.geometry.geo2d.GLine;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Thing;

import java.util.ArrayList;
import java.util.UUID;

public class ZDepthIdBuffer {
    private UUID[][] buffer;
    private float[][] depthBuffer;

    // Default constructor: allocates new buffers and clears them
    public ZDepthIdBuffer() {
        this(
                new UUID[Main.scrSize.height][Main.scrSize.width],
                new float[Main.scrSize.height][Main.scrSize.width]
        );
    }

    // Constructor for using existing buffers
    public ZDepthIdBuffer(UUID[][] buffer, float[][] depthBuffer) {
        this.buffer = buffer;
        this.depthBuffer = depthBuffer;
    }

    // Resets the buffers to initial state
    public void clear() {
        for (int y = 0; y < Main.scrSize.height; y++) {
            for (int x = 0; x < Main.scrSize.width; x++) {
                buffer[y][x] = null;
                depthBuffer[y][x] = Float.POSITIVE_INFINITY;
            }
        }
    }

    public void line(GLine line) {
        int x1 = line.getStart().getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y1 = line.getStart().getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;
        int x2 = line.getEnd().getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y2 = line.getEnd().getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;
        float z = (float) line.getStart().getPivot().getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < Main.scrSize.width && y1 >= 0 && y1 < Main.scrSize.height) {
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

    public void point(GPoint pt) {
        int x = pt.getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y = pt.getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;
        float z = (float) pt.getPivot().getZ();

        if (x >= 0 && x < Main.scrSize.width && y >= 0 && y < Main.scrSize.height) {
            if (z < depthBuffer[y][x]) {
                depthBuffer[y][x] = z;
                buffer[y][x] = pt.getId();
            }
        }
    }

    public void tri(GTri triangle) {
        line(triangle.getLegA());
        line(triangle.getLegB());
        line(triangle.getLegC());

        GPoint p1 = triangle.getLegA().getStart();
        GPoint p2 = triangle.getLegB().getStart();
        GPoint p3 = triangle.getLegC().getStart();

        int x1 = p1.getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y1 = p1.getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;
        int x2 = p2.getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y2 = p2.getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;
        int x3 = p3.getPivot().toPoint(Main.camera).toScreen(Main.renderer).x;
        int y3 = p3.getPivot().toPoint(Main.camera).toScreen(Main.renderer).y;

        int minX = Math.max(0, Math.min(x1, Math.min(x2, x3)));
        int maxX = Math.min(Main.scrSize.width - 1, Math.max(x1, Math.max(x2, x3)));
        int minY = Math.max(0, Math.min(y1, Math.min(y2, y3)));
        int maxY = Math.min(Main.scrSize.height - 1, Math.max(y1, Math.max(y2, y3)));

        Main.log.println("Triangle screen bounds: " + minX + "," + maxX + " x " + minY + "," + maxY);


        int denom = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
        if (denom == 0) {
            // Degenerate triangle, skip rendering
            return;
        }

        float z1 = (float) p1.getPivot().getZ();
        float z2 = (float) p2.getPivot().getZ();
        float z3 = (float) p3.getPivot().getZ();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int w1 = (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1);
                int w2 = (x3 - x2) * (y - y2) - (y3 - y2) * (x - x2);
                int w3 = (x1 - x3) * (y - y3) - (y1 - y3) * (x - x3);

                float alpha = ((y2 - y3)*(x - x3) + (x3 - x2)*(y - y3)) / (float) denom;
                float beta  = ((y3 - y1)*(x - x3) + (x1 - x3)*(y - y3)) / (float) denom;
                float gamma = 1.0f - alpha - beta;

                float z = alpha * z1 + beta * z2 + gamma * z3;
                if ((w1 >= 0 && w2 >= 0 && w3 >= 0) || (w1 <= 0 && w2 <= 0 && w3 <= 0)) {
                    if (z < depthBuffer[y][x]) {
                        depthBuffer[y][x] = z;
                        buffer[y][x] = triangle.getId();
                    }
                }
            }
        }
    }

    public ArrayList<UUID> draw(ArrayList<Layer> layers) {
        System.out.println("Draw operation");
        for (Layer layer : layers) {
            for (Thing t : layer) {
                for (GObject obj : t.getObjects().reversed()) {
                    if (obj instanceof GTri tri) {
                        tri(tri);
                    } else if (obj instanceof GLine line) {
                        line(line);
                    } else if (obj instanceof GPoint pt) {
                        point(pt);
                    }
                }
            }
        }

        ArrayList<UUID> ids = new ArrayList<>();
        for (int y = 0; y < Main.scrSize.height; y++) {
            for (int x = 0; x < Main.scrSize.width; x++) {
                UUID id = buffer[y][x];
                if (id != null && !ids.contains(id)) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    // Getters for buffer access if needed
    public UUID[][] getBuffer() {
        return buffer;
    }

    public float[][] getDepthBuffer() {
        return depthBuffer;
    }
}
