package com.j3d.engine.interact.cmd.complete;

import com.j3d.StaticRefs;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.engine.scene.nodes.geometry.GObjectRegistry;
import com.j3d.engine.scene.nodes.geometry.GTri;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.func.QuadConsumer;
import com.j3d.utility.generic.SamePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Provides real-time typing hints, suggestions, and visual feedback for the command palette input.
 * It analyses user input (tokens) to suggest command names, validate argument types, and highlight
 * potential errors or matches. This includes tab completion for command names, argument type checking
 * (e.g., Vector3, Colour, UUID, numbers, booleans, arg sets), and styling of usage strings to indicate
 * correctness or partial matches.
 * @author Lehlogonolo Poole
 * @see CommandParser
 * @see CommandPalette
 * @see CmdToken
 * @see CommandsManager
 * @see SafeJLabel
 * @see JLabelRichText
 */
public class TypingHints {

    /**
     * Colour used for command names that are considered "likely matches" (e.g., start with the input).
     */
    public static Color CMDNAME_LIEKLY_MATCH = Color.GREEN;
    /**
     * Colour used for command names that are considered "partial matches" (e.g., contain the input but don't start with it).
     */
    public static Color CMDNAME_PARTIAL_MATCH = new Color(236, 191, 100);

    /**
     * Colour used for arguments that are an exact match in type and value.
     */
    public static Color EXACT_MATCH = new Color(154, 232, 57);
    /**
     * Colour used for arguments that are a partial match or unfinished but potentially correct.
     */
    public static Color PARTIAL_MATCH = new Color(222, 121, 0);
    /**
     * Colour used for arguments that are of an incorrect type or value.
     */
    public static Color INCORRECT_TYPE = new Color(255, 0, 0);

    /**
     * The maximum number of command name suggestions to display for both likely and partial matches.
     */
    int MAX_CMDNAME_SUGGESTIONS = 10;
    /**
     * A flag indicating whether an error related to tagged arguments has occurred.
     * This is used to prevent other hints from overriding the tagged argument error message.
     */
    private boolean taggedArgErr = false;

    public TypingHints() {

    }

    /**
     * Parses the given command tokens and provides typing hints or suggestions based on the current input.
     * This method updates the {@link com.j3d.engine.interact.cmd.CommandParser#safeJLabel()} with relevant
     * information, such as command matches, usage hints, or error messages.
     * @param init The initial list of {@link CmdToken} objects, including any tagged arguments, representing the user's input.
     * @param endsWithSpace A boolean indicating whether the user's input currently ends with a space.
     */
    public void parse(ArrayList<CmdToken> init, boolean endsWithSpace) {
        setOptions(new ArrayList<>());
        ArrayList<CmdToken> tokens = init
                .stream()
                .filter(
                        c ->
                                // tagged args and unfinished tagged args
                                c.getType() != CmdToken.Type.TAGGED && c.getType() != null
                )
                .collect(Collectors.toCollection(ArrayList::new));
        StaticRefs.getCommandParser().safeJLabel().clearLower();
        // If the tokens are empty. Do nothing
        if (tokens.isEmpty()) return;

        if (tokens.getFirst().getType() != CmdToken.Type.CMD_NAME) {
            StaticRefs.getCommandParser().safeJLabel().setText(
                    new JLabelRichText("The first argument (command name) is usually a string bro")
                            .italic().wrapHTML()
            );
            return;
        }

        // if there is a single token. it's the command name try finder matches.
        if (tokens.size() == 1 && !endsWithSpace) {
            CmdToken token = tokens.getFirst();
            SamePair<ArrayList<JLabelRichText>> matches = possibleCommandAliasMatches(token);
            // limit to 5 per likely/partial
            StringBuilder likely = new StringBuilder(), partial = new StringBuilder();
            for (int i = 0; i < MAX_CMDNAME_SUGGESTIONS; i++) {
                if (i < matches.first.size())
                    likely.append(matches.first.get(i)).append(" ");
                if (i < matches.second.size())
                    partial.append(matches.second.get(i)).append(" ");
            }

            StaticRefs.getCommandParser().safeJLabel().setLower(
                    new JLabelRichText(likely + " " + partial)
                            .font("4")
                            .wrapHTML(),
                    20
            );
            // repaint cuz wtf is happening.
            StaticRefs.getCommandParser().safeJLabel().repaint();

            return;
        }

        String commandAlias = tokens.getFirst().getInput();
        Command command = CommandsManager.getCommand(commandAlias);
        if (command == null) {
            StaticRefs.getCommandParser().safeJLabel().setText(
                    "This shouldn't happen... No command " + commandAlias + " found..."
            );
            return;
        }
        ArrayList<CmdToken> argsList = new ArrayList<>(tokens.subList(1, tokens.size()));
        ArrayList<String> usages = command.usages(commandAlias);

        if (!taggedArgErr)
            StaticRefs.getCommandParser().safeJLabel().setText(
                    new JLabelRichText(command.description).bold().wrapHTML(),
                    20
            );

        ArrayList<String> usag = findUsages(commandAlias, usages, argsList);

        // Just take the first element
        if (usag.isEmpty()) {
            StaticRefs.getCommandParser().safeJLabel().setLower(
                    "No usage found.... Try removing some characters"
            );
            return;
        }

        StaticRefs.getCommandParser().safeJLabel().setLower(
                colourTaggedArgs(
                        colourGivenUsage(usag.getFirst(), tokens),
                        command,
                        init
                ).wrapHTML(),
                20
        );
    }

