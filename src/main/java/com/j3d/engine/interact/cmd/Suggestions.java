package com.j3d.engine.interact.cmd;

import com.j3d.Main;
import com.j3d.engine.interact.cmd.base.Command;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class Suggestions {

    private final ArrayList<Suggestion> suggestions = new ArrayList<>();
    private final JPanel suggestionsPanel;
    private final JScrollPane suggestionScrollPane;
    private final JTextArea commandTxtArea;

    public Suggestions(JPanel suggestionsPanel, JScrollPane scrollPane, JTextArea commandTxtArea) {
        this.suggestionsPanel = suggestionsPanel;
        this.suggestionScrollPane = scrollPane;
        this.commandTxtArea = commandTxtArea;
        suggestionsPanel.removeAll();
        Main.repaintL();
//        suggestionContainer.revalidate();
//        suggestionContainer.repaint();

        for (Command cmd : new CommandsManager().commands.values()) {
            for (String alias : cmd.aliases) {
                Suggestion sug = createSuggestion(alias, createIcon(cmd.aliases.getFirst()));
                suggestions.add(sug);
                suggestionsPanel.add(sug.panel);
            }
        }

//        suggestionContainer.revalidate();
//        suggestionContainer.repaint();
        Main.repaintL();
    }



    public void onKeyInput(String input, ArrayList<Object> currentArgs) {
        suggestionsPanel.removeAll();

        if (input.indexOf(' ') != -1) {
//            suggestionContainer.revalidate();
//            suggestionContainer.repaint();
            updateSuggestionPane();
            onEnd(input, currentArgs);
            Main.repaintL();
            return;
        }
        commandTxtArea.setText("");

        // sort suggestions. put those that start with input first, then those that contain input
        suggestions.sort(
            (a, b) -> {
                boolean aStarts = a.text.startsWith(input);
                boolean bStarts = b.text.startsWith(input);
                if (aStarts && !bStarts) return -1;
                if (!aStarts && bStarts) return 1;
                boolean aContains = a.text.contains(input);
                boolean bContains = b.text.contains(input);
                if (aContains && !bContains) return -1;
                if (!aContains && bContains) return 1;
                return a.text.compareTo(b.text);
            }
        );

        for (Suggestion sug : suggestions) {
            if (sug.text.startsWith(input) || sug.text.contains(input)) {
                suggestionsPanel.add(sug.panel);
            }
        }

//        suggestionContainer.revalidate();
//        suggestionContainer.repaint();
        updateSuggestionPane();
        Main.repaintL();
    }

    private void onEnd(String input, ArrayList<Object> currentArgs) {
        // Get the name from arg[0]
        String[] parts = input.split(" ");
        if (parts.length == 0) return;
        String cmdName = parts[0];
        Command cmd = CommandsManager.getCommand(cmdName);
        if (cmd == null) return;
        // print all usages to commandTxtArea
        String output = cmd.getUsages().values().stream().reduce("",
                (acc, usage) -> acc + cmdName + usage + "\n"
                );
        commandTxtArea.setText(output.trim());
    }

    private ImageIcon createIcon(String id) {
        URL imageURL = Main.class.getResource("/commands/" + id + ".png");
        assert imageURL != null;
        return new ImageIcon(new ImageIcon(imageURL).getImage().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));
    }

    /**
     * Update the suggestion pane size based on the number of suggestions.
     */
    public void updateSuggestionPane() {
        suggestionsPanel.revalidate();
//        suggestionContainer.repaint();

        int suggestionCount = suggestionsPanel.getComponentCount();
        int rowHeight = 32; // Approximate height of one suggestion
        int padding = 10;

        int newHeight = Math.min(120, suggestionCount * rowHeight + padding); // Cap at 120px
        suggestionScrollPane.setPreferredSize(new java.awt.Dimension(suggestionScrollPane.getWidth(), newHeight));
        suggestionScrollPane.revalidate();
//        suggestionScrollPane.getVerticalScrollBar().setValue(suggestionScrollPane.getVerticalScrollBar().getMaximum());
    }


    public static Suggestion createSuggestion(String text, ImageIcon icon) {
        JPanel suggestionPanel = new JPanel();
        suggestionPanel.setOpaque(false);

        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.X_AXIS));
        suggestionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel iconLabel = new JLabel(icon);
        iconLabel.setPreferredSize(new Dimension(32, 32));
        iconLabel.setMinimumSize(new Dimension(32, 32));
        iconLabel.setMaximumSize(new Dimension(32, 32));
        iconLabel.setOpaque(true);
//        iconLabel.setBackground(Color.RED);
        iconLabel.setVisible(true);

        JLabel textLabel = new JLabel(text);
        textLabel.setOpaque(false);

        suggestionPanel.add(iconLabel);
        suggestionPanel.add(Box.createHorizontalStrut(5));
        suggestionPanel.add(textLabel);

        textLabel.setHorizontalAlignment(SwingConstants.LEFT);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        textLabel.setAlignmentY(Component.CENTER_ALIGNMENT);


        return new Suggestion(text, suggestionPanel);
    }

    public static class Suggestion {
        public String text;
        public JPanel panel;

        public Suggestion(String text, JPanel panel) {
            this.text = text;
            this.panel = panel;
        }

        public void onSelect() {
            // Clear suggestions list
            // Populate the command prompt with the suggestion text
        }
    }
}