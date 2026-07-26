package com.j3d.ui.theme;


import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom ScrollBar UI class because Swing's scrollbars are huge no matter what you do
 * and just insanely disgusting.
 * <p>
 *     Literally all this class does it apply {@link J3DTheme} to the scrollbars.
 * </p>
 * @implNote UI classes can just call {@link #setBars(JScrollPane)} with the scroll pane
 * and the method will set both the vertical and the horizontal scrollbar.
 * @author Lehlogonolo Poole
 */
public class J3DScrollBarUI extends BasicScrollBarUI {

    private final Color THUMB_COLOR = J3DTheme.TEXT_PRIMARY.color();
    private final Color THUMB_HOVER_COLOR = J3DTheme.TEXT_SECONDARY.color();
    private final Color TRACK_COLOR = J3DTheme.UI_SURFACE.color();

    private static final int NORMAL_EDGE_RADIUS = 8;
    private static final int HOVER_EDGE_RADIUS = 20;// just to make them unique to standard
                                                    // swing scrollbars.

    public static void setBars(JScrollPane toolboxScrollpane) {
        JScrollBar vertScrollBar = toolboxScrollpane.getVerticalScrollBar();
        JScrollBar horiScrollBar = toolboxScrollpane.getHorizontalScrollBar();
        vertScrollBar.setUI(new J3DScrollBarUI());
        horiScrollBar.setUI(new J3DScrollBarUI());
    }

    @Override
    protected void configureScrollBarColors() {
        thumbColor = THUMB_COLOR;
        trackColor = TRACK_COLOR;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = isThumbRollover() ? THUMB_HOVER_COLOR : THUMB_COLOR;
        int edgeRadius = isThumbRollover() ? HOVER_EDGE_RADIUS : NORMAL_EDGE_RADIUS;
        g2.setPaint(color);
        g2.fillRoundRect(
                thumbBounds.x,
                thumbBounds.y,
                thumbBounds.width,
                thumbBounds.height,
                edgeRadius, edgeRadius);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(TRACK_COLOR);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new BasicArrowButton(
                orientation,
                J3DTheme.BACKGROUND.color(),
                J3DTheme.TEXT_PRIMARY.color(),
                J3DTheme.TEXT_SECONDARY.color().darker(),
                J3DTheme.ACCENT_PRIMARY.color()
        );
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new BasicArrowButton(
                orientation,
                J3DTheme.BACKGROUND.color(),
                J3DTheme.TEXT_PRIMARY.color(),
                J3DTheme.TEXT_SECONDARY.color().darker(),
                J3DTheme.ACCENT_PRIMARY.color()
        );
    }
}