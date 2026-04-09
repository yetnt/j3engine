package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.TaggedArgValue;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventReactor;
import com.j3d.engine.react.events.EventType;

import java.util.ArrayList;
import java.util.List;

public class TransformCmd extends Command {

    public TransformCmd() {
        super("transform", "do stuff wit selection");
        this.aliases("sel", "s", "select", "trans", "t").args(
                new RotateSelection(),
                new TranslateSelection(),
                new ScaleSelection()
        ).parseUsages();
        this.usages.put(
                new ArrayList<>(List.of(String.class, String.class)),
                " [scale|translate] [p|v|t|f] ...key:value");
    }

    private String subcommandName;
    private SafeJLabel logLabel;
    private Object[] _args;
    private ArrayList<TaggedArgValue<?>> _taggedArgs;
    private final EventReactor listener = new EventReactor() {
        @Override
        public <K> void onEvent(EventType event, EventPayload<K> properties) {
            if (Static.renderer.getSelected().isEmpty()) return;
            dispatchToSubcommands(subcommandName, logLabel, _args, _taggedArgs);
        }
    };

    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        // There has to be at least 2 arguments, the subcommand and its argument(s)
        if (args.length < 1 || !(args[0] instanceof String subcommandNamei)) {
            logLabel.setText("Invalid arguments. Usage: "+aliasUsed+" <subcommand> ...");
            return;
        }
        if (this.args.stream()
                .map(o->(Command)o)
                .flatMap(Command::aliasStream)
                .noneMatch(alias -> alias.equals(subcommandNamei))
        ) {
            logLabel.setText("Invalid subcommand. Usage: "+aliasUsed+" [\"translate\", \"rotate\", \"scale\"] ...");
            return;
        }
        Static.mainFrame.requestFocusInWindow(); // Remove focus from the command pallete
        this.subcommandName = subcommandNamei;
        this.logLabel = logLabel;
        this._args = args;
        this._taggedArgs = taggedArgs;
        if (Static.renderer.getSelected().isEmpty()) {
            logLabel.setText("Make a selection then left click to continue this command.");
            SelectionManager.selectionMouseOwner.attach(
                    listener
            );
            return;
        }
        SelectionManager.selectionMouseOwner.clearSelectionSquare();
        dispatchToSubcommands(subcommandNamei, logLabel, args, taggedArgs);
    }
}
