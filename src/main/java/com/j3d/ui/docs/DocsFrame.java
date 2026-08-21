/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.docs;

import com.j3d.StaticRefs;
import com.j3d.gen.docs.Documentation;
import com.j3d.gen.docs.api.HeaderIdentifier;
import com.j3d.gen.docs.api.ImageTag;
import com.j3d.gen.docs.reader.*;
import com.j3d.gen.docs.reader.tokens.*;
import com.j3d.gen.docs.reader.tokens.wrappers.*;
import com.j3d.storage.JarPath;
import com.j3d.ui.theme.swing.J3DScrollBarUI;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.theme.swing.J3DTreeCellRenderer;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.tuple.Pair;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * The {@code DocsFrame} class represents a JFrame for displaying documentation within the J3Engine.
 * It can display a list of available documentation files or the content of a specific document,
 * including formatted text, images, and navigable headers.
 * <p>
 * This frame dynamically parses J3Engine's custom markdown-like documentation files (.j3.md)
 * and renders them using Swing components. It supports hierarchical navigation through headers
 * via a JTree and handles internal and external links.
 * </p>
 * @see com.j3d.gen.docs.DocsProvider
 * @see com.j3d.gen.docs.reader.J3DocsReader
 * @author yetnt
 */
public class DocsFrame extends javax.swing.JFrame {

    private final int size = 400;
    private final ArrayList<HeaderIdentifier> tempHeaderHierachy = new ArrayList<>();
    private final LinkedHashMap<String, HeaderIdentifier> headerIdentifiers = new LinkedHashMap<>();
    private HeaderIdentifier rootHeader;
    private Documentation doc;

    /**
     * Creates a new HelpFrame that displays a list of available help documents.
     */
    public DocsFrame() {
        initComponents();
        Documentation.toMap().forEach((key, value) -> contentPanel.add(
                new LinkPanel(key,value, size)
        ));
        J3DScrollBarUI.setBars(contentScrollPane);
        J3DScrollBarUI.setBars(jScrollPane2);
        jPanel2.remove(jPanel3);

        colors();
    }

