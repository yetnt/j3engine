package com.j3d.engine.interact.cmd;

import com.j3d.Main;
import com.j3d.engine.geometry.geo2d.GObject;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.engine.interact.cmd.avail.EchoCmd;
import com.j3d.engine.interact.cmd.avail.LineCmd;
import com.j3d.engine.interact.cmd.avail.PointCmd;
import com.j3d.engine.interact.cmd.avail.TriCmd;
import com.j3d.engine.interact.cmd.base.Command;
import com.jaiva.utils.Find;
import com.jaiva.utils.Pair;
import com.jaiva.utils.Tuple2;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.UUID;

public class CommandParser {
    private JLabel logLabel;
    private JTextField inputField;
    private String accumalator = "";
    private ArrayList<Object> arguments = new ArrayList<>();
    public CommandParser(JLabel logLabel, JTextField field) {
        this.logLabel = logLabel;
        this.inputField = field;
        field.addActionListener(e -> {
            parse();
            run();
            arguments.clear();
            accumalator = "";
            field.setText("");
        });
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                char c = field.getText().charAt(field.getText().length() - 1);
                accumalator += c;
                if (c == ' ' && !inBrace(c)) {
                    parse();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (!accumalator.isEmpty()) {
                    accumalator = accumalator.substring(0, accumalator.length() - 1);
                }

                if (field.getText().isEmpty()) {
                    arguments.clear();
                    accumalator = "";
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components do not fire these events\
                // But this mf wants me to implement it anyways.
            }
        });

    }

    public void argAddUUID(UUID uuid) {
        GObject g = Main.renderer.findObjectByUUID(uuid);
        if (g == null) {
            logLabel.setText("No object found with UUID: " + uuid);
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
                Find.bracePairs(accumalator);
        ArrayList<Pair<Integer>> sp = Find.quotationPairs(accumalator);
        return (bp.first.isEmpty() && accumalator.charAt(0) == '(') ||
                (sp.isEmpty() && accumalator.charAt(0) == '"');
//        return (c != ')' && accumalator.charAt(0) == '(') || (c != '"' && accumalator.charAt(0) == '"');
    }

    private void parse() {
        accumalator = accumalator.trim();
        if (accumalator.isEmpty()) {
            return;
        }
        // First check for the obvious, whether the accumulator starts and ends with double qutoes
        if (accumalator.charAt(0) == '"' && accumalator.charAt(accumalator.length() - 1) == '"') {
            arguments.add(accumalator.substring(1, accumalator.length() - 1));
        } else if (accumalator.charAt(0) == '(' && accumalator.charAt(accumalator.length() - 1) == ')') {
            // Now check for parenthesis
            String[] nums = accumalator.substring(1, accumalator.length() - 1).split(",");
            ArrayList<Double> parsedNums = new ArrayList<>();
            for (String num : nums) {
                try {
                    parsedNums.add(Double.parseDouble(num.trim()));
                } catch (NumberFormatException e) {
                    logLabel.setText("Invalid number format: " + num);
                    return;
                }
            }
            if (parsedNums.size() != 3) {
                logLabel.setText("Invalid number of values in Vector3. Expected 3, got " + parsedNums.size());
                return;
            }
            arguments.add(new Vector3(parsedNums.getFirst(), parsedNums.get(1), parsedNums.getLast()));
        } else {
            // Otherwise, it may be a UUID, if so parse as UUID, find the given GObject, and pass it into the arguments
            // Otherwise, just pass it as a string
            try {
                UUID uuid = UUID.fromString(accumalator.trim());
                // Find the GObject with the given UUID
                argAddUUID(uuid);
            } catch (IllegalArgumentException e) {
                // TODO: Implement tag argument parsing.
                arguments.add(accumalator.trim());
            }
        }
        accumalator = "";
    }

    public void run() {
        if (arguments.getFirst() instanceof String cmdName) {;
            Command cmd = getCommand(cmdName);
            if (cmd == null) {
                logLabel.setText("Command not found: " + cmdName);
                return;
            }
            // Remove the command name from the arguments
            arguments.removeFirst();
            cmd.run(logLabel, arguments.toArray());
        } else {
            logLabel.setText("Invalid command name.");
        }
    }

    public Command getCommand(String name) {
        LineCmd lineCmd = new LineCmd();
        PointCmd pointCmd = new PointCmd();
        TriCmd triCmd = new TriCmd();
        EchoCmd echoCmd = new EchoCmd();
        if (lineCmd.aliases.contains(name)) {
            return lineCmd;
        } else if (pointCmd.aliases.contains(name)) {
            return pointCmd;
        } else if (triCmd.aliases.contains(name)) {
            return triCmd;
        } else if (echoCmd.aliases.contains(name)) {
            return echoCmd;
        }else {
            return null;
        }
    }
}
