package com.j3d.engine.interact.macros;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.camera.CameraCmd;
import com.j3d.engine.interact.cmd.commands.macro.MacroCmd;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.storage.files.engine.MacrosFolder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MacroUtils {

    private final MacroRecorder macroRecorder;
    private final MacroRunner macroRunner;
    private final HashMap<String, Macro> macros = new HashMap<>();
    private HashMap<KeyStroke, String> macroKeys = new HashMap<>();
    private final ArrayList<J3Key> keys = new ArrayList<>();

    public MacroUtils() {
        macroRecorder = new MacroRecorder();
        macroRunner = new MacroRunner();
        CommandsManager.commands.attachListener(macroRecorder);
        SelectionManager.selectionMouseOwner.attachListener(macroRecorder);
        StaticRefs.getCamera().attachListener(macroRecorder);
        CommandsManager.commands.attachListener(macroRunner);
        SelectionManager.selectionMouseOwner.attachListener(macroRunner);
        StaticRefs.getCamera().attachListener(macroRunner);

        discover();
    }

    public void discover() {
        MacrosFolder mf = StaticRefs.getEngineFiles().macrosFile;
        try {
            HashMap<String, ArrayList<MacroLine>> ms = mf.readAll();
            ms.forEach((key, value) -> macros.put(key, new Macro(key, value)));
            macroKeys = mf.readKeysMap();
            addJ3Keys();
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO: custom exception.
        }
    }

    public HashMap<String, Macro> getMacros() {
        return new HashMap<>(macros);
    }

    public void addMacro(String s, Macro m) {
        macros.put(s, m);
    }

    public HashMap<KeyStroke, String> getMacroKeys() {
        return macroKeys;
    }

    public MacroRecorder getMacroRecorder() {
        return macroRecorder;
    }

    public MacroRunner getMacroRunner() {
        return macroRunner;
    }

    public void setMacroKeys(HashMap<KeyStroke, String> macroKeys) {

        this.macroKeys = macroKeys;

        try {
            StaticRefs.getEngineFiles().macrosFile.writeKeysMap(macroKeys);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }

        addJ3Keys();
    }

    public void addJ3Keys() {
        if (!keys.isEmpty())
            keys
                    .stream()
                    .map(J3Key::getId)
                    .forEach(StaticRefs.getGlobalKeybinds()::removeJ3KeyConsumer);

        keys.clear();
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
                keys.add(key);
                StaticRefs.getGlobalKeybinds().registerJ3Key(key);

                JMenuItem item = new JMenuItem();
                item.setText(macroName);
                item.addActionListener(action);
                macroMenu.add(item);
            }
        });
    }

    public static final List<Class<? extends Command>> commandsToIgnore = List.of(
            CameraCmd.class, MacroCmd.class
    );

}
