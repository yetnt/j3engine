/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d.ui.engine;

import com.j3d.Startup;
import com.j3d.StaticRefs;
import com.j3d.Executor;
import com.j3d.engine.DefaultObjectDeletionException;
import com.j3d.engine.geometry.Dim;
import com.j3d.engine.interact.Interactable;
import com.j3d.engine.interact.cmd.commands.engine.ExitCmd;
import com.j3d.engine.interact.cmd.commands.orbit.*;
import com.j3d.engine.interact.cmd.commands.transform.*;
import com.j3d.engine.interact.cmd.commands.transform.mouse.*;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.input.mouse.*;
import com.j3d.engine.interact.selection.*;
import com.j3d.StaticConfig;
import com.j3d.errors.ErrorHandler;
import com.j3d.gen.settings.Settings;
import com.j3d.storage.files.FilesUtility;
import com.j3d.storage.files.protocol.proj.PF1;
import com.j3d.storage.files.protocol.proj.PF2;
import com.j3d.storage.files.protocol.proj.ProjectFile;
import com.j3d.threads.LongTask;
import com.j3d.ui.engine.floating.DebugPanel;
import com.j3d.ui.engine.floating.tree.LayerTree;
import com.j3d.ui.engine.toolbox.ToolboxButtons;
import com.j3d.ui.theme.CursorManager;
import com.j3d.ui.theme.CursorNames;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.settings.PreferencesFrame;
import com.j3d.ui.engine.toolbox.Toolbox;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import static com.j3d.engine.interact.input.keyboard.KeyBindings.commandPaletteFocusOwner;

import com.j3d.engine.draw.ViewType;
import com.j3d.ui.dialog.AreYouSure;
import com.j3d.ui.HoverJLabel;
import com.j3d.utility.generators.JLabelRichText;

/**
 * Possibly. The most chaotic, most important UI. This is the main UI of the entire app where the user
 * will spend ALL of their time and where just everything has to connect.
 * <p>
 *     As a result, this class is also the orchestrator of all the important references storted
 *     within {@link StaticRefs}.
 * </p>
 * <p>
 *     Like all other UI, it's generated with NetBeans, hence the {@link #initComponents()}.
 * </p>
 * @see Toolbox
 * @see ToolboxButtons
 * @see DebugPanel
 * @see LayerTree
 * @see CommandPalette
 * @see FloatingPanel
 * @see HoverJLabelPanel
 * @see StaticRefs
 * @see SceneManager
 * @author Lehlogonolo Poole
 */
public class EngineFrame extends javax.swing.JFrame {
    /**
     * Boolean flag to run the {@link Executor}. Once the executor has run this is immediately set
     * to false.
     */
    public static boolean run = true;
    /**
     * The default {@link MouseOwner}. being selection
     * @see MouseOwner
     * @see MOwner
     */
    private static MOwner mouseOwner = MOwner.SELECTION;
    /**
     * The position of the mouse that is suspected by this frame.
     * Do not trust this value. you might need to do offset magic with
     * {@link StaticConfig#jMenuBarOffsetY}
     */
    public static ScreenPoint mousePos = null;
    /**
     * The selection area of the actual selection
     * @see SelectionQuery
     * @see SelectionUI
     * @see SelectionManager
     */
    public static ScreenPoint[] selectionArea = new ScreenPoint[2];
    /**
     * The command palette instance.
     * @see CommandParser
     */
    public static final CommandPalette COMMAND_PALETTE = new CommandPalette(); // TODO: refactor to a getter from prolly command palette.

    /**
     * Sets the mouse owner.
     * @param owner The mouse owner's enum identifier
     * @see MouseOwner
     * @see MOwner
     */
    public static void setMouseOwner(MOwner owner) {
        mouseOwner = owner == null ? MOwner.SELECTION : owner;
    }

    /**
     * Retries the mouse owner.
     * @return The mouse owner's enum identifier.
     * @see MouseOwner
     * @see MOwner
     */
    public static MOwner getMouseOwner() {
        return mouseOwner;
    }

    /**
     * The arraylist of runnables to initialise floaters when {@link EngineFrame} is ready.
     */
    private static final ArrayList<Runnable> floats = new ArrayList<>();

    /**
     * Default constructor.
     * Initialises the entire engine in a clean state.
     */
    public EngineFrame() {
        this(false);
    }

