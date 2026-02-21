package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.commands.*;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.selection.SelectionCmd;
import com.j3d.engine.interact.cmd.commands.thing.ThingCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandsManager {

    public HashMap<ArrayList<String>, Command> commands = new HashMap<>();

    public CommandsManager() {
        LineCmd lineCmd = new LineCmd();
        PointCmd pointCmd = new PointCmd();
        TriCmd triCmd = new TriCmd();
        DebugCmd debugCmd = new DebugCmd();
        ThingCmd thingCmd = new ThingCmd();
        SelectionCmd selectionCmd = new SelectionCmd();
        LookAtCmd lookAtCmd = new LookAtCmd();
        commands.put(lineCmd.aliases, lineCmd);
        commands.put(pointCmd.aliases, pointCmd);
        commands.put(triCmd.aliases, triCmd);
        commands.put(debugCmd.aliases, debugCmd);
        commands.put(thingCmd.aliases, thingCmd);
        commands.put(selectionCmd.aliases, selectionCmd);
        commands.put(lookAtCmd.aliases, lookAtCmd);
    }

    public static Command getCommand(String name) {
        for (Map.Entry<ArrayList<String>, Command> entry : new CommandsManager().commands.entrySet()) {
            List<String> aliases = entry.getKey();
            if (aliases.contains(name)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
