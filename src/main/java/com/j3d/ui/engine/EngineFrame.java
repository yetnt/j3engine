/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.engine;

import com.j3d.Static;
import com.j3d.Executor;
import com.j3d.J3DSettings;
import com.j3d.engine.geometry.Dim;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.interact.cmd.commands.orbit.OrbitCmd;
import com.j3d.engine.interact.cmd.commands.transform.RotateSelection;
import com.j3d.engine.interact.cmd.commands.transform.ScaleSelection;
import com.j3d.engine.interact.cmd.commands.transform.TranslateSelection;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.engine.interact.input.mouse.NoMouseOwner;
import com.j3d.engine.interact.selection.SelectionManager;
//import com.j3d.jaiva.Testing;
import com.j3d.gen.settings.CoreSettings;
import com.j3d.gen.settings.Settings;
import com.j3d.storage.files.FilesUtility;
import com.j3d.storage.files.ProjectFile;
import com.j3d.threads.LongTask;
import com.j3d.ui.CursorManager;
import com.j3d.ui.CursorNames;
import com.j3d.ui.settings.PreferencesFrame;
import com.j3d.ui.engine.tb.Toolbox;
//import com.jaiva.JBundler;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import static com.j3d.J3DSettings.jMenuBarOffsetY;
import static com.j3d.engine.interact.input.keyboard.KeyBindings.commandPaletteFocusOwner;

import com.j3d.engine.draw.ViewType;
import com.j3d.ui.dialog.AreYouSure;
import com.j3d.ui.HoverJLabel;

/**
 *
 * @author ACER
 */
public class EngineFrame extends javax.swing.JFrame {
    public static boolean run = true;
    private static MOwner mouseOwner = MOwner.SELECTION;
    public static ScreenPoint mousePos = null;
    public static ScreenPoint[] selectionArea = new ScreenPoint[2];
    public static final CommandPalette COMMAND_PALETTE = new CommandPalette();

    public static void setMouseOwner(MOwner owner) {
        mouseOwner = owner == null ? MOwner.SELECTION : owner;
    }
    public static MOwner getMouseOwner() {
        return mouseOwner;
    }

    public static ArrayList<Runnable> floats = new ArrayList<>();

    public static void addFloaterAt(FloatingPanel p, Point lastLocation) {
        Runnable r = () -> {
            if (p.isHidden()) p.hideThis();
            p.setBounds(lastLocation.x, lastLocation.y, p.getPreferredSize().width, p.getPreferredSize().height);
            Static.mainFrame.getLayeredPane().add(p, JLayeredPane.POPUP_LAYER);
            Static.mainFrame.revalidate();
            Static.mainFrame.repaint();
        };
        if (run) floats.add(r);
        else r.run();
    }

    public static void bringForward(FloatingPanel p) {
        Static.mainFrame.getLayeredPane().moveToFront(p);
        Static.mainFrame.revalidate();
        Static.mainFrame.repaint();
    }

    public static void removeFloater(FloatingPanel p) {
        Static.mainFrame.getLayeredPane().remove(p);
        Static.mainFrame.revalidate();
        Static.mainFrame.repaint();
    }

