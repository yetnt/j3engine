package com.j3d.gen.docs.reader.tokens;

public class TText {
    private String content;
    private boolean
            bold = false,
            italic = false,
            inlineCode = false;

    public TText(String content) {
        this.content = content;
    }

    public TText setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isInlineCode() {
        return inlineCode;
    }

    public TText setInlineCode(boolean inlineCode) {
        this.inlineCode = inlineCode;
        return this;
    }

    public TText setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public String getContent() {
        return content;
    }
}