    /**
     * Creates a new HelpFrame that displays the content of a specific help document.
     * @param fileIdentifier The identifier of the help document to display.
     */
    public DocsFrame(String fileIdentifier) {
        initComponents();
        Pair<String, JarPath> pair = Documentation.toMap().get(fileIdentifier);
        doc = Documentation.from(fileIdentifier);
        String helpContentName = pair.first;
        jLabel1.setText(helpContentName);
        setTree("Top");
        J3DScrollBarUI.setBars(contentScrollPane);
        J3DScrollBarUI.setBars(jScrollPane2);
        try {
            parse(pair.second);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        colors();
    }

    private void colors() {
        J3DTheme.commitAsGenericLbl(jLabel1, false);
        J3DTheme.commitAsGenericUi(jPanel2);
        J3DTheme.commitAsGenericUi(jPanel3);
        J3DTheme.commitAsGenericUi(jScrollPane2);
        J3DTheme.commitAsGenericUi(headerTree);
        J3DTheme.commitAsGenericUi(contentScrollPane);
        J3DTheme.commitAsGenericUi(contentPanel);

    }
    
    /**
     * Initializes the JTree component for displaying help document headers.
     * It sets up the tree's UI, creates a root node, and adds a mouse listener
     * to handle double-click events for navigating to header content.
     * @param title The title for the root node of the help tree.
     */
    private void setTree(String title) {
        BasicTreeUI treeUi = (BasicTreeUI)headerTree.getUI();
        treeUi.setLeftChildIndent(4);

        J3DTreeCellRenderer rend = new J3DTreeCellRenderer();
        headerTree.setCellRenderer(rend);
        rend.init(headerTree);

        rootHeader = new HeaderIdentifier(title);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootHeader);
        rootHeader.setNode(root);

        DefaultTreeModel model = new DefaultTreeModel(root);
        headerTree.setModel(model);

        headerTree.setToggleClickCount(0);

        headerTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                TreePath path = headerTree.getPathForLocation(
                        e.getX(), e.getY()
                );

                if (path != null) {
                    DefaultMutableTreeNode node
                            = (DefaultMutableTreeNode) path.getLastPathComponent();

                    HeaderIdentifier headerIdentifier = (HeaderIdentifier) node.getUserObject();


                    if (e.getClickCount() == 1) {
                        if (headerTree.isExpanded(path)) {
                            headerTree.collapsePath(path);
                        } else {
                            headerTree.expandPath(path);
                        }
                    } else if (e.getClickCount() == 2) {
                        headerIdentifier.getScrollTo().accept(contentPanel, contentScrollPane);
                    }

                }
            }
        });
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        for (int i = 0; i < node.getChildCount(); i++) {
            expandAll(tree, parent.pathByAddingChild(node.getChildAt(i)));
        }

        tree.expandPath(parent);
    }

    /**
     * Adds a child node to the tree under a specified parent node.
     *
     * @param parent The {@link HeaderIdentifier} representing the parent node.
     * @param child The {@link HeaderIdentifier} representing the child node to be added.
     */
    private void addTreeNode(HeaderIdentifier parent, HeaderIdentifier child) {
        child.parseId(headerIdentifiers);
        headerIdentifiers.put(child.getParsedId(), child);
        DefaultTreeModel model = (DefaultTreeModel) headerTree.getModel();
        model.insertNodeInto(
                child.getNode(), parent.getNode(), parent.getNode().getChildCount()
        );
    }

    // remember to set the defualt mutable treenode and make a header identifier
    // before calling this.

    /**
     * Adds a header to the tree structure, maintaining the hierarchical order
     * based on header levels. This method also updates the internal
     * {@code tempHeaderHierachy} to keep track of the current path in the tree.
     *
     * @param child The {@link HeaderIdentifier} representing the header to be added.
     */
    private void addHeader(HeaderIdentifier child) {

        // at root
        if (tempHeaderHierachy.isEmpty()) {

            tempHeaderHierachy.add(child);
            addTreeNode(rootHeader, child);
            return;
        }

        HeaderIdentifier parent = tempHeaderHierachy.getLast();

        int childLevel = child.getToken().getHeaderLevel();
        int parentLevel = parent.getToken().getHeaderLevel();

        // if the child is more than the parent, add.
        if (childLevel > parentLevel) {
            tempHeaderHierachy.add(child);
            addTreeNode(parent, child);
            return;
        }

        // if the child equals the parent, remove the last parent from the list, and recurse.
        if (childLevel == parentLevel) {
            tempHeaderHierachy.removeLast();
            addHeader(child);
            return;
        }

        // while the parent element's header is not this current header's
        // level, go up one level
        while (childLevel < parentLevel) {
            tempHeaderHierachy.removeLast();
            if (tempHeaderHierachy.isEmpty()) {
                break;
            }
            parent = tempHeaderHierachy.getLast();
            parentLevel = parent.getToken().getHeaderLevel();
        }

        // recurse
        addHeader(child);
    }

    public DocsFrame scrollToHeader(String header) {
        HeaderIdentifier h = headerIdentifiers.get(header);
        if (h != null) {
            h.getScrollTo().accept(contentPanel, contentScrollPane);
        }
        return this;
    }

    private void parse(JarPath file) throws FileNotFoundException {
//        testStuff();

        ArrayList<TWrapper> wrappers = J3DocsReader.parseFile(file);

        JLabelRichText div = new JLabelRichText()
                .wrapDiv(size);
        ArrayList<TLink> linksPerParagraph = new ArrayList<>();

        wrappers.forEach(wrapper -> {
            if (wrapper instanceof TWLineSeparator) {
                lineSeparator();
            } else if (wrapper instanceof TWHeader header) {
                addLinks(linksPerParagraph);
                TextPanel p = new TextPanel(
                        new JLabelRichText(header.getContent())
                                .heading(JLabelRichText.Heading.fromInt(header.getHeaderLevel()))
                                .addStyle(new LinkedHashMap<>(Map.of(
                                        "text-align", "center"
                                )))
                                .font(J3DTheme.TEXT_PRIMARY.color())
                                .wrapUsing(div)
                                .wrapHTML()
                );
                HeaderIdentifier headerIdentifier = new HeaderIdentifier(
                        header, p
                );
                headerIdentifier.setNode(
                        new DefaultMutableTreeNode(headerIdentifier)
                );
                addHeader(headerIdentifier);
                contentPanel.add(
                        p
                );
                linksPerParagraph.clear();
            } else if (wrapper instanceof TWParagraph paragraph) {
                StringBuilder content = new StringBuilder();
                paragraph.getParagraph().forEach(text -> {
                    JLabelRichText text1 = new JLabelRichText(
                            text.getContent() + (text instanceof TLink ?
                                    new JLabelRichText(" [" + (linksPerParagraph.size()+1) + "]")
                                            .bold().subscript()
                            : "")

                    )
                            .font(J3DTheme.TEXT_PRIMARY.color(), "4");
                    if (text instanceof TLink l) {
                        linksPerParagraph.add(l);
                        text1.underline();
                    }
                    if (text.isBold()) text1.bold();
                    if (text.isItalic()) text1.italic();
                    if (text.isInlineCode()) text1.wrapTag("code", new LinkedHashMap<>());
                    content.append(text1).append(" ");
                });
                contentPanel.add(
                        new TextPanel(
                                new JLabelRichText(content.toString()).wrapUsing(div)
                                        .wrapHTML()
                        )
                );
                // TODO: add links via buttons here at end of paragraph.
            } else if (wrapper instanceof TWhtmlTag tw) {
                ImageTag t = ImageTag.getInstance(tw);
                if (t != null) {
                    String alt = t.getAltText();
                    if (t.isInvalid())
                        contentPanel.add(
                            // just add some generic text with the image alt text instead

                            new TextPanel(
                                    new JLabelRichText("Image not found or invalid: " + alt)
                                            .font(J3DTheme.TEXT_PRIMARY.color())
                                            .wrapUsing(div)
                                            .wrapHTML()
                            )

                        );
                    else contentPanel.add(new TextPanel(t));
                }
            } else if (wrapper instanceof TWCodeBlock tc) {
                Color textCol = Color.WHITE;
                Color backCol = Color.DARK_GRAY;

                StringBuilder lines = new StringBuilder();
                for (int i = 0; i < tc.getLines().size(); i++) {
                    String line = tc.getLines().get(i);
                    lines.append(i+1).append(". ").append(line);
                    if (i+1 != tc.getLines().size())
                        lines.append(JLabelRichText.LINE_BREAK);
                }

                lineSeparator();

                contentPanel.add(
                        new TextPanel(
                                new JLabelRichText(lines.toString())
                                        .font(textCol, "+0", backCol)
                                        .wrapUsing(div)
                                        .addStyle(new LinkedHashMap<>(Map.of(
                                                // add background
                                                "padding", "10px",
                                                "border-radius", "5px",
                                                "background-color", JLabelRichText.colToStr(backCol)
                                        )))
                                        .wrapHTML()
                        ).asCodeBlock(backCol, tc, true)
                );

                lineSeparator();
            }
        });

        addLinks(linksPerParagraph);

        expandAll(
                headerTree,
                new TreePath(headerTree.getModel().getRoot())
        );
    }

    private void lineSeparator() {
        contentPanel.add(new TextPanel(
                new JLabelRichText(JLabelRichText.HORIZONTAL_LINE)
                        .wrapHTML()
        ));
    }

    private void addLinks(ArrayList<TLink> linksPerParagraph) {
        if (!linksPerParagraph.isEmpty()) {
            // add here
//                    contentPanel.add(
//                            new TextPanel(
//                                    JLabelRichText.from("links", div).wrapHTML()
//                            )
//                    );
            lineSeparator();
            contentPanel.add(
                    new LinksPanel(this, linksPerParagraph, size+100, doc)
            );
            linksPerParagraph.clear();
        }
    }

    private void testStuff() {
        String some = "pdsdfd ".repeat(20);

        TextPanel p = new TextPanel(
                new JLabelRichText(some)
                        .wrapDiv(size)
                        .heading(JLabelRichText.Heading.H1)
                        .addStyle(
                                new LinkedHashMap<>(
                                        Map.of(
                                                "text-align", "center"
                                        )
                                )
                        )
                        .wrapHTML()
        );
        TextPanel p2 = new TextPanel(
                new JLabelRichText(some.repeat(300))
                        .wrapDiv(size)
                        .wrapHTML()
        );

        contentPanel.add(p);
        contentPanel.add(p2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        headerTree = new javax.swing.JTree();
        contentScrollPane = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(J3DTheme.UI_SURFACE.color());
        setIconImage(StaticRefs.logo());
        setMinimumSize(new java.awt.Dimension(870, 500));

        jLabel1.setBackground(J3DTheme.UI_SURFACE.color());
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Help Menu");
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(J3DTheme.UI_SURFACE.color());
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(J3DTheme.UI_SURFACE.color());
        jPanel3.setLayout(new java.awt.BorderLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        headerTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        headerTree.setMaximumSize(new java.awt.Dimension(150, 74));
        headerTree.setPreferredSize(new java.awt.Dimension(150, 74));
        jScrollPane2.setViewportView(headerTree);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

        contentPanel.setBackground(J3DTheme.UI_SURFACE.color());
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        contentScrollPane.setViewportView(contentPanel);

        jPanel2.add(contentScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DocsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DocsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DocsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DocsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                StaticRefs.none();
                new DocsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JTree headerTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
