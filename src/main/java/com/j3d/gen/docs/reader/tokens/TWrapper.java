package com.j3d.gen.docs.reader.tokens;

import com.j3d.gen.docs.reader.J3DocsReader;
import com.j3d.gen.docs.reader.tokens.wrappers.*;

import java.util.ArrayList;

/**
 * A base class for all token wrappers used in the J3Engine documentation reader.
 * <p>
 * This class provides a common structure for different types of documentation elements,
 * such as headers, paragraphs, code blocks, and HTML tags, by encapsulating their
 * {@link WrapperType} and raw content.
 * </p>
 * @author Lehlogonolo Poole
 * @see WrapperType
 * @see J3DocsReader
 * @see TWHeader
 * @see TWParagraph
 * @see TWCodeBlock
 * @see TWLineSeparator
 * @see TWhtmlTag
 * @author Lehlogonolo Poole
 */
public class TWrapper {
    /**
     * The type of the wrapper.
     */
    private final WrapperType type;
    /**
     * The raw content of the wrapper, stored as a list of strings.
     */
    private final ArrayList<String> rawContent;

    /**
     * Constructs a new TWrapper with the specified type and raw content.
     *
     * @param type The type of the wrapper.
     * @param rawContent The raw content of the wrapper as an ArrayList of strings.
     */
    public TWrapper(WrapperType type, ArrayList<String> rawContent) {
        this.type = type;
        this.rawContent = rawContent;
    }

    /**
     * Returns the raw content of the wrapper.
     *
     * @return An ArrayList of strings representing the raw content.
     */
    public ArrayList<String> getRawContent() {
        return rawContent;
    }

    /**
     * Returns the type of the wrapper.
     *
     * @return The WrapperType of the wrapper.
     */
    public WrapperType getType() {
        return type;
    }
}
