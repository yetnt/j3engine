package com.j3d.engine.interact.cmd.complete;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.SamePair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TypingHints {

    public static Color COMMAND_NAME_LIEKLY_MATCH = Color.GREEN;
    public static Color COMMAND_NAME_PARTIAL_MATCH = new Color(236, 191, 100);

    public static Color EXACT_MATCH = new Color(154, 232, 57);
    public static Color PARTIAL_MATCH = new Color(222, 121, 0);
    public static Color INCORRECT_TYPE = new Color(255, 0, 0);
//
//    public static JLabelRichText incorrectStyle = new JLabelRichText()
//            .font(INCORRECT_TYPE).italic().underline();

    int MAX_SUGGESTIONS = 8;

    public TypingHints() {

    }

    public void parse(ArrayList<CmdToken> init, boolean endsWithSpace) {
        ArrayList<CmdToken> tokens = init
                .stream()
                .filter(
                        c ->
                                // tagged args and unfinished tagged args
                                c.getType() != CmdToken.Type.TAGGED && c.getType() != null
                )
                .collect(Collectors.toCollection(ArrayList::new));
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
        if (tokens.size() == 1 && !endsWithSpace) {
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
                .map(c -> {
                    CmdToken.Type clazz = c.getType();
                    if (clazz == CmdToken.Type.STRING && c.getInput().startsWith("(") && !c.getInput().endsWith(")"))
                        return Vector3.class;
                    if (clazz == CmdToken.Type.STRING && c.getInput().startsWith("#"))
                        return Color.class;
                    return c.getType().getTypeClass();
                })
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
        Static.commandParser.safeJLabel().setLower(
                colourUsage(usag.getFirst(), tokens).wrapHTML()
        );
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
                        if (usage.contains("any"))
                            System.out.println("picle");
                        return similarTypes(token, usage);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return use;
    }

    public boolean similarTypes(CmdToken token, String usage) {
        // long if-else. unfortunately
        if (usage.contains("any")) {
            return true;
        } else if (usage.startsWith(token.getInput())
                || (usage.contains(token.getInput()) && usage.contains("["))
        ) {
            // string input
            return true;
        } else if (usage.contains("<" + token.getType().toUsage())) {
            // typed argument
            return true;
        } else if (usage.contains("number") &&
                token.getType().getTypeClass().isAssignableFrom(Number.class)) {
            return true;
        } else if (token.getType() == CmdToken.Type.STRING) {
            // check for unfinished or malformed stuff.
            if (usage.contains("vector") && token.getInput().contains("("))
                return true;
            else if (usage.contains("string") && token.getInput().contains("\""))
                return true;
            else if (usage.contains("col") && token.getInput().contains("#"))
                return true;
            else if (usage.contains("bool") && (
                    token.getInput().startsWith("t") || token.getInput().startsWith("f") ||
                            token.getInput().startsWith("y") || token.getInput().startsWith("a")
                    ) )
                return true;
            else if (
                    (usage.contains("point")
                            || usage.contains("line")
                            || usage.contains("tri")
                            || usage.contains("thing"))
                    && token.getInput().length() > 5//TODO: add UUID like syntax
            )
                return true;
        }
        return false;
    }

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

    public JLabelRichText colourUsage(String usage, ArrayList<CmdToken> tokens) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> args = Parsing.split(usage, ' ');
        args.removeLast(); // tagged arg

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i).trim();
            CmdToken token = tokens.size() > i ? tokens.get(i) : null;
            // argument not given yet.
            if (token == null) {
                sb.append(notGivenYet(arg)).append(" ");
                continue;
            }

            // check for typed arguments
            // <string> <boolean> <vector3> <number> <int> <thing> <point> <tri> <line> <#color#> <>
            //

            if (arg.contains("<")) {
                if (arg.contains("any")) {
                    // all types accepted.
                    sb.append(correctType(arg)).append(" ");
                } else if (arg.contains("vector")) {
                    // vector3
                    sb.append(vector3(arg, token)).append(" ");
                } else if (arg.contains("bool")) {
                    // boolean
                    sb.append(boolMatch(arg, token)).append(" ");
                } else if (arg.contains("col")) {
                    // colour.
                    sb.append(colourMatch(arg, token)).append(" ");
                } else if (arg.contains("ing") || arg.contains("number")) {
                    // int or double
                    sb.append(numberMatch(arg, token)).append(" ");
                } else if (usage.contains("point")
                        || usage.contains("line")
                        || usage.contains("tri")
                        || usage.contains("thing")) {
                    // uuid reference
                    sb.append(idReferenceMatch(arg, token)).append(" ");
                } else if (usage.contains("string")) {
                    // now it actually accepts a string
                    sb.append(
                            token.getType() != CmdToken.Type.STRING
                                    ? incorrectType(arg) // an unfinished string still parses itself as a string.
                                    : correctType(arg)
                    ).append(" ");
                } else {
                    throw new UnsupportedOperationException(
                            "Seems the command usage definitions and typing hints aren't up to date. " +
                                    usage
                    );
                }
                continue;
            } else {
                // the input has to undoubtedly be a stirng.
                if (token.getType() == CmdToken.Type.CMD_NAME) {
                    sb.append(new JLabelRichText(arg).font(COMMAND_NAME_LIEKLY_MATCH).bold().underline()).append(" ");
                    continue;
                }
                if (token.getType() != CmdToken.Type.STRING) {
                    sb.append(incorrectType(arg)).append(" ");
                    continue;
                }
                if (arg.contains("[")) {
                    // arg set
                    sb.append(argSetMatch(arg, token, i == tokens.size() - 1)).append(" ");
                } else {
                    // it has to be a subcommand
                    // TODO: somehow get other subcommand aliases
                    // just partially match, dont give exact matches incase subcommand alias.
                    // and since subcommand aliaes really can just be anything. just partial
                    // match and hope for the best
                    sb.append(correctType(arg).italic()).append(" ");
                }
            }
        }
        // remove last space
        sb.deleteCharAt(sb.length() - 1);
        return new JLabelRichText(sb.toString());
    }

    private JLabelRichText notGivenYet(String arg) {
        JLabelRichText jLabelRichText = new JLabelRichText(arg, true)
                .italic();
        if (!arg.contains("?") || !arg.contains("<")) jLabelRichText.bold();
        return jLabelRichText;
    }

    private JLabelRichText argSetMatch(String arg, CmdToken token, boolean lastToken) {
        // remove braces
        arg = arg.substring(1, arg.length() - 1);
        // get each accepted value
        ArrayList<String> acceptedValues = Parsing.split(arg, '|');
        // get first value that's an exact match.
        String exactMatch = acceptedValues
                .stream()
                .filter(s -> s.equals(token.getInput()))
                .findFirst()
                .orElse(null);
        if (exactMatch != null) {
            // return that exact match
            return correctType(exactMatch);
        }

        // otherwise return all input that partially matches.
        // check first though.
        ArrayList<String> partialMatches = acceptedValues
                .stream()
                .filter(s -> s.startsWith(token.getInput()))
                .collect(Collectors.toCollection(ArrayList::new));
        if (!partialMatches.isEmpty()) {
            // build a string
            StringBuilder stringBuilder = new StringBuilder().append("[");
            partialMatches.forEach(p -> {
                stringBuilder.append(partialType(p)).append(", ");
            });
            // remove last space and comma
            stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            stringBuilder.append("]");
            return new JLabelRichText(stringBuilder.toString());
        }

        return incorrectType(arg);
    }

    private JLabelRichText idReferenceMatch(String arg, CmdToken token) {
        if (token.getType() == CmdToken.Type.STRING) {
            // check if its maybe like a uuid
            String regex =
                    "^[0-9a-fA-F]{8}-" +
                            "[0-9a-fA-F]{4}-" +
                            "[0-9a-fA-F]{4}-" +
                            "[0-9a-fA-F]{4}-" +
                            "[0-9a-fA-F]{12}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(token.getInput());

            return matcher.lookingAt() ? partialType(arg) : incorrectType(arg);
        } else {
            // it works. im not changing it. damn you if you dare change
            // this beautiful piece of code.
            return switch (token.getType().toUsage().substring(0, 2) + arg.substring(1, 3)) {
                case "popo", "trtr", "thth", "lili" -> correctType(arg);
                default -> incorrectType(arg);
            };
        }
    }

    private JLabelRichText numberMatch(String arg, CmdToken token) {
        // check all posible cases.
        if (token.getType() != CmdToken.Type.INT && token.getType() != CmdToken.Type.DOUBLE) {
            // incorrect.
            return incorrectType(arg);
        }

        // Now all 4 cases
        // arg expects an int
        if (arg.contains("int")) {
            if (token.getType() != CmdToken.Type.INT)
                return incorrectType(arg);
            return correctType(arg);
        } else if (arg.contains("number")) {
            // both int and double are accepted. (int we be converted to a double)
            return correctType(arg);
        }

        // how do we even get here.
        return incorrectType(arg);

    }

    private JLabelRichText colourMatch(String arg, CmdToken token) {
        if (token.getType() == CmdToken.Type.STRING && token.getInput().startsWith("#")) {
            // unfinished colour. Although it has to start with #
            return partialType(arg);
        } else if (token.getType() == CmdToken.Type.COLOUR) {
            // correct
            return correctType(arg).font(EXACT_MATCH, "", (Color) token.getParsedValue());
        } else {
            return incorrectType(arg);
        }
    }

    private JLabelRichText boolMatch(String arg, CmdToken token) {
        ArrayList<String> validBools =
                new ArrayList<>(List.of(
                        "yebo", "aowa", "true", "false"
                ));
        if (token.getType() == CmdToken.Type.STRING) {
            // check if the current input start with the boolean.
            if (validBools.stream()
                    .anyMatch(s -> s.startsWith(token.getInput()))
            ) {
                return partialStringMatch(validBools, token.getInput());
            } else {
                return incorrectType(arg);
            }
        } else if (token.getType() == CmdToken.Type.BOOL) {
            // correct
            return correctType(arg);
        } else {
            return incorrectType(arg);
        }
    }

    private JLabelRichText vector3(String arg, CmdToken token) {
        if (token.getType() == CmdToken.Type.STRING) {
            // vector3 in progress
            return partialType(arg);
        } else if (token.getType() == CmdToken.Type.VECTOR3) {
            // correct
            return correctType(arg);
        } else {
            return incorrectType(arg);
        }
    }

    private JLabelRichText correctType(String arg) {
        JLabelRichText jLabelRichText = new JLabelRichText(arg, true);
        return jLabelRichText
                .bold()
                .font(EXACT_MATCH);
    }

    private JLabelRichText partialType(String arg) {
        return new JLabelRichText(arg, true).italic().font(PARTIAL_MATCH);
    }

    private JLabelRichText incorrectType(String arg) {
        return new JLabelRichText(arg, true).italic().font(INCORRECT_TYPE);
    }

    private JLabelRichText partialStringMatch(ArrayList<String> options, String input) {
        // guaranteed at least one option partially matches.
        String bestMatch = options
                .stream()
                .filter(s -> s.startsWith(
                        input
                ))
                .min((s1, s2) -> s2.length() - s1.length())
                .orElse(null);
        if (bestMatch == null) {
            System.out.println("something....");
            // TODO: something.....
            return new JLabelRichText(input);
        }
        // Style.
        JLabelRichText match = partialType(input);
        // rest of alias name
        JLabelRichText rest = new JLabelRichText(
                bestMatch.substring(input.length())
        ).italic();
        return new JLabelRichText(match.toString() + rest);
    }

    private JLabelRichText isColour(JLabelRichText common, CmdToken actual) {
        return common;
    }
}
