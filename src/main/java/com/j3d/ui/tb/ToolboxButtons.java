package com.j3d.ui.tb;

import com.j3d.Static;
import com.j3d.engine.DebugDump;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.threads.LongTask;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;
import com.j3d.ui.J3DTheme;
import com.j3d.ui.engine.FloatingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ToolboxButtons {
    private static final ArrayList<JPanel> toolboxButtons = new ArrayList<>();

    private static final PrintWriter out;

    static {
        try {
            out = DebugDump.writer(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".csv");

            out.println("time,cx,cy,cz,cpitch,cyaw,croll,layerID,layerVisible,thingName,thingID,triID,tridist,trix,triy,triz,trinx,triny,trinz,tricol,triVisible");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            out.flush();
            out.close();
        }));
        register("Toggle Debug", e -> {
            // Toggle debug mode
            Static.debugPanel.setVisible(!Static.debugPanel.isVisible());
        });
        // Example button registration
        register("Toggle Layers", e -> {
            Static.layerTree.setVisible(!Static.layerTree.isVisible());
        }, "layers.png");
        // another for exmaple
        register("Toggle Throbber", e -> {
            String nString = JOptionPane.showInputDialog("Input time in ms to sleep");
            if (nString == null) return;
            int n = Integer.parseInt(nString);
            LongTask<Void> task = new LongTask<>(
                    t -> {
                        int max = 10;
                        t.progressStart("Doing", max);
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
                    (t, i)-> {
                        // No cleanup needed.
                    }
            );
            task.run();
        });
        register("Dump to Debug", e -> {
            long current = System.currentTimeMillis();
            Camera cam = Static.camera;

            for (Layer l : Static.renderer.layers) {
                l.forEach(thing -> {
                    thing.getObjects().stream()
                            .filter(GTri.class::isInstance)
                            .map(GTri.class::cast)
                            .forEach(tri -> {
                                String sb = current + "," +
                                        cam.getPosition().getX() + "," +
                                        cam.getPosition().getY() + "," +
                                        cam.getPosition().getZ() + "," +
                                        cam.getRotation().getPitch() + "," +
                                        cam.getRotation().getYaw() + "," +
                                        cam.getRotation().getRoll() + "," +
                                        l.getIdentifier() + "," +
                                        !l.isHidden() + "," +
                                        thing.getName() + "," +
                                        thing.getId() + "," +
                                        tri.getId() + "," +
                                        tri.euclideanDist() + "," +
                                        tri.getPivot().getX() + "," +
                                        tri.getPivot().getY() + "," +
                                        tri.getPivot().getZ() + "," +
                                        tri.normal.getX() + "," +
                                        tri.normal.getY() + "," +
                                        tri.normal.getZ() + "," +
                                        String.format("#%02X%02X%02X", tri.getColour().getRed(), tri.getColour().getGreen(), tri.getColour().getBlue()) + "," +
                                        !tri.isHidden();

                                out.println(sb);
                                out.flush();
                            });
                });
            }

        });
        register("Transform", e -> {
            TransformCmd cmd = new TransformCmd();
        }, "transform.png");

        JLabel label1 = new javax.swing.JLabel();
        label1.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); // NOI18N
        label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label1.setText("yo");
        label1.setMaximumSize(new java.awt.Dimension(100, 16));
        label1.setMinimumSize(new java.awt.Dimension(120, 16));
        label1.setPreferredSize(new java.awt.Dimension(120, 16));
        label1.setForeground(J3DTheme.TEXT_PRIMARY.color());

        FloatingPanel fp = new FloatingPanel("HIII", label1);

        register("UI elem", e -> {
            if (fp.isHidden()) fp.showThis();
            else fp.hideThis();
        });
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
