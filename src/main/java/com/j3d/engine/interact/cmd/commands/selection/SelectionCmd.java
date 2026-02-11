package com.j3d.engine.interact.cmd.commands.selection;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.interact.selection.SelectionMouseOwner;
import com.j3d.engine.react.events.EventBroadcast;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventType;

import javax.swing.*;

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
    private SafeJLabel logLabel;
    private Object[] _args;
    private EventListener listener = new EventListener() {
        @Override
        public <K> void onEvent(EventType event, EventBroadcast<K> properties) {
            if (Static.renderer.getSelected().isEmpty()) return;
            dispatchToSubcommands(subcommandName, logLabel, _args);
            // concurrent modification is an issue.
            // TODO: Implement on the event listener level, a single use listener.
            removeListener();
        }
    };
    public void removeListener() {
        if (listener != null) {
            SelectionManager.selectionMouseOwner.detach(listener);
            listener = null;
        }
    }

    public void run(SafeJLabel logLabel, String aliasUsed, Object... args) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandNamei)) {
            logLabel.setText("Invalid arguments. Usage: selection <subcommand> ...");
            return;
        }
        Static.mainFrame.requestFocusInWindow(); // Remove focus from the command pallete
        this.subcommandName = subcommandNamei;
        this.logLabel = logLabel;
        this._args = args;
        if (Static.renderer.getSelected().isEmpty()) {
            logLabel.setText("Make a selection then left click to continue this command.");
            SelectionManager.selectionMouseOwner.attach(
                    listener
            );
            return;
        }
        SelectionManager.selectionMouseOwner.clearSelectionSquare();
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
