package com.j3d.gen.docs.reader.tokens.wrappers;

import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.WrapperType;

import java.util.ArrayList;

public class TWCodeBlock extends TWrapper {
    private final ArrayList<String> lines;
    private final String language;
    public TWCodeBlock(ArrayList<String> lines) {
        super(WrapperType.CODEBLOCK, new ArrayList<>(lines));
        this.lines = new ArrayList<>(lines);
        this.language = lines.getFirst();
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public String getLanguage() {
        return language;
    }
}
