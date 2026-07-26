package com.j3d.gen.docs.reader.tokens.wrappers;

/**
 * Enum representing common HTML tags.
 * Each enum constant stores the string representation of the HTML tag name.
 */
public enum HTMLTags {
    /**
     * Represents the {@code <img>} HTML tag.
     */
    IMG("img");

    private final String tagName;

    HTMLTags(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Returns the string representation of the HTML tag name.
     *
     * @return The HTML tag name as a String.
     */
    public String getTagName() {
        return tagName;
    }

    public static HTMLTags fromTagName(String tagName) {
        for (HTMLTags tag : HTMLTags.values()) {
            if (tag.getTagName().equalsIgnoreCase(tagName)) {
                return tag;
            }
        }
        return null;
    }

}
