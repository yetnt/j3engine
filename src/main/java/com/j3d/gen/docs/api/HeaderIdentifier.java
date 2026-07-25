package com.j3d.gen.docs.api;

import com.j3d.gen.docs.reader.tokens.wrappers.TWHeader;
import com.j3d.ui.help.HelpFrame;
import com.j3d.ui.help.TextPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/**
 * Represents a {@link TWHeader} within a {@link HelpFrame}'s {@link JTree}.
 *  * <p>
 *     This class is used to identify a header within the help documentation,
 *     allowing for navigation and hierarchical representation in a {@link JTree}.
 * </p>
 * @implNote When creating, the methods {@link #setNode(DefaultMutableTreeNode)} and
 * {@link #parseId(LinkedHashMap)} must be called at some point after the constructor.
 * @author Lehlogonolo Poole
 */
public class HeaderIdentifier {
    /**
     * The {@link TWHeader} token associated with this identifier, if it represents a specific header.
     */
    private TWHeader token;
    /**
     * The {@link TextPanel} where this header's content is displayed.
     */
    private TextPanel panel;
    /**
     * A {@link BiConsumer} that defines how to scroll to this header's content within a {@link JScrollPane}.
     */
    private BiConsumer<JPanel, JScrollPane> scrollTo = (p, s) -> {
        Rectangle rect = panel.getBounds();
        p.scrollRectToVisible(new Rectangle(
                rect.x,
                rect.y,
                rect.width,
                rect.height /*+ p.getSize().height*/
        ));
    };
    /**
     * A boolean indicating if this identifier represents the head of a file (i.e., the document's main title).
     */
    private boolean fileHead = false;
    /**
     * The title of the header.
     */
    private final String title;
    /**
     * The {@link DefaultMutableTreeNode} in the {@link JTree} that corresponds to this header.
     */
    private DefaultMutableTreeNode node;
    /**
     * The parsed, unique ID for this header, used for navigation and linking.
     * @implNote This is just what a normal markdown reader would expect this header to parse to.
     * So something like {@code Getting Started} would be {@code getting-started}
     */
    private String parsedId;

    /**
     * Constructs a new {@code HeaderIdentifier} for a specific header token.
     * @param token The {@link TWHeader} token representing the header.
     * @param panel The {@link TextPanel} where the header's content is displayed.
     */
    public HeaderIdentifier(TWHeader token, TextPanel panel) {
        this.token = token;
        this.panel = panel;
        this.title =
                token.getContent();
    }

    /**
     * Constructs a new {@code HeaderIdentifier} representing the head of a file (document's main title).
     * @param title The title of the file head.
     */
    public HeaderIdentifier(String title) {
        fileHead = true;
        scrollTo = (p, s) ->
                s.getViewport().setViewPosition(new Point(0, 0));
        this.title = title;
    }

    public void parseId(LinkedHashMap<String, HeaderIdentifier> other) {
        String parsed = title.trim();
        // replace all spaces with string
        parsed = parsed.replaceAll(" ", "-");
        // lowercase.
        parsed = parsed.toLowerCase();
        String finalParsed = parsed;
        int sz = other
                .entrySet()
                .stream().filter(
                        o -> {
                            return o.getKey().startsWith(finalParsed);
                        }
                ).toList().size();
        if (sz == 0) {
            parsedId = parsed;
            return;
        }

        parsedId = parsed + "-" + sz;
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

    public String getParsedId() {
        return parsedId;
    }
}