    public void complete(boolean runExecutor) {
        Static.mainFrame = this;
        final int menuBarOffsetY = (Static.mainFrame.getJMenuBar().getSize().height + jMenuBarOffsetY);
        Static.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Static.sceneManager = new SceneManager(J3DSettings.screenSize);
        if (runExecutor)
            Static.executor = new Executor(Static.sceneManager);
        Static.debugPanel.run(Static.sceneManager, Static.executor, Static.mainFrame);
        HoverJLabelPanel lbl = new HoverJLabelPanel();
        lbl.setBounds(0 ,0, lbl.getPreferredSize().width, lbl.getPreferredSize().height);
        JLayeredPane layeredPane = Static.mainFrame.getLayeredPane();
        layeredPane.add(lbl, JLayeredPane.DRAG_LAYER);
        lbl.setVisible(true);
        Static.hoverLabel = new HoverJLabel(lbl.getLabel());
        Static.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Static.mainFrame.setSize(J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        Static.mainFrame.setResizable(false);

        Static.mainPanel = mainPanel;
        Static.mainPanel.setFocusable(true);

        InputMap im = Static.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = Static.mainPanel.getActionMap();
        Static.keybinds = new KeyBindings(im, am);

        Toolbox toolbox = new Toolbox();
        // Toolbox at the top and extends full width but not very tall
        toolbox.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width - 260, toolbox.getPreferredSize().height);
        layeredPane.add(toolbox, JLayeredPane.MODAL_LAYER); // above default layer

        Static.mainPanel.requestFocusInWindow();
        Static.mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        Static.mainPanel.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        Static.mainPanel.setPreferredSize(new Dimension(J3DSettings.screenSize.width, J3DSettings.screenSize.height));

        Static.getLog().setLogArea(Static.debugPanel.logTextArea); // initialize logger with the text area

        Rectangle bounds = Static.mainFrame.getBounds();
        Dimension size = COMMAND_PALETTE.getPreferredSize();
        int x = ((bounds.width - size.width) / 2) - 60;
        int y = bounds.height - size.height - 200;
        COMMAND_PALETTE.setBounds(x, y, size.width, size.height);

        COMMAND_PALETTE.setOpaque(true);
        COMMAND_PALETTE.setBackground(new Color(30, 30, 30, 8));
        COMMAND_PALETTE.setVisible(true);
        layeredPane.add(COMMAND_PALETTE, JLayeredPane.POPUP_LAYER);

        Static.commandParser = new CommandParser(COMMAND_PALETTE);

        Static.mainPanel.getRootPane().setFocusable(true);
        Static.mainPanel.getRootPane().requestFocusInWindow();

        Static.mainFrame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                J3DSettings.screenSize = new Dim(Static.mainFrame.getSize());
                Static.sceneManager.screenSize = J3DSettings.screenSize;
                toolbox.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width - 10, toolbox.getPreferredSize().height);
                Static.mainPanel.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width, J3DSettings.screenSize.height);

//                Static.layerTree.setBounds(
//                        J3DSettings.screenSize.width - 260 -(Static.layerTree.getPreferredSize().width),
//                        toolbox.getPreferredSize().height + menuBarOffsetY,
//                        Static.layerTree.getPreferredSize().width,
//                        Static.layerTree.getPreferredSize().height);

                Rectangle bounds = Static.mainFrame.getBounds();
                Dimension size = COMMAND_PALETTE.getPreferredSize();
                int x = ((bounds.width - size.width) / 2) - 10;
                int y = bounds.height - size.height - 50;
                COMMAND_PALETTE.setBounds(x, y, size.width, size.height);

                Static.mainFrame.repaint(); // repaint the frame
                Static.mainPanel.repaint(); // repaint the panel too.
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        CursorManager.init(Static.mainFrame);
        CursorManager.setDefault();

        floats.forEach(Runnable::run);

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
//                lbl.setLocation(e.getPoint());
                int y = bounds.height - size.height - 200;
                if (e.getY() > y - 20) return;
                lbl.setBounds(e.getX(), e.getY(), lbl.getPreferredSize().width, lbl.getPreferredSize().height);
                layeredPane.revalidate();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                lbl.setLocation(e.getPoint());
                int y = bounds.height - size.height - 200;
                if (e.getY() > y - 20) return;
                lbl.setBounds(e.getX(), e.getY(), lbl.getPreferredSize().width, lbl.getPreferredSize().height);
                layeredPane.revalidate();
            }
        });
        Static.getLog().uiPrintLn("EngineFrame completed building");
    }

    public EngineFrame(File file) {
        this(false);
        this.setVisible(true);

        String path = file.getAbsolutePath();
        Static.getLog().println(path);
        Path p = Paths.get(path);
        String fileName = p.getFileName().toString();
        String fileDir = p.getParent().toString();

//        J3DSettings.setProject(fileDir, fileName);
        Settings.projectOutputFile.setValue(file);

        LongTask<ArrayList<Interactable>> t = new LongTask<>(
                ta -> {
                    ArrayList<Interactable> a = null;
                    try {
                        a = new ProjectFile()
                                .readFile(fileDir, fileName, ta);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(a.size());
                    return a;
                },
                (tb, i) -> {
                    i.forEach(Interactable::invokeSwingHooks);
                }
        );

        t.run();
    }

    /**
     * Creates new form Main2
     */
    public EngineFrame(boolean runExecutor) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this); // 'this' refers to the frame
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<MouseOwner> owners = new ArrayList<>();
        owners.add(SelectionManager.selectionMouseOwner);
        owners.add(new NoMouseOwner());
        owners.add(ScaleSelection.scaleMouseOwner);
        owners.add(TranslateSelection.translateMouseOwner);
        owners.add(RotateSelection.rotateMouseOwner);
        owners.add(OrbitCmd.orbitMouseOwner);

        owners.forEach(this::addMouseListener);
        owners.forEach(this::addMouseMotionListener);
        initComponents();
        complete(runExecutor);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boolean saved = CoreSettings.hasSaved;
                if (saved) {
                    Static.mainFrame.dispose();
                    System.exit(0);
                }
                AreYouSure ays = new AreYouSure(Static.mainFrame, true, "You have not saved this project. Progress will be lost.");
                ays.setVisible(true);
                if (ays.canProceed()) {
                    Static.mainFrame.dispose();
                    System.exit(0);
                }
            }
        });
    }

