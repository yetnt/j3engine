package com.j3d;

import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Rotation;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.KeyBindings;
import com.j3d.ui.engine.DebugPanel;
import com.j3d.ui.engine.tree.LayerTree;

import javax.swing.*;

public class Static {
    //    public static JBundler jBundler = null;
    /**
     * The Renderer Instance.
     */
    public static Renderer renderer = null;
    /**
     * The Executor Instance.
     */
    public static Executor executor = null;
    /**
     * The Main Frame that is displayed.
     */
    public static JFrame mainFrame = null;
    /**
     * The Camera Instance.
     */
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 20, -20))
            .setRotation(new Rotation(0, 0, 0))
            .setProjectionPlane(new Vector3(0, 0, 50));
    /**
     * The Debug Panel
     */
    public static DebugPanel debugPanel = new DebugPanel();
    /**
     * The Command Parser Instance
     */
    public static CommandParser commandParser;
    /**
     * The Tree of Layers and Things Panel
     */
    public static LayerTree layerTree = new LayerTree();
    /**
     * The Main Draw Panel
     */
    public static JPanel mainPanel;
    /**
     * Keybinds
     */
    public static KeyBindings keybinds;
}