    /**
     * Filters a list of command usages based on the provided command tokens.
     * It iteratively narrows down the possible usages by checking if each token's type is compatible
     * with the corresponding argument in the usage string.
     * @param alias The alias of the command being used.
     * @param usages An array of all possible usage strings for the command.
     * @param tokens An {@link ArrayList} of {@link CmdToken} objects representing the user's input arguments.
     * @return An {@link ArrayList} of {@link String} containing the usage strings that are compatible with the given tokens.
     */
    public ArrayList<String> findUsages(String alias, ArrayList<String> usages, ArrayList<CmdToken> tokens) {
        ArrayList<String> use = new ArrayList<>(usages);
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

    /**
     * Determines if a given command token's type is compatible with a specified usage string.
     * This method checks for various type matches, including generic ("any"), string, typed arguments, numbers, and specific object references (UUIDs).
     * @param token The {@link CmdToken} representing the user's input argument.
     * @param usage The expected usage string for an argument, e.g., {@code <string>},
     *              {@code <int>}, {@code [option1|option2]}, or {@code <vector3>}.
     * @return {@code true} if the token's type is similar or compatible with the usage string, {@code false} otherwise.
     */
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
            else if ((GObjectRegistry.fuzzyMatch(usage) || usage.contains("thing"))
                    && token.getInput().length() > 5//TODO: add UUID like syntax
            )
                return true;
        }
        return false;
    }

    /**
     * Finds and styles possible command alias matches based on the given command token.
     * It categorises matches into "likely" (aliases starting with the input) and "partial"
     * (aliases containing the input but not starting with it), and styles them accordingly.
     * @param token The {@link CmdToken} representing the user's current input for a command name.
     * @return A {@link SamePair} containing two {@link ArrayList}s of {@link JLabelRichText}.
     *         The first list contains likely matches, and the second contains partial matches.
     */
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

        ArrayList<String> all = new ArrayList<>(likelyMatches);

        // Aliases whose substring contains the input (and isnt in the likelyMatches)
        ArrayList<JLabelRichText> possibleMatches = commandAliases
                .stream()
                .filter(s -> s.contains(input))
                .filter(s -> !likelyMatches.contains(s))
                .peek(all::add)
                .map(s -> {
                    // Style.
                    JLabelRichText match = new JLabelRichText(input)
                            .bold().font(CMDNAME_PARTIAL_MATCH);
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
                    .bold().font(CMDNAME_LIEKLY_MATCH);
            // rest of alias name
            JLabelRichText rest = new JLabelRichText(
                    s.substring(input.length())
            ).bold();
            likelyMatchesJL.add(new JLabelRichText(match.toString() + rest));
        });

        setOptions(all);

        return new SamePair<>(likelyMatchesJL, possibleMatches);
    }

    /**
     * Provides a {@link QuadConsumer} that handles tab completion logic for the command palette.
     * This consumer attempts to complete the current command name or argument based on available
     * aliases and suggestions. If no specific completion is found, it delegates to the default
     * {@link Action}.
     * @return A {@link QuadConsumer} that takes:
     *         <ul>
     *             <li>An {@link ArrayList} of {@link CmdToken} representing the current input.</li>
     *             <li>The default {@link Action} to perform if no completion is possible.</li>
     *             <li>The {@link ActionEvent} that triggered the completion.</li>
     *             <li>An {@link ArrayList} of {@link String} containing potential completion options.</li>
     *         </ul>
     */
    public QuadConsumer<ArrayList<CmdToken>,
                Action, ActionEvent, ArrayList<String>> onTabComplete() {
        return (tokens, action, actionEvent, opts) -> {
            // If the tokens are empty. Do nothing
            if (tokens.isEmpty()) {
                action.actionPerformed(actionEvent);
                return;
            }

            // if there is a single token. its the command name try finder matches.
            // (separate to prioritise startsWith over contains)
            if (tokens.size() == 1) {
                CmdToken token = tokens.getFirst();

                if (token.getType() != CmdToken.Type.CMD_NAME) {
                    action.actionPerformed(actionEvent);
                    return;
                }

                String alias = token.getInput();

                // longer aliases sort higher.
                String longestMatchedAlias = CommandsManager.commands.getCommands()
                        .stream()
                        .flatMap(Command::aliasStream)
                        .filter(
                                s -> s.contains(alias)
                        )
                        .sorted((s1, s2) -> s2.length() - s1.length())
                        .min((s1, s2) -> {
                            // string that start with sorted over ones that only contain
                            if (s1.startsWith(alias)) return -1;
                            if (s2.startsWith(alias)) return 1;
                            return 0;
                        })
                        .orElse(alias);


                StaticRefs.getCommandParser().setInputField(
                        longestMatchedAlias + " " // Space so the typing hint can kick in.
                );

                return;
            } else if (!opts.isEmpty()) {
                // Get what the user currently typed
                ArrayList<CmdToken> toks = new ArrayList<>(tokens);
                // sort opts so startsWith matches first
                String match = opts.stream().min(
                        (s1, s2) -> {
                            if (s1.startsWith(toks.getLast().getInput())) return -1;
                            if (s2.startsWith(toks.getLast().getInput())) return 1;
                            return 0;
                        }
                ).orElse(null);
                toks.removeLast();

                StaticRefs.getCommandParser().setInputField(
                        CmdToken.toStr(toks) + " " + match + " " // Space so the typing hint can kick in.
                );
            }
            action.actionPerformed(actionEvent);
        };
    }

    /**
     * Appends a hint for tagged arguments to the given {@link JLabelRichText} based on the command's
     * support for tagged arguments and whether any tagged arguments are present in the input.
     * @param rich The current {@link JLabelRichText} to append to.
     * @param command The {@link Command} for which the hints are being generated.
     * @param init The initial list of {@link CmdToken}s, including any tagged arguments.
     * @return The modified {@link JLabelRichText} with the tagged argument hint appended and styled.
     */
    private JLabelRichText colourTaggedArgs(JLabelRichText rich, Command command, ArrayList<CmdToken> init) {
        if (init.stream()
                .anyMatch(tk -> tk.getType() == CmdToken.Type.TAGGED)) {
            return new JLabelRichText(
                    rich.toString() + (
                            command.hasNoArgs() || command.varTaggedArgs()
                                    ? partialType(" ...key:value")
                            : incorrectType(" ...key:value"))
            );
        } else if (command.hasNoArgs() || command.varTaggedArgs()) {
            return rich.add(" ...key:value");
        }
        return rich;
    }

    /**
     * Styles a given command usage string based on the provided user input tokens.
     * This method provides visual feedback (colors) to indicate whether each argument
     * in the usage string is correctly typed, partially matched, or incorrectly typed
     * by the user.
     * @param usage The expected command usage string, e.g., "mycommand <string> [option1|option2] <vector3>".
     * @param tokens An {@link ArrayList} of {@link CmdToken} representing the user's parsed input.
     * @return A {@link JLabelRichText} object containing the styled usage string.
     */
    public JLabelRichText colourGivenUsage(String usage, ArrayList<CmdToken> tokens) {
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
                } else if (arg.contains("int") || arg.contains("number")) {
                    // int or double
                    sb.append(numberMatch(arg, token)).append(" ");
                } else if (
                        (GObjectRegistry.fuzzyMatch(usage) && !usage.contains("string"))
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
                    sb.append(new JLabelRichText(arg).font(CMDNAME_LIEKLY_MATCH).bold().underline()).append(" ");
                    continue;
                }
                if (token.getType() != CmdToken.Type.STRING) {
                    sb.append(incorrectType(arg)).append(" ");
                    continue;
                }
                if (arg.contains("[")) {
                    // arg set
                    sb.append(argSetMatch(arg, token)).append(" ");
                } else {
                    // it has to be a subcommand
                    // just partially match, dont give exact matches incase subcommand alias.
                    // and since subcommand aliaes really can just be anything. just partial
                    // match and hope for the best
                    sb.append(partialStringMatch(new ArrayList<>(List.of(arg)), token.getInput()).italic()).append(" ");
                }
            }
        }
        // remove last space
        sb.deleteCharAt(sb.length() - 1);
        return new JLabelRichText(sb.toString());
    }

    /**
     * Type checks a given {@link String} value as to be at least partially or fully matched
     * within the given argument set.
     * @param arg The expected arg string, which is {@code [value1|value2|value3]} and defines the
     *            accepted values.
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument.
     * This is either, the single fully matched value, a list of partially matched values or otherwise
     * incorrect.
     */
    private JLabelRichText argSetMatch(String arg, CmdToken token) {
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
        setOptions(partialMatches);
        if (!partialMatches.isEmpty()) {
            // build a string
            StringBuilder stringBuilder = new StringBuilder().append("[");
            partialMatches.forEach(p -> {
                stringBuilder.append(partialStringMatch(partialMatches, token.getInput())).append(", ");
            });
            // remove last space and comma
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append("]");
            return new JLabelRichText(stringBuilder.toString());
        }

        return incorrectType(arg);
    }

    /**
     * Type checks a given value as to expect a {@link UUID} which has to reference a {@link GObject}
     * or {@link Thing}
     * @param arg The expected arg string, which is {@code <point>}, {@code <line>}, {@code <tri>}, {@code <thing>},
     *            {@code <point?>}, {@code <line?>}, {@code <tri?>} or {@code <thing?>}
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument.
     * @implNote Even if the given {@link UUID} is valid, if it is an ID which belongs to something
     * different from what the arg expects, e.g. the user giving a {@link GTri}'s id but the arg expects
     * {@code <point>}, then this is coloured incorrectly.
     */
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
            String input = token.getType().toUsage().substring(0, 2) + arg.substring(1, 3);
            if (GObjectRegistry.tHintsUsageStringTypeMatch(input))
                return correctType(arg);
            else
                return incorrectType(arg);
        }
    }

    /**
     * Type checks a given value as to expect an {@link Integer} or {@link Double}
     * @param arg The expected arg string, which is {@code <int>}, {@code <int?>}, {@code <number>} or {@code <number?>}
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument
     */
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

    /**
     * Type checks a given value as to expect a {@link Color}
     * @param arg The expected arg string, which is {@code <color>} or {@code <color?>}
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument.
     * This goes the extra mile by colouring the background of the rich text to be the
     * given input colour as to tell the user the input they gave.
     */
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

    /**
     * Type checks a given value as to expect a {@link Boolean}
     * @param arg The expected arg string, which is {@code <boolean>} or {@code <boolean?>}
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument. This is either
     * a fully matched boolean, or a partially matched boolean. Otherwise it is incorrect.
     */
    private JLabelRichText boolMatch(String arg, CmdToken token) {
        ArrayList<String> validBools =
                new ArrayList<>(List.of(
                        "yebo", "aowa", "true", "false", "yes", "no"
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
            return correctType(token.getInput());
        } else {
            return incorrectType(arg);
        }
    }

    /**
     * Type checks a given value as to expect a {@link Vector3}
     * @param arg The expected arg string, which is {@code <vector3>} or {@code <vector3?>}
     * @param token The token to check against
     * @return A styled {@link JLabelRichText} which colours the given expected argument
     */
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

    /**
     * Colours the given type as fully correct.
     * @param arg The expected arg string, like {@code <vector3>} or {@code [p|r|e]}
     * @return A styled {@link JLabelRichText} which colours the given expected argument
     */
    private JLabelRichText correctType(String arg) {
        JLabelRichText jLabelRichText = new JLabelRichText(arg, true);
        return jLabelRichText
                .bold()
                .font(EXACT_MATCH);
    }

    /**
     * Colours the given type as partially correct.
     * Partial matches are usually those who aren't a fully enclosed typed and get parsed as a string.
     * These include:
     * <ul>
     *     <li>{@link Color} {@code #...}</li>
     *     <li>{@link Vector3} {@code (...}</li>
     *     <li>{@link UUID} {@code <uuid like string>}</li>
     *     <li>{@link }</li>
     * </ul>
     * @param arg The expected arg string, like {@code <vector3>} or {@code [p|r|e]}
     * @return A styled {@link JLabelRichText} which colours the given expected argument
     * {@link #PARTIAL_MATCH}
     */
    private JLabelRichText partialType(String arg) {
        return new JLabelRichText(arg, true).italic().font(PARTIAL_MATCH);
    }

    /**
     * Colours the given type as incorrect
     * @param arg The expected arg string, like {@code <vector3>} or {@code [p|r|e]}
     * @return A styled {@link JLabelRichText} which colours the given expected argument
     * {@link #INCORRECT_TYPE}
     */
    private JLabelRichText incorrectType(String arg) {
        return new JLabelRichText(arg, true).italic().font(INCORRECT_TYPE);
    }

    /**
     * Styles a given input as generic (The user has not given this input yet.)
     * @param arg The input to style
     * @return A {@link JLabelRichText} object containing the styled input
     */
    private JLabelRichText notGivenYet(String arg) {
        JLabelRichText jLabelRichText = new JLabelRichText(arg, true)
                .italic();
        if (!arg.contains("?") || !arg.contains("<")) jLabelRichText.bold();
        return jLabelRichText;
    }

    /**
     * @implSpec This method expects that there is at least one match already.
     * Given a list of options, finder the first match and colour it partially.
     * @param options The list of options
     * @param input The input the user gave
     * @return A styled {@link JLabelRichText} consisting of a partially coloured match.
     */
    private JLabelRichText partialStringMatch(ArrayList<String> options, String input) {
        if (options.size() > 1) {
            setOptions(options);
        }
        // guaranteed at least one option partially matches.
        String bestMatch = options
                .stream()
                .filter(s -> s.startsWith(
                        input
                ))
                .min((s1, s2) -> s2.length() - s1.length())
                .orElse(null);
        if (bestMatch == null) {
            return incorrectType(options.getFirst());
        }
        if (bestMatch.length() == input.length()) {
            return correctType(bestMatch);
        }
        // Style.
        JLabelRichText match = partialType(input);
        // rest of alias name
        JLabelRichText rest = new JLabelRichText(
                bestMatch.substring(input.length())
        ).italic();
        return new JLabelRichText(match.toString() + rest);
    }

    /**
     * Sets the {@link #taggedArgErr} flag.
     * @param value The value to set it to.
     * @implNote This flag is used such as to allow {@link TaggedArgUtil} to print errors via
     * {@link SafeJLabel#setText(String)} and not be overriden by {@link #parse(ArrayList, boolean)}.
     */
    public void taggedArgErr(boolean value) {
        taggedArgErr = value;
    }

    ArrayList<String> options;

    public ArrayList<String> getOptions() {
        return options;
    }

    private void setOptions(ArrayList<String> options) {
        this.options = options;
    }
}
