package com.j3d.engine.interact.cmd;

import com.j3d.Static;
import com.j3d.ui.engine.CommandPallete;

import javax.swing.*;

/**
 * A SafeJLabel is just a wrapper around 2 or soon more labels which when calling setText calls
 * {@link SwingUtilities#invokeLater(Runnable)}
 * @author Lehlogonolo Poole
 * @see CommandParser
 * @see CommandPallete
 */
public class SafeJLabel {
    private JLabel label;
    private JLabel label2;

    public SafeJLabel(JLabel label, JLabel label2) {
        this.label = label;
        this.label2 = label2;
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
                Static.mainFrame.repaint();
            }
        });
    }

    public void setLower(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label2.setText(text);
                Static.mainFrame.repaint();
            }
        });
    }

    public void clearHigher() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label2.setText("");
                Static.mainFrame.repaint();
            }
        });
    }

    public void clearLower() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText("");
                Static.mainFrame.repaint();
            }
        });
    }

    public void clear() {
        clearHigher();
        clearLower();
    }
}
