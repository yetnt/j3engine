package com.j3d.utility;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static ImageIcon createCroppedIcon(File file, int width, int height, int multiplier) {
        try {
            BufferedImage original = ImageIO.read(file);

            int W = original.getWidth();
            int H = original.getHeight();

            double targetRatio = (double) width / height;
            double currentRatio = (double) W / H;

            int cropX = 0, cropY = 0, cropW = W, cropH = H;

            if (currentRatio > targetRatio) {
                // too wide → crop width
                cropW = (int) (H * targetRatio);
                cropX = (W - cropW) / 2;
            } else {
                // too tall → crop height
                cropH = (int) (W / targetRatio);
                cropY = (H - cropH) / 2;
            }

            BufferedImage cropped = original.getSubimage(cropX, cropY, cropW, cropH);

            int scaledW = width * multiplier;
            int scaledH = height * multiplier;

            Image scaled = cropped.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);

            return new ImageIcon(scaled);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static ImageIcon createCroppedIcon2(File file, int width, int height, int multiplier) throws IIOException {
        try {
            BufferedImage original = ImageIO.read(file);

            int W = original.getWidth();
            int H = original.getHeight();

            double targetRatio = (double) width / height;

            int cropW = W;
            int cropH = (int) (W / targetRatio);

            if (cropH > H) {
                cropH = H;
                cropW = (int) (H * targetRatio);
            }

            int cropX = (W - cropW) / 2;
            int cropY = (H - cropH) / 2;

            BufferedImage cropped = original.getSubimage(cropX, cropY, cropW, cropH);

            int scaledW = width * multiplier;
            int scaledH = height * multiplier;

            Image scaled = cropped.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);

            return new ImageIcon(scaled);

        } catch (IIOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}