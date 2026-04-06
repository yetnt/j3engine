package com.j3d;

import com.j3d.engine.Logger;
import com.j3d.engine.Renderer;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.settings.Settings;
import com.j3d.ui.engine.DebugPanel;
import com.j3d.ui.engine.tree.LayerTree;

import javax.swing.*;

/**
 * Static is a class which holds static references to important instances in the engine, such as the Renderer, Executor, Main Frame, Camera, etc.
 * <p>
 *     This is used to allow for easy access to these instances from anywhere in the code, without having to pass them around
 * </p>
 */
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
     * Rather call {@link Static#mainPanel}.repaint() instead of mainFrame.repaint() for better performance.
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
     * The Key Bindings Manager
     */
    public static KeyBindings keybinds;
    /**
     * The Logger Instance
     */
    public static Logger log;
    /**
     * The Settings Object
     */
    public static Settings settings = new Settings();
}
