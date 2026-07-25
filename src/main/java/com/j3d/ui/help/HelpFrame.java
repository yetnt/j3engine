/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.help;

import com.j3d.StaticRefs;
import com.j3d.gen.docs.api.HeaderIdentifier;
import com.j3d.gen.docs.reader.*;
import com.j3d.gen.docs.reader.tokens.*;
import com.j3d.gen.docs.reader.tokens.wrappers.*;
import com.j3d.ui.generic.J3DScrollBarUI;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.utility.generators.JLabelRichText;

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
 * @author yetnt
 */
public class HelpFrame extends javax.swing.JFrame {

    private final int size = 400;
    private final ArrayList<HeaderIdentifier> tempHeaderHierachy = new ArrayList<>();
    private HeaderIdentifier rootHeader;

    /**
     * Creates new form HelpFrame
     */
    public HelpFrame(String fileName) {
        initComponents();
        jLabel1.setText(fileName);
        setTree(fileName);
        J3DScrollBarUI.setBars(contentScrollPane);
        J3DScrollBarUI.setBars(jScrollPane2);
        try {
            test();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
//            if (h.getHeaderLevel() != 1)
//                StaticRefs.getErrs().handle(
//                        new DocsGenException(
//                                "First header is not an H1 header."
//                        )
//                );

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

    private void test() throws FileNotFoundException {
//        testStuff();

        ArrayList<TWrapper> wrappers = J3DocsReader.parseFile(
                StaticRefs.getEngineFiles().docsFolder.about
        );

        JLabelRichText div = new JLabelRichText()
                .wrapDiv(size);
        ArrayList<TLink> linksPerParagraph = new ArrayList<>();

        wrappers.forEach(wrapper -> {
            if (wrapper instanceof TWLineSeparator) {
                contentPanel.add(new TextPanel(
                        new JLabelRichText(JLabelRichText.HORIZONTAL_LINE)
                                .wrapHTML()
                ));
            } else if (wrapper instanceof TWHeader header) {
                if (!linksPerParagraph.isEmpty()) {
                    // add here
                    contentPanel.add(
                            new TextPanel(
                                    JLabelRichText.from("links", div).wrapHTML()
                            )
                    );
                }
                TextPanel p = new TextPanel(
                        JLabelRichText.from(
                                        header.getContent(),
                                        div
                                )
                                .heading(JLabelRichText.Heading.fromInt(header.getHeaderLevel()))
                                .addStyle(new LinkedHashMap<>(Map.of(
                                        "text-align", "center"
                                )))
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
                    if (text instanceof TLink l) {
                        linksPerParagraph.add(l);
                    }
                    JLabelRichText text1 = new JLabelRichText(
                            text.getContent() + (text instanceof TLink ?
                                    "[" + linksPerParagraph.size() + "]"
                            : "")
                    );
                    if (text.isBold()) text1.bold();
                    if (text.isItalic()) text1.italic();
                    if (text.isInlineCode()) text1.wrapTag("code", new LinkedHashMap<>());
                    content.append(text1).append(" ");
                });
                contentPanel.add(
                        new TextPanel(
                                JLabelRichText.from(
                                        content.toString(),
                                        div
                                )
                                        .wrapHTML()
                        )
                );
                // TODO: add links via buttons here at end of paragraph.
            }
        });

        expandAll(
                headerTree,
                new TreePath(headerTree.getModel().getRoot())
        );
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
        setMinimumSize(new java.awt.Dimension(870, 500));
        setPreferredSize(new java.awt.Dimension(286, 410));

        jLabel1.setBackground(J3DTheme.UI_SURFACE.color());
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Help Menu");
        getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        headerTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        headerTree.setMaximumSize(new java.awt.Dimension(150, 74));
        headerTree.setPreferredSize(new java.awt.Dimension(150, 74));
        jScrollPane2.setViewportView(headerTree);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.WEST);

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
            java.util.logging.Logger.getLogger(HelpFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HelpFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HelpFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HelpFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HelpFrame("TEST").setVisible(true);
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
