package com.j3d.gen.docs.reader.tokens.wrappers;

import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.WrapperType;

import java.util.ArrayList;

public class TWCodeBlock extends TWrapper {
    private final ArrayList<String> lines;
    private final String language;
    private boolean isCmd = false;
    public TWCodeBlock(ArrayList<String> lines) {
        super(WrapperType.CODEBLOCK, new ArrayList<>(lines));
        this.lines = new ArrayList<>(lines.subList(1, lines.size()));
        this.language = lines.getFirst();
        if (this.language.equals("cmd") || this.language.equals("command")) {
            this.isCmd = true;
        }
    }

    public boolean isCommand() {
        return isCmd;
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public String getLanguage() {
        return language;
    }
}
