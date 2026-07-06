package com.j3d.utility;

import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.utility.generic.Pair;
import com.j3d.utility.generic.SamePair;

import java.util.ArrayList;

/**
 * Parsing utilities used by {@link CommandParser}. This code is borrowed from the
 * {@linkplain <a href="https://github.com/yetnt/jaiva">Jaiva Programming Language</a>}, my custom
 * personal project.
 * <p>
 *     This was abstracted from Jaiva as to not import the entire language runtime and parser
 *     for it's utilities and to remove parsing quirks that the Jaiva methods would've introduced.
 * </p>
 * @see CommandParser
 * @see TaggedArgUtil
 *
 * @author Lehlogonolo Poole
 */
public class Parsing {

    /**
     * Finds pairs of double quotation marks within a given line of text,
     * ignoring escaped quotation marks.
     *
     * @param line The string to search for quotation mark pairs.
     * @return An {@link ArrayList} of {@link SamePair} where each pair represents the start and end index of a quotation block.
     */
    public static ArrayList<SamePair<Integer>> quotationPairs(String line) {
        ArrayList<SamePair<Integer>> arr = new ArrayList<>();
        int oldCharIndex = -1;

        for(int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            char before = i > 0 ? line.charAt(i - 1) : 0;
            char before2 = i > 1 ? line.charAt(i - 2) : 0;
            if (c == '"' && (before != '\\' || before2 == '\\')) {
                if (oldCharIndex == -1) {
                    oldCharIndex = i;
                } else {
                    arr.add(new SamePair<>(oldCharIndex, i));
                    oldCharIndex = -1;
                }
            }
        }

        return arr;
    }

    /**
     * Finds matching brace pairs (parentheses and square brackets) within a given line of text.
     * It also returns any unclosed braces.
     *
     * @param line The string to search for brace pairs.
     * @return A {@link BracePairs} instance containing a {@link Pair} with an {@link ArrayList} of {@link SamePair} for closed brace pairs (start and end index),
     *         and an {@link ArrayList} of {@link Pair} for unclosed braces (index and character).
     */
    public static BracePairs bracePairs(String line) {
        ArrayList<SamePair<Integer>> finalArr = new ArrayList<>();
        ArrayList<Pair<Integer, Character>> stack = new ArrayList<>();

        for(int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if ((c == '[' || c == '(') /*&& Validate.isOpInQuotePair(line, i) == -1*/) {
                stack.add(new Pair<>(i, c));
            }

            if ((c == ']' || c == ')') /*&& Validate.isOpInQuotePair(line, i) == -1*/) {
                Pair<Integer, Character> t = stack.getLast();
                if (t.second == '[' && c == ']' || t.second == '(' && c == ')') {
                    finalArr.add(new SamePair<>(t.first, i));
                    stack.removeLast();
                }
            }
        }

        return new BracePairs(finalArr, stack);
    }

    /**
     * A record to hold the results of the {@link #bracePairs(String)} method.
     *
     * @param closedPairs An {@link ArrayList} of {@link SamePair} representing the start and end indices of closed brace pairs.
     * @param unclosedBraces An {@link ArrayList} of {@link Pair} where each pair contains the index and character of an unclosed brace.
     */
    public record BracePairs(
            ArrayList<SamePair<Integer>> closedPairs,
            ArrayList<Pair<Integer, Character>> unclosedBraces
    ) {}

    /**
     * Splits a string by a given character, ignoring delimiters within quotation marks or brace pairs.
     * This method handles nested braces and quotes.
     *
     * @param input The string to be split.
     * @param c The character to split the string by.
     * @return An {@link ArrayList} of strings, where each string is a segment of the input string.
     */
    public static ArrayList<String> split(String input, char c) {
        BracePairs bracePairs = bracePairs(input);
        ArrayList<SamePair<Integer>> quotePairs = quotationPairs(input);

        StringBuilder acc = new StringBuilder();
        ArrayList<String> content = new ArrayList<>();
        // since we know where brace pairs and quote pairs are
        // we trust it. meaning if we encounter a quote or opening brace and
        // nether say it belongs there, accumulate everything as mishandled string.
        boolean dangling = false;
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int finalI = i;
            boolean inBrace = bracePairs.closedPairs.stream().anyMatch(
                    pair -> finalI >= pair.first && finalI <= pair.second);
            boolean inQuote = quotePairs.stream().anyMatch(
                    pair -> finalI >= pair.first && finalI <= pair.second
            );
            if (ch != c || dangling || (inBrace || inQuote)) {
                if ((ch == '\"' || ch == '(') && !(inBrace || inQuote))
                    dangling = true;
                acc.append(ch);
                continue;
            }

            if (!acc.isEmpty()) {
                content.add(acc.toString());
                acc = new StringBuilder();
            }
        }

        if (!acc.isEmpty()) {
            content.add(acc.toString());
        }

        return content;
    }
}
