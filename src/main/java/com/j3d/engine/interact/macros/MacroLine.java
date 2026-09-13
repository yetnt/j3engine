package com.j3d.engine.interact.macros;

import java.io.IOException;

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
