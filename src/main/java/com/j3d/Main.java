package com.j3d;

import com.j3d.engine.Logger;
import com.j3d.engine.geometry.Dimension;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.SelectionQuery;
import com.j3d.engine.interact.SelectionType;
import com.j3d.jaiva.Testing;
import com.jaiva.JBundler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

    public Main() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectionArea = new ScreenPoint[]{null, null}; // Reset selection area
                f.repaint();
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
            g.setColor(new Color(255, 1, 1, 92));
            ScreenPoint i = selectionArea[0];
            ScreenPoint ii = selectionArea[1];
            g.fillRect(Math.min(i.x, ii.x), Math.min(i.y, ii.y), Math.abs(i.x - ii.x), Math.abs(i.y - ii.y));
            SelectionQuery selectionBounds = new SelectionQuery(
                    i, ii,
                    SelectionType.BOUNDS_STRICT
            );
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dot Drawer");

        f = frame;
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
//        frame.add(new Main());
        frame.setVisible(true);

    }

}