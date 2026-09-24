package com.j3d.engine.interact.macros;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.camera.CameraCmd;
import com.j3d.engine.interact.cmd.commands.macro.MacroCmd;
import com.j3d.engine.interact.cmd.commands.macro.RunCmd;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.react.events.EventType;
import com.j3d.storage.files.engine.MacrosFolder;
import com.j3d.ui.engine.ContextMenu;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.engine.toolbox.ToolboxButtons;
import com.j3d.ui.settings.popouts.macros.MacrosEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Da Macro Utils. Basically the single instance manager of all the macros in J3Engine, this class is responsible for:
 * <ul>
 *     <li>
 *         Providing the entire set of macros from the user's macros folder and the keys map
 *     </li>
 *     <li>
 *         Updating both ends when a new macro is added or a key is changed. Although thats specific to {@link MacrosEditor}
 *     </li>
 *     <li>
 *         Removing all {@link J3Key} in the global {@link KeyBindings} when macros change and the same for the {@link EngineFrame#getMacroJMenu()}
 *         where the macros are clickable via the JMenu
 *     </li>
 *     <li>
 *         Holding the single instances to the {@link MacroRecorder} and {@link MacroRunner}
 *     </li>
 * </ul>
 * It can be accessed via {@link StaticRefs#getMacroUtils()} at any time.
 * @see Macro
 * @see MacroRunner
 * @see MacroRecorder
 * @see MacrosEditor
 * @see EngineFrame
 * @see J3Key
 * @see KeyBindings
 * @see MacroCmd
 * @see StaticRefs#getMacroUtils()
 *
 * @author Lehlogonolo Poole
 */
public class MacroUtils {

    /**
     * Recorder instance
     */
    private final MacroRecorder macroRecorder;
    /**
     * Runner instance
     */
    private final MacroRunner macroRunner;
    /**
     * The map of macros that exist or were discovered
     */
    private final HashMap<String, Macro> macros = new HashMap<>();
    /**
     * The key map of macros. A macro might be defined in {@link #macros} but not in in macro keys. That implies
     * the macro was freshly added and currently does not have a key association.
     */
    private HashMap<KeyStroke, String> macroKeys = new HashMap<>();
    /**
     * The registered list of {@link J3Key} representing these macros such that {@link #tearDownAndUpdate()} can purge
     * all the keys when rebuilding.
     */
    private final ArrayList<J3Key> registeredKeys = new ArrayList<>();

    /**
     * Default Constructor.
     * <p>
     *     This has the added job of instantiating {@link MacroRecorder} and {@link MacroRunner} and attaching them as listeners
     *     to the instances they wish to listen to
     * </p>
     */
    public MacroUtils() {
        macroRecorder = new MacroRecorder();
        macroRunner = new MacroRunner();

        // to listen for COMMAND_FIRED
        CommandsManager.commands.attachListener(macroRecorder);
        // to listen for X_SELECTED
        SelectionManager.selectionMouseOwner.attachListener(macroRecorder);
        // to listen for CAMERA_MOVED
        StaticRefs.getCamera().attachListener(macroRecorder);

        // to listen for STATEFUL_COMMAND_COMPLETED
        CommandsManager.commands.attachListener(macroRunner);

        discover();
    }

    /**
     * Discovers macros from the user's macro's foldr and the keys map along with it.
     */
    private void discover() {
        MacrosFolder mf = StaticRefs.getEngineFiles().macrosFile;
        try {
            HashMap<String, ArrayList<MacroLine>> ms = mf.readAll();
            ms.forEach((key, value) -> macros.put(key, new Macro(key, value)));
            macroKeys = mf.readKeysMap();
            tearDownAndUpdate();
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: custom exception.
        }
    }

    /**
     * Adds a new macro into the list and to the user's macros folder as a new file.
     * @param macro The macro to add
     * @throws IOException if {@link MacrosFolder} could not add the new macro.
     */
    public void addMacro(Macro macro) throws IOException {
        macros.put(macro.getName(), macro);
        StaticRefs.getEngineFiles().macrosFile.write(macro.getName() ,macro.getMacroLines());
    }

    /**
     * Gets the current macros in the engine
     * @return A shallow-copy of {@link #macros}
     */
    public HashMap<String, Macro> getMacros() {
        return new HashMap<>(macros);
    }

    /**
     * gets the current key map of keystrokes to macro entries
     * @return A shallow-copy of {@link #macroKeys}
     */
    public HashMap<KeyStroke, String> getMacroKeys() {
        return new HashMap<>(macroKeys);
    }

    /**
     * Gets the macro recorder
     * @return Returns the {@link MacroRecorder} instance
     */
    public MacroRecorder getMacroRecorder() {
        return macroRecorder;
    }

    /**
     * Gets the macro runner
     * @return Returns the  {@link MacroRunner} instance
     */
    public MacroRunner getMacroRunner() {
        return macroRunner;
    }

    /**
     * Sets the key map to a new map.
     * @implNote This does call {@link #tearDownAndUpdate()} so an implementor need not worry.
     * @param macroKeys The new keys to write in memory and on disk.
     */
    public void setMacroKeys(HashMap<KeyStroke, String> macroKeys) {

        this.macroKeys = macroKeys;

        try {
            StaticRefs.getEngineFiles().macrosFile.writeKeysMap(macroKeys);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }

        tearDownAndUpdate();
    }

    /**
     * Removes all macros in {@link KeyBindings} global instance and removes the JMenu items from {@link EngineFrame#getMacroJMenu()}
     * and rebuilds it from scratch.
     * @implNote Like any other engine thing such as {@link ToolboxButtons} or {@link ContextMenu},
     * the macro is not ran directly but instead we call {@link MacroCmd} to dedicate to its subcommand {@link RunCmd} via
     * {@link CommandParser#run(Command, ArrayList, ArrayList)}. Keeping the philosophy that everything is an abstraction of a command that
     * a user cna do since they cna just as well invoke the macro via {@code macro run <macro name>}
     * @see EngineFrame#getMacroJMenu()
     * @see CommandParser
     * @see KeyBindings
     * @see StaticRefs#getGlobalKeybinds()
     */
    private void tearDownAndUpdate() {
        if (!registeredKeys.isEmpty())
            registeredKeys
                    .stream()
                    .map(J3Key::getId)
                    .forEach(StaticRefs.getGlobalKeybinds()::removeJ3KeyConsumer);

        registeredKeys.clear();
        JMenu macroMenu = StaticRefs.getMainFrame().getMacroJMenu();
        macroMenu.removeAll();

        macroKeys.forEach((keyStroke, macroName) -> {
            Macro macro = macros.get(macroName);
            if (macro != null) {
                AbstractAction action =
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                System.out.println(keyStroke + "from macro " + macroName + " was pressed!!!");
                                StaticRefs.getCommandParser().run(
                                        CommandsManager.commands.macroCmd,
                                        new ArrayList<>(List.of("run", macroName)),
                                        new ArrayList<>()
                                );
                            }
                        };
                J3Key key = new J3Key(
                        macroName + "_macro",
                        keyStroke,
                        action
                );
                registeredKeys.add(key);
                StaticRefs.getGlobalKeybinds().registerJ3Key(key);

                JMenuItem item = new JMenuItem();
                item.setText(macroName);
                item.addActionListener(action);
                macroMenu.add(item);
            }
        });
    }

    /**
     * List of classes the {@link MacroRecorder} will ignore when listening for {@link EventType#COMMAND_FIRED}
     */
    public static final List<Class<? extends Command>> commandsToIgnore = List.of(
            CameraCmd.class, MacroCmd.class
    );

}
