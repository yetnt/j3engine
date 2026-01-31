/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.engine;

import com.j3d.ui.engine.tree.LayerTree;
import com.j3d.Executor;
import com.j3d.J3DSettings;
import com.j3d.engine.interact.input.KeyBindings;
import com.j3d.engine.Logger;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandPallete;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.engine.interact.input.mouse.NoMouseOwner;
import com.j3d.engine.interact.selection.SelectionManager;
//import com.j3d.jaiva.Testing;
import com.j3d.ui.Cursors;
import com.j3d.ui.tb.Toolbox;
//import com.jaiva.JBundler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.*;

import static com.j3d.J3DSettings.jMenuBarOffsetY;

/**
 *
 * @author ACER
 */
public class EngineFrame extends javax.swing.JFrame {
//    public static JBundler jBundler = null;
    public static Renderer renderer = null;
    public static Executor executor = null;
    public static boolean run = true;
    public static JFrame f = null;
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 20, -20))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 50));
    public static DebugPanel dp = new DebugPanel();
    private static MOwner mouseOwner = MOwner.SELECTION;
    public static ScreenPoint mousePos = null;
    public static ScreenPoint[] selectionArea = new ScreenPoint[2];
    private static final CommandPallete commandPallete = new CommandPallete();
    public static CommandParser commandParser;
    public static LayerTree list = new LayerTree();

    public static void setMouseOwner(MOwner owner) {
        mouseOwner = owner;
    }
    public static MOwner getMouseOwner() {
        return mouseOwner;
    }
    
    public void complete() {
        f = this;
        final int menuBarOffsetY = (f.getJMenuBar().getSize().height + jMenuBarOffsetY);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        renderer = new Renderer(J3DSettings.screenSize);
        executor = new Executor(renderer);
        dp.run(renderer, executor, f);
        JLayeredPane layeredPane = f.getLayeredPane();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        f.setResizable(false);

        mainPanel.setFocusable(true);

        InputMap im = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainPanel.getActionMap();
        new KeyBindings(im, am, commandPallete);

        Toolbox toolbox = new Toolbox();
        // Toolbox at the top and extends full width but not very tall
        toolbox.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width - 260, toolbox.getPreferredSize().height);
        layeredPane.add(toolbox, JLayeredPane.MODAL_LAYER); // above default layer

        mainPanel.requestFocusInWindow();
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mainPanel.setBounds(0, menuBarOffsetY, J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        mainPanel.setPreferredSize(new Dimension(J3DSettings.screenSize.width, J3DSettings.screenSize.height));

        list.setBounds(
                J3DSettings.screenSize.width - 260 -(list.getPreferredSize().width),
                toolbox.getPreferredSize().height + menuBarOffsetY,
                list.getPreferredSize().width,
                list.getPreferredSize().height);
        layeredPane.add(list, JLayeredPane.POPUP_LAYER);
        list.setVisible(true);

        dp.setBounds(20, toolbox.getPreferredSize().height + menuBarOffsetY, dp.getPreferredSize().width, dp.getPreferredSize().height); // small corner overlay
        dp.setOpaque(true);
        dp.setBackground(Color.WHITE);
        dp.setVisible(false);
        J3DSettings.log = new Logger(dp.logTextArea); // initialize logger with the text area
        layeredPane.add(dp, JLayeredPane.PALETTE_LAYER);
        
        Rectangle bounds = f.getBounds();
        Dimension size = commandPallete.getPreferredSize();
        int x = ((bounds.width - size.width) / 2) - 60;
        int y = bounds.height - size.height - 200;
        commandPallete.setBounds(x, y, size.width, size.height);

        commandPallete.setOpaque(true);
        commandPallete.setBackground(new Color(30, 30, 30, 8));
        commandPallete.setVisible(true);
        layeredPane.add(commandPallete, JLayeredPane.POPUP_LAYER);

        commandParser = new CommandParser(commandPallete);

        mainPanel.getRootPane().setFocusable(true);
        mainPanel.getRootPane().requestFocusInWindow();

//        frame.add(new EngineFrame());
//        f.setVisible(true);

        Cursors.init(f);
        Cursors.setDefault();
    }

    /**
     * Creates new form Main2
     */
    public EngineFrame() {
        ArrayList<MouseOwner> owners = new ArrayList<>();
        owners.add(SelectionManager.selectionMouseOwner);
        owners.add(new NoMouseOwner());

        owners.forEach(this::addMouseListener);
        owners.forEach(this::addMouseMotionListener);
        initComponents();
        complete();
    }

