package com.j3d.ui.engine.tb;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.react.history.History;
import com.j3d.storage.files.engine.DebugDump;
import com.j3d.threads.LongTask;
import com.j3d.ui.generic.CursorManager;
import com.j3d.ui.generic.CursorNames;
import com.j3d.ui.generic.J3DTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolboxButtons {
    private static final ArrayList<JPanel> toolboxButtons = new ArrayList<>();
    public static Subbox currentViewableSubbox = null;
    public static final int MAX_BUTTONS = 6;
    public static final int BUTTON_PANEL_SIZE = 120;

    static {
        register("Debug Panel", e -> {
            // Toggle debug mode
            Static.getDebugPanel().toggleHidden();
        });
        register("Properties", e -> {
            // Toggle props mode
            Static.getPropertiesPanel().toggleHidden();
        });

        // Example button registration
        register("Layers",
                e -> Static.getLayerTree().toggleHidden(),
                "layers.png");

        // another for exmaple
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
            Camera cam = Static.camera;
            try (PrintWriter out = Static.getEngineFiles().debugDump.writer(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".csv")) {
                DebugDump.print(out, current, cam);
                Desktop.getDesktop().open(Static.getEngineFiles().debugDump.getFolder());
            } catch (RuntimeException | IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        registerComplex("Transform", new Subbox(s -> s
                .add("translate", e -> Static.commandParser.runCommand(
                        CommandsManager.commands.transform, "transform",
                        new ArrayList<>(List.of("translate")), new ArrayList<>()
                ), "translate.png")
                .add("rotate", e -> Static.commandParser.runCommand(
                        CommandsManager.commands.transform, "transform",
                        new ArrayList<>(List.of("rotate")), new ArrayList<>()
                ), "rotate.png")
                .add("scale", e -> Static.commandParser.runCommand(
                        CommandsManager.commands.transform, "transform",
                        new ArrayList<>(List.of("scale")), new ArrayList<>()
                ), "scale.png")), "transform.png");

        register("Orbit", e -> Static.commandParser.runCommand(
                CommandsManager.commands.orbit, "orbit",
                new ArrayList<>(), new ArrayList<>()), "orbit.png");
        register("History", e -> History.panel.toggleHidden());
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
                            Static.mainFrame.getWidth() / 2 - (sub.getPreferredSize().width / 2),
                            (Static.mainFrame.getHeight() / 2 - (sub.getPreferredSize().height / 2)) - 30,
                            sub.getPreferredSize().width,
                            sub.getPreferredSize().height + 10
                    );
                    Static.mainFrame.getLayeredPane().add(sub, JLayeredPane.DRAG_LAYER +1);

                    s.setEnabled(true);
                    s.setVisible(true);
                    currentViewableSubbox = s;
                }
                Static.mainFrame.repaint();
                Static.mainFrame.revalidate();
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
        return btnA;
    }

    public static ArrayList<JPanel> getToolboxButtons() {
        return toolboxButtons;
    }
}
