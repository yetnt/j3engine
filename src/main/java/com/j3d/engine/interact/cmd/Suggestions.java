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

    public Suggestions(JPanel suggestionsPanel, JScrollPane scrollPane) {
        this.suggestionsPanel = suggestionsPanel;
        this.suggestionScrollPane = scrollPane;
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

    public void onKeyInput(String input) {
        suggestionsPanel.removeAll();

        if (input.indexOf(' ') != -1) {
//            suggestionContainer.revalidate();
//            suggestionContainer.repaint();
            updateSuggestionPane();
            Main.repaintL();
            return;

        }

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

    private ImageIcon createIcon(String id) {
        URL imageURL = Main.class.getResource("/commands/" + id + ".png");
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