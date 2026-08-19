/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.home;

import com.j3d.Startup;
import com.j3d.StaticRefs;
import com.j3d.StaticConfig;
import com.j3d.engine.interact.input.keyboard.GlobalKeybinds;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.storage.files.FilesUtility;
import com.j3d.storage.files.util.ProjectImagePair;
import com.j3d.ui.theme.J3DScrollBarUI;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.ImageUtils;
import com.j3d.utility.generators.JLabelRichText;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ACER
 */
public class Projects extends javax.swing.JFrame {

    HashSet<ProjectButton> pinned = new HashSet<>();
    KeyBindings ks;

    /**
     * Creates new form Projects
     */
    public Projects() {
        initComponents();
        jLabel4.setText(
                JLabelRichText.htmlOf(
                        new JLabelRichText("Welcome "),
                        new JLabelRichText(StaticConfig.user.firstName.getValue()).italic(),
                        new JLabelRichText("!")
                )
        );
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                StaticRefs.getEngineFiles().pinned.writeProjs(
                        pinned.stream()
                                .map(ProjectButton::getIdentity)
                                .collect(Collectors.toCollection(HashSet::new))
                );
            }
        });
        J3DScrollBarUI.setBars(jScrollPane1);
        J3DScrollBarUI.setBars(starterScrollPane);
        J3DScrollBarUI.setBars(pinnedScrollPane);
        J3DScrollBarUI.setBars(recentsScrollPane);
        StaticRefs.getLog().uiPrintLn("Projects completed building");
        ks = new KeyBindings(
                pinnedProjectsPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
                pinnedProjectsPanel.getActionMap()
        );
        ks.registerJ3Key(GlobalKeybinds.F1.getKey());
        Projects p = this;
        ks.registerJ3Key(new J3Key(
                "quickOpenDebug",
                KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        StaticRefs.getLog().println(
                                "Quick Open Debug Scene"
                        );
                        p.dispose();
                        Startup.engineDebug();
                    }
                }
        ));
    }

    public ImageIcon scaleImage(File image) {
        try {
            return ImageUtils.createCroppedIcon2(image, 37, 16, 4);
        } catch (IIOException e) {
            try {
                return ImageUtils.createCroppedIcon2(StaticRefs.getEngineFiles().recents.NO_IMAGE, 37, 16, 4);
            } catch (IIOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public int genericLoad(ArrayList<ProjectImagePair> projs, JPanel targetPanel, boolean pin) {
        int width = 0;
        for (ProjectImagePair proj : projs) {
            File image = proj.getProjectImage();
            File project = proj.getProjectFile();
            ProjectButton pb = new ProjectButton(proj, scaleImage(image), project.getName(), this, targetPanel);
            if (pin) {
                pb.setPinned(true);
                pinned.add(pb);
            }
            // If not adding to the pinned panel, check if this project is already pinned
            // and mark it as a duplicate if it is.
            if (!pin) {
                pinned.stream()
                        .filter(p -> ProjectImagePair.isCopy(p.identity, proj))
                        .findFirst()
                        .ifPresent(pb::setDuplicate);
            }
            targetPanel.add(pb);
            targetPanel.add(pb.filler());
            width += pb.getPreferredSize().width;
            width += pb.filler().getPreferredSize().width;
        }
        return width;
    }

    public void loadRecentProjects() {
        ArrayList<ProjectImagePair> projs = StaticRefs.getEngineFiles().recents.readRecents();
        int w = genericLoad(projs, recentProjectsPanel, false);
        Dimension dim = new Dimension(w, 20);
        recentProjectsPanel.setPreferredSize(dim);
        recentProjectsPanel.setMaximumSize(dim);
        recentProjectsPanel.setMinimumSize(dim);
    }

    public void loadPinnedProjects() {
        ArrayList<ProjectImagePair> projs = StaticRefs.getEngineFiles().pinned.readPinned();
        int w = genericLoad(projs, pinnedProjectsPanel, true);
        Dimension dim = new Dimension(w, 20);
        pinnedProjectsPanel.setPreferredSize(dim);
        pinnedProjectsPanel.setMaximumSize(dim);
        pinnedProjectsPanel.setMinimumSize(dim);
    }

    public void loadStarterProjects() {
        StaticProjectButton freshProject = new StaticProjectButton("New Project", e -> {
            Startup.engine(false, false);
            dispose();
        });
        StaticProjectButton tutorialProject = new StaticProjectButton("Tutorial Project", e -> {
            Startup.engine(false, true);
            dispose();
        });
        StaticProjectButton openProject = new StaticProjectButton("Open Project", this::openProject);
//        StaticProjectButton debugScene = new StaticProjectButton("Debug Project", e -> {
//            Startup.engine(true, false);
//            dispose();
//        });
        int width = 0;
        
        starterProjectsPanel.add(freshProject);
        width += freshProject.getPreferredSize().width;
        starterProjectsPanel.add(freshProject.filler());
        width += freshProject.filler().getPreferredSize().width;
        
        starterProjectsPanel.add(openProject);
        width += openProject.getPreferredSize().width;
        starterProjectsPanel.add(openProject.filler());
        width += openProject.filler().getPreferredSize().width;

        starterProjectsPanel.add(tutorialProject);
        width += tutorialProject.getPreferredSize().width;
        starterProjectsPanel.add(tutorialProject.filler());
        width += tutorialProject.filler().getPreferredSize().width;
        
//        starterProjectsPanel.add(debugScene);
//        width += debugScene.getPreferredSize().width;
//        starterProjectsPanel.add(debugScene.filler());
//        width += debugScene.filler().getPreferredSize().width;
        
        Dimension dim = new Dimension(width, 20);
        starterProjectsPanel.setPreferredSize(dim);
        starterProjectsPanel.setMaximumSize(dim);
        starterProjectsPanel.setMinimumSize(dim);
    }
    
    public void openProject(MouseEvent e) {
        File file = FilesUtility.fileChooser(jfcConfig -> {
            jfcConfig.setDialogTitle("choose a filel");
            jfcConfig.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfcConfig.setAcceptAllFileFilterUsed(false);
            jfcConfig.setFileFilter(
                    new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getName().endsWith(".j3p") || !f.isFile();
                        }

                        @Override
                        public String getDescription() {
                            return "J3D Project File";
                        }
                    }
            );
        }, this);
        if (file == null) return;
        Startup.engine(file, false);
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        welcomePanelLabels = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scrollpanepanel = new javax.swing.JPanel();
        pinnedScrollPane = new javax.swing.JScrollPane();
        pinnedProjectsPanel = new ProjectsScrollPanel();
        starterScrollPane = new javax.swing.JScrollPane();
        starterProjectsPanel = new ProjectsScrollPanel();
        recentsScrollPane = new javax.swing.JScrollPane();
        recentProjectsPanel = new ProjectsScrollPanel();
        startLabelPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pinnedLabelPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        recentLabelPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("J3Engine Projects");
        setIconImage(StaticRefs.logo());
        setMaximumSize(new java.awt.Dimension(16, 2147483647));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        welcomePanelLabels.setBackground(J3DTheme.UI_SURFACE.color());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel4.setText("Weclome Name!");

        jLabel5.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel5.setText("Create a project or a starter project to get started");

        javax.swing.GroupLayout welcomePanelLabelsLayout = new javax.swing.GroupLayout(welcomePanelLabels);
        welcomePanelLabels.setLayout(welcomePanelLabelsLayout);
        welcomePanelLabelsLayout.setHorizontalGroup(
            welcomePanelLabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, welcomePanelLabelsLayout.createSequentialGroup()
                .addContainerGap(264, Short.MAX_VALUE)
                .addGroup(welcomePanelLabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(259, Short.MAX_VALUE))
        );
        welcomePanelLabelsLayout.setVerticalGroup(
            welcomePanelLabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLabelsLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        getContentPane().add(welcomePanelLabels);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollpanepanel.setBackground(J3DTheme.UI_SURFACE.color());

        pinnedScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        pinnedScrollPane.setMaximumSize(new java.awt.Dimension(327, 300));
        pinnedScrollPane.setPreferredSize(new java.awt.Dimension(120, 20));

        pinnedProjectsPanel.setBackground(J3DTheme.UI_SURFACE.color());
        pinnedProjectsPanel.setMaximumSize(new java.awt.Dimension(327, 300));
        pinnedProjectsPanel.setMinimumSize(new java.awt.Dimension(120, 20));
        pinnedProjectsPanel.setPreferredSize(new java.awt.Dimension(120, 20));
        pinnedProjectsPanel.setLayout(new javax.swing.BoxLayout(pinnedProjectsPanel, javax.swing.BoxLayout.LINE_AXIS));
        pinnedScrollPane.setViewportView(pinnedProjectsPanel);

        starterScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        starterScrollPane.setMaximumSize(new java.awt.Dimension(327, 300));
        starterScrollPane.setPreferredSize(new java.awt.Dimension(120, 20));

        starterProjectsPanel.setBackground(J3DTheme.UI_SURFACE.color());
        starterProjectsPanel.setMaximumSize(new java.awt.Dimension(327, 300));
        starterProjectsPanel.setMinimumSize(new java.awt.Dimension(120, 20));
        starterProjectsPanel.setPreferredSize(new java.awt.Dimension(120, 20));
        starterProjectsPanel.setLayout(new javax.swing.BoxLayout(starterProjectsPanel, javax.swing.BoxLayout.LINE_AXIS));
        starterScrollPane.setViewportView(starterProjectsPanel);

        recentsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        recentsScrollPane.setMaximumSize(new java.awt.Dimension(327, 300));
        recentsScrollPane.setPreferredSize(new java.awt.Dimension(120, 20));

        recentProjectsPanel.setBackground(J3DTheme.UI_SURFACE.color());
        recentProjectsPanel.setMaximumSize(new java.awt.Dimension(327, 300));
        recentProjectsPanel.setMinimumSize(new java.awt.Dimension(120, 20));
        recentProjectsPanel.setPreferredSize(new java.awt.Dimension(120, 20));
        recentProjectsPanel.setLayout(new javax.swing.BoxLayout(recentProjectsPanel, javax.swing.BoxLayout.LINE_AXIS));
        recentsScrollPane.setViewportView(recentProjectsPanel);

        startLabelPanel.setBackground(J3DTheme.UI_SURFACE.color());
        startLabelPanel.setPreferredSize(new java.awt.Dimension(400, 25));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel7.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel7.setText("Get Started");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(J3DTheme.TEXT_SECONDARY.color());
        jLabel1.setText("Start the tutorial or create a fresh new project.");

        javax.swing.GroupLayout startLabelPanelLayout = new javax.swing.GroupLayout(startLabelPanel);
        startLabelPanel.setLayout(startLabelPanelLayout);
        startLabelPanelLayout.setHorizontalGroup(
            startLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        startLabelPanelLayout.setVerticalGroup(
            startLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, startLabelPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(startLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1)))
        );

        pinnedLabelPanel.setBackground(J3DTheme.UI_SURFACE.color());
        pinnedLabelPanel.setPreferredSize(new java.awt.Dimension(400, 25));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel6.setText("Pinned Projects");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(J3DTheme.TEXT_SECONDARY.color());
        jLabel3.setText("Any projects you've pinned will persists here. Regardless of if it's in the revents already.");

        javax.swing.GroupLayout pinnedLabelPanelLayout = new javax.swing.GroupLayout(pinnedLabelPanel);
        pinnedLabelPanel.setLayout(pinnedLabelPanelLayout);
        pinnedLabelPanelLayout.setHorizontalGroup(
            pinnedLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pinnedLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        pinnedLabelPanelLayout.setVerticalGroup(
            pinnedLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pinnedLabelPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pinnedLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        recentLabelPanel.setBackground(J3DTheme.UI_SURFACE.color());
        recentLabelPanel.setPreferredSize(new java.awt.Dimension(400, 25));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel8.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel8.setText("Recent Projects");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(J3DTheme.TEXT_SECONDARY.color());
        jLabel2.setText("View all your recently saved projects. (Up to 20 maximum before discarding happens.)");

        javax.swing.GroupLayout recentLabelPanelLayout = new javax.swing.GroupLayout(recentLabelPanel);
        recentLabelPanel.setLayout(recentLabelPanelLayout);
        recentLabelPanelLayout.setHorizontalGroup(
            recentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recentLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        recentLabelPanelLayout.setVerticalGroup(
            recentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recentLabelPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(recentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jSeparator1.setForeground(J3DTheme.ACCENT_SECONDARY.color());
        jSeparator1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jSeparator2.setForeground(J3DTheme.ACCENT_SECONDARY.color());
        jSeparator2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout scrollpanepanelLayout = new javax.swing.GroupLayout(scrollpanepanel);
        scrollpanepanel.setLayout(scrollpanepanelLayout);
        scrollpanepanelLayout.setHorizontalGroup(
            scrollpanepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollpanepanelLayout.createSequentialGroup()
                .addGroup(scrollpanepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scrollpanepanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(scrollpanepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pinnedLabelPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                            .addComponent(startLabelPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                            .addComponent(recentLabelPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                            .addComponent(recentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pinnedScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(starterScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(scrollpanepanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1)))
                .addGap(7, 7, 7))
            .addGroup(scrollpanepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2)
                .addContainerGap())
        );
        scrollpanepanelLayout.setVerticalGroup(
            scrollpanepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scrollpanepanelLayout.createSequentialGroup()
                .addComponent(startLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(starterScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recentLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(recentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pinnedLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pinnedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(scrollpanepanel);

        getContentPane().add(jScrollPane1);
        loadPinnedProjects();
        loadRecentProjects();
        loadStarterProjects();

        pack();
        setLocationRelativeTo(null);
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
            java.util.logging.Logger.getLogger(Projects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Projects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Projects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Projects.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Projects().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel pinnedLabelPanel;
    public javax.swing.JPanel pinnedProjectsPanel;
    private javax.swing.JScrollPane pinnedScrollPane;
    private javax.swing.JPanel recentLabelPanel;
    public javax.swing.JPanel recentProjectsPanel;
    private javax.swing.JScrollPane recentsScrollPane;
    private javax.swing.JPanel scrollpanepanel;
    private javax.swing.JPanel startLabelPanel;
    public javax.swing.JPanel starterProjectsPanel;
    private javax.swing.JScrollPane starterScrollPane;
    private javax.swing.JPanel welcomePanelLabels;
    // End of variables declaration//GEN-END:variables
}
