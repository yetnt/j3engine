package com.j3d.engine.interact.cmd;

import com.j3d.Main;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.base.Command;
import com.jaiva.utils.Find;
import com.jaiva.utils.Pair;
import com.jaiva.utils.Tuple2;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static com.j3d.engine.interact.cmd.CommandsManager.getCommand;

public class CommandParser {
//    private final JLabel cmdP.logLabel;
    private String accumulator = "";
    private final ArrayList<Object> arguments = new ArrayList<>();
    private boolean ignoreDocumentEvent = false;
    private final CommandPallete cmdP;

    public CommandParser(CommandPallete p) {
        this.cmdP = p;
        cmdP.inputField.addActionListener(e -> {
            ignoreDocumentEvent = true;
            parse();
            run();
            arguments.clear();
            accumulator = "";
            cmdP.inputField.setText("");
            ignoreDocumentEvent = false;
        });
        cmdP.inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (ignoreDocumentEvent) return;

                try {
                    int offset = e.getOffset();
                    int length = e.getLength();
                    String insertedText = cmdP.inputField.getDocument().getText(offset, length);

                    for (char c : insertedText.toCharArray()) {
                        //noinspection StringConcatenationInLoop
                        accumulator += c;
                        if (c == ' ' && !inBrace(c)) {
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

                if (cmdP.inputField.getText().isEmpty()) {
                    arguments.clear();
                    accumulator = "";
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events\
                // But this mf wants me to implement it anyways.
            }
        });

    }

    /**
     * Attempts to find a GObject or Thing with the given UUID and adds it to the arguments list.
     * <p>
     *     This will be used by the command parser to convert UUID strings into actual objects when parsing command arguments.
     * </p>
     * @param uuid The UUID of the GObject or Thing to find.
     */
    public void argAddUUID(UUID uuid) {
        GObject g = Main.renderer.findObjectByUUID(uuid);
        if (g == null) {
            // try to find a Thing with the given UUID
            Thing t = Main.renderer.findThingByUUID(uuid);
            if (t == null) {
                cmdP.logLabel.setText("No object or thing found with UUID: " + uuid);
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
        Tuple2<ArrayList<Pair<Integer>>, ArrayList<Tuple2<Integer, Character>>> bp =
                Find.bracePairs(accumulator);
        ArrayList<Pair<Integer>> sp = Find.quotationPairs(accumulator);
        return (bp.first.isEmpty() && accumulator.charAt(0) == '(') ||
                (sp.isEmpty() && accumulator.charAt(0) == '"');
//        return (c != ')' && accumalator.charAt(0) == '(') || (c != '"' && accumalator.charAt(0) == '"');
    }

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
                    cmdP.logLabel.setText("Invalid number format: " + num);
                    return;
                }
            }
            if (parsedNums.size() != 3) {
                cmdP.logLabel.setText("Invalid number of values in Vector3. Expected 3, got " + parsedNums.size());
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
        }else {
            // Otherwise, it may be a UUID, if so parse as UUID, find the given GObject, and pass it into the arguments
            // Otherwise, just pass it as a string
            try {
                UUID uuid = UUID.fromString(accumulator.trim());
                // Find the GObject with the given UUID
                argAddUUID(uuid);
            } catch (IllegalArgumentException e) {
                // TODO: Implement tag argument parsing.
                arguments.add(accumulator.trim());
            }
        }
        accumulator = "";
    }

    public void run() {
        if (arguments.getFirst() instanceof String cmdName) {;
            Command cmd = getCommand(cmdName);
            if (arguments.isEmpty())
                return;
            if (cmd == null) {
                Main.repaintL();
                cmdP.logLabel.setText("Command not found: " + cmdName);
                return;
            }
            // Remove the command name from the arguments
            arguments.removeFirst();
            cmd.run(cmdP.logLabel,cmdName, arguments.toArray());
        } else {
            cmdP.logLabel.setText("Invalid command name.");
        }
        Main.repaintL();
//        Main.f.repaint(); // Repaint the frame to reflect any changes.
    }

    private Color parseColor(String input) {
        // if 4 colons, if so, assume R:G:B:A format
        // if 3 colons, assume R:G:B format
        // if no colons, assume hex format
        if (input.chars().filter(ch -> ch == ':').count() == 3) {
            String[] parts = input.split(":");
            if (parts.length != 4) {
                cmdP.logLabel.setText("Invalid color format. Expected R:G:B:A");
                return null;
            }
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                int a = Integer.parseInt(parts[3].trim());
                return new Color(r, g, b, a);
            } catch (NumberFormatException e) {
                cmdP.logLabel.setText("Invalid number format in color.");
                return null;
            }
        } else if (input.chars().filter(ch -> ch == ':').count() == 2) {
            String[] parts = input.split(":");
            if (parts.length != 3) {
                cmdP.logLabel.setText("Invalid color format. Expected R:G:B");
                return null;
            }
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return new Color(r, g, b);
            } catch (NumberFormatException e) {
                cmdP.logLabel.setText("Invalid number format in color.");
                return null;
            }
        } else {
            // Hex format
            try {
                return Color.decode(input);
            } catch (NumberFormatException e) {
                cmdP.logLabel.setText("Invalid hex color format.");
                return null;
            }
        }
    }
}
