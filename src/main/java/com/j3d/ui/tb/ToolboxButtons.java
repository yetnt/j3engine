package com.j3d.ui.tb;

import com.j3d.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ToolboxButtons {
    private static final ArrayList<JPanel> toolboxButtons = new ArrayList<>();

    static {
        register("Toggle Debug", e -> {
            // Toggle debug mode
            Main.dp.setVisible(!Main.dp.isVisible());
        });
        // Example button registration
        register("Example Button", e -> {
            System.out.println("Example Button Clicked");
        });
        // another for exmaple
        register("Another Button", e -> {
            System.out.println("Another Button Clicked");
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
