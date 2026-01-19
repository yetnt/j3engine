package com.j3d;

import com.j3d.engine.interact.cmd.CommandPallete;
import com.j3d.engine.Logger;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.mouse.MOwner;
import com.j3d.engine.interact.input.mouse.MouseOwner;
import com.j3d.engine.interact.input.mouse.NoMouseOwner;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.jaiva.Testing;
import com.j3d.ui.Cursors;
import com.j3d.ui.tb.Toolbox;
import com.jaiva.JBundler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Main is main.
 */
public class Main extends J3DPanel {
    public static JBundler jBundler = null;
    public static Renderer renderer = null;
    public static Executor executor = null;
    public static boolean run = true;
    public static Frame f = null;
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 20, -20))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 50));
    public static DebugPanel dp = new DebugPanel();
    private static MOwner mouseOwner = MOwner.SELECTION;
    public static ScreenPoint mousePos = null;
    public static ScreenPoint[] selectionArea = new ScreenPoint[2];
    private static CommandPallete commandPallete = new CommandPallete();
    public static CommandParser commandParser;

    public static void setMouseOwner(MOwner owner) {
        mouseOwner = owner;
    }
    public static MOwner getMouseOwner() {
        return mouseOwner;
    }

    public Main() {
        ArrayList<MouseOwner> owners = new ArrayList<>();
        owners.add(SelectionManager.selectionMouseOwner);
        owners.add(new NoMouseOwner());

        owners.forEach(this::addMouseListener);
        owners.forEach(this::addMouseMotionListener);

    }

    /**
     * Initializes (if not already initialized) the Jaiva Instance by inputting the input file and passing {@link Testing} class
     * @param g The graphics
     * @param r The Renderer Instance.
     */
    private void initBundler(Graphics g, Renderer r) {
        if (jBundler == null) {
            try {
                jBundler = new JBundler("C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\src\\main\\resources\\file.jiv", Testing.class);
                jBundler.run(r);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (run) {
//            renderer.axis((Graphics2D) g, camera);
            executor.run((Graphics2D) g);
            run = false;
        }
        renderer.draw((Graphics2D) g, camera);
        // draw selection area ontop of all render things.
        if (selectionArea[0] != null && selectionArea[1] != null) {
            SelectionUI.run((Graphics2D)g, selectionArea, renderer);
        }
//        log.println("Painted/Repainted Scene");
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
////        renderer.draw();
//        // Draw a dot at (100, 100)
////        initBundler(g, renderer);
    }

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
            if (commandPallete != null) {
                commandPallete.revalidate();
                commandPallete.repaint();
            }
            if (f != null) {
                f.revalidate();
                f.repaint();
            }
        });
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        JFrame frame = new JFrame("J3D");

        f = frame;
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        renderer = new Renderer(J3DSettings.screenSize);
        executor = new Executor(renderer);
        dp.run(renderer, executor, f);
        JLayeredPane layeredPane = frame.getLayeredPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        frame.setResizable(false);

        Main mainPanel = new Main();
        mainPanel.setVisible(true);
        mainPanel.setFocusable(true);

        InputMap im = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainPanel.getActionMap();
        new KeyBindings(im, am, commandPallete);

        Toolbox toolbox = new Toolbox();
        // Toolbox at the top and extends full width but not very tall
        toolbox.setBounds(0, 0, J3DSettings.screenSize.width - 50, toolbox.getPreferredSize().height);
        layeredPane.add(toolbox, JLayeredPane.MODAL_LAYER); // above default layer

        mainPanel.requestFocusInWindow();
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mainPanel.setBounds(0, 0, J3DSettings.screenSize.width, J3DSettings.screenSize.height);
        mainPanel.setPreferredSize(new Dimension(J3DSettings.screenSize.width, J3DSettings.screenSize.height));
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        dp.setBounds(20, toolbox.getPreferredSize().height + 40, dp.getPreferredSize().width, dp.getPreferredSize().height); // small corner overlay
        dp.setOpaque(true);
        dp.setBackground(Color.WHITE);
        dp.setVisible(false);
        J3DSettings.log = new Logger(dp.logTextArea); // initialize logger with the text area
        layeredPane.add(dp, JLayeredPane.PALETTE_LAYER);

//        JButton toggleButton = new JButton("Toggle Debug");
//        toggleButton.addActionListener(e -> dp.setVisible(!dp.isVisible()));
//        toggleButton.setBounds(20, 5, toggleButton.getPreferredSize().width, toggleButton.getPreferredSize().height); // position it below dp
//        layeredPane.add(toggleButton, JLayeredPane.PALETTE_LAYER);

//        commandPallete.validate(); // Ensures layout is calculated
        Rectangle bounds = frame.getBounds();
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

        mainPanel.getRootPane().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
//                if (!commandPallete.inputField.isFocusOwner()) {
//                    commandPallete.inputField.requestFocus();
//                    commandPallete.inputField.setText(String.valueOf(e.getKeyChar()));
//                }
                if (commandPallete.inputField.isFocusOwner() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    mainPanel.requestFocus();
                }
            }
        });

//        frame.add(new Main());
        frame.setVisible(true);

        Cursors.init(frame);
        Cursors.setDefault();
    }

}