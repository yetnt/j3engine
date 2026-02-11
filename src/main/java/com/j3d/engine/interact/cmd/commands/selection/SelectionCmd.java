package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.GPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.base.ArgSet;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.input.KeyBindings;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.react.events.EventBroadcast;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SelectionCmd extends Command {

    public SelectionCmd() {
        super("selection", "do stuff wit selection");
        this.aliases("sel", "s", "select").args(
                new RotateSelection(),
                new TranslateSelection(),
                new ScaleSelection()
        ).parseUsages();
    }

    private String subcommandName;
    private JLabel logLabel;
    private EventListener listener = new EventListener() {
        @Override
        public <K> void onEvent(EventType event, EventBroadcast<K> properties) {
            if (Static.renderer.getSelected().isEmpty()) return;
            dispatchToSubcommands(subcommandName, logLabel, args);
            removeListener();
        }
    };
    public void removeListener() {
        if (listener != null) {
            SelectionManager.selectionMouseOwner.detach(listener);
            listener = null;
        }
    }

    public void run(JLabel logLabel, String aliasUsed, Object... args) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandNamei)) {
            logLabel.setText("Invalid arguments. Usage: selection <subcommand> ...");
            return;
        }
        Static.mainFrame.requestFocusInWindow(); // Remove focus from the command pallete
        this.subcommandName = subcommandNamei;
        this.logLabel = logLabel;
        if (Static.renderer.getSelected().isEmpty()) {
            logLabel.setText("Make a selection then left click to continue this command.");
            SelectionManager.selectionMouseOwner.attach(
                    listener
            );
            return;
        }
        dispatchToSubcommands(subcommandNamei, logLabel, args);
    }

    /**
     * Dispatches to the appropriate selection subcommand based on the provided option.
     * This method is called by the subcommands to perform their specific actions.
     *
     * @param option The selection option indicating which action to perform (rotate, translate, scale).
     */
    public static void selectDispatch(SC_Option option) {
        switch (option) {
            case ROTATE -> {
                // j
            }
            case TRANSLATE -> {
                // a
            }
            case SCALE -> {

            }
        }
    }
}
