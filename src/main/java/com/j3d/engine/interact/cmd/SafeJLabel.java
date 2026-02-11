package com.j3d.engine.interact.cmd;

import javax.swing.*;

/**
 * A SafeJLabel
 */
public class SafeJLabel {
    private JLabel label;

    public SafeJLabel(JLabel label) {
        this.label = label;
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }
}
