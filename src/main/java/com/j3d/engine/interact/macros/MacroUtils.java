package com.j3d.engine.interact.macros;

import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.camera.CameraCmd;
import com.j3d.engine.interact.cmd.commands.macro.MacroCmd;

import java.util.ArrayList;
import java.util.List;

public abstract class MacroUtils {
    public static final List<Class<? extends Command>> commandsToIgnore = List.of(
            CameraCmd.class, MacroCmd.class
    );
}
