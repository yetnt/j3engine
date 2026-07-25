package com.j3d.gen.docs.reader.tokens.wrappers;

public enum HTMLTags {
    IMG("img");

    private final String tagName;

    HTMLTags(String tagName) {
        this.tagName = tagName;
    }

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
