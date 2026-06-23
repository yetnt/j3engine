package com.j3d.ui;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A SafeJLabel is just a wrapper around 2 or soon more labels which when calling setText calls
 * {@link SwingUtilities#invokeLater(Runnable)}
 * @author Lehlogonolo Poole
 * @see CommandParser
 * @see CommandPalette
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

    public void error(String text) {
        setText(
                JLabelRichText.htmlOf(
                        new JLabelRichText(text).font(Color.RED)
                )
        );
    }

    public static String EMPH = "%EMPH%";

    public void error(String text, Object... emphasize) {
        ArrayList<JLabelRichText> emphasized = Arrays.stream(emphasize).map(
                t -> new JLabelRichText(t.toString()).underline().italic().font(new Color(65, 22, 22))
        ).collect(Collectors.toCollection(ArrayList::new));
        String html = JLabelRichText.htmlOf(new JLabelRichText(text).font(Color.RED).bold().paragraph());
        for (JLabelRichText emph : emphasized) {
            html = html.replaceFirst(
                    EMPH,
                    emph.toString()
            );
        }
        setText(html);
    }

    public void setText(String text, Object... emphasize) {
        ArrayList<JLabelRichText> emphasized = Arrays.stream(emphasize).map(
                t -> new JLabelRichText(t.toString()).underline().italic().font(J3DTheme.TEXT_PRIMARY.color())
        ).collect(Collectors.toCollection(ArrayList::new));
        String html = JLabelRichText.htmlOf(new JLabelRichText(text).font(J3DTheme.TEXT_SECONDARY.color()).bold().paragraph());
        for (JLabelRichText emph : emphasized) {
            html = html.replaceFirst(
                    EMPH,
                    emph.toString()
            );
        }
        setText(html);
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

    public void clearLower() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label2.setText("");
                Static.mainFrame.repaint();
            }
        });
    }

    public void clearHigher() {
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
