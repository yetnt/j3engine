package com.j3d.gen.docs.reader.tokens;

/**
 * Enum representing different types of wrappers or blocks of content that can be
 * found in a document.
 * @author Lehlogonolo Poole
 */
public enum WrapperType {
    /**
     * Represents a header element (e.g., H1, H2, etc.).
     */
    HEADER,
    /**
     * Represents a generic HTML tag that is not specifically handled by other wrapper types.
     */
    HTML_TAG,
    /**
     * Represents a hyperlink.
     */
    LINK,
    /**
     * Represents a paragraph of text.
     */
    PARAGRAPH,
    /**
     * Represents a line separator.
     */
    LINE_SEPARATOR,
    /**
     * Represents a block of code.
     */
    CODEBLOCK
}
