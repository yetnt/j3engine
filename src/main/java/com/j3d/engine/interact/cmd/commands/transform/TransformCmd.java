package com.j3d.engine.interact.cmd.commands.transform;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.base.conditions.SelectionPreCondition;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.selection.SelectionManager;

import java.util.ArrayList;

/**
 * A very complicated command which is a dispatcher for its specific transform subcommands.
 * <p>
 *     This command, before dispatching to it's 3 possible subcommands requires that a selection be made
 *     first (enforced by {@link SelectionPreCondition}, making the command {@link SemiStatefulCommand}
 *     as after a selection is made it's "stateful" label is released.
 * </p>
 * <p>
 *     The command does nothing more but setup inital state. The logic lies within the subcommands.
 *     {@link RotateSelection}, {@link TranslateSelection} and {@link ScaleSelection}
 * </p>
 * <p>
 *     Aliases: {@code selection}, {@code trans}, {@code t}, {@code tr}
 * </p>
 * @see SelectionPreCondition
 * @see SemiStatefulCommand
 * @see RotateSelection
 * @see TranslateSelection
 * @see ScaleSelection
 * @see CommandsManager
 * @see CommandParser
 * @see Command
 * @author Lehlogonolo Poole
 */
public class TransformCmd extends Command implements SemiStatefulCommand {

    public TransformCmd() {
        super("transform", "Transform a selection of objects");
        this.aliases("trans", "t", "tr", "selection").args(
                new RotateSelection(),
                new TranslateSelection(),
                new ScaleSelection()
        ).parseUsages();
//        this.usages.put(
//                new ArrayList<>(List.of(String.class, String.class)),
//                " [scale|translate] [p|v|t|f] ...key:value");
    }

    private SelectionPreCondition selectionPreCondition;
    private String subcommandName;
    private SafeJLabel logLabel;
    private Object[] _args;
    private ArrayList<TaggedArgValue<?>> _taggedArgs;

    @Override
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
                    selectionPreCondition.finaliseCleanup();
                    SelectionManager.selectionMouseOwner.clearSelectionSquare();
                    dispatchToSubcommands(subcommandName, logLabel, _args, _taggedArgs);
                },
                () -> {
                    CommandsManager.clearCurrent();
                    SelectionManager.selectionMouseOwner.clearSelectionSquare();
                }
        );
        StaticRefs.getMainFrame().requestFocusInWindow(); // Remove focus from the command pallete
        this.subcommandName = subcommandNamei;
        this.logLabel = logLabel;
        this._args = args;
        this._taggedArgs = taggedArgs;
        if (selectionPreCondition.execute(logLabel)) {
            selectionPreCondition.finaliseCleanup();
        }
    }
}
