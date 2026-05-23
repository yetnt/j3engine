package com.j3d.utility.generators;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A utility class for creating rich text content suitable for display in Swing's JLabel.
 * It allows chaining methods to apply various HTML-like formatting to a given string.
 */
public class JLabelRichText {
    private String content;
    private ArrayList<String> open = new ArrayList<>();
    private ArrayList<String> close = new ArrayList<>();

    public JLabelRichText(String cont) {
        content = cont;
    }

    /**
     * Applies bold formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText bold() {
        open.add("<b>");
        close.add("</b>");
        return this;
    }

    /**
     * Applies italic formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText italic() {
        open.add("<i>");
        close.add("</i>");
        return this;
    }

    /**
     * Applies underline formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText underline() {
        open.add("<u>");
        close.add("</u>");
        return this;
    }

    /**
     * Applies a specific colour to the font of the content.
     * The colour is converted to a hexadecimal RGB string.
     *
     * @param col The Color object to apply.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(Color col) {
        open.add("<font color=\"" +
                String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue())
                + "\">");
        close.add("</font>");
        return this;
    }

    /**
     * Applies a specific colour and size to the font of the content.
     * The colour is converted to a hexadecimal RGB string.
     *
     * @param col The Colour object to apply.
     * @param size The font size as a string (e.g., "1", "+2", "-3").
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(Color col, String size) {
        open.add("<font color=\"" +
                String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue()) + "\" size=\"" + size + "\">");
        close.add("</font>");
        return this;
    }

    /**
     * Applies a specific font size to the content.
     *
     * @param size The font size as a string (e.g., "1", "+2", "-3").
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(String size) {
        open.add("<font size=\"" + size + "\">");
        close.add("</font>");
        return this;
    }

    /**
     * Wraps the content in a paragraph tag.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText paragraph() {
        open.add("<p>");
        close.add("</p>");
        return this;
    }

    /**
     * Applies superscript formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText superscript() {
        open.add("<sup>");
        close.add("</sup>");
        return this;
    }
    /**
     * Applies subscript formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText subscript() {
        open.add("<sub>");
        close.add("</sub>");
        return this;
    }

    public String wrapHTML() {
        return "<html>" + toString() + "</html>";
    }

    /**
     * Generates the HTML string representation of the rich text content,
     * applying all accumulated open and close tags.
     * @return The HTML string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        open.forEach(
                sb::append
        );
        sb.append(content);
        close.reversed().forEach(
                sb::append
        );
        return sb.toString();
    }

    /**
     * Static factory method to combine multiple JLabelRichText instances into a single HTML string,
     * wrapped in an {@code <html>} tag.
     *
     * @param richTexts An array of JLabelRichText objects to combine.
     * @return A single HTML string containing all the rich text content.
     */
    public static String htmlOf(JLabelRichText... richTexts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        Arrays.stream(richTexts).forEach(sb::append);
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Static factory method to combine multiple JLabelRichText instances and wrap them
     * within a single paragraph {@code <p>} tag.
     *
     * @param richTexts An array of JLabelRichText objects to combine.
     * @return A new JLabelRichText instance representing the combined content within a paragraph.
     */
    public static JLabelRichText paragraphWrap(JLabelRichText... richTexts) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(richTexts).forEach(sb::append);
        return new JLabelRichText(sb.toString()).paragraph();
    }

    /**
     * A constant string representing an HTML horizontal rule tag.
     */
    public static String HORIZONTAL_LINE = "<hr>";
}