//    /**
//     * Initializes (if not already initialized) the Jaiva Instance by inputting the input file and passing {@link Testing} class
//     * @param g The graphics
//     * @param r The SceneManager Instance.
//     */
//    private void initBundler(Graphics g, SceneManager r) {
//        if (jBundler == null) {
//            try {
//                jBundler = new JBundler("C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\src\\main\\resources\\file.jiv", Testing.class);
//                jBundler.run(r);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }


    /**
     * Repaints the debug panel, command pallete, and main frame on the Event Dispatch Thread.
     * This should only be called from non-EDT threads. e.g. from the SceneManager thread.
     */
    public static void repaintL() {
        SwingUtilities.invokeLater(() -> {
            if (Static.debugPanel != null) {
                Static.debugPanel.revalidate();
                Static.debugPanel.repaint();
            }
            COMMAND_PALETTE.revalidate();
            COMMAND_PALETTE.repaint();
            if (Static.mainFrame != null) {
                Static.mainFrame.revalidate();
                Static.mainFrame.repaint();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new J3DPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveProjectJMenuItem = new javax.swing.JMenuItem();
        openProjectMenuItem = new javax.swing.JMenuItem();
        newProjectJMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
        editJMenu = new javax.swing.JMenu();
        undoJMenuItem = new javax.swing.JMenuItem();
        redoJMenuItem = new javax.swing.JMenuItem();
        mouseJMenu = new javax.swing.JMenu();
        viewJMenu = new javax.swing.JMenu();
        viewAsNormalJMenuItem = new javax.swing.JMenuItem();
        viewAsWireframeJMenuItem = new javax.swing.JMenuItem();
        redrawJMenuItem = new javax.swing.JMenuItem();
        resetJMenuItemDropDown = new javax.swing.JMenu();
        resetCameraJMenuItem = new javax.swing.JMenuItem();
        resetOrientationJMenuItem = new javax.swing.JMenuItem();
        resetPositionJMenuItem = new javax.swing.JMenuItem();
        exportJMenuItemDropDown = new javax.swing.JMenu();
        exportAsPNGJMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        logOutJMenuItem = new javax.swing.JMenuItem();
        deleteAccountJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("J3D");
        setIconImage(Static.logo());
        setMinimumSize(new java.awt.Dimension(1024, 768));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1489, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 787, Short.MAX_VALUE)
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        saveProjectJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveProjectJMenuItem.setText("Save");
        saveProjectJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveProjectJMenuItem);

        openProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        openProjectMenuItem.setText("Open Project");
        openProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(openProjectMenuItem);

        newProjectJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        newProjectJMenuItem.setText("New Project");
        newProjectJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(newProjectJMenuItem);

        settingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(settingsMenuItem);

        jMenuBar1.add(jMenu1);

        editJMenu.setText("Edit");

        undoJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        undoJMenuItem.setText("Undo");
        undoJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoJMenuItemActionPerformed(evt);
            }
        });
        editJMenu.add(undoJMenuItem);

        redoJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        redoJMenuItem.setText("Redo");
        redoJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoJMenuItemActionPerformed(evt);
            }
        });
        editJMenu.add(redoJMenuItem);

        jMenuBar1.add(editJMenu);

        mouseJMenu.setText("Mouse");
        jMenuBar1.add(mouseJMenu);

        viewJMenu.setText("View");

        viewAsNormalJMenuItem.setText("Normal View");
        viewAsNormalJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsNormalJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(viewAsNormalJMenuItem);

        viewAsWireframeJMenuItem.setText("Wireframe View");
        viewAsWireframeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsWireframeJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(viewAsWireframeJMenuItem);

        redrawJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        redrawJMenuItem.setText("Redraw");
        redrawJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redrawJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(redrawJMenuItem);

        resetJMenuItemDropDown.setText("Reset");

        resetCameraJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetCameraJMenuItem.setText("Reset Camera");
        resetCameraJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCameraJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetCameraJMenuItem);

        resetOrientationJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetOrientationJMenuItem.setText("Reset Orientation");
        resetOrientationJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetOrientationJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetOrientationJMenuItem);

        resetPositionJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetPositionJMenuItem.setText("Reset Position");
        resetPositionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPositionJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetPositionJMenuItem);

        viewJMenu.add(resetJMenuItemDropDown);

        exportJMenuItemDropDown.setText("Export As...");

        exportAsPNGJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        exportAsPNGJMenuItem.setText("PNG");
        exportAsPNGJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAsPNGJMenuItemActionPerformed(evt);
            }
        });
        exportJMenuItemDropDown.add(exportAsPNGJMenuItem);

        viewJMenu.add(exportJMenuItemDropDown);

        jMenuBar1.add(viewJMenu);

        jMenu2.setText("User");

        logOutJMenuItem.setText("Log Out");
        logOutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(logOutJMenuItem);

        deleteAccountJMenuItem.setText("Delete Account");
        jMenu2.add(deleteAccountJMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1503, 817));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void resetPositionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPositionJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        Static.camera.setPosition(new Vector3(0, 0, 0));
        Static.mainFrame.repaint();
    }//GEN-LAST:event_resetPositionJMenuItemActionPerformed

    private void undoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoJMenuItemActionPerformed
        SceneManager.history.undo();
        Static.mainFrame.repaint();
    }//GEN-LAST:event_undoJMenuItemActionPerformed

    private void redoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoJMenuItemActionPerformed
        SceneManager.history.redo();
        Static.mainFrame.repaint();
    }//GEN-LAST:event_redoJMenuItemActionPerformed

    private void resetOrientationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetOrientationJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        Static.camera.setRotation(new Rotation(0, 0, 0));
        Static.mainFrame.repaint();
    }//GEN-LAST:event_resetOrientationJMenuItemActionPerformed

    private void resetCameraJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCameraJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        Static.camera.setPosition(new Vector3(0, 0, 0));
        Static.camera.setRotation(new Rotation(0, 0, 0));
        Static.mainFrame.repaint();
    }//GEN-LAST:event_resetCameraJMenuItemActionPerformed

    private void redrawJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redrawJMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_redrawJMenuItemActionPerformed

    private void viewAsWireframeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAsWireframeJMenuItemActionPerformed
        J3DSettings.setViewType(ViewType.WIREFRAME);
        Static.mainFrame.repaint();
    }//GEN-LAST:event_viewAsWireframeJMenuItemActionPerformed

    private void viewAsNormalJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAsNormalJMenuItemActionPerformed
        J3DSettings.setViewType(ViewType.NORMAL);
        Static.mainFrame.repaint();
    }//GEN-LAST:event_viewAsNormalJMenuItemActionPerformed

    private void openProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectMenuItemActionPerformed
        AreYouSure ays = new AreYouSure(Static.mainFrame, true,
                "Whatever is on screen currently will be discarded.");
        ays.setVisible(true);

        if (!ays.canProceed()) return;

        Static.sceneManager.resetScene();

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
        }, Static.mainFrame);
        if (file == null) return;
        String path = file.getAbsolutePath();
        Static.getLog().println(path);
        Path p = Paths.get(path);
        String fileName = p.getFileName().toString();
        String fileDir = p.getParent().toString();

