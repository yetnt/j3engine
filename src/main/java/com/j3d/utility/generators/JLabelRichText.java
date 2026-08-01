package com.j3d.utility.generators;

import com.j3d.ui.theme.J3DTheme;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * The most important class that helps me avoid learning the pain that is {@link JFormattedTextField}
 * <p>
 *     {@link JLabelRichText} is a utility class designed to simplify the creation of rich text
 *     content for {@link JLabel}s.
 *     It provides methods for applying various HTML-like formatting, such as bold, italic,
 *     underline, font colour, font size, and paragraph wrapping, without directly manipulating
 *     raw HTML strings.
 * </p>
 * <p>
 *     The class internally manages a list of opening and closing HTML tags, which are applied
 *     to the content
 *     when {@code toString()} or {@code wrapHTML()} is called. This allows for method chaining
 *     to build complex
 *     formatted text efficiently.
 * </p>
 * <p>
 *     Example usage:
 *     <pre>{@code
 *     JLabelRichText richText = new JLabelRichText("Hello, World!")
 *         .bold()                              // wraps in <b></b> tag
 *         .font(Color.BLUE)                    // wraps in <font color="#0000FF"></font>
 *         .paragraph();                        // wraps in <p></p>
 *     myLabel.setText(richText.wrapHTML());    // finally wraps in <html></html>
 *     }</pre>
 * </p>
 * @author Lehlogonolo Poole
 * </p>
 * @implSpec
 * To ensure a properly formed HTML document one must call {@link #wrapHTML()} to close
 * a {@link JLabelRichText} in {@code <html></html>} tags.
 */
public class JLabelRichText {
    private String content;
    private ArrayList<String> open = new ArrayList<>();
    private ArrayList<String> close = new ArrayList<>();

    /**
     * Constructs a new JLabelRichText instance with the given content.
     *
     * @param cont The initial string content.
     */
    public JLabelRichText(String cont) {
        content = cont;
    }

    /**
     * Constructs an empty JLabelRichText instance.
     * The content can be added later using {@code add()} or {@code addLn()} methods.
     */
    public JLabelRichText() {

    }

    /**
     * Creates a new JLabelRichText instance from a given string and copies the
     * opening and closing tags from an existing JLabelRichText style object.
     * This is useful for applying an existing style to new content.
     *
     * @param string The initial string content for the new instance.
     * @param style The JLabelRichText instance from which to copy the open and close tags.
     * @return A new JLabelRichText instance with the specified content and copied style.
     */
    public static JLabelRichText from(String string, JLabelRichText style) {
        return new JLabelRichText(string)
                .setClose(new ArrayList<>(style.getClose()))
                .setOpen(new ArrayList<>(style.getOpen()));
    }

    /**
     * Appends additional content to the current rich text.
     *
     * @param cont The string content to append.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText add(String cont) {
        content = content + cont;
        return this;
    }

    /**
     * Appends additional content to the current rich text, preceded by an HTML line break.
     *
     * @param cont The string content to append.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText addLn(String cont) {
        content = content + LINE_BREAK + cont;
        return this;
    }

    /**
     * Constructs a new JLabelRichText instance with the given content,
     * optionally escaping HTML special characters.
     *
     * @param cont The initial string content.
     * @param esc If true, HTML special characters ({@code <}, {@code >}, {@code =}) will be escaped.
     */
    public JLabelRichText(String cont, boolean esc) {
        if (esc) {
            // replace greater than and less than symbols with html escape
            cont = cont.replaceAll(">", GREATER_THAN);
            cont = cont.replaceAll("<", LESS_THAN);
            cont = cont.replaceAll("=", EQUAL);
        }
        content = cont;
    }

    /**
     * Adds attributes to the most recently opened HTML tag.
     * This method is useful for dynamically adding properties like {@code id}, {@code class},
     * or other custom attributes to a tag that has just been opened using methods like
     * {@code wrapTag()}.
     * <p>
     * The attributes are appended to the last tag added to the {@code open} list.
     *
     * @param attributes A {@code LinkedHashMap} where keys are attribute names and values are attribute values.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText addAttributes(LinkedHashMap<String, String> attributes) {
        StringBuilder sb = new StringBuilder();
        attributes.forEach((k, v) -> sb.append(" ").append(k).append
                ("=\"").append(v).append("\""));
        open.set(
                open.size()-1,
                open.getLast().substring(0, open.getLast().length()-1) + sb + ">"
        );
        return this;
    }

    /**
     * Adds inline CSS styles to the most recently opened HTML tag.
     * This method allows for dynamic application of CSS properties to a tag that has just
     * been opened. It checks if a 'style' attribute already exists on the last opened tag
     * and throws a {@code RuntimeException} if it does, to prevent overwriting.
     * <p>
     * The styles are formatted into a CSS string and then added as a 'style' attribute.
     *
     * @param style A {@code LinkedHashMap} where keys are CSS property names and values are their corresponding values.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText addStyle(LinkedHashMap<String, String> style) {
        String openStr = open.getLast();
        if (openStr.contains("style"))
            throw new RuntimeException("No extra style"); // TODO: Wrap in better error and ErrorHandler
        StringBuilder styleBuilder = new StringBuilder();
        style.forEach((k, v) -> styleBuilder.append(k).append
                (":").append(v).append(";"));
        return addAttributes(new LinkedHashMap<>(
                Map.of("style", styleBuilder.toString())
        ));
    }

    /**
     * Applies bold formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText bold() {
        return wrapTag("b", new LinkedHashMap<>());
    }

    /**
     * Applies italic formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText italic() {
        return wrapTag("i", new LinkedHashMap<>());
    }

    /**
     * Applies underline formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText underline() {
        return wrapTag("u", new LinkedHashMap<>());
    }

    /**
     * Applies a specific colour to the font of the content.
     * The colour is converted to a hexadecimal RGB string.
     *
     * @param col The Color object to apply.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(Color col) {
        return wrapTag("font",
                new LinkedHashMap<>(
                        Map.of(
                                "color", colToStr(col)
                        )
                )
        );
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
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("color", colToStr(col));
        attributes.put("size", size);
        return wrapTag("font",
                attributes
        );
    }

    /**
     * Applies a specific font size to the content.
     *
     * @param size The font size as a string (e.g., "1", "+2", "-3").
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(String size) {
        return wrapTag("font",
                new LinkedHashMap<>(
                        Map.of(
                                "size", size
                        )
                )
        );
    }

    private String colToStr(Color col) {
        return String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue());
    }

    /**
     * Applies a specific colour, size, and background colour to the font of the content.
     * The colours are converted to hexadecimal RGB strings.
     *
     * @param col The font Colour object to apply.
     * @param size The font size as a string (e.g., "1", "+2", "-3").
     * @param backgroundCol The background Colour object to apply.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText font(Color col, String size, Color backgroundCol) {
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("color", colToStr(col));
        attributes.put("size", size);
        attributes.put("bgcolor", colToStr(backgroundCol));
        return wrapTag("font", attributes);
//        open.add("<font color=\"" +
//                String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue())
//                + "\" size=\"" + size + "\" bgcolor=\"" +
//                String.format("#%02x%02x%02x", backgroundCol.getRed(), backgroundCol.getGreen(), backgroundCol.getBlue()) + "\">");
//        close.add("</font>");
//        return this;
    }

    /**
     * Wraps the content in a paragraph tag.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText paragraph() {
        return wrapTag("p", new LinkedHashMap<>());
    }

    /**
     * Applies a heading tag (H1-H6) to the content.
     * @param heading The {@link Heading} enum representing the desired heading level.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText heading(Heading heading) {
        open.add(heading.getLeft());
        close.add(heading.getRight());
        return this;
    }

    /**
     * Applies superscript formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText superscript() {
        return wrapTag("sup", new LinkedHashMap<>());
    }
    /**
     * Applies subscript formatting to the content.
     *
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText subscript() {
        return wrapTag("sub", new LinkedHashMap<>());
    }
    /**
     * Wraps the content with a generic HTML tag, applying specified attributes.
     * This method is used internally by other formatting methods like {@code bold()},
     * {@code italic()}, and {@code font()}.
     * @param tag The HTML tag name (e.g., "b", "i", "font").
     * @param attributes A LinkedHashMap of attribute names and their values (e.g., "color" -> "#FF0000").
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText wrapTag(String tag, LinkedHashMap<String, String> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tag);
        attributes.forEach((k, v) -> sb.append(" ").append(k).append
                ("=\"").append(v).append("\""));
        sb.append(">");
        open.add(sb.toString());
        close.add("</" + tag + ">");
        return this;
    }

    /**
     * Wraps the content with a generic HTML tag, applying specified attributes and inline styles.
     * This method allows for more complex styling than {@code wrapTag(String, LinkedHashMap)}.
     *
     * @param tag The HTML tag name (e.g., "div", "span").
     * @param attributes A LinkedHashMap of attribute names and their values (e.g., "id" -> "myDiv").
     * @param style A LinkedHashMap of CSS style properties and their values (e.g., "color" -> "red", "font-size" -> "12px").
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText wrapTag(String tag, LinkedHashMap<String, String> attributes, LinkedHashMap<String, String> style) {
        StringBuilder sb = new StringBuilder();
        StringBuilder styleBuilder = new StringBuilder();
        sb.append("<").append(tag);
        attributes.forEach((k, v) -> sb.append(" ").append(k).append
                ("=\"").append(v).append("\""));
        style.forEach((k, v) -> styleBuilder.append(k).append
                (":").append(v).append(";"));
        sb.append(" style='").append(styleBuilder).append("'");
        sb.append(">");
        open.add(sb.toString());
        close.add("</" + tag + ">");
        return this;
    }

    /**
     * Wraps the content in a div tag with a specified width.
     * @implNote This is useful for controlling the layout and word wrapping of text within a JLabel.
     * @param width The width of the div in pixels.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText wrapDiv(int width) {
        return wrapTag("div", new LinkedHashMap<>(),
                new LinkedHashMap<>(Map.of(
                        "width", width + "px"
                ))
                );
    }

    /**
     * Applies the opening and closing tags from another {@code JLabelRichText} instance
     * to the current instance. The tags from {@code other} are added {@code before} the current
     * instance's own tags, effectively wrapping the current instance's content and styling.
     * @param other The {@code JLabelRichText} instance whose tags are to be applied.
     * @return The current {@code JLabelRichText} instance for method chaining.
     */
    public JLabelRichText wrapUsing(JLabelRichText other) {
        // add tags from other BEFORE current opening and closing
        other.getOpen().reversed().forEach(o -> open.addFirst(o));
        other.getClose().reversed().forEach(o -> close.addFirst(o));
        return this;
    }

    /**
     * Wraps the generated HTML string with {@code <html>} tags.
     *
     * @return The complete HTML string, including the {@code <html>} and {@code </html>} tags.
     */
    public String wrapHTML() {
        return "<html>" + this + "</html>";
    }

    /**
     * Generates the HTML string representation of the rich text content,
     * applying all accumulated open and close tags.
     * @return The HTML string.
     * @implNote This does not include the {@code <html>} and {@code </html>} tags. To return that
     * version call {@link #wrapHTML()}
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
     * StaticRefs factory method to combine multiple JLabelRichText instances into a single HTML string,
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
     * StaticRefs factory method to combine multiple String instances into a single HTML string,
     * wrapped in an {@code <html>} tag.
     * @param txt An array of String objects to combine.
     * @return A single HTML string containing all the text content.
     */
    public static String htmlOf(String... txt) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        Arrays.stream(txt).forEach(sb::append);
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * StaticRefs factory method to combine multiple JLabelRichText instances and wrap them
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
     * HTML constant for a horizontal rule.
     */
    public static String HORIZONTAL_LINE = "<hr>";

    /**
     * HTML constant for a line break.
     */
    public static String LINE_BREAK = "<br>";

    /**
     * HTML entity for the greater than symbol.
     */
    public static String GREATER_THAN = "&gt;";

    /**
     * HTML entity for the less than symbol.
     */
    public static String LESS_THAN = "&lt;";
    /**
     * HTML entity for the equals symbol.
     */
    public static String EQUAL = "&equals;";

    public String getRawContent() {
        return content;
    }

    public JLabelRichText add(JLabelRichText jLabelRichText) {
        this.content += jLabelRichText.toString();
        return this;
    }

    /**
     * Returns the list of closing HTML tags that will be applied to the content.
     * These tags are applied in reverse order when {@code toString()} is called.
     *
     * @return An ArrayList of closing HTML tag strings.
     */
    public ArrayList<String> getClose() {
        return close;
    }

    /**
     * Returns the list of opening HTML tags that will be applied to the content.
     * These tags are applied in order when {@code toString()} is called.
     *
     * @return An ArrayList of opening HTML tag strings.
     */
    public ArrayList<String> getOpen() {
        return open;
    }

    /**
     * Sets the list of closing HTML tags. This method is primarily used internally
     * for copying styles or for advanced manipulation.
     *
     * @param close An ArrayList of closing HTML tag strings to set.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText setClose(ArrayList<String> close) {
        this.close = close;
        return this;
    }

    /**
     * Sets the list of opening HTML tags. This method is primarily used internally
     * for copying styles or for advanced manipulation.
     *
     * @param open An ArrayList of opening HTML tag strings to set.
     * @return The current JLabelRichText instance for method chaining.
     */
    public JLabelRichText setOpen(ArrayList<String> open) {
        this.open = open;
        return this;
    }

    /**
     * An enumeration representing standard HTML heading tags (H1 to H6).
     * Each enum constant stores the opening and closing tag strings.
     */
    public enum Heading {

        /** Represents an H1 heading tag. */
        H1("h1"),
        /** Represents an H2 heading tag. */
        H2("h2"),
        /** Represents an H3 heading tag. */
        H3("h3"),
        /** Represents an H4 heading tag. */
        H4("h4"),
        H5("h5"),
        H6("h6"),;

        Heading(String i) {
            this.left = "<" + i + ">";
            this.right = "</"+ i + ">";
        }

        private String left, right;

        public static Heading fromInt(int headerLevel) {
            return switch (headerLevel) {
                case 1 -> H1;
                case 2 -> H2;
                case 3 -> H3;
                case 4 -> H4;
                case 5 -> H5;
                case 6 -> H6;
                default -> H1;
            };
        }

        /**
         * Returns the closing HTML tag for this heading.
         *
         * @return The closing tag string (e.g., {@code "</h1>"}).
         */
        public String getRight() {
            return right;
        }

        /**
         * Returns the opening HTML tag for this heading.
         *
         * @return The opening tag string (e.g., {@code "<h1>"}).
         */
        public String getLeft() {
            return left;
        }
    }
}
