package com.j3d.gen.docs.reader;

import com.j3d.gen.docs.reader.tokens.TLink;
import com.j3d.gen.docs.reader.tokens.TText;
import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.wrappers.*;
import com.j3d.utility.Parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class J3DocsReader {
    public static ArrayList<TWrapper> parseFile(File file) throws FileNotFoundException {
        if (!file.isFile()) return new ArrayList<>();
        if (!file.getPath().endsWith(".j3.md")) return new ArrayList<>();

        Scanner scanner = new Scanner(file);
        ArrayList<TWrapper> wrapper = read(scanner);
        scanner.close();
        return wrapper;
    }

    private static ArrayList<TWrapper> read(Scanner scanner) {
        ArrayList<TWrapper> wrappers = new ArrayList<>();
        ArrayList<String> arbitary = new ArrayList<>();
        boolean codeBlock = false,
                leaveNewLn = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            if (codeBlock && !line.startsWith("```")) {
                arbitary.add(line);
                continue;
            }
            if (line.startsWith("```")) {
                if (codeBlock) {
                    codeBlock = false;
                    wrappers.add(new TWCodeBlock(arbitary));
                    arbitary.clear();
                } else {
                    codeBlock = true;
                    // add the codeblock language
                    arbitary.add(line.substring(3));
                }
            } else if (line.equals("---")) {
                leaveNewLn = true;
            } else if (line.startsWith("#")) {
                // get amount of # at the start.
                int amount = 0;
                for (char c : line.toCharArray()) {
                    if (c == '#') amount++;
                    else break;
                }
                wrappers.add(new TWHeader(line.substring(amount).trim(), amount));
            } else if (line.startsWith("<")) {
                // handle html. delegate to the
                wrappers.add(new TWhtmlTag(line));
            } else {
                // parse everything else.
                ArrayList<String> lineContent = Parsing.split(
                        line,
                        ' '
                );
                ArrayList<TText> paragraphText = new ArrayList<>();
                for (String part : lineContent) {
                    TText text = unwrapPart(part);
                    String raw = text.getContent();

                    if (raw.startsWith("[") && raw.endsWith(")")) {
                        // possible a link.
                        String label = raw.substring(1, raw.indexOf("]"));
                        String url = raw.substring(raw.indexOf("(") + 1, raw.length() - 1);
                        paragraphText.add(TLink.fromText(text, label, url));
                    } else {
                        paragraphText.add(text);
                    }
                }
                if (wrappers.getLast() instanceof TWParagraph twp) {
                    twp.getParagraph().addAll(paragraphText);
                } else {
                    wrappers.add(new TWParagraph(paragraphText));
                }
                paragraphText.clear();
            }

            if (leaveNewLn) {
                leaveNewLn = false;
                wrappers.add(new TWLineSeparator());
            }
        }
        return wrappers;
    }

    private static TText unwrapPart(String part) {
        boolean bold = false, italic = false, inlineCode = false;
        while (sw(part)) {
            if (part.startsWith("**")) {
                bold = true;
                part = part.substring(2, part.length()-2);
            } else if (part.startsWith("_")) {
                italic = true;
                part = part.substring(1, part.length()-1);
            } else if (part.startsWith("`")) {
                inlineCode = true;
                part = part.substring(1, part.length()-1);
            }
        }
        return new TText(part)
                .setBold(bold)
                .setItalic(italic)
                .setInlineCode(inlineCode);
    }

    private static boolean sw(String part) {
        return part.startsWith("**") || part.startsWith("_")
                || part.startsWith("`");
    }
}
