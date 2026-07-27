package com.j3d.gen.docs.api;

import com.j3d.StaticRefs;
import com.j3d.errors.ErrorHandler;
import com.j3d.gen.docs.DocsGenException;
import com.j3d.gen.docs.ImgGenException;
import com.j3d.gen.docs.reader.tokens.wrappers.HTMLTags;
import com.j3d.gen.docs.reader.tokens.wrappers.TWhtmlTag;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * A utility class for parsing and representing {@code <img>} HTML tags found within J3Engine documentation.
 * <p>
 * This class extracts relevant attributes such as {@code src}, {@code alt}, and {@code scale}
 * from a {@link TWhtmlTag} object, validates them, and provides access to the parsed information.
 * It ensures that image paths are relative to the application's resources and that the image
 * files exist and are of supported types (.png, .jpg).
 * </p>
 * @author Lehlogonolo Poole
 * @see TWhtmlTag
 * @see DocsGenException
 * @see ErrorHandler
 */
public class ImageTag {
    private TWhtmlTag tag;
    private String altText;
    private File imagePath;
    private double scale;
    private boolean invalid = false;

    private ImageTag(TWhtmlTag tag, File imagePath, String altText, double scale) {
        this.tag = tag;
        this.imagePath = imagePath;
        this.altText = altText;
        this.scale = scale;
    }

    private ImageTag() {
        invalid = true;
        altText = "No alt text";
    }

    public static ImageTag getInstance(TWhtmlTag tag) {
        // check if this holds img tag
        if (tag.getTag() != HTMLTags.IMG) return null;
        String fp = tag.getAttributes().get("src");
        String alt = tag.getAttributes().get("alt");
        String scale = tag.getAttributes().get("scale");

        ImageTag imageTag = new ImageTag();

        if (fp == null || alt == null) {
            StaticRefs.getErrs().handle(
                    new ImgGenException(
                            "Image tag has no src or no alt attribute: " + tag.getRawContent().getFirst()
                    )
            );
            return imageTag;
        }

        imageTag.setAltText(
                alt.isBlank() ? "No alt text" : alt
        );

        // if this file is an absolute path, error. (only trust paths from resources folder)
        if (new File(fp).isAbsolute()) {
            StaticRefs.getErrs().handle(
                    new ImgGenException(
                            "Image tag has an absolute path for src: " + tag.getRawContent().getFirst()
                    )
            );
            return imageTag;
        }

        // file path starts with ../art then its valid.

        if (fp.startsWith("../art")) {
            fp = fp.substring(2);
        }

        File image;
        // check that this file exists within resources.
        try {
            image =
                    new File(Objects.requireNonNull(ImageTag.class.getResource(
                            fp
                    )).toURI());
        } catch (URISyntaxException | NullPointerException f) {
            StaticRefs.getErrs().handle(
                    new ImgGenException(
                            "Image tag has an invalid path for src: " + tag.getRawContent().getFirst()
                    )
            );
            return imageTag;
        }

        if (!(image.getAbsolutePath().endsWith(".png") || image.getAbsolutePath().endsWith(".jpg"))) {
            StaticRefs.getErrs().handle(
                    new ImgGenException(
                            "Image tag has an invalid file type for src (only .png and .jpg are supported): " + tag.getRawContent().getFirst()
                    )
            );
            return imageTag;
        }

        double s = 1;

        if (scale != null) {
            s = Double.parseDouble(scale);
        }

        return new ImageTag(tag, image, alt, s);
    }

    public TWhtmlTag getTag() {
        return tag;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public File getImagePath() {
        return imagePath;
    }

    public double getScale() {
        return scale;
    }

    public boolean isInvalid() {
        return invalid;
    }
}
