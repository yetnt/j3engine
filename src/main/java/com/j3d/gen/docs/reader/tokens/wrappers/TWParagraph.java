package com.j3d.gen.docs.reader.tokens.wrappers;

import com.j3d.gen.docs.reader.tokens.TText;
import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.WrapperType;

import java.util.ArrayList;

public class TWParagraph extends TWrapper {

    private ArrayList<TText> paragraph;

    public TWParagraph(ArrayList<TText> text) {
        super(WrapperType.PARAGRAPH, new ArrayList<>());
        this.paragraph = new ArrayList<>(text);
    }

    public ArrayList<TText> getParagraph() {
        return paragraph;
    }
}
