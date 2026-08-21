package com.j3d.ui.theme.swing;


import com.j3d.ui.theme.J3DTheme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.function.Supplier;

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

    private final Supplier<Color> THUMB_COLOR = J3DTheme.TEXT_PRIMARY::color;
    private final Supplier<Color>  THUMB_HOVER_COLOR = J3DTheme.TEXT_SECONDARY::color;
    private final Supplier<Color>  TRACK_COLOR = J3DTheme.UI_SURFACE::color;

    private static final int NORMAL_EDGE_RADIUS = 8;
    private static final int HOVER_EDGE_RADIUS = 20;// just to make them unique to standard
                                                    // swing scrollbars.

    public Color getThumb() {
        return  THUMB_COLOR.get();
    }

    public Color getThumbHover() {
        return THUMB_HOVER_COLOR.get();
    }

    public Color getTrack() {
        return TRACK_COLOR.get();
    }

    private J3DScrollBarUI() {
        super();
    }

    public static void setBars(JScrollPane toolboxScrollpane) {
        JScrollBar vertScrollBar = toolboxScrollpane.getVerticalScrollBar();
        JScrollBar horiScrollBar = toolboxScrollpane.getHorizontalScrollBar();
        vertScrollBar.setUI(new J3DScrollBarUI());
        horiScrollBar.setUI(new J3DScrollBarUI());
    }

    @Override
    protected void configureScrollBarColors() {
        thumbColor = getThumb();
        trackColor = getTrack();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = isThumbRollover() ? getThumbHover() : getThumb();
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
        g2.setPaint(getTrack());
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        BasicArrowButton b = new BasicArrowButton(
                orientation,
                J3DTheme.BACKGROUND.color(),
                J3DTheme.TEXT_PRIMARY.color(),
                J3DTheme.TEXT_SECONDARY.color().darker(),
                J3DTheme.ACCENT_PRIMARY.color()
        );
        J3DTheme.commitAsGenericLbl(b, true);
        return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        BasicArrowButton b = new BasicArrowButton(
                orientation,
                J3DTheme.BACKGROUND.color(),
                J3DTheme.TEXT_PRIMARY.color(),
                J3DTheme.TEXT_SECONDARY.color().darker(),
                J3DTheme.ACCENT_PRIMARY.color()
        );
        J3DTheme.commitAsGenericLbl(b, true);
        return b;
    }
}