    /**
     * Constructor to initialise the engine frame and immediately load up a project file.
     * @param file The File instance to load up.
     * @see PF1
     * @see LongTask
     */
    public EngineFrame(File file) {
        // Call the other constructor, cuz it kinda does important stuff.
        this(false);
        this.setVisible(true);

        // read the file.
        readProjectFile(file);
    }

    /**
     * Constructor with a boolean flag to initialise the executor.
     * @param runExecutor Whether to run the executor or not. If false, then the engine is initialised
     *                    in a clean state.
     */
    public EngineFrame(boolean runExecutor) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this); // 'this' refers to the frame
        } catch (Exception e) {
            e.printStackTrace();
        }

        initMouseOwners();
        initComponents();
        complete(runExecutor);
        setCloseOperations();
    }

    /**
     * Adds a floating panel to the engine. Or otherwise if the engine hasn't been built
     * will add it later via a {@link Runnable}
     * @param p The floating panel to add
     * @param lastLocation The last location the mouse was at
     * @see FloatingPanel
     */
    public static void addFloaterAt(FloatingPanel p, Point lastLocation) {
        Runnable r = () -> {
            if (p.isHidden()) p.hideThis();
            p.setBounds(lastLocation.x, lastLocation.y, p.getPreferredSize().width, p.getPreferredSize().height);
            StaticRefs.getMainFrame().getLayeredPane().add(p, JLayeredPane.POPUP_LAYER);
            StaticRefs.getMainFrame().revalidate();
            StaticRefs.getMainFrame().repaint();
        };
        if (run) floats.add(r);
        else r.run();
    }

    /**
     * Brings a floating panel forward in the layer pane.
     * @param p The floating panel
     */
    public static void bringForward(FloatingPanel p) {
        StaticRefs.getMainFrame().getLayeredPane().moveToFront(p);
        StaticRefs.getMainFrame().revalidate();
        StaticRefs.getMainFrame().repaint();
    }

    /**
     * Removes a floating panel from the EngineFrame
     * @param p The floating panel
     */
    public static void removeFloater(FloatingPanel p) {
        StaticRefs.getMainFrame().getLayeredPane().remove(p);
        StaticRefs.getMainFrame().revalidate();
        StaticRefs.getMainFrame().repaint();
    }
    
    @Override
    public void dispose() {
        StaticRefs.getLog().println("[ENGINE-DISPOSAL] EngineFrame disposed.");
        StaticRefs.getErrs().ignore(DefaultObjectDeletionException.class);
        StaticRefs.getSceneManager().resetScene();
        StaticRefs.clear();
        super.dispose();
    }

    /**
     * Completes by manually initialising almost everything. I have no more words.
     * @implNote Usually i hate documenting code extensively. Code should be self-documenting.
     * However, this method is TOO LONG. I have to.
     * @param runExecutor Boolean to run the executor. if false, it's basically running the engine in a
     *                    new fresh state.
     */
    public void complete(boolean runExecutor) {
        // create the layered pane where all the panels will be layered on top of each other.
        JLayeredPane layeredPane = this.getLayeredPane();
        // initialise the menu bar offset
        final int menuBarOffsetY = (this.getJMenuBar().getSize().height + StaticConfig.jMenuBarOffsetY);
        // initialise extra properties of this frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(StaticConfig.screenSize.width, StaticConfig.screenSize.height);
        this.setResizable(false);

        // Add to the StaticRefs references and allow the draw panel to be focusable
        // and do a lot of other window shenanigans. Swing is the best. (sarcasm)
        StaticRefs.registerMainPanel((J3DPanel) mainPanel); // Cast to custom panel for drawing capabilities
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mainPanel.setBounds(0, menuBarOffsetY, StaticConfig.screenSize.width, StaticConfig.screenSize.height);
        mainPanel.setPreferredSize(new Dimension(StaticConfig.screenSize.width, StaticConfig.screenSize.height));

        // initialise the StaticRefs reference to this frame and set it to be maximised both vertically and horizontally
        StaticRefs.registerMainFrame(this);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // initialise the scene manager with the screen size.
        SceneManager sceneManager = new SceneManager(StaticConfig.screenSize);
        StaticRefs.registerSceneManager(sceneManager);
        // If we can run the executor, initialise it.
        if (runExecutor)
            StaticRefs.registerExecutor(new Executor(sceneManager));
        // initialise the debug panel's components.
        StaticRefs.getDebugPanel().startStatisticsThread();
        // create and set the bounds for the hover JLabel's panel.
        HoverJLabelPanel lbl = new HoverJLabelPanel();
        lbl.setBounds(0 ,0, lbl.getPreferredSize().width, lbl.getPreferredSize().height);
        // Add it to the layered pane. At level 400 and set it to visible.
        layeredPane.add(lbl, JLayeredPane.DRAG_LAYER);
        lbl.setVisible(true);
        // Add to the StaticRefs references.
        StaticRefs.registerHoverLabel(new HoverJLabel(lbl.getLabel()));

        // Initialise all keybinds to be part of the mainPanel and add to the static references
        InputMap im = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainPanel.getActionMap();
        StaticRefs.registerGlobalKeybinds(new KeyBindings(im, am, true));

        // Initialise the toolbox (at layer 200)
        Toolbox toolbox = new Toolbox();
        // Toolbox at the top and extends full width but not very tall
        toolbox.setBounds(0, menuBarOffsetY, StaticConfig.screenSize.width - 260, toolbox.getPreferredSize().height);
        layeredPane.add(toolbox, JLayeredPane.MODAL_LAYER); // above default layer

        // Set the text area of the Logger to be the DebugPanel's JTextArea
        StaticRefs.getLog().setLogArea(StaticRefs.getDebugPanel().logTextArea);

        // Calculate offset for the command pallet.
        // ideal offset should be sort of at the bottom of the frame, but centered horizontally.
        Rectangle bounds = this.getBounds();
        Dimension size = COMMAND_PALETTE.getPreferredSize();
        int x = ((bounds.width - size.width) / 2) - 60;
        int y = bounds.height - size.height - 200;
        COMMAND_PALETTE.setBounds(x, y, size.width, size.height);
        // Set extra properties of the command palette
        COMMAND_PALETTE.setOpaque(true);
        COMMAND_PALETTE.setBackground(new Color(30, 30, 30, 8));
        COMMAND_PALETTE.setVisible(true);
        // Add to layer 300
        layeredPane.add(COMMAND_PALETTE, JLayeredPane.POPUP_LAYER);
        StaticRefs.registerCommandParser(new CommandParser(COMMAND_PALETTE));

        // Set the main panel to be focusable and immediately set the user's focus to it.
        mainPanel.getRootPane().setFocusable(true);
        mainPanel.getRootPane().requestFocusInWindow();

        // Component Listener that triggers whenever the entire frame is resized to
        // reposition the command palette.
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Set values so other stuff know these values changed.
                StaticConfig.screenSize = new Dim(StaticRefs.getMainFrame().getSize());
                StaticRefs.getSceneManager().screenSize = StaticConfig.screenSize;
                toolbox.setBounds(0, menuBarOffsetY, StaticConfig.screenSize.width - 10, toolbox.getPreferredSize().height);
                StaticRefs.getMainPanel().setBounds(0, menuBarOffsetY, StaticConfig.screenSize.width, StaticConfig.screenSize.height);


                // Calculate new offsets (TODO: Idnetical to the previous offset calculation, extract?)
                Rectangle bounds = StaticRefs.getMainFrame().getBounds();
                Dimension size = COMMAND_PALETTE.getPreferredSize();
                int x = ((bounds.width - size.width) / 2) - 10;
                int y = bounds.height - size.height - 50;
                COMMAND_PALETTE.setBounds(x, y, size.width, size.height);

                StaticRefs.getMainFrame().repaint(); // repaint the frame
                StaticRefs.getMainPanel().repaint(); // repaint the panel too.
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // stub
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // stub
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // stub
            }
        });

        // Initialise the custom cursors to be for the entire frame. and set it to the default.
        CursorManager.init(this);
        CursorManager.setDefault();

        // Add all the floaters safely.
        floats.forEach(Runnable::run);

        // MouseMotionListener to set the position of the hoverlabel below the mouse at all times
        // but avoid the command palette. Don't ask me, it just starts glitching tf out when it goes
        // over or even under the command palette. so when it gets even near those coordinates, it doesn't
        // bother.
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int y = bounds.height - size.height - 200;
                if (e.getY() > y - 20) return;
                lbl.setBounds(e.getX(), e.getY(), lbl.getPreferredSize().width, lbl.getPreferredSize().height);
                layeredPane.revalidate();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int y = bounds.height - size.height - 200;
                if (e.getY() > y - 20) return;
                lbl.setBounds(e.getX(), e.getY(), lbl.getPreferredSize().width, lbl.getPreferredSize().height);
                layeredPane.revalidate();
            }
        });
        StaticRefs.getLog().uiPrintLn("EngineFrame completed building");
    }

    /**
     * Extracts all the file stuff like its path and name, logs the read
     * and also actually reads it by wrapping it in a {@link LongTask} so it
     * runs off the EDT
     * @param file The file to read.
     * @see LongTask
     * @see PF2
     * @see Interactable
     */
    private void readProjectFile(File file) {
        readFileUsingVers(file, 2);
    }

    /**
     * A flag to indicate if an old project file version was loaded during the {@link #readFileUsingVers(File, int)} process.
     * This is used to prevent redundant processing when attempting to load newer versions after a fallback
     * to an older version has already occurred.
     */
    private static boolean LOADED_OLD = false;

    /**
     * Reads a project file using a specified protocol version.
     * This method extracts file information, sets the project output file, and initiates
     * an asynchronous read operation using a {@link LongTask}. It includes error handling
     * that can trigger a fallback to an older project file version if the initial read fails.
     *
     * @param file The {@link File} object representing the project file to read.
     * @param vers The version number of the {@link ProjectFile} protocol to attempt reading with.
     *             For example, `2` for {@link PF2}.
     * @see ProjectFile
     * @see LongTask
     * @see ProjectFile#handleErr(ProjectFile, Exception, BiConsumer) 
     * @see Settings#projectOutputFile
     * @see StaticRefs#getLog()
     * @see Interactable#invokeSwingHooks()
     * @implNote This method utilizes the {@link #LOADED_OLD} flag to prevent infinite loops or
     *           redundant processing when a fallback to an older version has already been handled.
     *           The `LOADED_OLD` flag is reset at the beginning of the success callback.
     */
    public static void readFileUsingVers(File file, int vers) {
        String path = file.getAbsolutePath();
        StaticRefs.getLog().println(path);
        Path p = Paths.get(path);
        String fileName = p.getFileName().toString();
        String fileDir = p.getParent().toString();

        Settings.projectOutputFile.setValue(file);

        ProjectFile using = ProjectFile.getFromVersion(vers);
        if (using == null) {
            return;
            // The error handler would've already thrown the fatal error if we tried
            // referencing a version that doesnt exist. So this block is just to appease
            // Java. Otherwise in case weird stuff happen TODO: it might be here.
        }

        LongTask<ArrayList<Interactable>> t = new LongTask<>(
                ta -> {
                    ArrayList<Interactable> a = null;
                    try {
                        a = using.readFile(fileDir, fileName, ta);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(a.size());
                    return a;
                },
                (tb, i, completed) -> {
                    if (!completed || (vers == 2 && LOADED_OLD)) {
                        LOADED_OLD = false;
                        return;
                    }
                    i.forEach(Interactable::invokeSwingHooks);
                },
                (err) -> {
                    BiConsumer<Integer, ProjectFile> loadable = (currentV, convertTo) -> {
                        // just read the other version since this is being called.
                        StaticRefs.getLog().println(
                                "An old Project File Version of version 1 was detected.");
                        readFileUsingVers(file, convertTo.getProtocolVersion());
                        LOADED_OLD = true;
                    };
                    ProjectFile.handleErr(using, err, loadable);
                }
        );

        t.run();
    }

    /**
     * Sets all the closing operations:
     * <p>
     *     1. The window will do nothing when it closes
     * </p>
     * <p>
     *     2. If the user made a change and hasn't saved since, will prompt the user with and
     *     are you sure dialogue. Similar to running the command {@link ExitCmd}
     * </p>
     */
    private void setCloseOperations() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boolean saved = StaticConfig.hasSaved;
                if (saved) {
                    StaticRefs.getMainFrame().dispose();
                    System.exit(0);
                }
                AreYouSure ays = new AreYouSure(StaticRefs.getMainFrame(), true, "You have not saved this project. Progress will be lost.");
                ays.setVisible(true);
                if (ays.canProceed()) {
                    StaticRefs.getMainFrame().dispose();
                    System.exit(0);
                }
            }
        });
    }

    /**
     *  Adds all the mouse owners.
     * @see MouseOwner
     * @see MOwner
     * @see SelectionManager
     * @see ScaleMouseOwner
     * @see TranslateMouseOwner
     * @see RotateMouseOwner
     * @see OrbitMouseOwner
     * @see NoMouseOwner
     */
    private void initMouseOwners() {
        ArrayList<MouseOwner> owners = new ArrayList<>();
        owners.add(SelectionManager.selectionMouseOwner);
        owners.add(new NoMouseOwner());
        owners.add(ScaleSelection.scaleMouseOwner);
        owners.add(TranslateSelection.translateMouseOwner);
        owners.add(RotateSelection.rotateMouseOwner);
        owners.add(OrbitCmd.orbitMouseOwner);

        owners.forEach(m -> {
            this.addMouseMotionListener(m);
            this.addMouseListener(m);
            this.addMouseWheelListener(m);
        });
    }

    /**
     * Repaints the debug panel, command pallete, and main frame on the Event Dispatch Thread.
     * This should only be called from non-EDT threads. e.g. from the SceneManager thread.
     * @implNote The {@link CommandParser} is the only class using this. Thats how fragile the
     * {@link CommandPalette} is and i wish i could tell you why.
     */
    public static void repaintL() {
        SwingUtilities.invokeLater(() -> {
            if (StaticRefs.getDebugPanel() != null) {
                StaticRefs.getDebugPanel().revalidate();
                StaticRefs.getDebugPanel().repaint();
            }
            COMMAND_PALETTE.revalidate();
            COMMAND_PALETTE.repaint();
            if (StaticRefs.getMainFrame() != null) { // it cant be null if we're in this call. but you know java.
                StaticRefs.getMainFrame().revalidate();
                StaticRefs.getMainFrame().repaint();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new J3DPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveProjectJMenuItem = new javax.swing.JMenuItem();
        openProjectMenuItem = new javax.swing.JMenuItem();
        newProjectJMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
        editJMenu = new javax.swing.JMenu();
        undoJMenuItem = new javax.swing.JMenuItem();
        redoJMenuItem = new javax.swing.JMenuItem();
        mouseJMenu = new javax.swing.JMenu();
        viewJMenu = new javax.swing.JMenu();
        viewAsNormalJMenuItem = new javax.swing.JMenuItem();
        viewAsWireframeJMenuItem = new javax.swing.JMenuItem();
        redrawJMenuItem = new javax.swing.JMenuItem();
        resetJMenuItemDropDown = new javax.swing.JMenu();
        resetCameraJMenuItem = new javax.swing.JMenuItem();
        resetOrientationJMenuItem = new javax.swing.JMenuItem();
        resetPositionJMenuItem = new javax.swing.JMenuItem();
        exportJMenuItemDropDown = new javax.swing.JMenu();
        exportAsPNGJMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        logOutJMenuItem = new javax.swing.JMenuItem();
        deleteAccountJMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("J3D");
        setIconImage(StaticRefs.logo());
        setMinimumSize(new java.awt.Dimension(1024, 768));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1489, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 787, Short.MAX_VALUE)
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        jMenuBar1.setBackground(J3DTheme.UI_SURFACE.color().brighter());
        jMenuBar1.setOpaque(true);

        jMenu1.setText("File");

        saveProjectJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveProjectJMenuItem.setText("Save");
        saveProjectJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveProjectJMenuItem);

        openProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        openProjectMenuItem.setText("Open Project");
        openProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(openProjectMenuItem);

        newProjectJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        newProjectJMenuItem.setText("New Project");
        newProjectJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectJMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(newProjectJMenuItem);

        settingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(settingsMenuItem);

        jMenuBar1.add(jMenu1);

        editJMenu.setText("Edit");

        undoJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        undoJMenuItem.setText("Undo");
        undoJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoJMenuItemActionPerformed(evt);
            }
        });
        editJMenu.add(undoJMenuItem);

        redoJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        redoJMenuItem.setText("Redo");
        redoJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoJMenuItemActionPerformed(evt);
            }
        });
        editJMenu.add(redoJMenuItem);

        jMenuBar1.add(editJMenu);

        mouseJMenu.setText("Mouse");
        jMenuBar1.add(mouseJMenu);

        viewJMenu.setText("View");

        viewAsNormalJMenuItem.setText("Normal View");
        viewAsNormalJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsNormalJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(viewAsNormalJMenuItem);

        viewAsWireframeJMenuItem.setText("Wireframe View");
        viewAsWireframeJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAsWireframeJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(viewAsWireframeJMenuItem);

        redrawJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        redrawJMenuItem.setText("Redraw");
        redrawJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redrawJMenuItemActionPerformed(evt);
            }
        });
        viewJMenu.add(redrawJMenuItem);

        resetJMenuItemDropDown.setText("Reset");

        resetCameraJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetCameraJMenuItem.setText("Reset Camera");
        resetCameraJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCameraJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetCameraJMenuItem);

        resetOrientationJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetOrientationJMenuItem.setText("Reset Orientation");
        resetOrientationJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetOrientationJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetOrientationJMenuItem);

        resetPositionJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        resetPositionJMenuItem.setText("Reset Position");
        resetPositionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPositionJMenuItemActionPerformed(evt);
            }
        });
        resetJMenuItemDropDown.add(resetPositionJMenuItem);

        viewJMenu.add(resetJMenuItemDropDown);

        exportJMenuItemDropDown.setText("Export As...");

        exportAsPNGJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        exportAsPNGJMenuItem.setText("PNG");
        exportAsPNGJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAsPNGJMenuItemActionPerformed(evt);
            }
        });
        exportJMenuItemDropDown.add(exportAsPNGJMenuItem);

        viewJMenu.add(exportJMenuItemDropDown);

        jMenuBar1.add(viewJMenu);

        jMenu2.setText("User");

        logOutJMenuItem.setText("Log Out");
        logOutJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutJMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(logOutJMenuItem);

        deleteAccountJMenuItem.setText("Delete Account");
        jMenu2.add(deleteAccountJMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        setSize(new java.awt.Dimension(1503, 817));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void resetPositionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPositionJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        StaticRefs.getCamera().setPosition(new Vector3(0, 0, 0));
        this.repaint();
    }//GEN-LAST:event_resetPositionJMenuItemActionPerformed

    private void undoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoJMenuItemActionPerformed
        SceneManager.history.undo();
        this.repaint();
    }//GEN-LAST:event_undoJMenuItemActionPerformed

    private void redoJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoJMenuItemActionPerformed
        SceneManager.history.redo();
        this.repaint();
    }//GEN-LAST:event_redoJMenuItemActionPerformed

    private void resetOrientationJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetOrientationJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        StaticRefs.getCamera().setRotation(new Rotation(0, 0, 0));
        this.repaint();
    }//GEN-LAST:event_resetOrientationJMenuItemActionPerformed

    private void resetCameraJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCameraJMenuItemActionPerformed
        if (commandPaletteFocusOwner(COMMAND_PALETTE)) return;
        StaticRefs.getCamera().setPosition(new Vector3(0, 0, 0));
        StaticRefs.getCamera().setRotation(new Rotation(0, 0, 0));
        this.repaint();
    }//GEN-LAST:event_resetCameraJMenuItemActionPerformed

    private void redrawJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redrawJMenuItemActionPerformed
        // no.
    }//GEN-LAST:event_redrawJMenuItemActionPerformed

    private void viewAsWireframeJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAsWireframeJMenuItemActionPerformed
        StaticConfig.setViewType(ViewType.WIREFRAME);
        this.repaint();
    }//GEN-LAST:event_viewAsWireframeJMenuItemActionPerformed

    private void viewAsNormalJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAsNormalJMenuItemActionPerformed
        StaticConfig.setViewType(ViewType.NORMAL);
        this.repaint();
    }//GEN-LAST:event_viewAsNormalJMenuItemActionPerformed

    private void openProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectMenuItemActionPerformed
        AreYouSure ays = new AreYouSure(this, true,
                "Whatever is on screen currently will be discarded.");
        ays.setVisible(true);

        if (!ays.canProceed()) return;

        File file = FilesUtility.fileChooser(jfcConfig -> {
            jfcConfig.setDialogTitle("choose a filel");
            jfcConfig.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfcConfig.setAcceptAllFileFilterUsed(false);
            jfcConfig.setFileFilter(
                    new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getName().endsWith(".j3p") || !f.isFile();
                        }

                        @Override
                        public String getDescription() {
                            return "J3D Project File";
                        }
                    }
            );
        }, this);
        if (file == null) return;
        StaticRefs.getSceneManager().resetScene();
        readProjectFile(file);
    }//GEN-LAST:event_openProjectMenuItemActionPerformed

    private void saveProjectJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectJMenuItemActionPerformed
        if (Settings.projectOutputFile.getValue() == null) {
            String fileName = JOptionPane.showInputDialog("Project name?");
            fileName = (
                    fileName == null || fileName.trim().isEmpty()
                            ? "project1"
                            : fileName
            ) + ".j3p";

            File folder = FilesUtility.folderChooser(this);

            if (folder == null)
                return;

            StaticRefs.getLog().println("Picked the location " + folder.getAbsolutePath() + " with the file name " + fileName);

//            J3DSettings.setProject(folder.getAbsolutePath(), fileName);
            Settings.projectOutputFile.setValue(new File(folder, fileName));
        }

        new PF2().writeFile(
                Settings.projectOutputFile.getValue().getParent(),
                Settings.projectOutputFile.getValue().getName(), StaticRefs.getSceneManager().layers);
    }//GEN-LAST:event_saveProjectJMenuItemActionPerformed

    private void newProjectJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectJMenuItemActionPerformed
        boolean canProceed = true;
        if (!StaticConfig.hasSaved) {
            AreYouSure ays = new AreYouSure(
                    this, true,
                    new JLabelRichText(
                            "You haven't saved this project! Click Nah Fam then use"
                    ).addLn("CTRL+S to save, or click Hell Yeah to proceed anyway.")
                            .wrapHTML()
            );
            ays.setVisible(true);
            canProceed = ays.canProceed();
        }
        if (!canProceed) return;
        Settings.projectOutputFile.setValue(null);
        StaticRefs.getSceneManager().resetScene();
    }//GEN-LAST:event_newProjectJMenuItemActionPerformed

    private void exportAsPNGJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAsPNGJMenuItemActionPerformed
        File o = FilesUtility.fileChooser(
                jfc -> {
                    // allow folders or other PNG files to be chosen
                    jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getName().endsWith(".png") || !f.isFile();
                        }

                        @Override
                        public String getDescription() {
                            return "PNG File or otherwise output directory";
                        }
                    });
                },
                this
        );

        if (o == null)
            return;
        File file = !o.isFile() ? new File(o, "export.png") : o;

        try {
            StaticRefs.getMainPanel().exportAs("png", file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }//GEN-LAST:event_exportAsPNGJMenuItemActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        // Open PreferencesFrame JFrame on top of this frame at the centre of the screen.
        PreferencesFrame preferencesFrame = StaticRefs.getSettings().panel();
        CursorManager.set(CursorNames.DEFAULT, preferencesFrame);
        preferencesFrame.setLocationRelativeTo(this);
        preferencesFrame.setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void logOutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutJMenuItemActionPerformed
        // log out one day
        boolean canProceed = true;
        if (!StaticConfig.hasSaved) {
            AreYouSure ays = new AreYouSure(
                    this, true,
                    new JLabelRichText(
                            "You haven't saved this project! Click Nah Fam then use"
                    ).addLn("CTRL+S to save, or click Hell Yeah to proceed anyway.")
                            .wrapHTML()
            );
            ays.setVisible(true);
            canProceed = ays.canProceed();
        }
        if (!canProceed) return;

        this.dispose();

        Startup.run();
    }//GEN-LAST:event_logOutJMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EngineFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                StaticRefs.getMainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem deleteAccountJMenuItem;
    private javax.swing.JMenu editJMenu;
    private javax.swing.JMenuItem exportAsPNGJMenuItem;
    private javax.swing.JMenu exportJMenuItemDropDown;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    public javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem logOutJMenuItem;
    public static javax.swing.JPanel mainPanel;
    private javax.swing.JMenu mouseJMenu;
    private javax.swing.JMenuItem newProjectJMenuItem;
    private javax.swing.JMenuItem openProjectMenuItem;
    private javax.swing.JMenuItem redoJMenuItem;
    private javax.swing.JMenuItem redrawJMenuItem;
    private javax.swing.JMenuItem resetCameraJMenuItem;
    private javax.swing.JMenu resetJMenuItemDropDown;
    private javax.swing.JMenuItem resetOrientationJMenuItem;
    private javax.swing.JMenuItem resetPositionJMenuItem;
    private javax.swing.JMenuItem saveProjectJMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem undoJMenuItem;
    private javax.swing.JMenuItem viewAsNormalJMenuItem;
    private javax.swing.JMenuItem viewAsWireframeJMenuItem;
    private javax.swing.JMenu viewJMenu;
    // End of variables declaration//GEN-END:variables
}
