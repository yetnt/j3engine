package com.j3d.ui.engine.toolbox;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.scene.Camera;
import com.j3d.engine.react.history.History;
import com.j3d.storage.files.engine.DebugDump;
import com.j3d.threads.LongTask;
import com.j3d.ui.theme.CursorManager;
import com.j3d.ui.theme.CursorNames;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generic.func.TrinaryConsumer;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ToolboxButtons {
    private static final ArrayList<JPanel> toolboxButtons = new ArrayList<>();
    public static Subbox currentViewableSubbox = null;
    public static final int MAX_BUTTONS = 6;
    public static final int BUTTON_PANEL_SIZE = 120;

    static {
        register("Documentation", e -> {
            // Toggle props mode
            StaticRefs.getDocsProvider().provideMain().setVisible(true);
        }, "docs.png");
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
                ), "scale.png")), "transform.png");
        register("Properties", e -> {
            // Toggle props mode
            StaticRefs.getPropertiesPanel().toggleHidden();
        }, "properties.png");
        register("Orbit", e -> StaticRefs.getCommandParser().run(
                CommandsManager.commands.camera,
                new ArrayList<>(List.of("orbit")), new ArrayList<>()), "orbit.png");
        register("History",
                e -> History.panel.toggleHidden(),
                "history.png"
        );
        register("Layers",
                e -> StaticRefs.getLayerTree().toggleHidden(),
                "layers.png");
        register("Debug Panel", e -> {
            // Toggle debug mode
            StaticRefs.getDebugPanel().toggleHidden();
        });
        register("2D Grid", e -> {
            StaticRefs.getGrid2DPanel().toggleHidden();
        });

        // another for example
        register("Toggle Spinner", e -> {
            String nString = JOptionPane.showInputDialog("Input time in ms to sleep");
            if (nString == null) return;
            int n = Integer.parseInt(nString);
            LongTask<Void> task = new LongTask<>(
                    t -> {
                        int max = 10;
                        t.progressStart("Doing stuff", max);
                        try {
                            for (int i = 0; i < max; i++) {
                                Thread.sleep(n);
                                t.updateProgress(i);
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        return null;
                    },
                    (t, i, completed)-> {
                        // No cleanup needed.
                    }
            );
            task.run();
        });
        register("Dump to Debug", e -> {
            long current = System.currentTimeMillis();
            Camera cam = StaticRefs.getCamera();
            try (PrintWriter out = StaticRefs.getEngineFiles().debugDump.writer(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".csv")) {
                DebugDump.print(out, current, cam);
                Desktop.getDesktop().open(StaticRefs.getEngineFiles().debugDump.getFolder());
            } catch (RuntimeException | IOException ex) {
                throw new RuntimeException(ex);
            }

        });

    }

    public static void registerComplex(String label, Subbox sub, String imageFileName) {
        sub.setPreferredSize(
                new Dimension(
                        Math.min(sub.getButtons(), MAX_BUTTONS) * BUTTON_PANEL_SIZE,
                        sub.getPreferredSize().height + 2
                )
        );
        sub.toolboxScrollpane.setPreferredSize(
                new Dimension(
                        Math.min(sub.getButtons(), MAX_BUTTONS) * BUTTON_PANEL_SIZE,
                        sub.getPreferredSize().height
                )
        );
        JButton l = register(label, new ActionListener() {
            final Subbox s = sub;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentViewableSubbox == s) {
                    sub.delete();
                    currentViewableSubbox = null;
                } else {
                    if (currentViewableSubbox instanceof Subbox other)
                        other.delete();
                    sub.setBounds(
                            StaticRefs.getMainFrame().getWidth() / 2 - (sub.getPreferredSize().width / 2),
                            (StaticRefs.getMainFrame().getHeight() / 2 - (sub.getPreferredSize().height / 2)) - 30,
                            sub.getPreferredSize().width,
                            sub.getPreferredSize().height + 10
                    );
                    StaticRefs.getMainFrame().getLayeredPane().add(sub, JLayeredPane.DRAG_LAYER +1);

                    s.setEnabled(true);
                    s.setVisible(true);
                    currentViewableSubbox = s;
                }
                StaticRefs.getMainFrame().repaint();
                StaticRefs.getMainFrame().revalidate();
            }
        });
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sub.mousePos = e.getLocationOnScreen();
            }
        });
        ImageIcon unscaled = new ImageIcon(Objects.requireNonNull(ToolboxButtons.class.getResource("/art/toolbox/" + imageFileName)));
        Image scaled = unscaled.getImage().getScaledInstance(l.getPreferredSize().width, l.getPreferredSize().height, Image.SCALE_SMOOTH);
        l.setText(""); // Set the text to nun so that it doesn't push the picture
        l.setIcon(new ImageIcon(scaled));
    }

    public static void register(String label, ActionListener actionListener, String imageFileName) {
        JButton l = register(label, actionListener);
        ImageIcon unscaled = new ImageIcon(Objects.requireNonNull(ToolboxButtons.class.getResource("/art/toolbox/" + imageFileName)));
        Image scaled = unscaled.getImage().getScaledInstance(l.getPreferredSize().width, l.getPreferredSize().height, Image.SCALE_SMOOTH);
        l.setText(""); // Set the text to nun so that it doesnt push the picture
        l.setIcon(new ImageIcon(scaled));
    }

    public static JButton register(String label, ActionListener actionListener) {
        JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setMaximumSize(new java.awt.Dimension(100, 120));
        buttonPanel.setMinimumSize(new java.awt.Dimension(120, 120));
        buttonPanel.setPreferredSize(new java.awt.Dimension(100, 120));
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS));
        buttonPanel.setBackground(J3DTheme.UI_SURFACE.color());

        JButton btnA = new javax.swing.JButton();
        btnA.setText("examplebtn");
        btnA.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnA.setMaximumSize(new java.awt.Dimension(100, 100));
        btnA.setMinimumSize(new java.awt.Dimension(100, 100));
        btnA.setPreferredSize(new java.awt.Dimension(100, 100));
        btnA.addActionListener(actionListener);
        btnA.setBackground(J3DTheme.BACKGROUND.color());
        btnA.setForeground(J3DTheme.TEXT_PRIMARY.color());
        btnA.setCursor(CursorManager.get(CursorNames.HAND_POINTER));
        buttonPanel.add(btnA);

        JLabel label1 = new javax.swing.JLabel();
        label1.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); // NOI18N
        label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label1.setText(label);
        label1.setMaximumSize(new java.awt.Dimension(100, 16));
        label1.setMinimumSize(new java.awt.Dimension(120, 16));
        label1.setPreferredSize(new java.awt.Dimension(120, 16));
        label1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        buttonPanel.add(label1);

        toolboxButtons.add(buttonPanel);
        J3DTheme.commitAsGenericUi(buttonPanel);
        J3DTheme.commitAsGenericUi(btnA);
        J3DTheme.commitAsGenericLbl(label1, false);
        return btnA;
    }

    public static void tripleText(String t1, String t2, String t3, int d, TrinaryConsumer<JLabel> after) {

        JPanel buttonPanel = new JPanel();

        buttonPanel.setBackground(J3DTheme.UI_SURFACE.color());
        buttonPanel.setMaximumSize(new java.awt.Dimension(100*d, 120));
        buttonPanel.setMinimumSize(new java.awt.Dimension(120*d, 120));
        buttonPanel.setPreferredSize(new java.awt.Dimension(100*d, 120));
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS));

        ArrayList<String> content = new ArrayList<>(List.of(t1, t2, t3));
        ArrayList<JLabel> labels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel();
            labels.add(label);
            label.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); // NOI18N
            label.setForeground(J3DTheme.TEXT_PRIMARY.color());
            label.setBackground(J3DTheme.BACKGROUND.color());
            label.setOpaque(true);
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setText(content.get(i));
            label.setMaximumSize(new java.awt.Dimension(100*d, 35));
            label.setMinimumSize(new java.awt.Dimension(120*d, 16));
            label.setPreferredSize(new java.awt.Dimension(120*d, 16));
            label.setBorder(new BevelBorder(BevelBorder.RAISED, J3DTheme.ACCENT_PRIMARY.color(), J3DTheme.ACCENT_SECONDARY.color()));
            J3DTheme.commit(J3DTheme.BACKGROUND, (c) -> {
                // reset the colours.
                label.setForeground(J3DTheme.TEXT_PRIMARY.color());
                label.setBackground(J3DTheme.BACKGROUND.color());
                label.setBorder(new BevelBorder(BevelBorder.RAISED, J3DTheme.ACCENT_PRIMARY.color(), J3DTheme.ACCENT_SECONDARY.color()));
            });
            buttonPanel.add(label);
        }

        toolboxButtons.add(buttonPanel);
        J3DTheme.commitAsGenericUi(buttonPanel);
        after.accept(
                labels.getFirst(),
                labels.get(1),
                labels.getLast()
        );
    }

    public static ArrayList<JPanel> getToolboxButtons() {
        return toolboxButtons;
    }
}
