package com.j3d.ui.engine.toolbox;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.react.history.History;
import com.j3d.gen.settings.Settings;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.j3d.ui.engine.toolbox.ToolboxButtons.*;

public abstract class ButtonsRegistry {
    public static void registerAll() {
        docsBtn();
        statsText();
        spacer();
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


    public static void editBtns() {
        registerComplex("Geometry Tools", new Subbox(s -> s
                        .add("Join", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.joinCmd,
                                new ArrayList<>(), new ArrayList<>()
                        ), "join.png")
                        .add("Measure", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.measureCmd,
                                new ArrayList<>(), new ArrayList<>()
                        ), "measure.png")
                        .add("Explode", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.explodeCmd,
                                new ArrayList<>(), new ArrayList<>()
                        ), "explode.png")),
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
        register("2D Grid", e -> {
            StaticRefs.getGrid2DPanel().toggleHidden();
        });
    }

    private static void debugPanel() {
        register("Debug Panel", e -> {
            // Toggle debug mode
            StaticRefs.getDebugPanel().toggleHidden();
        });
    }

    private static void layers() {
        register("Layers",
                e -> StaticRefs.getLayerTree().toggleHidden(),
                "layers.png");
    }

    private static void history() {
        register("History",
                e -> History.panel.toggleHidden(),
                "history.png"
        );
    }

    private static void properties() {
        register("Properties", e -> {
            // Toggle props mode
            StaticRefs.getPropertiesPanel().toggleHidden();
        }, "properties.png");
    }

    public static void docsBtn() {
        register("Documentation", e -> {
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
        registerComplex("Transform", new Subbox(s -> s
                .add("quick translate", e -> StaticRefs.getCommandParser().run(
                        CommandsManager.commands.quickTranslateCmd,
                        new ArrayList<>(), new ArrayList<>()
                ), "quicktrans.png")
                .add("translate", e -> StaticRefs.getCommandParser().run(
                        CommandsManager.commands.transform,
                        new ArrayList<>(List.of("translate")), new ArrayList<>()
                ), "translate.png")
                .add("rotate", e -> StaticRefs.getCommandParser().run(
                        CommandsManager.commands.transform,
                        new ArrayList<>(List.of("rotate")), new ArrayList<>()
                ), "rotate.png")
                .add("scale", e -> StaticRefs.getCommandParser().run(
                        CommandsManager.commands.transform,
                        new ArrayList<>(List.of("scale")), new ArrayList<>()
                ), "scale.png")),
                "transform.png");
    }

    public static void cameraBtns() {
        registerComplex("Camera", new Subbox(s -> s
                        .add("Orbit", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.camera,
                                new ArrayList<>(List.of("orbit")), new ArrayList<>()
                        ), "orbit.png")
                        .add("Look At", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.camera,
                                new ArrayList<>(List.of("lookAt")), new ArrayList<>()
                        ), "lookAt.png")
                        .add("Reset To Start", e -> {
                            StaticRefs.getCamera()
                                    .setPosition(new Vector3(20, 50, -90))
                                    .setProjectionPlane(
                                            new Vector3(0, 0, Settings.cameraProperties.focalLength.getValue())
                                    );
                        }, "reset.png")
                        .add("Info", e -> StaticRefs.getCommandParser().run(
                                CommandsManager.commands.camera,
                                new ArrayList<>(List.of("info")), new ArrayList<>()
                        ), "info.png")
                ),
                "camera.png");
    }
}
