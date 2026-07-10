package com.j3d.gen.docs.tokens;

import java.util.ArrayList;

public class TWrapper {
    private final WrapperType type;
    private final ArrayList<String> rawContent;

    public TWrapper(WrapperType type, ArrayList<String> rawContent) {
        this.type = type;
        this.rawContent = rawContent;
    }

    public ArrayList<String> getRawContent() {
        return rawContent;
    }

    public WrapperType getType() {
        return type;
    }
}
