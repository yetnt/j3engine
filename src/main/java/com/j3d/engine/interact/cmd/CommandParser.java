package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.engine.CommandPalette;
import com.j3d.Static;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.engine.geometry.geo2d.graphics.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.utility.Parsing;
import com.j3d.utility.generic.Pair;
import com.j3d.utility.generic.SamePair;
//import com.jaiva.utils.Find;
//import com.jaiva.utils.Pair;
//import com.jaiva.utils.Tuple2;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j3d.engine.interact.cmd.CommandsManager.getCommand;

/**
 * Parses text input from a {@link CommandPalette} input field and converts it into
 * command invocations. The parser maintains an argument accumulator while the user
 * types and converts typed tokens into appropriate argument objects (strings, quoted
 * text, numeric vectors, colours, UUID-referenced objects, etc.). Once a full command
 * line is entered the parser resolves the command name and executes the corresponding
 * {@link Command}.
 *
 * <p>The parser also integrates with the UI via {@link SafeJLabel} to present parsing
 * or execution errors to the user and controls the enabled/disabled state of the
 * input field.
 *
 * @author Lehlogonolo Poole
 * @see CommandPalette
 * @see CommandsManager
 */
public class CommandParser {
    /**
     * Working buffer for the current token being typed. Characters from the input field
     * are appended to this accumulator until a token boundary (typically a space) is detected.
     */
    private String accumulator = "";
    /**
     * Collected argument objects for the current command invocation. Elements may be
     * instances of {@link String}, {@link UUID}, {@link Vector3},
     * {@link Color}, {@link GObject}, {@link Thing},
     * or other types produced by argument parsing.
     */
    private final ArrayList<Object> arguments = new ArrayList<>();
    /**
     * Collected tagged argument values for the current command invocation.
     */
    private final ArrayList<TaggedArgValue<?>> taggedArguments = new ArrayList<>();

    /**
     * When true, document events coming from the input field are ignored. This is used
     * to prevent re-entrant parsing when the parser programmatically updates the field.
     */
    private boolean ignoreDocumentEvent = false;
    /**
     * The UI command palette that supplies input and receives feedback from this parser.
     */
    public final CommandPalette commandPalette;
    /**
     * Helper label wrapper used to show parsing/execution messages to the user.
     */
    private final SafeJLabel label;
    /**
     * Helper variable to deduce whether some code can inject an argument to the command line.
     */
    private boolean argumentClosed = true;

    /**
     * Enable the command input field and apply the 'active' background styling.
     * <p>
     * This makes the input field editable and darkens its background to indicate focus/availability.
     */
    public void enable() {
        commandPalette.inputField.setEnabled(true);
        commandPalette.inputField.setBackground(
                commandPalette.inputField.getBackground().darker().darker()
        );
    }

    /**
     * Disable the command input field and restore the default background styling.
     * <p>
     * This prevents user input and brightens the field background to indicate it is inactive.
     */
    public void disable() {
        commandPalette.inputField.setEnabled(false);
        commandPalette.inputField.setBackground(
                commandPalette.inputField.getBackground().brighter().brighter()
        );
    }

