package com.j3d.engine.interact.macros;

import java.io.IOException;

/**
 * A single line of a {@link Macro} with an {@link InstructionType} and the string representing the instruction.
 *
 * @see Macro
 * @see InstructionType
 * @see MacroRecorder
 * @see MacroRunner
 * @see MacroUtils
 * @author Lehlogonolo Poole
 *
 * @param instructionType The instruction type
 * @param instruction The instruction
 */
public record MacroLine(
        InstructionType instructionType,
        String instruction
) {
    @Override
    public String toString() {
        return instructionType.getId() + " : " + instruction;
    }

    public static MacroLine read(String instructionType, String instruction) {
        InstructionType type = InstructionType.fromId(instructionType);
        return new MacroLine(type, instruction);
    }
}
