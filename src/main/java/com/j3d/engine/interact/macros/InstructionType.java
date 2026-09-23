package com.j3d.engine.interact.macros;

/**
 * The type of instruction for a given {@link MacroLine}.
 *
 * @see MacroLine
 * @see Macro
 * @see MacroRecorder
 * @see MacroRunner
 * @author Lehlogonlo Poole
 */
public enum InstructionType {
    /**
     * The line is a command execution string to be pasted into the command palette
     */
    COMMAND_EXEC("cmd"),
    /**
     * unused.
     */
    MOUSE_MOVE("mse"),
    /**
     * The line represents a selection box with a specific type
     */
    SELECTION("sel"),
    /**
     * The line represents a camera movement position.
     */
    CAMERA("cam");

    /**
     * The serialized form of the instruction type
     */
    private final String id;
    InstructionType(String id) {
        this.id = id;
    }

    /**
     * Gets the id of the instruction. The 3 character string which defines the line
     * @return The Instruction Id
     */
    public String getId() {
        return id;
    }

    /**
     * Converts the given 3-character string into an instruction type enum
     * @param id The incoming id
     * @return The enum which this id belongs to or null if not found.
     */
    public static InstructionType fromId(String id) {
        for (InstructionType instructionType : InstructionType.values()) {
            if (instructionType.id.equals(id)) {
                return instructionType;
            }
        }
        return null;
    }
}
