package com.j3d.gen.docs.reader.tokens;

public class TLink extends TText {
    private final String url;
    public TLink(String content, String url) {
        super(content);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static TLink fromText(TText t, String content, String url) {
        return (TLink) (new TLink(content, url)
                .setItalic(t.isItalic())
                .setBold(t.isBold())
                .setInlineCode(t.isInlineCode()));
    }
}
