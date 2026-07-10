package com.j3d.gen.docs.tokens.wrappers;

import com.j3d.errors.ErrorHandler;
import com.j3d.gen.docs.DocsGenException;
import com.j3d.gen.docs.tokens.TWrapper;
import com.j3d.gen.docs.tokens.WrapperType;
import com.j3d.utility.Parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TWhtmlTag extends TWrapper {

    private HTMLTags tag;
    private String tagContent;
    private final HashMap<String, String> attributes = new HashMap<>();

    public TWhtmlTag(String content) {
        super(WrapperType.HTML_TAG, new ArrayList<>(Collections.singletonList(content)));
        read(content);
    }

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
            ErrorHandler.handle(
                    new DocsGenException(
                            "The following content has "
                            + "different opening and closing tag: "
                            + content
                    )
            );
        }

        this.tag = HTMLTags.fromTagName(tag);
        if (this.tag == null) {
            ErrorHandler.handle(
                    new DocsGenException(
                            "The following tag name is not a valid HTML tag: "
                            + tag
                    )
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
                ErrorHandler.handle(
                        new DocsGenException(
                            "Malformed HTML attribute: " + attribute
                        )
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

    public HTMLTags getTag() {
        return tag;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public String getTagContent() {
        return tagContent;
    }
}
