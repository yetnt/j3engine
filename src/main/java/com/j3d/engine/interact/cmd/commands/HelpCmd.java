package com.j3d.engine.interact.cmd.commands;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.generators.JLabelRichText;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** A command that provides help information about other commands.
 * <p>
 *     When invoked without arguments, it lists all available top-level commands.
 *     When invoked with a command name, it displays the description, aliases, and usages
 *     for that specific command.
 * </p>
 * <p>
 *     It leverages {@link JLabelRichText} to format the output for better readability
 *     in the UI.
 * </p>
 * <p>
 *     Aliases: {@code h}, {@code use}, {@code usage}
 * </p>
 * @see Command
 * @see CommandsManager
 * @see JLabelRichText
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
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length == 0) {
            // show list of available commands
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new JLabelRichText("Available Commands:").bold())
                    .append(JLabelRichText.LINE_BREAK).append(JLabelRichText.LINE_BREAK);
            // show 10 commands only
            AtomicInteger maxCmd = new AtomicInteger();
            ArrayList<Command> commands = getCommands(maxCmd, stringBuilder);

            if (maxCmd.get() == 10) {
                stringBuilder
                        .append(JLabelRichText.LINE_BREAK).append(JLabelRichText.LINE_BREAK)
                        .append(new JLabelRichText("and like " + (commands.size()-10) + " more...").italic());
            }

            Static.hoverLabel.setText(
                    new JLabelRichText(stringBuilder.toString()).wrapHTML()
            );
            return;
        }

        if (!(args[0] instanceof String cmdName)) {
            logLabel.setText("Usage: "+aliasUsed+" <cmdName>");
            return;
        }

        Command cmd = Static.commandManager.commandsAliasMap.get(cmdName);

        if (cmd == null) {
            logLabel.setText("No command with the name \"" + cmdName + "\" exists.");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        // top
        stringBuilder.
                append("[")
                .append(new JLabelRichText(cmdName).bold())
                .append("] ")
                .append(new JLabelRichText(cmd.description).underline());

        stringBuilder.append(JLabelRichText.LINE_BREAK).append(JLabelRichText.LINE_BREAK);

        // aliases

        stringBuilder.append("Aliases: ");

        cmd.aliases.forEach(
                alias -> stringBuilder.append(alias).append(", ")
        );

        stringBuilder.append(JLabelRichText.LINE_BREAK).append(JLabelRichText.LINE_BREAK);

        // usages

        cmd.getUsages().values().forEach(
                usage -> stringBuilder.append(new JLabelRichText(cmdName + " " + usage, true).italic())
                        .append(JLabelRichText.LINE_BREAK)
        );

        // print

        Static.hoverLabel.setText(new JLabelRichText(stringBuilder.toString()).wrapHTML());
    }

    private static ArrayList<Command> getCommands(AtomicInteger maxCmd, StringBuilder stringBuilder) {
        ArrayList<Command> commands = CommandsManager.commands.getCommands();
        commands.forEach(
                cmd -> {
                    if (maxCmd.get() < 10) maxCmd.getAndIncrement();
                    if (maxCmd.get() == 10) return;

                    stringBuilder.append(
                            new JLabelRichText(cmd.aliases.getFirst()).bold()
                    ).append(" - ").append(
                            new JLabelRichText(cmd.description)
                    )
                            .append(JLabelRichText.LINE_BREAK);
                }
        );
        return commands;
    }
}