    /**
     * Create a new {@code CommandParser} bound to the given {@link CommandPalette}.
     * <p>
     * The constructor wires action and document listeners to the palette's input field
     * to accumulate typed characters, split tokens, and trigger parsing and execution
     * when the user submits a command.
     *
     * @param p the {@link CommandPalette} instance this parser should use for input and output
     */
    public CommandParser(CommandPalette p) {
        this.commandPalette = p;
        this.label = new SafeJLabel(commandPalette.logLabel, commandPalette.logLabel2);
        commandPalette.inputField.addActionListener(e -> {
            ignoreDocumentEvent = true;
            parse();
            run();
            arguments.clear();
            taggedArguments.clear();
            accumulator = "";
            commandPalette.inputField.setText("");
            ignoreDocumentEvent = false;
        });
        commandPalette.inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (ignoreDocumentEvent) return;

                try {
                    int offset = e.getOffset();
                    int length = e.getLength();
                    String insertedText = commandPalette.inputField.getDocument().getText(offset, length);

                    for (char c : insertedText.toCharArray()) {
                        //noinspection StringConcatenationInLoop
                        accumulator += c;
                        if (c == ' ' && !inBrace(c)) {
                            argumentClosed = true;
                            parse();
                        }
                    }

                } catch (BadLocationException ex) {
                    ex.printStackTrace(); // Or log it properly
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (ignoreDocumentEvent) return;
                if (!accumulator.isEmpty()) {
                    accumulator = accumulator.substring(0, accumulator.length() - 1);
                }

                if (commandPalette.inputField.getText().isEmpty()) {
                    arguments.clear();
                    taggedArguments.clear();
                    accumulator = "";
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events\
                // But this mf wants me to implement it anyway.
            }
        });

    }

    /**
     * Inject an argument into the current argument list and append its textual
     * representation to the command palette input field.
     *
     * <p>The method supports multiple argument types (Vector3, GObject, Thing,
     * Color, String, Integer, Double). When invoked it will add the object to
     * {@link #arguments} and update the {@link #commandPalette} input field with
     * a command-compatible textual form of the argument. During the update the
     * {@link #ignoreDocumentEvent} flag is set to prevent the document listener
     * from processing the programmatic change.
     *
     * @param obj the argument object to inject (must be one of the supported types)
     * @throws RuntimeException if the argument type is not recognised
     */
    public void addArgument(Object obj) {
        if (!argumentClosed) return;
        if (arguments.isEmpty()) return; // no command name, no arguments
        ignoreDocumentEvent = true; // don't trigger any updates.
        switch (obj) {
            case Vector3 v -> {
                arguments.add(v);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + v.toCommandPaletteString() + " "
                );
            }
            case GObject g -> {
                arguments.add(g);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + g.getId() + " "
                );
            }
            case Thing t -> {
                arguments.add(t);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + t.getId() + " "
                );
            }
            case Color c -> {
                arguments.add(c);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + colourToCommandPaletteString(c) + " "
                );
            }
            case String s -> {
                arguments.add(s);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + "\"" + s + "\" "
                );
            }
            case Integer i -> {
                arguments.add(i);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + i + " "
                );
            }
            case Double d -> {
                arguments.add(d);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + d + " "
                );
            }
            case Boolean b -> {
                arguments.add(b);
                commandPalette.inputField.setText(
                        commandPalette.inputField.getText() + (b ? "true" : "false") + " "
                );
            }
            default -> throw new RuntimeException("Unknown argument type: " + obj.getClass().getName());
        }
        ignoreDocumentEvent = false;
    }

    /**
     * Convert a {@link Color} into the textual representation used by the
     * command palette (format: #R:G:B:A#).
     *
     * @param col the colour to convert
     * @return a string in the palette colour format, including surrounding '#'
     */
    public String colourToCommandPaletteString(Color col) {
        return String.format(
                "#%d:%d:%d:%d#",
                col.getRed(),
                col.getGreen(),
                col.getBlue(),
                col.getAlpha()
        );
    }

    /**
     * Attempts to find a GObject or Thing with the given UUID and adds it to the arguments list.
     * <p>
     *     This will be used by the command parser to convert UUID strings into actual objects when parsing command arguments.
     * </p>
     * @param uuid The UUID of the GObject or Thing to find.
     */
    public void argAddUUID(UUID uuid) {
        GObject g = Static.sceneManager.findObjectByUUID(uuid);
        if (g == null) {
            // try to find a Thing with the given UUID
            Thing t = Static.sceneManager.findThingByUUID(uuid);
            if (t == null) {
                label.error("No object or thing found with UUID: " + SafeJLabel.EMPH, uuid);
            } else {
                arguments.add(t);
            }
            return;
        }
        arguments.add(g);
    }

    /**
     * Checks if the character is within braces.
     * @param c The character to check.
     * @return True if the character is within braces, false otherwise.
     */
    private boolean inBrace(char c) {
        Pair<ArrayList<SamePair<Integer>>, ArrayList<Pair<Integer, Character>>> bp =
                Parsing.bracePairs(accumulator);
        ArrayList<SamePair<Integer>> sp = Parsing.quotationPairs(accumulator);
        if (accumulator.contains(":(") && sp.isEmpty())
            return true; // Vector3 object within TaggedArgUtil
        return (bp.first.isEmpty() && accumulator.charAt(0) == '(') ||
                (sp.isEmpty() && accumulator.charAt(0) == '"');
//        return (c != ')' && accumalator.charAt(0) == '(') || (c != '"' && accumalator.charAt(0) == '"');
    }

    /**
     * Parse the current {@link #accumulator} token and convert it into an argument
     * object which is appended to {@link #arguments}.
     *
     * <p>The parser recognises:
     * <ul>
     *   <li>Quoted strings: "..."</li>
     *   <li>Parenthesized numeric tuples: (x,y,z) -> {@link com.j3d.engine.geometry.geo3d.matrix.Vector3}</li>
     *   <li>Colour literals surrounded by hashes: #...# (supports R:G:B, R:G:B:A, or hex)</li>
     *   <li>UUIDs: resolves to a {@link GObject} or
     *       {@link .Thing} via {@link #argAddUUID(UUID)}</li>
     *   <li>Fallback: plain string tokens</li>
     * </ul>
     */
    private void parse() {
        accumulator = accumulator.trim();
        if (accumulator.isEmpty()) {
            return;
        }
        // First check for the obvious, whether the accumulator starts and ends with double qutoes
        if (accumulator.charAt(0) == '"' && accumulator.charAt(accumulator.length() - 1) == '"') {
            arguments.add(accumulator.substring(1, accumulator.length() - 1));
        } else if (accumulator.charAt(0) == '(' && accumulator.charAt(accumulator.length() - 1) == ')') {
            // Now check for parenthesis
            String[] nums = accumulator.substring(1, accumulator.length() - 1).split(",");
            ArrayList<Double> parsedNums = new ArrayList<>();
            for (String num : nums) {
                try {
                    parsedNums.add(Double.parseDouble(num.trim()));
                } catch (NumberFormatException e) {
                    label.error("Invalid number format: " + SafeJLabel.EMPH, num);
                    return;
                }
            }
            if (parsedNums.size() != 3) {
                label.error("Invalid number of values in Vector3. Expected "+SafeJLabel.EMPH+" got "+SafeJLabel.EMPH ,3, parsedNums.size());
                return;
            }
            arguments.add(new Vector3(parsedNums.getFirst(), parsedNums.get(1), parsedNums.getLast()));
        } else if (accumulator.charAt(0) == '#' && accumulator.charAt(accumulator.length() - 1) == '#') {
            // This is a colour. In either:
            /*
            #R:G:B#
            #R:G:B:A#
            #FFFFFF#
             */
            Color col = parseColor(accumulator.substring(1, accumulator.length() - 1));
            if (col != null) arguments.add(col);
        } else {
            // Otherwise, it may be a UUID, if so parse as UUID, find the given GObject, and pass it into the arguments
            // Otherwise, just pass it as a string
            try {
                UUID uuid = UUID.fromString(accumulator.trim());
                // Find the GObject with the given UUID
                argAddUUID(uuid);
            } catch (IllegalArgumentException e) {
                parseAsNumberOrBool(accumulator, acc -> {
                    // if numbers fail, check if this is a tagged arg
                    TaggedArgValue<?> v = TaggedArgUtil.parse(acc, true, label);
                    if (v.isErr()) return;
                    if (v.isEmpty()) {
                        arguments.add(acc.trim()); // something like an extra arg, just put it.
                        return;
                    }
                    taggedArguments.add(v);
                });
            }
        }
        argumentClosed = true;
        accumulator = "";
    }

    /**
     * Attempt to parse the given token as an integer or double and add the
     * resulting number to {@link #arguments}. If parsing as a number fails,
     * the provided {@code otherwise} consumer is invoked with the original token.
     *
     * @param accumulator the token to parse
     * @param otherwise a fallback consumer called when the token is not numeric
     */
    private void parseAsNumberOrBool(String accumulator, Consumer<String> otherwise) {
        try {
            arguments.add(Integer.parseInt(accumulator.trim()));
        } catch (NumberFormatException e) {
            try {
                arguments.add(Double.parseDouble(accumulator.trim()));
            } catch (NumberFormatException f) {
                try {
                    String acc = accumulator.trim().toLowerCase();
                    arguments.add(switch (acc) {
                        case "yes", "yebo", "true" -> true;
                        case "no", "aowa", "false" -> false;
                        default -> throw new IllegalArgumentException("rah");
                    });
                } catch (IllegalArgumentException ex) {
                    otherwise.accept(accumulator);
                }
            }
        }
    }

    /**
     * Execute the parsed command using the collected {@link #arguments}.
     *
     * <p>The first argument is expected to be the command name (a {@link String}). The
     * parser will resolve the command via {@link com.j3d.engine.interact.cmd.CommandsManager#getCommand(String)},
     * check for any currently running stateful command, and if applicable mark the resolved
     * command as the current stateful command before invoking its {@code run} method.
     */
    public void run() {
        label.clear();
        if (arguments.isEmpty())
            return;
        if (arguments.getFirst() instanceof String cmdName) {;
            Command cmd = getCommand(cmdName);
            if (arguments.isEmpty())
                return;
            if (cmd == null) {
                EngineFrame.repaintL();
                label.error("Command not found: " + SafeJLabel.EMPH, cmdName);
                return;
            }
            arguments.removeFirst();
            if (!runCommand(cmd, cmdName, arguments, taggedArguments)) return;
            taggedArguments.clear();
        } else {
            label.error("Invalid command name.");
        }
        EngineFrame.repaintL();
//        EngineFrame.f.repaint(); // Repaint the frame to reflect any changes.
    }

    public boolean runCommand(Command cmd, String cmdName, ArrayList<Object> arguments, ArrayList<TaggedArgValue<?>> taggedArguments) {
        if (CommandsManager.commandIsRunning()) {
            Static.hoverLabel.error("Command is currently running: " + SafeJLabel.EMPH, CommandsManager.getCurrentCommandName());
            Static.mainFrame.requestFocusInWindow();
            return false;
        }
        if (cmd instanceof StatefulCommand statefulCommand)
            CommandsManager.setAsCurrent(statefulCommand);

        cmd.run(label, cmdName, arguments.toArray(), taggedArguments);
        Static.hoverLabel.clear();
        return true;
    }

    /**
     * Parse a color specification string into a {@link Color}.
     *
     * <p>Supported formats:
     * <ul>
     *   <li>R:G:B:A (four integers separated by colons)</li>
     *   <li>R:G:B (three integers separated by colons)</li>
     *   <li>Hex string parseable by {@link Color#decode(String)}</li>
     * </ul>
     *
     * @param input the color string (without surrounding hashes)
     * @return a {@link Color} instance if parsing succeeds, or {@code null} on failure
     */
    private Color parseColor(String input) {
        // if 4 colons, if so, assume R:G:B:A format
        // if 3 colons, assume R:G:B format
        // if no colons, assume hex format
        if (input.chars().filter(ch -> ch == ':').count() == 3) {
            String[] parts = input.split(":");
            if (parts.length != 4) {
                label.setText("Invalid color format. Expected R:G:B:A");
                return null;
            }
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                int a = Integer.parseInt(parts[3].trim());
                return new Color(r, g, b, a);
            } catch (NumberFormatException e) {
                label.setText("Invalid number format in color.");
                return null;
            }
        } else if (input.chars().filter(ch -> ch == ':').count() == 2) {
            String[] parts = input.split(":");
            if (parts.length != 3) {
                label.setText("Invalid color format. Expected R:G:B");
                return null;
            }
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return new Color(r, g, b);
            } catch (NumberFormatException e) {
                label.setText("Invalid number format in color.");
                return null;
            }
        } else {
            // Hex format
            try {
                return Color.decode(input);
            } catch (NumberFormatException e) {
                label.setText("Invalid hex color format.");
                return null;
            }
        }
    }

    public SafeJLabel safeJLabel() {
        return label;
    }
}
