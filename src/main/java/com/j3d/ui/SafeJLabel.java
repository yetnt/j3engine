package com.j3d.ui;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.threads.TimeoutWorker;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
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
    private TimeoutWorker higher;
    private TimeoutWorker lower;

    public SafeJLabel(JLabel label, JLabel label2) {
        this.label = label;
        this.label2 = label2;
    }

    private void addTimeoutWorker(boolean higher, Runnable runnable, int seconds) {
        if (higher) {
            if (this.higher != null)
                this.higher.cancel(true);

            if (seconds < 0) return;

            this.higher = new TimeoutWorker(seconds, runnable);
            this.higher.execute();
        } else {
            if (this.lower != null)
                this.lower.cancel(true);

            if (seconds < 0) return;

            this.lower = new TimeoutWorker(seconds, runnable);
            this.lower.execute();
        }
    }

    public static final int ERROR_SECONDS = 10;

    public void setText(String text) {
        setText(text, 5);
    }

    public void setText(String text, int seconds) {
        SwingUtilities.invokeLater(() -> {
            label.setText(text);
            StaticRefs.getMainFrame().repaint();
            addTimeoutWorker(true, SafeJLabel.this::clearHigher, seconds);
        });
    }

    public void setLower(String text) {
        setLower(text, 5);
    }

    public void setLower(String text, int seconds) {
        SwingUtilities.invokeLater(() -> {
            label2.setText(text);
            StaticRefs.getMainFrame().repaint();
            addTimeoutWorker(false, SafeJLabel.this::clearLower, seconds);
        });
    }

    public void error(String text) {
        setText(
                JLabelRichText.htmlOf(
                        new JLabelRichText(text).font(Color.RED)
                ), ERROR_SECONDS
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
        setText(html, ERROR_SECONDS);
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

    public void setTextWithSeconds(String text, int seconds, Object... emphasize) {
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
        setText(html, seconds);
    }

    public void clearLower() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label2.setText("");
                StaticRefs.getMainFrame().repaint();
            }
        });
    }

    public void clearHigher() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText("");
                StaticRefs.getMainFrame().repaint();
            }
        });
    }

    public void clear() {
        clearHigher();
        clearLower();
    }

    public void repaint() {
        SwingUtilities.invokeLater(() -> {
            label.repaint();
            label2.repaint();
        });
    }
}
