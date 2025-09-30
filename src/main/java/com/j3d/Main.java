package com.j3d;

import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo2d.Dimension;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo2d.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
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
    public static Dimension scrSize = new Dimension(1800, 1000);
    public static JBundler jBundler = null;
    public static Renderer renderer = null;
    public static Executor executor = null;
    public static boolean run = true;
    public static Frame f = null;
    public static Camera camera = new Camera()
            .setPosition(new Vector3(10, 10, 10))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 1));

    private GPoint currentlyDragging = null;
    private static final double SNAP_RADIUS = 2.0;

    public Main() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                // You can trigger a repaint or other logic here
            }

            @Override
            public void mousePressed(MouseEvent e) {
                CartesianPoint mousePos = new ScreenPoint(e.getX(), e.getY()).toPoint(renderer);
                currentlyDragging = renderer.findPointNearCursor(mousePos, SNAP_RADIUS); // 2 unit snap radius
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentlyDragging = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentlyDragging != null) {
                    Vector3 newPos = new Vector3(e.getX(), e.getY(), 0);
                    renderer.movePointTo(currentlyDragging, newPos);
                    f.repaint();
                }
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
        if (!run) {
//            renderer.axis((Graphics2D) g, camera);
            double axisLength = camera.getPosition().magnitude() * 0.9;
            Vector3 origin = new Vector3(0, 0, 0);
            Vector3 offset = new Vector3(0.01, 0.01, 0.01); // avoids collapse

            g.setColor(Color.RED);
            renderer.drawLine3D((Graphics2D) g, origin.add(offset), origin.add(new Vector3(axisLength, 0, 0)), camera);

            g.setColor(Color.GREEN);
            renderer.drawLine3D((Graphics2D) g, origin.add(offset), origin.add(new Vector3(0, axisLength, 0)), camera);

            g.setColor(Color.BLUE);
            renderer.drawLine3D((Graphics2D) g, origin.add(offset), origin.add(new Vector3(0, 0, axisLength)), camera);

            g.setColor(Color.BLACK);
            renderer.draw((Graphics2D) g, camera);
            return;
        }
        Renderer renderer1 = new Renderer(new Dimension(getWidth(), getHeight()));
        renderer = renderer1;
        executor = new Executor(renderer1);
        NewJFrame.run(renderer, executor, f);
        executor.run((Graphics2D) g);
        run = false;
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(scrSize.width, scrSize.height);
        frame.setResizable(false);
        frame.add(new Main());
        frame.setVisible(true);
        f = frame;
    }

}