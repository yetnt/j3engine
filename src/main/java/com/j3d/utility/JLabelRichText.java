package com.j3d.utility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JLabelRichText {
    private String content;
    private ArrayList<String> open = new ArrayList<>();
    private ArrayList<String> close = new ArrayList<>();

    public JLabelRichText(String cont) {
        content = cont;
    }

    public JLabelRichText bold() {
        open.add("<b>");
        close.add("</b>");
        return this;
    }

    public JLabelRichText italic() {
        open.add("<i>");
        close.add("</i>");
        return this;
    }

    public JLabelRichText underline() {
        open.add("<u>");
        close.add("</u>");
        return this;
    }

    public JLabelRichText font(Color col) {
        open.add("<font color=\"" +
                String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue())
                + "\">");
        close.add("</font>");
        return this;

    }

    public JLabelRichText font(Color col, String size) {
        open.add("<font color=\"" +
                String.format("#%02x%02x%02x", col.getRed(), col.getGreen(), col.getBlue()) + "\" size=\"" + size + "\">");
        close.add("</font>");
        return this;
    }

    public JLabelRichText paragraph() {
        open.add("<p>");
        close.add("</p>");
        return this;
    }

    public JLabelRichText superscript() {
        open.add("<sup>");
        close.add("</sup>");
        return this;
    }

    public JLabelRichText subscript() {
        open.add("<sub>");
        close.add("</sub>");
        return this;
    }

    public String wrapHTML() {
        return "<html>" + toString() + "</html>";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        open.forEach(
                sb::append
        );
        sb.append(content);
        close.reversed().forEach(
                sb::append
        );
        return sb.toString();
    }

    public static String htmlOf(JLabelRichText... richTexts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        Arrays.stream(richTexts).forEach(sb::append);
        sb.append("</html>");
        return sb.toString();
    }

    public static JLabelRichText paragraphWrap(JLabelRichText... richTexts) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(richTexts).forEach(sb::append);
        return new JLabelRichText(sb.toString()).paragraph();
    }

    public static String HORIZONTAL_LINE = "<hr>";
}
