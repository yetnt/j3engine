package com.j3d.engine.interact.macros;

public enum InstructionType {
    COMMAND_EXEC("cmd"), MOUSE_MOVE("mse"), SELECTION("sel"), CAMERA("cam");

    private final String id;
    InstructionType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static InstructionType fromId(String id) {
        for (InstructionType instructionType : InstructionType.values()) {
            if (instructionType.id.equals(id)) {
                return instructionType;
            }
        }
        return null;
    }
}
