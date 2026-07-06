package com.j3d.engine.interact.cmd.complete;

import com.j3d.Static;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.Argument;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.SamePair;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TypingHintSession {

    public static Color COMMAND_NAME_LIEKLY_MATCH = Color.GREEN;
    public static Color COMMAND_NAME_PARTIAL_MATCH = new Color(236, 191, 100);
    int MAX_SUGGESTIONS = 8;

    public TypingHintSession() {

    }

    public void parse(ArrayList<CmdToken> tokens) {
        Static.commandParser.safeJLabel().clearLower();
        // If the tokens are empty. Do nothing
        if (tokens.isEmpty()) return;

        // if there is a single token. its the command name try find matches.
        if (tokens.size() == 1) {
            CmdToken token = tokens.getFirst();
            if (token.getType() != CmdToken.Type.STRING) {
                Static.commandParser.safeJLabel().setText(
                        new JLabelRichText("The first argument (command name) is usually a string bro")
                                .italic().wrapHTML()
                );
                return;
            }
            SamePair<ArrayList<JLabelRichText>> matches = possibleCommandMatches(token);
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

        //TODO: the rest
    }

    private SamePair<ArrayList<JLabelRichText>> possibleCommandMatches(CmdToken token) {
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
}