//    /**
//     * Initializes (if not already initialized) the Jaiva Instance by inputting the input file and passing {@link Testing} class
//     * @param g The graphics
//     * @param r The Renderer Instance.
//     */
//    private void initBundler(Graphics g, Renderer r) {
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
     * This should only be called from non-EDT threads. e.g. from the Renderer thread.
     */
    public static void repaintL() {
        SwingUtilities.invokeLater(() -> {
            if (dp != null) {
                dp.revalidate();
                dp.repaint();
            }
            commandPallete.revalidate();
            commandPallete.repaint();
            if (f != null) {
                f.revalidate();
                f.repaint();
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
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        editJMenu = new javax.swing.JMenu();
        undoJMenuItem = new javax.swing.JMenuItem();
        redoJMenuItem = new javax.swing.JMenuItem();
        mouseJMenu = new javax.swing.JMenu();
        viewJMenu = new javax.swing.JMenu();
        resetPositionJMenuItem = new javax.swing.JMenuItem();
        resetOrientationJMenuItem = new javax.swing.JMenuItem();
        resetCameraJMenuItem = new javax.swing.JMenuItem();
        redrawJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("J3D");
        setMinimumSize(new java.awt.Dimension(1800, 1000));

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

        jMenuItem1.setText("jMenuItem1");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("jMenuItem2");
        jMenu1.add(jMenuItem2);

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

        resetPositionJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetPositionJMenuItem.setText("Reset Position");
        resetPositionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPositionJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(resetPositionJMenuItem);

        resetOrientationJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetOrientationJMenuItem.setText("Reset Orientation");
        resetOrientationJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetOrientationJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(resetOrientationJMenuItem);

        resetCameraJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetCameraJMenuItem.setText("Reset Camera");
        resetCameraJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCameraJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(resetCameraJMenuItem);

        redrawJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        redrawJMenuItem.setText("Redraw");
        redrawJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redrawJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(redrawJMenuItem);

        jMenuBar1.add(viewJMenu);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1503, 817));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void resetPositionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPositionJMenuItemActionPerformed
        camera.setPosition(new Vector3(0, 0, 0));
        f.repaint();
    }//GEN-LAST:event_resetPositionJMenuItemActionPerformed

    private void undoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoJMenuItemActionPerformed
        Renderer.history.undo();
        f.repaint();
    }//GEN-LAST:event_undoJMenuItemActionPerformed

    private void redoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoJMenuItemActionPerformed
        Renderer.history.redo();
        f.repaint();
    }//GEN-LAST:event_redoJMenuItemActionPerformed

    private void resetOrientationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetOrientationJMenuItemActionPerformed
        camera.setRotation(new Rotation(0, 0, 0));
        f.repaint();
    }//GEN-LAST:event_resetOrientationJMenuItemActionPerformed

    private void resetCameraJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCameraJMenuItemActionPerformed
        camera.setPosition(new Vector3(0, 0, 0));
        camera.setRotation(new Rotation(0, 0, 0));
        f.repaint();
    }//GEN-LAST:event_resetCameraJMenuItemActionPerformed

    private void redrawJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redrawJMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_redrawJMenuItemActionPerformed

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
//        f = new EngineFrame();
//        final int menuBarOffsetY = (f.getJMenuBar().getSize().height + jMenuBarOffsetY);
//        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        renderer = new Renderer(J3DSettings.screenSize);
//        executor = new Executor(renderer);
//        dp.run(renderer, executor, f);
//        JLayeredPane layeredPane = f.getLayeredPane();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setSize(J3DSettings.screenSize.width, J3DSettings.screenSize.height);
//        f.setResizable(false);
//
//        mainPanel.setFocusable(true);
//
//        InputMap im = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//        ActionMap am = mainPanel.getActionMap();
//        new KeyBindings(im, am, commandPallete);
//
//        Toolbox toolbox = new Toolbox();
//        // Toolbox at the top and extends full width but not very tall
//        toolbox.setBounds(0, 0 + menuBarOffsetY, J3DSettings.screenSize.width - 50, toolbox.getPreferredSize().height);
//
//        // add menubar to layered pane
//        JMenuBar mb = f.getJMenuBar();
//        if (mb != null) {
//            layeredPane.add(mb, JLayeredPane.POPUP_LAYER);
//        }
//        layeredPane.add(toolbox, JLayeredPane.MODAL_LAYER); // above default layer
//
//        mainPanel.requestFocusInWindow();
//        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
//        mainPanel.setBounds(0, 0 + menuBarOffsetY, J3DSettings.screenSize.width, J3DSettings.screenSize.height);
//        mainPanel.setPreferredSize(new Dimension(J3DSettings.screenSize.width, J3DSettings.screenSize.height));
////        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
//
//        dp.setBounds(20, toolbox.getPreferredSize().height + menuBarOffsetY, dp.getPreferredSize().width, dp.getPreferredSize().height); // small corner overlay
//        dp.setOpaque(true);
//        dp.setBackground(Color.WHITE);
//        dp.setVisible(false);
//        J3DSettings.log = new Logger(dp.logTextArea); // initialize logger with the text area
//        layeredPane.add(dp, JLayeredPane.PALETTE_LAYER);
//
//        Rectangle bounds = f.getBounds();
//        Dimension size = commandPallete.getPreferredSize();
//        int x = ((bounds.width - size.width) / 2) - 60;
//        int y = bounds.height - size.height - 200;
//        commandPallete.setBounds(x, y, size.width, size.height);
//
//        commandPallete.setOpaque(true);
//        commandPallete.setBackground(new Color(30, 30, 30, 8));
//        commandPallete.setVisible(true);
//        layeredPane.add(commandPallete, JLayeredPane.POPUP_LAYER);
//
//        commandParser = new CommandParser(commandPallete);
//
//        mainPanel.getRootPane().setFocusable(true);
//        mainPanel.getRootPane().requestFocusInWindow();
//
////        frame.add(new EngineFrame());
////        f.setVisible(true);
//
//        Cursors.init(f);
//        Cursors.setDefault();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                f.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editJMenu;
    private javax.swing.JMenu jMenu1;
    public javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    public static javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mouseJMenu;
    private javax.swing.JMenuItem redoJMenuItem;
    private javax.swing.JMenuItem redrawJMenuItem;
    private javax.swing.JMenuItem resetCameraJMenuItem;
    private javax.swing.JMenuItem resetOrientationJMenuItem;
    private javax.swing.JMenuItem resetPositionJMenuItem;
    private javax.swing.JMenuItem undoJMenuItem;
    private javax.swing.JMenu viewJMenu;
    // End of variables declaration//GEN-END:variables
}
