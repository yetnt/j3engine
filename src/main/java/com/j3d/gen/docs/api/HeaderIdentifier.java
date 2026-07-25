package com.j3d.gen.docs.api;

import com.j3d.gen.docs.reader.tokens.TText;
import com.j3d.gen.docs.reader.tokens.wrappers.TWHeader;
import com.j3d.ui.help.HelpFrame;
import com.j3d.ui.help.TextPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a {@link TWHeader} within a {@link HelpFrame}'s {@link JTree}.
 * It stores a reference to the header token, the corresponding UI panel,
and a {@link BiConsumer} for scrolling to this header's content in the UI.
 * @author Lehlogonolo Poole
 */
public class HeaderIdentifier {
    private TWHeader token;
    private TextPanel panel;
    private BiConsumer<JPanel, JScrollPane> scrollTo = (p, s) ->
            p.scrollRectToVisible(panel.getBounds());
    private boolean fileHead = false;
    private String title;
    private DefaultMutableTreeNode node;

    public HeaderIdentifier(TWHeader token, TextPanel panel) {
        this.token = token;
        this.panel = panel;
        this.title =
                token.getContent();
    }

    public HeaderIdentifier(String title) {
        fileHead = true;
        scrollTo = (p, s) ->
                s.getViewport().setViewPosition(new Point(0, 0));
        this.title = title;
    }

    public boolean isFileHead() {
        return fileHead;
    }

    public TWHeader getToken() {
        return token;
    }

    public TextPanel getPanel() {
        return panel;
    }

    public BiConsumer<JPanel, JScrollPane> getScrollTo() {
        return scrollTo;
    }

    @Override
    public String toString() {
        return title;
    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }
}
