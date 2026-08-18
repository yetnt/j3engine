package com.j3d.utility;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class for image manipulation, specifically for cropping and scaling images
 * to fit specific dimensions while maintaining aspect ratio.
 * @author Lehlogonolo Poole
 */
public class ImageUtils {

    /**
     * Creates an {@link ImageIcon} from a given file, cropping and scaling it to fit
     * the specified target width and height, then multiplying its size.
     * The cropping logic prioritizes fitting the target aspect ratio by removing excess from the wider/taller dimension.
     * @param file The image file to process.
     * @param width The target width for the cropped image before scaling.
     * @param height The target height for the cropped image before scaling.
     * @param multiplier A factor by which the cropped image's dimensions (width, height) will be scaled.
     * @return An {@link ImageIcon} representing the cropped and scaled image.
     * @throws RuntimeException if an error occurs during image reading or processing.
     */
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

    /**
     * Creates an {@link ImageIcon} from a given file, cropping and scaling it to fit
     * the specified target width and height, then multiplying its size.
     * This method uses a slightly different cropping logic compared to {@code createCroppedIcon},
     * ensuring the cropped area always fits within the original image boundaries while
     * trying to match the target aspect ratio.
     * @param file The image file to process.
     * @param width The target width for the cropped image before scaling.
     * @param height The target height for the cropped image before scaling.
     * @param multiplier A factor by which the cropped image's dimensions (width, height) will be scaled.
     * @return An {@link ImageIcon} representing the cropped and scaled image.
     * @throws IIOException if an error occurs during image reading (e.g., file not found, invalid format).
     * @throws RuntimeException if another type of error occurs during image processing.
     */
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