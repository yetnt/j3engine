package com.j3d.gen.docs.reader.tokens;

import com.j3d.gen.docs.reader.J3DocsReader;

/**
 * A class representing a piece of text with potential formatting (bold, italic, inline code).
 * @see TLink
 * @see J3DocsReader
 * @author Lehlogonolo Poole
 */
public class TText {
    private String content;
    private boolean
            bold = false,
            italic = false,
            inlineCode = false;

    /**
     * Constructs a new TText object with the given content.
     *
     * @param content The text content.
     */
    public TText(String content) {
        this.content = content;
    }

    /**
     * Sets whether the text should be bold.
     *
     * @param bold True if the text should be bold, false otherwise.
     * @return This TText instance for method chaining.
     */
    public TText setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    /**
     * Checks if the text is bold.
     *
     * @return True if the text is bold, false otherwise.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Checks if the text is italic.
     *
     * @return True if the text is italic, false otherwise.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Checks if the text is inline code.
     *
     * @return True if the text is inline code, false otherwise.
     */
    public boolean isInlineCode() {
        return inlineCode;
    }

    /**
     * Sets whether the text should be inline code.
     *
     * @param inlineCode True if the text should be inline code, false otherwise.
     * @return This TText instance for method chaining.
     */
    public TText setInlineCode(boolean inlineCode) {
        this.inlineCode = inlineCode;
        return this;
    }

    /**
     * Sets whether the text should be italic.
     *
     * @param italic True if the text should be italic, false otherwise.
     * @return This TText instance for method chaining.
     */
    public TText setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    /**
     * Gets the content of the text.
     *
     * @return The text content.
     */
    public String getContent() {
        return content;
    }
}
