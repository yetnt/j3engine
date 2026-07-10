package com.j3d.gen.docs.tokens.wrappers;

import com.j3d.gen.docs.tokens.TWrapper;
import com.j3d.gen.docs.tokens.WrapperType;

import java.util.ArrayList;
import java.util.Collections;

public class TWHeader extends TWrapper {
    private int headerLevel;
    private String content;
    public TWHeader(String content, int headerLevel) {
        super(WrapperType.HEADER, new ArrayList<>(Collections.singleton(content)));
        this.content = content;
        this.headerLevel = headerLevel;
    };

    public String getContent() {
        return content;
    }

    public int getHeaderLevel() {
        return headerLevel;
    }
}
