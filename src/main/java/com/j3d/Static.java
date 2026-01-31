package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.ui.engine.DebugPanel;
import com.j3d.ui.engine.tree.LayerTree;

import javax.swing.*;

public class Static {
    //    public static JBundler jBundler = null;
    public static Renderer renderer = null;
    public static Executor executor = null;
    public static JFrame mainFrame = null;
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 20, -20))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 50));
    public static DebugPanel debugPanel = new DebugPanel();
    public static CommandParser commandParser;
    public static LayerTree layerTree = new LayerTree();
    public static JPanel mainPanel;
}
