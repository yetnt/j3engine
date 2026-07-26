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
import com.j3d.errors.ErrorHandler;
import com.j3d.gen.docs.DocsProvider;
import com.j3d.gen.settings.Settings;
import com.j3d.gen.settings.classes.CameraProperties;
import com.j3d.storage.files.engine.EngineFiles;
import com.j3d.storage.files.engine.LogFile;
import com.j3d.storage.files.protocol.proj.PF1;
import com.j3d.storage.files.protocol.proj.PF2;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.ui.engine.FloatingPanel;
import com.j3d.ui.engine.J3DPanel;
import com.j3d.ui.engine.floating.DebugPanel;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.engine.floating.grid2d.Grid2DPanel;
import com.j3d.ui.engine.floating.tree.LayerTree;
import com.j3d.ui.HoverJLabel;
import com.j3d.ui.engine.floating.properties.PropertiesPanel;
import com.j3d.ui.engine.toolbox.Toolbox;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * StaticRefs is a class which holds static references to important instances in the engine, such as the SceneManager, Executor, Main Frame, Camera, etc.
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
public class StaticRefs {
    // References below are all initialised by EngineFrame and have a setter method that only EngineFrame should usz
    /**
     * The SceneManager Instance. A very important class initialized by {@link EngineFrame}
     * who's job it is to store all information and references to the scene.
     */
    private static SceneManager sceneManager = null;
    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerSceneManager(SceneManager instance) {
        sceneManager = instance;
    }
    /**
     * The Executor Instance. A once off scene initialiser which populates the scene with some test
     * triangles and other stuff. Only via {@link EngineFrame#EngineFrame(boolean)} (set to {@code true}).
     * This is also initialised by {@link EngineFrame} but then only called once and never again.
     */
    private static Executor executor = null;
    public static Executor getExecutor() {
        return executor;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerExecutor(Executor instance) {
        executor = instance;
    }

    /**
     * The Main Frame that is displayed. {@link EngineFrame}
     * @implSpec
     *  {@link StaticRefs#mainPanel}.repaint() instead of mainFrame.repaint() for better performance.
     */
    private static EngineFrame mainFrame = null;
    public static EngineFrame getMainFrame() {
        return mainFrame;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerMainFrame(EngineFrame instance) {
        mainFrame = instance;
    }

    /**
     * The Command Parser Instance. Initialised by {@link EngineFrame} and is mainly a parsing
     * utility and manager for the {@link CommandPalette}. The engine that parses commands.
     * Initialised and owned by {@link EngineFrame}
     */
    private static CommandParser commandParser;
    public static CommandParser getCommandParser() {
        return commandParser;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerCommandParser(CommandParser instance) {
        commandParser = instance;
    }

    /**
     * The Main Draw Panel. This is where all geometry is calculated and drawn onto.
     * Initialised by {@link EngineFrame}
     */
    private static J3DPanel mainPanel;
    public static J3DPanel getMainPanel() {
        return mainPanel;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerMainPanel(J3DPanel instance) {
        mainPanel = instance;
    }
    /**
     * The Key Binds Manager, where all keybinds (Not {@link JMenu} accelerators) are stored.
     * Used in conjunction with {@link J3Key}. Initialised by {@link EngineFrame}
     * @see J3Key
     */
    private static KeyBindings globalKeybinds;
    public static KeyBindings getGlobalKeybinds() {
        return globalKeybinds;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerGlobalKeybinds(KeyBindings instance) {
        globalKeybinds = instance;
    }

    /**
     * The hover label. Literally just a hovering {@link JLabel} below the cursor.
     * Initialised by {@link EngineFrame}
     */
    private static HoverJLabel hoverLabel;
    public static HoverJLabel getHoverLabel() {
        return hoverLabel;
    }
    /**
     * @implSpec This should only be called by {@link EngineFrame}
     * @param instance The instance.
     */
    public static void registerHoverLabel(HoverJLabel instance) {
        hoverLabel = instance;
    }

    // Below are all references who are either lazily loaded or just final in general

    /**
     * The Camera Instance. The main camera used to view the scene, which is immediately
     * initialised statically and can be changed by say {@link GlobalKeybinds#MOVE_CAM_FORWARD}
     *  (moving the camera) or {@link OrbitCmd} (changing the camera's rotation).
     *  <p>
     *      Initialised to position {@code (20, 50, -90)} and with the projection plane at (0, 0)
     *      with a focal length of {@code 37.0} (Defined by {@link CameraProperties#focalLength})
     *  </p>
     */
    private static final Camera camera = new Camera()
            .setPosition(new Vector3(20, 50, -90))
            .setProjectionPlane(
                    new Vector3(0, 0, Settings.cameraProperties.focalLength.getValue())
            );
    public static Camera getCamera() {
        return camera;
    }

    /**
     * The Debug Panel. The panel that holds debug stuff. Initialized by StaticRefs
     * and can be accessed via it's {@link FloatingPanel} by the user within the {@link Toolbox}.
     */
    private static DebugPanel debugPanel;
    public static DebugPanel getDebugPanel() {
        if (debugPanel == null)
            debugPanel = new DebugPanel();

        return debugPanel;
    }

    /**
     * The Properties Panel. This panel displays and allows editing of properties for selected
     * objects. Initialised by StaticRefs and can be accessed via it's {@link FloatingPanel} by the
     * user within the {@link Toolbox}.
     */
    private static PropertiesPanel propertiesPanel;
    public static PropertiesPanel getPropertiesPanel() {
        if (propertiesPanel == null)
            propertiesPanel = new PropertiesPanel();

        return propertiesPanel;
    }

    /**
     * The Project File V1 instance. Used for reading and writing project files in version 1 format.
     * Lazily initialised.
     */
    private static PF1 projectFileV1;
    public static PF1 getProjectFileV1() {
        if (projectFileV1 == null) {
            projectFileV1 = new PF1();
        }
        return projectFileV1;
    }
    /**
     * The Project File V2 instance. Used for reading and writing project files in version 2 format.
     * Lazily initialised.
     */
    private static PF2 projectFileV2;
    public static PF2 getProjectFileV2() {
        if (projectFileV2 == null) {
            projectFileV2 = new PF2();
        }
        return projectFileV2;
    }

    /**
     * The layer tree instance which holds the tree view of {@link Layer} and {@link Thing}s.
     * The user can access this panel via it's wrapped {@link FloatingPanel} version within
     * the {@link Toolbox}. Initialised by StaticRefs
     */
    private static LayerTree layerTree;
    public static LayerTree getLayerTree() {
        if (layerTree == null)
            layerTree = new LayerTree();

        return layerTree;
    }

    private static Grid2DPanel grid2DPanel;
    public static Grid2DPanel getGrid2DPanel() {
        if (grid2DPanel == null)
            grid2DPanel = new Grid2DPanel();

        return grid2DPanel;
    }

    /**
     * The Logger Instance, which simply encapsulates the logging to the {@link DebugPanel#logTextArea}
     * and to the output file defined by  {@link LogFile} within {@link EngineFiles}.
     * Lazily initialised by {@link EngineFrame}
     */
    private static Logger log;
    public static Logger getLog() {
        if (log == null)
            log = new Logger();
        return log;
    }
    /**
     * The Engine Files instances. This encapsulates all the files stored in
     * {@code user.dir/J3Engine}. Lazily initialised by StaticRefs.
     */
    private static EngineFiles engineFiles;
    public static EngineFiles getEngineFiles() {
        if (engineFiles == null)
            engineFiles = new EngineFiles();
        return engineFiles;
    }

    /**
     * The Settings Instance. Usually all code accesses settings within {@link Settings}
     * statically. This instance is only really used to access it's UI. Initialised by StaticRefs
     */
    private static Settings settings;
    public static Settings getSettings() {
        if (settings == null)
            settings = new Settings();
        return settings;
    }
    /**
     * New help generator. No docs yet. lmao. Initialised by StaticRefs
     */
    private static DocsProvider docsProvider;
    public static DocsProvider getDocsProvider() {
        if (docsProvider == null)
            docsProvider = new DocsProvider();
        return docsProvider;
    }
    /**
     * The commands manager which just holds the current running {@link SemiStatefulCommand}
     * and gets all the commands otherwise. Initialised by StaticRefs
     */
    private static CommandsManager commandManager;
    public static CommandsManager getCommandManager() {
        if (commandManager == null)
            commandManager = new CommandsManager();
        return commandManager;
    }

    private static final ErrorHandler errs = new ErrorHandler();
    public static ErrorHandler getErrs() {
        return errs;
    }

    /**
     * Returns an {@link Image} of the J3Engine 1:1 logo for the app icon.
     * @return an Image
     */
    public static Image logo() {
        URL imgURL = StaticRefs.class.getResource("/art/logo/J3Dicon.png");
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

    public static void clear() {
        // clear engine frame set stuff.
        sceneManager    = null;
        executor        = null;
        mainFrame       = null;
        commandParser   = null;
        mainPanel       = null;
        globalKeybinds  = null;
        hoverLabel      = null;
        
        // clear lazily instantiayed stuff.
        debugPanel = null;
        propertiesPanel = null;
        projectFileV1 = null;
        projectFileV2 = null;
        layerTree = null;
        grid2DPanel = null;
//        engineFiles = null;   // Engine files should preferably not be remade.
        if (settings != null)
            settings.clearState();
        settings = null;      // Settings are always made on the fly.
//        docsProvider = null;
        commandManager = null;

        log.println("[ST-REFS] Cleared all static references.");
        log = null;
    }
}
