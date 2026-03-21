package com.j3d;

import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.*;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.react.actions.Action;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Executor is a class called by {@link EngineFrame#main(String[])} that just draws things ot the window
 */
public class Executor {
    /**
     * The renderer instance.
     */
    private final Renderer renderer;

    private final Layer layer = new Layer("exec");

    /**
     * Default Constructor
     * @param r The Renderer Instance.
     */
    public Executor(Renderer r) {
        renderer = r;
    }

    /**
     * Runs the executor.
     */
    public void run(Graphics2D graphics2D) {
        Static.renderer.layers.add(layer);
        Thing cub = cube();
        Thing tris = threeTris();
        ArrayList<Action<?>> actions = new ArrayList<>(List.of(
                cub.rotate(new Vector3(0, 0, 1), 45),
                cub.translate(new Vector3(4, 2, 3)),
                cub.scale(0.4),
                tris.translate(new Vector3(14, 0, 0)),
                cub.rotate(new Vector3(2, 3, 1), 2)
        ));
        actions.forEach(Action::run);
        actions.forEach(Renderer.history::add);

        Static.renderer.scheduleOverlap(UUID.randomUUID(), g -> {
            // draws a dot at (0, 0) and projects it to both a Vector3 and ScreenPoint for alignment purposes
            // Purely for testing.
            Vector3 p = new CartesianPoint(0, 0).toVector3(Static.camera);
            Vector3 z = new CartesianPoint(0, 10).toVector3(Static.camera);

            int circleSize = 10;
            g.setColor(Color.BLACK);
            ScreenPoint p2 = p.toPoint(Static.camera).toScreen(Static.renderer);
            ScreenPoint z2 = z.toPoint(Static.camera).toScreen(Static.renderer);
            g.fillOval(p2.x - circleSize / 2, p2.y - circleSize / 2, circleSize, circleSize);
            g.fillOval(z2.x - circleSize / 2, z2.y - circleSize / 2, circleSize, circleSize);
        });
    }

    /**
     * Creates three triangles stacked vertically along the Z axis.
     * This is mainly for depth testing.
     */
    public Thing threeTris() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(0, 10, 0));
        GPoint C = new GPoint(new Vector3(10, 10, 0));
        GTri tri1 = new GTri(Color.ORANGE, A, B, C);

        GPoint A1 = new GPoint(new Vector3(10, 0, 10));
        GPoint B1 = new GPoint(new Vector3(0, 10, 10));
        GPoint C1 = new GPoint(new Vector3(10, 10, 10));
        GTri tri2 = new GTri(Color.PINK, A1, B1, C1);

        GPoint A2 = new GPoint(new Vector3(10, 0, 20));
        GPoint B2 = new GPoint(new Vector3(0, 10, 20));
        GPoint C2 = new GPoint(new Vector3(10, 10, 20));
        GTri tri3 = new GTri(Color.CYAN, A2, B2, C2);

        ArrayList<GTri> tris = new ArrayList<>();
        tris.add(tri1);
        tris.add(tri2);
        tris.add(tri3);

        return new Thing(renderer, layer, "Three Tris").addObjs(tri1, tri2, tri3,
                tri1.getLegA(), tri1.getLegB(), tri1.getLegC(),
                tri2.getLegA(), tri2.getLegB(), tri2.getLegC(),
                tri3.getLegA(), tri3.getLegB(), tri3.getLegC(),
                A, B, C,
                A1, B1, C1,
                A2, B2, C2
        );
    }

    public Thing test() {
        GPoint A = new GPoint(new Vector3(10, 0, 0));
        GPoint B = new GPoint(new Vector3(0, 10, 0));
        GPoint C = new GPoint(new Vector3(0, 0, 10));
        GTri triangl = new GTri(Color.ORANGE, A, B, C);
        Static.log.println(triangl.getId().toString());
        return new Thing(renderer, null, "Test").addObjs(triangl, triangl.getLegA(), triangl.getLegB(), triangl.getLegC(), A, B, C);
    }

    public Thing cube() {
        GPoint A = new GPoint(new Vector3(-5, 5, -5));
        GPoint B = new GPoint(new Vector3(-5, -5, -5));
        GPoint C = new GPoint(new Vector3(5, 5, -5));
        GTri face1tri1 = new GTri(Color.ORANGE, A, B, C);
        GPoint D = new GPoint(new Vector3(5, -5, -5));
        GTri face1tri2 = new GTri(Color.ORANGE.darker(), B, C, D);
        GPoint E = new GPoint(new Vector3(5, 5, 5));
        GTri face2tri1 = new GTri(Color.PINK, D, C, E);
        GPoint F = new GPoint(new Vector3(5, -5, 5));
        GTri face2tri2 = new GTri(Color.PINK.darker(), D, E, F);
        GPoint H = new GPoint(new Vector3(-5, 5, 5));
        GTri face3tri1 = new GTri(Color.GREEN, F, E, H);
        GPoint G = new GPoint(new Vector3(-5, -5, 5));
        GTri face3tri2 = new GTri(Color.GREEN.darker(), F, H, G);
        GTri face4tri1 = new GTri(Color.BLUE, H, A, G);
        GTri face4tri2 = new GTri(Color.BLUE.darker(), A, G, B);
        GTri face5tri1 = new GTri(Color.LIGHT_GRAY, A, H, C);
        GTri face5tri2 = new GTri(Color.LIGHT_GRAY.darker(), H, C, E);
        GTri face6tri1 = new GTri(Color.YELLOW, D, F, B);
        GTri face6tri2 = new GTri(Color.YELLOW.darker(), F, B, G);

        return new Thing(renderer, layer, "Cube").addObjs(
                A, B, C, D, E, F, G, H, face1tri1, face1tri2, face2tri1, face2tri2, face3tri1, face3tri2, face4tri1, face4tri2,
                face5tri1, face5tri2, face6tri1, face6tri2,
                face1tri1.getLegA(), face1tri1.getLegB(), face1tri1.getLegC(),
                face1tri2.getLegA(), face1tri2.getLegB(), face1tri2.getLegC(),
                face2tri1.getLegA(), face2tri1.getLegB(), face2tri1.getLegC(),
                face2tri2.getLegA(), face2tri2.getLegB(), face2tri2.getLegC(),
                face3tri1.getLegA(), face3tri1.getLegB(), face3tri1.getLegC(),
                face3tri2.getLegA(), face3tri2.getLegB(), face3tri2.getLegC(),
                face4tri1.getLegA(), face4tri1.getLegB(), face4tri1.getLegC(),
                face4tri2.getLegA(), face4tri2.getLegB(), face4tri2.getLegC(),
                face5tri1.getLegA(), face5tri1.getLegB(), face5tri1.getLegC(),
                face5tri2.getLegA(), face5tri2.getLegB(), face5tri2.getLegC(),
                face6tri1.getLegA(), face6tri1.getLegB(), face6tri1.getLegC(),
                face6tri2.getLegA(), face6tri2.getLegB(), face6tri2.getLegC()

        );
    }
}
