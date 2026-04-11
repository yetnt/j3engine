package com.j3d.ui.util;

import javax.swing.*;

public class HoverJLabel {
    private SafeJLabel sjl;
    public HoverJLabel(JLabel hover) {
        this.sjl = new SafeJLabel(hover, null);
    }

    public void setText(String text) {
        sjl.setText(text);
    }

    public static String EMPH = "%EMPH%";

    public void setText(String text, Object... emphasize) {
        sjl.setText(text, emphasize);
    }

    public void error(String text) {
        sjl.error(text);
    }

    public void error(String text, Object... emphasize) {
        sjl.error(text, emphasize);
    }

    public void clear() {
        sjl.clearHigher();
    }
}
