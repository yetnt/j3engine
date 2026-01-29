package com.j3d.ui.tb;

import com.j3d.ui.home.EngineFrame;
import com.j3d.engine.DebugDump;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.geometry.geo2d.GTri;
import com.j3d.engine.geometry.geo3d.Camera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
            EngineFrame.dp.setVisible(!EngineFrame.dp.isVisible());
        });
        // Example button registration
        register("Example Button", e -> {
            System.out.println("Example Button Clicked");
        });
        // another for exmaple
        register("Another Button", e -> {
            System.out.println("Another Button Clicked");
        });
        register("Dump to Debug", e -> {
            long current = System.currentTimeMillis();
            Camera cam = EngineFrame.camera;

            for (Layer l : EngineFrame.renderer.layers) {
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
    }

    public static void register(String label, ActionListener actionListener) {
        JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setMaximumSize(new java.awt.Dimension(100, 120));
        buttonPanel.setMinimumSize(new java.awt.Dimension(120, 120));
        buttonPanel.setPreferredSize(new java.awt.Dimension(100, 120));
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS));

        JButton btnA = new javax.swing.JButton();
        btnA.setText("examplebtn");
        btnA.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnA.setMaximumSize(new java.awt.Dimension(100, 100));
        btnA.setMinimumSize(new java.awt.Dimension(100, 100));
        btnA.setPreferredSize(new java.awt.Dimension(100, 100));
        btnA.addActionListener(actionListener);
        buttonPanel.add(btnA);

        JLabel label1 = new javax.swing.JLabel();
        label1.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); // NOI18N
        label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label1.setText(label);
        label1.setMaximumSize(new java.awt.Dimension(100, 16));
        label1.setMinimumSize(new java.awt.Dimension(120, 16));
        label1.setPreferredSize(new java.awt.Dimension(120, 16));
        buttonPanel.add(label1);

        toolboxButtons.add(buttonPanel);
    }

    public static ArrayList<JPanel> getToolboxButtons() {
        return toolboxButtons;
    }
}
