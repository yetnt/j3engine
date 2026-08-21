package com.j3d.ui.engine.toolbox;

import com.j3d.StaticRefs;
import com.j3d.ui.theme.cursors.CursorManager;
import com.j3d.ui.theme.cursors.CursorNames;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generic.func.TrinaryConsumer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolboxButtons {
    private static final ArrayList<JPanel> toolboxButtons = new ArrayList<>();
    public static Subbox currentViewableSubbox = null;
    public static final int MAX_BUTTONS = 6;
    public static final int BUTTON_PANEL_SIZE = 120;

    static {

        ButtonsRegistry.registerAll();


//        // another for example
//        register("Toggle Spinner", e -> {
//            String nString = JOptionPane.showInputDialog("Input time in ms to sleep");
//            if (nString == null) return;
//            int n = Integer.parseInt(nString);
//            LongTask<Void> task = new LongTask<>(
//                    t -> {
//                        int max = 10;
//                        t.progressStart("Doing stuff", max);
//                        try {
//                            for (int i = 0; i < max; i++) {
//                                Thread.sleep(n);
//                                t.updateProgress(i);
//                            }
//                        } catch (InterruptedException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                        return null;
//                    },
//                    (t, i, completed)-> {
//                        // No clean-up needed.
//                    }
//            );
//            task.run();
//        });
//        register("Dump to Debug", e -> {
//            long current = System.currentTimeMillis();
//            Camera cam = StaticRefs.getCamera();
//            try (PrintWriter out = StaticRefs.getEngineFiles().debugDump.writer(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".csv")) {
//                DebugDump.print(out, current, cam);
//                Desktop.getDesktop().open(StaticRefs.getEngineFiles().debugDump.getFolder());
//            } catch (RuntimeException | IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });

    }

    static void registerComplex(String label, String tooltip, Subbox sub, String imageFileName) {
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
        JButton l = register(label, tooltip, new ActionListener() {
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

    static void register(String label, String tooltip, ActionListener actionListener, String imageFileName) {
        JButton l = register(label, tooltip, actionListener);
        ImageIcon unscaled = new ImageIcon(Objects.requireNonNull(ToolboxButtons.class.getResource("/art/toolbox/" + imageFileName)));
        Image scaled = unscaled.getImage().getScaledInstance(l.getPreferredSize().width, l.getPreferredSize().height, Image.SCALE_SMOOTH);
        l.setText(""); // Set the text to nun so that it doesnt push the picture
        l.setIcon(new ImageIcon(scaled));
    }

    static JButton register(String label, String tooltip, ActionListener actionListener) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(100, 120));
        buttonPanel.setMinimumSize(new Dimension(120, 120));
        buttonPanel.setPreferredSize(new Dimension(100, 120));
        BoxLayout bl = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
        buttonPanel.setLayout(bl);
        buttonPanel.setBackground(J3DTheme.UI_SURFACE.color());

        JButton btnA = new JButton();
        btnA.setToolTipText(tooltip);
        btnA.setText("examplebtn");
        btnA.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        btnA.setMaximumSize(new Dimension(100, 100));
        btnA.setMinimumSize(new Dimension(100, 100));
        btnA.setPreferredSize(new Dimension(100, 100));
        btnA.addActionListener(actionListener);
        btnA.setBackground(J3DTheme.BACKGROUND.color());
        btnA.setForeground(J3DTheme.TEXT_PRIMARY.color());
        btnA.setCursor(CursorManager.get(CursorNames.HAND_POINTER));
        buttonPanel.add(btnA);

        JLabel label1 = new JLabel();
        label1.setFont(new Font("Tahoma", Font.BOLD, 12)); // NOI18N
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setText(label);
        label1.setMaximumSize(new Dimension(100, 16));
        label1.setMinimumSize(new Dimension(120, 16));
        label1.setPreferredSize(new Dimension(120, 16));
        label1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        buttonPanel.add(label1);

        toolboxButtons.add(buttonPanel);
        J3DTheme.commitAsGenericUi(buttonPanel);
        J3DTheme.commitAsGenericLbl(btnA, true);
        J3DTheme.commitAsGenericLbl(label1, false);
        return btnA;
    }

    static void tripleText(String t1, String t2, String t3, int d, TrinaryConsumer<JLabel> after) {

        JPanel buttonPanel = new JPanel();

        buttonPanel.setBackground(J3DTheme.UI_SURFACE.color());
        buttonPanel.setMaximumSize(new Dimension(100*d, 120));
        buttonPanel.setMinimumSize(new Dimension(120*d, 120));
        buttonPanel.setPreferredSize(new Dimension(100*d, 120));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        ArrayList<String> content = new ArrayList<>(List.of(t1, t2, t3));
        ArrayList<JLabel> labels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel();
            labels.add(label);
            label.setFont(new Font("Tahoma", Font.BOLD, 12)); // NOI18N
            label.setForeground(J3DTheme.TEXT_PRIMARY.color());
            label.setBackground(J3DTheme.BACKGROUND.color());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setText(content.get(i));
            label.setMaximumSize(new Dimension(100*d, 35));
            label.setMinimumSize(new Dimension(120*d, 16));
            label.setPreferredSize(new Dimension(120*d, 16));
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

    static void spacer() {

        JPanel buttonPanel = new JPanel();
        J3DTheme.commitAsGenericUi(buttonPanel);

        buttonPanel.setBackground(J3DTheme.UI_SURFACE.color());
        Dimension dim = new Dimension(20, 120);
        buttonPanel.setMaximumSize(dim);
        buttonPanel.setMinimumSize(dim);
        buttonPanel.setPreferredSize(dim);

        // add separator
        JSeparator separator = new JSeparator();
        int alpha = 60;
        Color col = J3DTheme.transparency(J3DTheme.ACCENT_PRIMARY, alpha);
        separator.setForeground(col);
        separator.setBackground(col);
        separator.setOpaque(true);
        separator.setMaximumSize(dim);
        separator.setMinimumSize(dim);
        separator.setPreferredSize(dim);
        separator.setBorder(new SoftBevelBorder(BevelBorder.RAISED, J3DTheme.ACCENT_PRIMARY.color(), J3DTheme.ACCENT_SECONDARY.color()));
        J3DTheme.themeUpdater.add(J3DTheme.ACCENT_PRIMARY, (c) -> {
            Color c2 = J3DTheme.transparency(J3DTheme.ACCENT_PRIMARY, alpha);
            separator.setForeground(c2);
            separator.setBackground(c2);
            separator.setBorder(new SoftBevelBorder(BevelBorder.RAISED, c, J3DTheme.ACCENT_SECONDARY.color()));
        });
        buttonPanel.add(separator);

        toolboxButtons.add(buttonPanel);
    }

    public static ArrayList<JPanel> getToolboxButtons() {
        return toolboxButtons;
    }
}
