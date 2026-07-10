package com.j3d;

import com.j3d.engine.Logger;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo3d.Camera;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.commands.orbit.OrbitCmd;
import com.j3d.engine.interact.input.keyboard.GlobalKeybinds;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.engine.layer.Layer;
import com.j3d.gen.docs.HelpGenerator;
import com.j3d.gen.settings.Settings;
import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.storage.files.engine.EngineFiles;
import com.j3d.storage.files.engine.LogFile;
import com.j3d.storage.files.protocol.proj.PF1;
import com.j3d.storage.files.protocol.proj.PF2;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.ui.engine.FloatingPanel;
import com.j3d.ui.engine.J3DPanel;
import com.j3d.ui.engine.popups.DebugPanel;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.engine.popups.tree.LayerTree;
import com.j3d.ui.HoverJLabel;
import com.j3d.ui.engine.properties.PropertiesPanel;
import com.j3d.ui.engine.tb.Toolbox;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Static is a class which holds static references to important instances in the engine, such as the SceneManager, Executor, Main Frame, Camera, etc.
 * <p>
 *     This is used to allow for easy access to these instances from anywhere in the code, without having to pass them around
 * </p>
 * <p>
 *     No app should have static references like this. but this is MY app. (and this app doesn't allow opening
 *     multiple windows anyway so having these as static references is a little more convenient than
 *     passing it everywhere in the world.)
 * </p>
 * @author Lehlogonolo Poole
 */
public class Static {
    private static PF1 projectFileV1 = new PF1();
    public static PF1 getProjectFileV1() {
        if (projectFileV1 == null) {
            projectFileV1 = new PF1();
        }
        return projectFileV1;
    }

    private static PF2 projectFileV2 = new PF2();
    public static PF2 getProjectFileV2() {
        if (projectFileV2 == null) {
            projectFileV2 = new PF2();
        }
        return projectFileV2;
    }

    /**
     * The SceneManager Instance. A very important class initialized by {@link EngineFrame}
     * who's job it is to store all information and references to the scene.
     */
    public static SceneManager sceneManager = null;
    /**
     * The Executor Instance. A once off scene initialiser which populates the scene with some test
     * triangles and other stuff. Only via {@link EngineFrame#EngineFrame(boolean)} (set to {@code true}).
     * This is also initialised by {@link EngineFrame} but then only called once and never again.
     */
    public static Executor executor = null;
    /**
     * The Main Frame that is displayed. {@link EngineFrame}
     * @implSpec
     *  {@link Static#mainPanel}.repaint() instead of mainFrame.repaint() for better performance.
     */
    public static EngineFrame mainFrame = null;
    /**
     * The Camera Instance. The main camera used to view the scene, which is immediately
     * initialised statically and can be changed by say {@link GlobalKeybinds#MOVE_CAM_FORWARD}
     *  (moving the camera) or {@link OrbitCmd} (changing the camera's rotation).
     *  <p>
     *      Initialised to position {@code (20, 50, -90)} and with the projection plane at (0, 0)
     *      with a focal length of {@code 37.0} (Defined by {@link CameraProperties#focalLength})
     *  </p>
     */
    public static Camera camera = new Camera()
            .setPosition(new Vector3(20, 50, -90))
            .setProjectionPlane(
                    new Vector3(0, 0, Settings.cameraProperties.focalLength.getValue())
            );
    /**
     * The Debug Panel. The panel that holds debug stuff. Initialized by Static
     * and can be accessed via it's {@link FloatingPanel} by the user within the {@link Toolbox}.
     */
    private static DebugPanel debugPanel;
    public static DebugPanel getDebugPanel() {
        if (debugPanel == null)
            debugPanel = new DebugPanel();

        return debugPanel;
    }
    private static PropertiesPanel propertiesPanel;
    public static PropertiesPanel getPropertiesPanel() {
        if (propertiesPanel == null)
            propertiesPanel = new PropertiesPanel();

        return propertiesPanel;
    }
    /**
     * The Command Parser Instance. Initialised by {@link EngineFrame} and is mainly a parsing
     * utility and manager for the {@link CommandPalette}. The engine that parses commands.
     * Initialised and owned by {@link EngineFrame}
     */
    public static CommandParser commandParser;
    /**
     * The layer tree instance which holds the tree view of {@link Layer} and {@link Thing}s.
     * The user can access this panel via it's wrapped {@link FloatingPanel} version within
     * the {@link Toolbox}. Initialised by Static
     */
    private static LayerTree layerTree;
    public static LayerTree getLayerTree() {
        if (layerTree == null)
            layerTree = new LayerTree();

        return layerTree;
    }
    /**
     * The Main Draw Panel. This is where all geometry is calculated and drawn onto.
     * Initialised by {@link EngineFrame}
     */
    public static J3DPanel mainPanel;
    /**
     * The Key Binds Manager, where all keybinds (Not {@link JMenu} accelerators) are stored.
     * Used in conjunction with {@link J3Key}. Initialised by {@link EngineFrame}
     * @see J3Key
     */
    public static KeyBindings keybinds;
    /**
     * The Logger Instance, which simply encapsulates the logging to the {@link DebugPanel#logTextArea}
     * and to the output file defined by  {@link LogFile} within {@link EngineFiles}.
     * Lazily initialised by {@link EngineFrame}
     */
    private static Logger log;

    /**
     * Initialises the logger instance.
     * @return The logger instance, otherwise sets it if null.
     */
    public static Logger getLog() {
        if (log == null)
            log = new Logger();
        return log;
    }
    /**
     * The Settings Instance. Usually all code accesses settings within {@link Settings}
     * statically. This instance is only really used to access it's UI. Initialised by Static
     */
    public static Settings settings = new Settings();
    /**
     * New help generator. No docs yet. lmao. Initialised by Static
     */
    public static HelpGenerator help = new HelpGenerator();
    /**
     * The commands manager which just holds the current running {@link SemiStatefulCommand}
     * and gets all the commands otherwise. Initialised by Static
     */
    public static CommandsManager commandManager = new CommandsManager();
    /**
     * The hover label. Literally just a hovering {@link JLabel} below the cursor.
     * Initialised by {@link EngineFrame}
     */
    public static HoverJLabel hoverLabel;
    /**
     * The Engine Files instances. This encapsulates all the files stored in
     * {@code user.dir/J3Engine}. Lazily initialised by Static.
     */
    private static EngineFiles engineFiles;

    /**
     * Initialises the engine files instance.
     * @return The logger instance, otherwise sets it if null.
     */
    public static EngineFiles getEngineFiles() {
        if (engineFiles == null)
            engineFiles = new EngineFiles();
        return engineFiles;
    }

    /**
     * Returns an {@link Image} of the J3Engine 1:1 logo for the app icon.
     * @return an Image
     */
    public static Image logo() {
        URL imgURL = Static.class.getResource("/art/logo/J3Dicon.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Couldn't find file.");
            return null;
        }
    }

    /**
     * Random ass method so static is forced to initialise everything in {@link Main}.
     * Does absolutely shit.
     */
    public static void none() {
    }
}
