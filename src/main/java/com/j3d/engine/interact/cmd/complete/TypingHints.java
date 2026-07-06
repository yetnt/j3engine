package com.j3d.engine.interact.cmd.complete;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.SamePair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TypingHints {

    public static Color COMMAND_NAME_LIEKLY_MATCH = Color.GREEN;
    public static Color COMMAND_NAME_PARTIAL_MATCH = new Color(236, 191, 100);
    int MAX_SUGGESTIONS = 8;

    public TypingHints() {

    }

    public void parse(ArrayList<CmdToken> tokens) {
        Static.commandParser.safeJLabel().clearLower();
        // If the tokens are empty. Do nothing
        if (tokens.isEmpty()) return;

        if (tokens.getFirst().getType() != CmdToken.Type.CMD_NAME) {
            Static.commandParser.safeJLabel().setText(
                    new JLabelRichText("The first argument (command name) is usually a string bro")
                            .italic().wrapHTML()
            );
            return;
        }

        // if there is a single token. it's the command name try find matches.
        if (tokens.size() == 1) {
            CmdToken token = tokens.getFirst();
            SamePair<ArrayList<JLabelRichText>> matches = possibleCommandAliasMatches(token);
            // limit to 5 per likely/partial
            StringBuilder likely = new StringBuilder(), partial = new StringBuilder();
            for (int i = 0; i < MAX_SUGGESTIONS; i++) {
                if (i < matches.first.size())
                    likely.append(matches.first.get(i)).append(" ");
                if (i < matches.second.size())
                    partial.append(matches.second.get(i)).append(" ");
            }

            Static.commandParser.safeJLabel().setLower(
                    JLabelRichText.htmlOf(likely.toString(), partial.toString())
            );

            return;
        }

        String commandAlias = tokens.getFirst().getInput();
        Command command = CommandsManager.getCommand(commandAlias);
        if (command == null) {
            Static.commandParser.safeJLabel().setText(
                    "This shouldn't happen... No command " + commandAlias + " found..."
            );
            return;
        }
        ArrayList<CmdToken> argsList = new ArrayList<>(tokens.subList(1, tokens.size()));
        Class<?>[] classes = argsList
                .stream()
                .map(c -> c.getType().getTypeClass())
                .toArray(Class[]::new);

        String[] usages = command.returnUsagesWhere(
                commandAlias, classes
        );

        // filter each usage by a possible subcommand
        Static.commandParser.safeJLabel().setText(
                new JLabelRichText(command.description).bold().wrapHTML()
        );

        ArrayList<String> usag = findUsages(commandAlias, usages, argsList);

        // Just take the first element

        System.out.println("user typed: " + tokens.toString());
        System.out.println(commandAlias + " has " + usages.length + " usages");
        if (usag.isEmpty()) {
            System.out.println("No narrowed usage found.");
            System.out.println();
            Static.commandParser.safeJLabel().setText(
                    "No usage found...."
            );
            return;
        }
        System.out.println("Narrowed to " + usag.size() + " usages.");
        System.out.println("First Usage: " + usag.getFirst());
        System.out.println();

        // TODO: the rest. Sequel.
    }

    public ArrayList<String> findUsages(String alias, String[] usages, ArrayList<CmdToken> tokens) {
        ArrayList<String> use = new ArrayList<>(List.of(usages));
        boolean firstPass = true;
        for (int i = 0; i < tokens.size(); i++) {
            if (!firstPass && use.isEmpty()) {
                // No matching usage found
                return use;
            }
            CmdToken token = tokens.get(i);
            int finalI = i;
            if (i != 0) firstPass = false;
            use = use.stream().filter(u -> {
                        ArrayList<String> split = Parsing.split(u, ' ');
                        split.removeFirst();
                        if (split.size() <= finalI) return false;
                        String usage = split.get(finalI).trim();
                        return similarTypes(token, usage);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return use;
    }

    public boolean similarTypes(CmdToken token, String usage) {
        // long if-else. unfortunately
        if (usage.startsWith(token.getInput())
                || (usage.contains(token.getInput()) && usage.contains("["))
        ) {
            // string input
            return true;
        } else if (usage.contains("<" + token.getType().toUsage())) {
            // typed argument
            return true;
        } else if (token.getType() == CmdToken.Type.STRING) {
            // check for unfinished or malformed stuff.
            if (usage.contains("vector3") && token.getInput().contains("("))
                return true;
            if (usage.contains("string") && token.getInput().contains("\""))
                return true;
            if (usage.contains("col") && token.getInput().contains("#"))
                return true;
            //TODO: add UUID like syntax
        }
        return false;
    }

//    public void p(String alias, String[] usages, ArrayList<CmdToken> tokens) {
//        ArrayList<SubcommandSearch> expectedSubCommands = find(alias, usages);
//    }
//    private ArrayList<SubcommandSearch> find(String alias, String[] usages) {
//        ArrayList<SubcommandSearch> subcommands = new ArrayList<>();
//        for (int i = 0; i < usages.length; i++) {
//            String u = usages[i].replace(alias + " ", "").trim();
//            ArrayList<String> split = Parsing.split(u, ' ');
//            for (int j = 0; j < split.size(); j++) {
//                String fromUsage = split.get(j);
//                if (
//                        !(fromUsage.contains("[") ||
//                                fromUsage.contains(":") ||
//                                fromUsage.contains("<") ||
//                                fromUsage.contains("?"))
//                )
//                    subcommands.add(new SubcommandSearch(fromUsage, i, j));
//            }
//        }
//        return subcommands;
//    }
//
//    public record SubcommandSearch(
//            String expectedValue,
//            int mainListIndex,
//            int usageIndex
//    ) {
//
//    }

    private SamePair<ArrayList<JLabelRichText>> possibleCommandAliasMatches(CmdToken token) {
        ArrayList<JLabelRichText> likelyMatchesJL = new ArrayList<>();

        String input = token.getInput();
        ArrayList<Command> commands = CommandsManager.commands.getCommands();

        // Get all possible command aliases
        ArrayList<String> commandAliases = commands
                .stream()
                .flatMap(Command::aliasStream)
                .filter( s -> {
                    // filter out aliases who are too short to match.
                    return s.length() >= input.length();
                })
                .collect(Collectors.toCollection(ArrayList::new));

        // Aliases who start with the input
        ArrayList<String> likelyMatches = commandAliases
                .stream()
                .filter(s -> s.startsWith(input))
                .collect(Collectors.toCollection(ArrayList::new));

        // Aliases whose substring contains the input (and isnt in the likelyMatches)
        ArrayList<JLabelRichText> possibleMatches = commandAliases
                .stream()
                .filter(s -> s.contains(input))
                .filter(s -> !likelyMatches.contains(s))
                .map(s -> {
                    // Style.
                    JLabelRichText match = new JLabelRichText(input)
                            .bold().font(COMMAND_NAME_PARTIAL_MATCH);
                    // style the rest (might be before or after)
                    JLabelRichText before = new JLabelRichText(
                            s.substring(0, s.indexOf(input))
                    ).bold();
                    // Abba
                    JLabelRichText after = new JLabelRichText(
                            s.substring(s.indexOf(input) + input.length())
                    ).bold();
                    return new JLabelRichText(
                            before.toString() + match.toString() + after.toString());
                })
                .collect(Collectors.toCollection(ArrayList::new));

        likelyMatches.forEach(s -> {
            // Style.
            JLabelRichText match = new JLabelRichText(input)
                    .bold().font(COMMAND_NAME_LIEKLY_MATCH);
            // rest of alias name
            JLabelRichText rest = new JLabelRichText(
                    s.substring(input.length())
            ).bold();
            likelyMatchesJL.add(new JLabelRichText(match.toString() + rest));
        });

        return new SamePair<>(likelyMatchesJL, possibleMatches);
    }

    public Consumer<ArrayList<CmdToken>> onTabComplete() {
        return (tokens) -> {
            // If the tokens are empty. Do nothing
            if (tokens.isEmpty()) return;

            // if there is a single token. its the command name try find matches.
            if (tokens.size() == 1) {
                CmdToken token = tokens.getFirst();

                if (token.getType() != CmdToken.Type.CMD_NAME)
                    return;

                String alias = token.getInput();

                // longer aliases sort higher.
                String longestMatchedAlias = CommandsManager.commands.getCommands()
                        .stream()
                        .flatMap(Command::aliasStream)
                        .filter(
                                s -> s.startsWith(alias)
                        )
                        .min((s1, s2) -> s2.length() - s1.length())
                        .orElse(alias);


                Static.commandParser.setInputField(
                        longestMatchedAlias
                );
            }
        };
    }
}