//        J3DSettings.setProject(fileDir, fileName);
        Settings.projectOutputFile.setValue(file);

        LongTask<ArrayList<Interactable>> t = new LongTask<>(
                ta -> {
                    ArrayList<Interactable> a = null;
                    try {
                        a = new ProjectFile()
                                .readFile(fileDir, fileName, ta);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(a.size());
                    return a;
                },
                (tb, i) -> {
                    i.forEach(Interactable::invokeSwingHooks);
                }
        );

        t.run();
    }//GEN-LAST:event_openProjectMenuItemActionPerformed

    private void saveProjectJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectJMenuItemActionPerformed
        if (Settings.projectOutputFile.getValue() == null) {
            String fileName = JOptionPane.showInputDialog("Project name?");
            fileName = (
                    fileName == null || fileName.trim().isEmpty()
                            ? "project1"
                            : fileName
            ) + ".j3p";

            File folder = FilesUtility.folderChooser(Static.mainFrame);

            Static.getLog().println("Picked the location " + folder.getAbsolutePath() + " with the file name " + fileName);

//            J3DSettings.setProject(folder.getAbsolutePath(), fileName);
            Settings.projectOutputFile.setValue(new File(folder, fileName));
        }

        new ProjectFile().writeFile(
                Settings.projectOutputFile.getValue().getParent(),
                Settings.projectOutputFile.getValue().getName(), Static.sceneManager.layers);
    }//GEN-LAST:event_saveProjectJMenuItemActionPerformed

    private void newProjectJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectJMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newProjectJMenuItemActionPerformed

    private void exportAsPNGJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAsPNGJMenuItemActionPerformed
        File o = FilesUtility.fileChooser(
                jfc -> {
                    // allow folders or other PNG files to be chosen
                    jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getName().endsWith(".png") || !f.isFile();
                        }

                        @Override
                        public String getDescription() {
                            return "PNG File or otherwise output directory";
                        }
                    });
                },
                Static.mainFrame
        );

        if (o == null)
            return;
        File file = !o.isFile() ? new File(o, "export.png") : o;

        J3DPanel panel = (J3DPanel) mainPanel;

        try {
            panel.exportAs("png", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }//GEN-LAST:event_exportAsPNGJMenuItemActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        // Open PreferencesFrame JFrame on top of this frame at the centre of the screen.
        PreferencesFrame preferencesFrame = Static.settings.panel();
        CursorManager.set(CursorNames.DEFAULT, preferencesFrame);
        preferencesFrame.setLocationRelativeTo(Static.mainFrame);
        preferencesFrame.setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void logOutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutJMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_logOutJMenuItemActionPerformed

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
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Static.mainFrame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem deleteAccountJMenuItem;
    private javax.swing.JMenu editJMenu;
    private javax.swing.JMenuItem exportAsPNGJMenuItem;
    private javax.swing.JMenu exportJMenuItemDropDown;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    public javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem logOutJMenuItem;
    public static javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mouseJMenu;
    private javax.swing.JMenuItem newProjectJMenuItem;
    private javax.swing.JMenuItem openProjectMenuItem;
    private javax.swing.JMenuItem redoJMenuItem;
    private javax.swing.JMenuItem redrawJMenuItem;
    private javax.swing.JMenuItem resetCameraJMenuItem;
    private javax.swing.JMenu resetJMenuItemDropDown;
    private javax.swing.JMenuItem resetOrientationJMenuItem;
    private javax.swing.JMenuItem resetPositionJMenuItem;
    private javax.swing.JMenuItem saveProjectJMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem undoJMenuItem;
    private javax.swing.JMenuItem viewAsNormalJMenuItem;
    private javax.swing.JMenuItem viewAsWireframeJMenuItem;
    private javax.swing.JMenu viewJMenu;
    // End of variables declaration//GEN-END:variables
}
