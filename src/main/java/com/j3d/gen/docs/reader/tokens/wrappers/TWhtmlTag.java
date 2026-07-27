package com.j3d.gen.docs.reader.tokens.wrappers;

import com.j3d.StaticRefs;
import com.j3d.gen.docs.DocsGenException;
import com.j3d.gen.docs.reader.J3DocsReader;
import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.WrapperType;
import com.j3d.utility.Parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
/**
 * Represents an HTML tag token wrapper.
 * This class parses an HTML string, extracts the tag name, attributes, and content,
 * and provides methods to access them.
 * @see TWrapper
 * @see J3DocsReader
 * @author Lehlogonolo Poole
 */
public class TWhtmlTag extends TWrapper {

    private HTMLTags tag;
    private String tagContent;
    private final HashMap<String, String> attributes = new HashMap<>();

    public TWhtmlTag(String content) {
        super(WrapperType.HTML_TAG, new ArrayList<>(Collections.singletonList(content)));
        read(content);
    }

    /**
     * Parses the given HTML string to extract the tag name, attributes, and content.
     * It performs basic validation for matching opening and closing tags and valid HTML tag names.
     *
     * @param content The HTML string representing a single tag, e.g., {@code <font t="d" what=10>hi</font>}.
     */
    private void read(String content) {
        // given a html string like
        // <font t="d" what=10>hi</font>
        // ensure valid tag.

        // <(name)(space)
        String tag = content.substring(1, content.indexOf(' '));
        // ... </font>
        String closingTag = content.substring(
                content.indexOf("</")+2,
                content.length()-1);
        if (!tag.equals(closingTag)) {
            StaticRefs.getErrs().handle(
                    new DocsGenException(
                            "The following content has "
                            + "different opening and closing tag: "
                            + content
                    ).code(211)
            );
        }

        this.tag = HTMLTags.fromTagName(tag);
        if (this.tag == null) {
            StaticRefs.getErrs().handle(
                    new DocsGenException(
                            "The following tag name is not a valid HTML tag: "
                            + tag
                    ).code(212)
            );
        }

        // No attribute.
        if (content.indexOf(tag + ">") == 1)
            return;

        String attributes =
                // right after the first space
            content.substring(
                    content.indexOf(' ')+1,
                    content.indexOf('>')
            );

        ArrayList<String> attributesList =
                Parsing.split(attributes, ' ');
        for (String attribute : attributesList) {
            String[] property = attribute.split("=");
            if (property.length != 2) {
                StaticRefs.getErrs().handle(
                        new DocsGenException(
                            "Malformed HTML attribute: " + attribute
                        ).code(213)
                );
            }

            String value = property[1];
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length()-1);
            }

            this.attributes.put(property[0], value);
        }

        // get the content
        // <tag>...</tag>

        this.tagContent = content.substring(
                content.indexOf('>')+1,
                content.lastIndexOf("</")
        );
    }

    /**
     * Returns the {@link HTMLTags} enum representing the HTML tag name.
     *
     * @return The HTML tag enum.
     */
    public HTMLTags getTag() {
        return tag;
    }

    /**
     * Returns a map of attributes found in the HTML tag.
     * The keys are attribute names (e.g., "t", "what") and values are their corresponding values (e.g., "d", "10").
     *
     * @return A {@link HashMap} where keys are attribute names and values are attribute values.
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Returns the content enclosed within the HTML tag.
     * @return The string content of the tag, or {@code null} if no content was found.
     */
    public String getTagContent() {
        return tagContent;
    }
}
