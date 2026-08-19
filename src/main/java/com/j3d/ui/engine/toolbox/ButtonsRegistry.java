package com.j3d.ui.engine.toolbox;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.history.History;
import com.j3d.gen.settings.Settings;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.j3d.ui.engine.toolbox.ToolboxButtons.*;

// da butoons for the toolbox.

public abstract class ButtonsRegistry {

    public static void registerAll() {
        docsBtn();
        statsText();
        spacer();
        newBtns();
        transformBtns();
        editBtns();
        spacer();
        properties();
        layers();
        selectionStatsText();
        spacer();
        history();
        spacer();
        cameraBtns();
        spacer();
        debugPanel();
        grid();
    }

    private static void newBtns() {
        registerComplex(
                "New Geometry",
                "Create new stuff from thin air",
                new Subbox(s -> s
                        .add(
                                "Cube", "Creates a new cube",
                                e -> CreateTools.CUBE.run(), "newCube.png"
                        )
                        .add(
                                "Prism", "Creates a new prism",
                                e -> CreateTools.PRISM.run(), "newPrism.png"
                        )
                        .add(
                                "Triangle", "Creates a new triangle",
                                e -> CreateTools.TRI.run(), "newTri.png"
                        )
                        .add(
                                "Point", "Creates a new point",
                                e -> CreateTools.POINT.run(), "newPoint.png"
                        )
                ), "new.png");
    }

    public static void editBtns() {
        registerComplex(
                "Geometry Tools",
                "Tools related to editing or inspecting geometry.",
                new Subbox(s -> s
                        .add("Join",
                                "(Selection) Joins 2 points into a line, or 3 points into a Bezier Curve.",
                                e -> GeometryTools.JOIN.run(), "join.png")
                        .add("Measure",
                                "(Selection) Measures the distance between 2 points or the area formed by 3 points",
                                e -> GeometryTools.MEASURE.run(), "measure.png")
                        .add("Explode",
                                "Explodes everything or a selection of objects into points. (Dangerous)",
                                e -> GeometryTools.EXPLODE.run(), "explode.png")
                        .add("Extrude",
                                "(Selection) Extrude a selected triangle into a 3d shape.",
                                // todo: EXTRUDE IMAGE
                                e -> GeometryTools.EXTRUDE.run(), "geo.png")),
                "geo.png");
    }

