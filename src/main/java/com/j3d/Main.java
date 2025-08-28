package com.j3d;

import com.j3d.engine.geometry.base.Dimension;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.base.CartesianPoint;
import com.j3d.engine.geometry.base.ScreenPoint;
import com.j3d.jaiva.Testing;
import com.jaiva.JBundler;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JPanel {
    public static Dimension scrSize = new Dimension(1800, 1000);
    public static JBundler jBundler = null;

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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Renderer renderer = new Renderer(g, new Dimension(getWidth(), getHeight()));
        Executor executor = new Executor(renderer);
        executor.run();
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
    }

}