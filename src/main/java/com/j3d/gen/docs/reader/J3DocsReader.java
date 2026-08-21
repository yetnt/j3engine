package com.j3d.gen.docs.reader;

import com.j3d.gen.docs.reader.tokens.TLink;
import com.j3d.gen.docs.reader.tokens.TText;
import com.j3d.gen.docs.reader.tokens.TWrapper;
import com.j3d.gen.docs.reader.tokens.wrappers.*;
import com.j3d.storage.JarPath;
import com.j3d.ui.docs.DocsFrame;
import com.j3d.utility.Parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A utility class for parsing J3Engine's custom markdown-like documentation files (.j3.md).
 * It reads the file content and converts it into a structured list of {@link TWrapper} objects,
 * representing headers, paragraphs, code blocks, and other elements to be read by
 * {@link DocsFrame}
 * <p>
 * The parser supports:
 * <ul>
 *     <li>Headers (e.g., {@code # Header 1}, {@code ## Header 2})</li>
 *     <li>Horizontal rules ({@code ---})</li>
 *     <li>Code blocks (fenced with {@code ```})</li>
 *     <li>Inline HTML tags (e.g., {@code <tag src="...">})</li>
 *     <li>Paragraphs with inline formatting (bold {@code **text**}, italic {@code _text_}, inline code {@code `code`})</li>
 *     <li>Links ({@code [label](url)})</li>
 * </ul>
 * </p>
 * @see DocsFrame
 * @see TWrapper
 * @see TWHeader
 * @see TWParagraph
 * @see TWCodeBlock
 * @see TWLineSeparator
 * @see TWhtmlTag
 * @author Lehlogonolo Poole
 */
public class J3DocsReader {
    /**
     * Parses a given file and returns a list of TWrapper objects representing the document structure.
     * @param file The file to parse.
     * @return An ArrayList of TWrapper objects.
     */
    public static ArrayList<TWrapper> parseFile(JarPath file) {
//        if (!file.isFile()) return new ArrayList<>();
//        if (!file.getPath().endsWith(".j3.md")) return new ArrayList<>();



        ArrayList<TWrapper> wrappers;
        try {
            wrappers = file.readAs((i) -> {
                Scanner scanner = new Scanner(i);
                return read(scanner);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wrappers;
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
