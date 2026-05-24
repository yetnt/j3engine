package com.j3d.engine.interact.cmd.complete;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.args.Argument;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.utility.generators.JLabelRichText;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoCompleteSession {
    private String cmdName;
    private Command command;
    private boolean validSession = false;

    private ArrayList<Object> arguments = new ArrayList<>();
    private String currentArg = "";

    private String cachedUsage = "";
    private boolean usageCached = false;

    public AutoCompleteSession(String cmdName) {
        this.cmdName = cmdName;
        Command c = Static.commandManager.commandsAliasMap.get(cmdName);
        if (c != null) {
            this.command = c;
            this.validSession = true;
        } else
            this.validSession = false;
    }

    public AutoCompleteSession(String subcommandName, Command parentCommand) {
        this.cmdName = subcommandName;
        parentCommand.args.stream().filter(
                cmd ->
                    cmd instanceof Subcommand && ((Subcommand) cmd).aliases.contains(subcommandName)
        ).findAny().ifPresent(
                cmd -> {
                    this.validSession = true;
                    this.command = (Subcommand) cmd;
                }
        );
        if (!this.validSession)
            this.command = null;
    }

    public void addArg(Object arg) {
        this.arguments.add(arg);
        updateSuggestions();
        this.currentArg = "";
    }

    public void setCurrentArg(String newValue) {
        this.currentArg = newValue;
        updateSuggestions();
    }

    public String getCmdName() {
        return cmdName;
    }

    public boolean isValidSession() {
        return validSession;
    }

    public Command getCommand() {
        return command;
    }

    private String findGoodUsage(String[] usages, String real) {
        if (!usageCached) {
            if (real.isEmpty()) return usages[0];
            // Next case, if the "real" string partially matches the next argument of a given usage, return that string, otherwise the first usage
            for (String usage : usages) {
                String[] args = usage.split(" ");
                if (arguments.size() < args.length) {
                    String nextArg = args[arguments.size() + 1];
                    if (nextArg.startsWith(real)) {
                        usageCached = true;
                        cachedUsage = usage;
                        return usage;
                    }
                }
            }
        } else {
            return cachedUsage;
        }
        return usages[0];
    }

    private JLabelRichText matchesExpectedType(JLabelRichText jLabelRichText, int i) {

        if (arguments.isEmpty()) return jLabelRichText.font(Color.GREEN);

        String[] rawUsage = jLabelRichText.getRawContent().split(" ");
        String expectedType = rawUsage[i];
        Object currentType = arguments.get(i);

        // logic
        return jLabelRichText.font(Color.GREEN);
    }
    public void updateSuggestions() {
        if (command == null) {
            Static.commandParser.safeJLabel().setLower("No command found...");
            return;
        }
        String[] possibleUsages = command.returnUsagesWhere(
                cmdName,
                arguments.stream()
                        .map(Object::getClass)
                        .toArray(Class[]::new)
        );

        if (possibleUsages.length == 0) {
            Static.commandParser.safeJLabel().setLower(cmdName + " has no expected type...");
            return;
        }

        TaggedArgValue t = TaggedArgUtil.parse(currentArg, false, null);
        String real = currentArg.trim();
        if (!t.isErr() || real.isEmpty()) {
            String[] usage = findGoodUsage(possibleUsages, real).split(" ");
            AtomicInteger i = new AtomicInteger();

            JLabelRichText[] rT = Arrays.stream(usage)
                    .map(s -> {
                        JLabelRichText richText = new JLabelRichText(s + " ", true);
                        // Abstracted method handling both index checking and validation
                        validateAndColorToken(richText, i.getAndIncrement(), s);
                        return richText;
                    })
                    .toArray(JLabelRichText[]::new);
            JLabelRichText otherLabel = new JLabelRichText(" | ");
            JLabelRichText descriptionLabel = new JLabelRichText(command.description).italic().font("4");

            ArrayList<JLabelRichText> richTexts = new ArrayList<>(List.of(rT));
            richTexts.add(otherLabel);
            richTexts.add(descriptionLabel);

            Static.commandParser.safeJLabel().setLower(JLabelRichText.htmlOf(
                    richTexts.toArray(JLabelRichText[]::new)
            ));
            return;
        }

        // other handling.
    }

    /**
     * Validates the token context against actual or currently typing arguments
     * and applies the appropriate UI styling.
     */
    private void validateAndColorToken(JLabelRichText richText, int index, String expectedToken) {
        // 1. Check already completed/parsed arguments
        if (index < arguments.size()) {
            Object actualArg = arguments.get(index);
            if (isArgumentValid(actualArg, expectedToken)) {
                richText.font(Color.GREEN);
            } else {
                richText.font(Color.RED);
            }
        }
        // 2. Check the argument slot currently being typed into
        else if (index == arguments.size()) {
            if (isCurrentArgValid(currentArg, expectedToken)) {
                richText.font(Color.ORANGE);
            } else {
                richText.font(Color.RED);
            }
        }
        // 3. Future/unreached arguments in the usage hint
        else {
            richText.underline();
        }
    }

    private boolean isArgumentValid(Object actualArg, String expectedToken) {
        // Your logic to check if the processed argument matches the usage hint token
        if (expectedToken.contains("|") && actualArg instanceof String arg) {
            // arg set.
            String[] expectedTokens = expectedToken.substring(1, expectedToken.length() - 1).split("\\|");
            for (String token : expectedTokens)
                if (arg.equals(token)) return true;
        } else if (expectedToken.contains("<vect") && !(actualArg instanceof Vector3)) {
            return false;
        } else if ((expectedToken.contains("<col") ) && !(actualArg instanceof Color)) {
            return false;
        } else if (expectedToken.contains("<str")&& !(actualArg instanceof String)) {
            return false;
        } else if (expectedToken.contains("<bool") && !(actualArg instanceof Boolean)) {
            return false;
        } else if (expectedToken.contains("<num") && !(actualArg instanceof Double)) {
            return false;
        } else if (expectedToken.contains("<int") && !(actualArg instanceof Integer)) {
            return false;
        } else if (expectedToken.contains("<poi") && !(actualArg instanceof GPoint)) {
            return false;
        } else if (expectedToken.contains("<line") && !(actualArg instanceof GLine)) {
            return false;
        } else if (expectedToken.contains("<tri") && !(actualArg instanceof GTri)) {
            return false;
        } else if (expectedToken.contains("<any")) {
            return true;
        }
            // if its literally none of those, then it can only be the exact string, meaning a possible subcommand.
            // just return true since we genunely cant check if it is or not yet
        return true;
    }

    private boolean isCurrentArgValid(String rawCurrentArg, String expectedToken) {
        // Your logic to check if the live string being typed fits the usage hint token
        return true;
    }

    private String getDescriptionForIndex(int index) {
        if (command == null) return "";

        Command currentHead = this.command;
        int relativeIndex = index;

        // Step down through subcommands, keeping track of our position relative to the active head
        for (int i = 0; i < index; i++) {
            if (i < arguments.size()) {
                Object actualArg = arguments.get(i);

                // Check if this step matches a subcommand definition under our current head
                Command finalCurrentHead = currentHead;
                Command sub = finalCurrentHead.args.stream()
                        .filter(argDef -> argDef instanceof Subcommand &&
                                ((Subcommand) argDef).aliases.contains(actualArg.toString()))
                        .map(argDef -> (Subcommand) argDef)
                        .findFirst()
                        .orElse(null);

                if (sub != null) {
                    currentHead = sub;
                    // CRUCIAL: Because subcommands reset the local argument scope,
                    // our target index shifts relative to this new subcommand head's base (0)
                    relativeIndex = index - (i + 1);
                }
            }
        }

        // Now currentHead is holding the exact final subcommand or root command scope
        if (relativeIndex >= 0 && relativeIndex < currentHead.args.size()) {
            Object argDefinition = currentHead.args.get(relativeIndex);
            if (argDefinition instanceof Subcommand sub) {
                return sub.description;
            } else if (argDefinition != null) {
                // Adjust this line to match your engine's actual base Argument class/field
                // e.g., ((CommandArgument) argDefinition).description
                return ((Argument)argDefinition).getDescription();
            }
        }

        // Fallback if we are looking at the command name token itself or out of bounds
        return currentHead.description != null ? currentHead.description : "";
    }




//    public void updateSuggestions() {
//        if (command == null) {
//            Static.commandParser.safeJLabel().setLower("No command found...");
//            return;
//        }
//        String[] possibleUsages = command.returnUsagesWhere(
//                cmdName,
//                arguments.stream()
//                        .map(Object::getClass)
//                        .toArray(Class[]::new)
//        );
//
//        if (possibleUsa
//        ges.length == 0) {
//            Static.hoverLabel.setText("No such command.");
//            return;
//        }
//
//        TaggedArgValue t = TaggedArgUtil.parse(currentArg, false, null);
//        String real = currentArg.trim();
//        if (!t.isErr() || real.isEmpty()) {
//            // show suggested args next
//            String[] usage = findGoodUsage(possibleUsages, real).split(" ");
//            AtomicInteger i = new AtomicInteger();
//            String html = JLabelRichText.htmlOf(
//                    Arrays.stream(usage)
//                            .map(s -> s + " ")
//                            .map(s -> new JLabelRichText(s, true))
//                            .peek(jLabelRichText -> {
//                                if (arguments.size() >= i.get()) jLabelRichText.font(Color.GREEN);
//                                else jLabelRichText.underline();
//                                i.getAndIncrement();
//                            })
//                            .toArray(JLabelRichText[]::new)
//            );
//            Static.commandParser.safeJLabel().setLower(
//                    html
//            );
//            return;
//        }
//
//        // other handling.
//    }
}
