package com.j3d;

import com.j3d.engine.interact.cmd.CommandPallete;
import com.j3d.engine.Logger;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.jaiva.Testing;
import com.jaiva.JBundler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main is main.
 */
public class Main extends JPanel {
    public static Logger log;
    public static Dimension scrSize = new Dimension(1800, 1000);
    public static JBundler jBundler = null;
    public static Renderer renderer = null;
    public static Executor executor = null;
    public static boolean run = true;
    public static Frame f = null;
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 20, -20))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 50));
    private static DebugPanel dp = new DebugPanel();
    private ScreenPoint mousePos = null;
    private ScreenPoint[] selectionArea = new ScreenPoint[2];
    private static CommandPallete commandPallete = new CommandPallete();
    public static CommandParser commandParser;

    public Main() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectionArea = new ScreenPoint[]{null, null}; // Reset selection area
                f.repaint();
                Main.Cursors.setDefault();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePos = new ScreenPoint(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePos = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                selectionArea[0] = mousePos;
                selectionArea[1] = new ScreenPoint(e.getX(), e.getY());
                f.repaint();
            }
        });
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
        log.println("Painted/Repainted Scene");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
//        renderer.draw();
        // Draw a dot at (100, 100)
//        initBundler(g, renderer);
    }

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
        JFrame frame = new JFrame("J3D");

        f = frame;
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        renderer = new Renderer(scrSize);
        executor = new Executor(renderer);
        dp.run(renderer, executor, f);
        JLayeredPane layeredPane = frame.getLayeredPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(scrSize.width, scrSize.height);
        frame.setResizable(false);

        Main mainPanel = new Main();
        mainPanel.setVisible(true);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mainPanel.setBounds(0, 0, scrSize.width, scrSize.height);
        mainPanel.setPreferredSize(new java.awt.Dimension(scrSize.width, scrSize.height));
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        dp.setBounds(20, 40, dp.getPreferredSize().width, dp.getPreferredSize().height); // small corner overlay
        dp.setOpaque(true);
        dp.setBackground(Color.WHITE);
        dp.setVisible(true);
        log = new Logger(dp.logTextArea); // initialize logger with the text area
        layeredPane.add(dp, JLayeredPane.PALETTE_LAYER);

        JButton toggleButton = new JButton("Toggle Debug");
        toggleButton.addActionListener(e -> dp.setVisible(!dp.isVisible()));
        toggleButton.setBounds(20, 5, toggleButton.getPreferredSize().width, toggleButton.getPreferredSize().height); // position it below dp
        layeredPane.add(toggleButton, JLayeredPane.PALETTE_LAYER);

//        commandPallete.validate(); // Ensures layout is calculated
        Rectangle bounds = frame.getBounds();
        java.awt.Dimension size = commandPallete.getPreferredSize();
        int x = ((bounds.width - size.width) / 2) - 60;
        int y = bounds.height - size.height - 200;
        commandPallete.setBounds(x, y, size.width, size.height);

        commandPallete.mainCommandsPanel.setOpaque(false);
        commandPallete.commandInfoPanel.setOpaque(false);
        commandPallete.setOpaque(true);
        commandPallete.setBackground(new Color(30, 30, 30, 8));
        commandPallete.setVisible(true);
        layeredPane.add(commandPallete, JLayeredPane.POPUP_LAYER);

        commandParser = new CommandParser(commandPallete);

        mainPanel.getRootPane().setFocusable(true);
        mainPanel.getRootPane().requestFocusInWindow();

        mainPanel.getRootPane().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!commandPallete.inputField.isFocusOwner()) {
                    commandPallete.inputField.requestFocus();
                    commandPallete.inputField.setText(String.valueOf(e.getKeyChar()));
                }
            }
        });

//        frame.add(new Main());
        frame.setVisible(true);

        Main.Cursors.init(frame);
        Main.Cursors.setDefault();
    }
    public static class Cursors {
        private static final Map<String, Cursor> cursors = new HashMap<>();
        private static Component defaultTarget;

        public static void init(Component defaultComponent) {
            defaultTarget = defaultComponent;
            loadCursors();
        }

        private static void loadCursors() {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            cursors.put("default", createScaledCursor("/cursors/default.png", "default"));
//            cursors.put("hand", Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cursors.put("selectSoft", createScaledCursor("/cursors/selectSoft.png", "selectSoft"));
            cursors.put("selectStrict", createScaledCursor("/cursors/selectStrict.png", "selectStrict"));
            cursors.put("selectInvert", createScaledCursor("/cursors/selectInvert.png", "selectInvert"));
        }

        private static Cursor createScaledCursor(String path, String name) {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource(path)));
            Image image = icon.getImage();
            Image scaled = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            return Toolkit.getDefaultToolkit().createCustomCursor(scaled, new Point(0, 0), name);
        }

        public static void set(String name) {
            set(name, defaultTarget);
        }

        public static void set(String name, Component target) {
            Cursor cursor = cursors.getOrDefault(name, Cursor.getDefaultCursor());
            if (target != null) target.setCursor(cursor);
        }

        public static void setDefault() {
            set("default");
        }
    }
}