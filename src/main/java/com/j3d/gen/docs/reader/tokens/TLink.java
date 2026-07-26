package com.j3d.gen.docs.reader.tokens;

/**
 * Represents a link token in the parsed documentation.
 * Extends {@link TText} to inherit text formatting properties.
 * @author Lehlogonolo Poole
 */
public class TLink extends TText {
    /**
     * The URL of the link.
     */
    private final String url;
    public TLink(String content, String url) {
        super(content);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Creates a new {@code TLink} instance by copying formatting properties from an existing {@code TText} object.
     * The content and URL for the new link are provided separately.
     *
     * @param t The {@code TText} object from which to copy italic, bold, and inline code properties.
     * @param content The text content of the link.
     * @param url The URL of the link.
     * @return A new {@code TLink} instance with the specified content and URL,
     *         and formatting properties copied from the input {@code TText} object.
     */
    public static TLink fromText(TText t, String content, String url) {
        return (TLink) (new TLink(content, url)
                .setItalic(t.isItalic())
                .setBold(t.isBold())
                .setInlineCode(t.isInlineCode()));
    }
}
