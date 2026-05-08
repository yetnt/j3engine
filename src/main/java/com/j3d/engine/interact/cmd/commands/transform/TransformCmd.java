package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.base.conditions.SelectionPreCondition;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.selection.SelectionManager;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class TransformCmd extends Command implements SemiStatefulCommand {

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

    private SelectionPreCondition selectionPreCondition;
    private String subcommandName;
    private SafeJLabel logLabel;
    private Object[] _args;
    private ArrayList<TaggedArgValue<?>> _taggedArgs;

    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
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
        CommandsManager.setAsCurrent(this);
        selectionPreCondition = new SelectionPreCondition(
                () -> {
                    CommandsManager.clearCurrent();
                    SelectionManager.selectionMouseOwner.clearSelectionSquare();
                    dispatchToSubcommands(subcommandName, logLabel, _args, _taggedArgs);
                }
        );
        Static.mainFrame.requestFocusInWindow(); // Remove focus from the command pallete
        this.subcommandName = subcommandNamei;
        this.logLabel = logLabel;
        this._args = args;
        this._taggedArgs = taggedArgs;
//        if (Static.sceneManager.getSelected().isEmpty()) {
//            logLabel.setText("Make a selection then left click to continue this command.");
//            SelectionManager.selectionMouseOwner.attach(
//                    listener
//            );
//            return;
//        }
//TODO: check if works
        selectionPreCondition.execute(logLabel);
    }
}
