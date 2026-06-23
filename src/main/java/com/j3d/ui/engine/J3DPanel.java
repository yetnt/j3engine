package com.j3d.ui.engine;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.interact.selection.SelectionUI;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static com.j3d.J3DSettings.jMenuBarOffsetY;
import static com.j3d.ui.engine.EngineFrame.*;
import static com.j3d.ui.engine.EngineFrame.selectionArea;

/**
 *  The J3DPanel class extends JPanel and serves as the main drawing surface for the 3D engine.
 * It overrides the `paint` method to handle rendering of 3D objects, axes, and selection UI.
 */
public class J3DPanel extends JPanel {
    private static ScreenPoint[] selectionAreaOld = new ScreenPoint[]{new ScreenPoint(0, 0), new ScreenPoint(0, 0)};
    private ArrayList<Runnable> runnables = new ArrayList<>();
    public J3DPanel() {
        super();
    }

    /**
     * Applies the JMenuBar offset to the selection square so it line sup with the mouse cursor.
     * Also adheres to the fact that a selection may have not previously changed by storing it statically.
     * @param sA The selection area
     * @return The new adjusted selection area
     * @apiNote      * This is needed because the JMenuBar is not part of the JPanel, but rather the JFrame.
     * So the mouse coordinates are relative to the JFrame, but the drawing is relative to the JPanel.
     */
    public ScreenPoint[] applySelectionAreaOffset(ScreenPoint[] sA) {
        if (selectionAreaOld[0].equals(sA[0]) && selectionAreaOld[1].equals(sA[1])) {
            return selectionAreaOld;
        }
        int offset = Static.mainFrame.getJMenuBar().getSize().height + jMenuBarOffsetY;
        ScreenPoint a = new ScreenPoint(sA[0].x, sA[0].y - offset);
        ScreenPoint b = new ScreenPoint(sA[1].x, sA[1].y - offset);
        selectionAreaOld = new ScreenPoint[] {
                new ScreenPoint(a.x, a.y),
                new ScreenPoint(b.x, b.y)
        };
        return new ScreenPoint[] {
                a, b
        };
    }

    public void registerRunnable(Runnable runnable) {
        runnables.add(runnable);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (run) {
//            sceneManager.axis((Graphics2D) g, camera);
            if (Static.executor != null)
                Static.executor.run((Graphics2D) g);
            run = false;
        }
        Static.sceneManager.draw((Graphics2D) g, Static.camera);
        // draw selection area ontop of all render things.
        if (selectionArea[0] != null && selectionArea[1] != null)
            SelectionUI.run((Graphics2D)g, applySelectionAreaOffset(selectionArea), Static.sceneManager);

        runnables.forEach(Runnable::run);
    }

    /**
     * Exports the current panel view as an image file of the specified type.
     * @param extension The extension of the image file to be exported.
     * @param file The file to which the image will be exported.
     * @throws Exception If an error occurs during the export process.
     */
    public void exportAs(String extension, File file) throws Exception {
        file.mkdirs();

        int scale = 2; // increase to 3 for even sharper output

        BufferedImage image = new BufferedImage(
                this.getWidth() * scale,
                this.getHeight() * scale,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = image.createGraphics();

        // --- High quality rendering ---
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // scale up rendering space
        g2.scale(scale, scale);

        // render Swing component
        this.printAll(g2);

        g2.dispose();

        switch (extension.toLowerCase()) {
            case "png" -> {
                ImageIO.write(image, "png", file);
            }

            case "jpg", "jpeg" -> {
                // JPEG does not support alpha, so flatten it
                BufferedImage rgbImage = new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );

                Graphics2D g = rgbImage.createGraphics();
                g.setColor(Color.WHITE); // background fill for transparency
                g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());

                g.drawImage(image, 0, 0, null);
                g.dispose();

                ImageIO.write(rgbImage, "jpg", file);
            }

            default -> throw new IllegalArgumentException("Unsupported format: " + extension);
        }
    }
//    public void exportAs(String extension, File file) throws Exception {
//        BufferedImage image = new BufferedImage(
//                this.getWidth(),
//                this.getHeight(),
//                BufferedImage.TYPE_INT_ARGB
//        );
//
//        Graphics2D g2 = image.createGraphics();
//        this.printAll(g2);
//        g2.dispose();
//
//        switch (extension) {
//            case "png" -> ImageIO.write(image, "png", file);
//            case "jpg" -> ImageIO.write(image, "jpg", file);
//        }
//    }
}
