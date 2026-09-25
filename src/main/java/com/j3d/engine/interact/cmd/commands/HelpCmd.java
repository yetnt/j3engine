package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;
import com.yetnt.utils.builders.InlineHTML;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** A command that provides help information about other commands.
 * <p>
 *     When invoked without arguments, it lists all available top-level commands.
 *     When invoked with a command name, it displays the description, aliases, and usages
 *     for that specific command.
 * </p>
 * <p>
 *     It leverages {@link InlineHTML} to format the output for better readability
 *     in the UI.
 * </p>
 * <p>
 *     Aliases: {@code h}, {@code use}, {@code usage}
 * </p>
 * @see Command
 * @see CommandsManager
 * @see InlineHTML
 * @author Lehlogonolo Poole
 */
public class HelpCmd extends Command {

    public HelpCmd() {
        super("help", "Prints the usage strings and description of a given command");
        this.aliases("h", "use", "usage").args(
                new TypedArg("cmdName", "The command get the usage of",
                        true, String.class)
        ).parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (args.length == 0) {
            // show list of available commands
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new InlineHTML("Available Commands:").bold())
                    .append(InlineHTML.LINE_BREAK).append(InlineHTML.LINE_BREAK);
            // show 10 commands only
            AtomicInteger maxCmd = new AtomicInteger();
            int max = 20;
            ArrayList<Command> commands = getCommands(maxCmd, max, stringBuilder);


            if (maxCmd.get() == max) {
                stringBuilder
                        .append(InlineHTML.LINE_BREAK).append(InlineHTML.LINE_BREAK)
                        .append(new InlineHTML("and like " + (commands.size()-max) + " more...").italic());
            }

            StaticRefs.getHoverLabel().setText(
                    new InlineHTML(stringBuilder.toString()).wrapHTML()
            );
            return;
        }

        if (!(args[0] instanceof String cmdName)) {
            logLabel.setText("Usage: "+aliasUsed+" <cmdName>");
            return;
        }

        Command cmd = StaticRefs.getCommandManager().commandsAliasMap.get(cmdName);

        if (cmd == null) {
            logLabel.setText("No command with the name \"" + cmdName + "\" exists.");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        // top
        stringBuilder.
                append("[")
                .append(new InlineHTML(cmdName).bold())
                .append("] ")
                .append(new InlineHTML(cmd.description).underline());

        stringBuilder.append(InlineHTML.LINE_BREAK).append(InlineHTML.LINE_BREAK);

        // aliases

        stringBuilder.append("Aliases: ");

        cmd.aliases.forEach(
                alias -> stringBuilder.append(alias).append(", ")
        );

        stringBuilder.append(InlineHTML.LINE_BREAK).append(InlineHTML.LINE_BREAK);

        // usages

        cmd.getUsages().forEach(
                usage -> stringBuilder.append(new InlineHTML(cmdName + " " + usage, true).italic())
                        .append(InlineHTML.LINE_BREAK)
        );

        // print

        StaticRefs.getHoverLabel().setText(new InlineHTML(stringBuilder.toString()).wrapHTML());
    }

    private static ArrayList<Command> getCommands(AtomicInteger maxCmd, int max, StringBuilder stringBuilder) {
        ArrayList<Command> commands = CommandsManager.commands.getCommands();
        commands.forEach(
                cmd -> {
                    if (maxCmd.get() < max) maxCmd.getAndIncrement();
                    if (maxCmd.get() == max) return;

                    stringBuilder.append(
                            new InlineHTML(cmd.aliases.getFirst()).bold()
                    ).append(" - ").append(
                            new InlineHTML(cmd.description)
                    )
                            .append(InlineHTML.LINE_BREAK);
                }
        );
        return commands;
    }
}