    private static void selectionStatsText() {
        tripleText(
                "No Objects Selected",
                "0 Layers",
                "0 Things",
                2,
                (a, b, c) -> {

                    ScheduledExecutorService executor =
                            Executors.newSingleThreadScheduledExecutor();

                    executor.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> {
                        if (StaticRefs.getSceneManager() == null) return;
                        int selected = StaticRefs.getSceneManager().getSelected().size();
                        a.setText(
                                (selected == 0 ? "No " : selected)
                                + " Object"
                                + (selected == 1 ? "" : "s") +
                                " Selected"
                        );
                        int layerSize = StaticRefs.getSceneManager().layers.size()-1;
                        b.setText(layerSize + (layerSize == 1 ? " layer" : " layers"));
                        int things = Math.toIntExact(StaticRefs.getSceneManager().layers.thingStream().count());
                        c.setText(things + (things == 1 ? " thing" : " things"));
                    }), 0, 1, TimeUnit.SECONDS);

                    Runtime.getRuntime().addShutdownHook(
                            new Thread(executor::shutdown)
                    );
                }
        );
    }

    private static void grid() {
        register("2D Grid",
                "A grid that allows the making of lines and points in 2D and rendering to 3D"
                ,e -> {
            StaticRefs.getGrid2DPanel().toggleHidden();
        });
    }

    private static void debugPanel() {
        register("Debug Panel",
                "Debug panel",
                e -> {
            // Toggle debug mode
            StaticRefs.getDebugPanel().toggleHidden();
        });
    }

    private static void layers() {
        register("Layers",
                "Display the scene's layer tree along with the Things inside.",
                e -> StaticRefs.getLayerTree().toggleHidden(),
                "layers.png");
    }

    private static void history() {
        register("History",
                "The history of everything that has been executed",
                e -> History.panel.toggleHidden(),
                "history.png"
        );
    }

    private static void properties() {
        register("Properties",
                "The properties of what you've currently selected.",
                e -> {
            // Toggle props mode
            StaticRefs.getPropertiesPanel().toggleHidden();
        }, "properties.png");
    }

    public static void docsBtn() {
        register("Documentation",
                "Documentation pages for extra help",
                e -> {
            // Toggle props mode
            StaticRefs.getDocsProvider().provideMain().setVisible(true);
        }, "docs.png");
    }

    public static void statsText() {
        tripleText(
                "J3Engine",
                "CPU: 0%",
                "J3D: 0%",
                2,
                (a, b, c) -> {

                    ScheduledExecutorService executor =
                            Executors.newSingleThreadScheduledExecutor();

                    executor.scheduleAtFixedRate(() -> {
                        OperatingSystemMXBean os =
                                (OperatingSystemMXBean)
                                        ManagementFactory.getOperatingSystemMXBean();

                        double system = os.getCpuLoad() * 100.0;
                        double process = os.getProcessCpuLoad() * 100.0;


                        SwingUtilities.invokeLater(() -> {
                            b.setText(String.format("CPU (Total): %.1f%%", system));
                            c.setText(String.format("CPU (By J3D) : %.1f%%", process));
                        });
                    }, 0, 1, TimeUnit.SECONDS);

                    Runtime.getRuntime().addShutdownHook(
                            new Thread(executor::shutdown)
                    );
                }
        );
    }

    public static void transformBtns() {
        registerComplex("Transform",
                "Tools for transforming a selection of objects",
                new Subbox(s -> s
                .add("Quick Translate",
                        "Quickly move stuff around using the mouse only.",
                        e -> TransformOperations.QUICK_TRANSLATE.run(), "quicktrans.png")
                .add("Translate",
                        "Translate a selection of objects via keyboard input.",
                        e -> TransformOperations.TRANSLATE.run(), "translate.png")
                .add("Rotate",
                        "Rotate a selection of objects via keyboard input.",
                        e -> TransformOperations.ROTATE.run(), "rotate.png")
                .add("Scale",
                        "Scale a selection of objects via keybaord input.",
                        e -> TransformOperations.SCALE.run(), "scale.png")),
                "transform.png");
    }

    public static void cameraBtns() {
        registerComplex("Camera",
                "Camera related operations and quick buttons",
                new Subbox(s -> s
                        .add("Orbit",
                                "Orbit the camera around itself (turn on CAPS LOCK for world centre)",
                                e -> CameraOperations.ORBIT.run(), "orbit.png")
                        .add("Look At",
                                "Directly look at a selection of objects",
                                e -> CameraOperations.LOOK_AT.run(), "lookAt.png")
                        .add("Reset To Start",
                                "Reset the camera's position and orientation to the engine startup values",
                                e -> CameraOperations.RESET.run(), "reset.png")
                        .add("Info",
                                "Camera rotation and position information",
                                e -> CameraOperations.INFO.run(), "info.png")
                ),
                "camera.png");
    }

    /**
     * Provides runnable actions for creating various geometric primitives.
     */
    public static class CreateTools {
        public static final Runnable CUBE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.createCmd,
                new ArrayList<>(Collections.singleton("cube")), new ArrayList<>()
        );
        public static final Runnable TRI = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.createCmd,
                new ArrayList<>(Collections.singleton("tri")), new ArrayList<>()
        );
        public static final Runnable POINT = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.createCmd,
                new ArrayList<>(Collections.singleton("point")), new ArrayList<>()
        );
        public static final Runnable PRISM = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.prismCmd,
                new ArrayList<>(List.of(
                        Vector3.ZERO,
                        Vector3.Y(10)
                )), new ArrayList<>(Collections.singleton(
                        new TaggedArgValue<>("XZ").setName("plane")
                ))
        );
    }

    /**
     * Provides runnable actions for various geometry manipulation tools.
     */
    public static class GeometryTools {
        public static final Runnable JOIN = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.joinCmd,
                new ArrayList<>(), new ArrayList<>()
        );
        public static final Runnable MEASURE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.measureCmd,
                new ArrayList<>(), new ArrayList<>()
        );
        public static final Runnable EXPLODE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.explodeCmd,
                new ArrayList<>(Collections.singleton("j")), new ArrayList<>()
        );
        public static final Runnable EXTRUDE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.extrudeCmd,
                new ArrayList<>(), new ArrayList<>()
        );
    }

    /**
     * Provides runnable actions for transforming selected objects.
     */
    public static class TransformOperations {
        public static final Runnable QUICK_TRANSLATE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.quickTranslateCmd,
                new ArrayList<>(), new ArrayList<>()
        );
        public static final Runnable TRANSLATE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.transform,
                new ArrayList<>(List.of("translate")), new ArrayList<>()
        );
        public static final Runnable ROTATE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.transform,
                new ArrayList<>(List.of("rotate")), new ArrayList<>()
        );
        public static final Runnable SCALE = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.transform,
                new ArrayList<>(List.of("scale")), new ArrayList<>()
        );
    }

    /**
     * Provides runnable actions for camera control and information.
     */
    public static class CameraOperations {
        public static final Runnable ORBIT = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.camera,
                new ArrayList<>(List.of("orbit")), new ArrayList<>()
        );
        public static final Runnable LOOK_AT = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.camera,
                new ArrayList<>(List.of("lookAt")), new ArrayList<>()
        );
        public static final Runnable RESET = () -> {
            StaticRefs.getCamera()
                    .setPosition(new Vector3(20, 50, -90))
                    .setProjectionPlane(
                            new Vector3(0, 0, Settings.cameraProperties.focalLength.getValue())
                    );
        };
        public static final Runnable INFO = () -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.camera,
                new ArrayList<>(List.of("info")), new ArrayList<>()
        );
    }
}
