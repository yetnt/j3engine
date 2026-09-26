package com.j3d.utility;

import com.yetnt.utils.builders.InlineHTML;
import com.yetnt.utils.tuple.Pair;
import com.yetnt.utils.tuple.SamePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParsingTest {

    @Test
    void quotationPairs() {
        String input = "hello \"world!\" i am a pair\"0\"";
        ArrayList<SamePair<Integer>> expected = new ArrayList<>(
                List.of(
                        new SamePair<>(6, 13),
                        new SamePair<>(26, 28)
                )
        );
        ArrayList<SamePair<Integer>> actual = Parsing.quotationPairs(input);
        Assertions.assertEquals(expected, actual);

        String unclosed = "str \"woah\\\"";
        ArrayList<SamePair<Integer>> actualUnclosed = Parsing.quotationPairs(unclosed);
        Assertions.assertEquals(new ArrayList<>(), actualUnclosed);
    }

    @Test
    void bracePairs() {
        String simpleCommand = "cmd (0, 2, 1)";
        String multi = "() ([])[] ()";
        String broken = ")[3( ";

        Parsing.BracePairs bracePairs = Parsing.bracePairs(simpleCommand);
        Parsing.BracePairs multiBracePairs = Parsing.bracePairs(multi);
        Parsing.BracePairs brokenBracePairs = Parsing.bracePairs(broken);

        Parsing.BracePairs expectedBracePairs = new Parsing.BracePairs(
                new ArrayList<>(List.of(new SamePair<>(4, 12))),
                new ArrayList<>(),
                new ArrayList<>()
        );
        Parsing.BracePairs multiBracePairsExpected = new Parsing.BracePairs(
                new ArrayList<>(
                        List.of(
                                new SamePair<>(0, 1), new SamePair<>(4, 5),
                                new SamePair<>(3, 6), new SamePair<>(7, 8),
                                new SamePair<>(10, 11)
                        )
                ),
                new ArrayList<>(),
                new ArrayList<>()
        );
        Parsing.BracePairs brokenBracePairsExpected = new Parsing.BracePairs(
                new ArrayList<>(),
                new ArrayList<>(List.of(
                        new Pair<>(1, '['),
                        new Pair<>(3, '(')
                )),
                new ArrayList<>(Collections.singletonList(
                        new Pair<>(0, ')')
                ))
        );

        Assertions.assertEquals(expectedBracePairs, bracePairs);
        Assertions.assertEquals(multiBracePairsExpected, multiBracePairs);
        Assertions.assertEquals(brokenBracePairsExpected, brokenBracePairs);

    }

    @Test
    void toCamelCase() {

        Assertions.assertEquals("genericCase", Parsing.toCamelCase("GENERIC_CASE"));

        Assertions.assertEquals("nounderscore", Parsing.toCamelCase("NOUNDERSCORE"));

        Assertions.assertEquals("willThisbreak2", Parsing.toCamelCase("WILL_THISBREAK_2"));

        Assertions.assertEquals("underscoreExists", Parsing.toCamelCase("UNDERSCORE__EXISTS"));
    }

    @Test
    void removeHTML() {

        Assertions.assertEquals(
                "hello. ", Parsing.removeHTML(new InlineHTML("hello.")
                        .bold().italic().font(Color.ORANGE)
                        .add(" ").wrapHTML())
        );

        Assertions.assertEquals(
                "Recover My String!&lt;",
                Parsing.removeHTML(
                        InlineHTML.htmlOf(
                                new InlineHTML("Recover")
                                        .bold().font("23"),
                                new InlineHTML(" ")
                                        .paragraph(),
                                new InlineHTML("My Str")
                                        .subscript().addStyle(
                                                new LinkedHashMap<>(Map.of("flexbox", "shii"))
                                        ).paragraph().bold().subscript(),
                                new InlineHTML("ing!<", true)
                                        .font(Color.RED)
                        )
                )
        );

    }

    @Test
    void split() {
        String command = "prism (1, 0, 0) (20, 1, 2) \"my loyal p(rism\" ( ol\"";

        Assertions.assertEquals(
                new ArrayList<>(List.of(
                        "prism", "(1, 0, 0)", "(20, 1, 2)", "\"my loyal p(rism\"",
                        "( ol\""
                )),
                Parsing.split(command, ' ')
        );
    }
}