package com.j3d;

import com.j3d.engine.Logger;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.settings.Settings;
import com.j3d.storage.files.engine.EngineFiles;
import com.j3d.ui.engine.DebugPanel;
import com.j3d.ui.engine.tree.LayerTree;
import com.j3d.ui.util.HoverJLabel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Static is a class which holds static references to important instances in the engine, such as the SceneManager, Executor, Main Frame, Camera, etc.
 * <p>
 *     This is used to allow for easy access to these instances from anywhere in the code, without having to pass them around
 * </p>
 */
public class Static {
    /**
     * The SceneManager Instance.
     */
    public static SceneManager sceneManager = null;
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
            .setPosition(new Vector3(20, 50, -90))
            .setProjectionPlane(new Vector3(0, 0, Settings.cameraProperties.focalLength.getValue()));
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
    private static Logger log;
    public static Logger getLog() {
        if (log == null)
            log = new Logger();
        return log;
    }
    /**
     * The Settings Object
     */
    public static Settings settings = new Settings();
    public static CommandsManager commandManager = new CommandsManager();
    public static HoverJLabel hoverLabel;
    private static EngineFiles engineFiles;
    public static EngineFiles getEngineFiles() {
        if (engineFiles == null)
            engineFiles = new EngineFiles();
        return engineFiles;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static Image logo() {
        URL imgURL = Static.class.getResource("/art/logo/J3Dicon.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Couldn't find file.");
            return null;
        }
    }

    public static void none() {
    }
}
