package com.j3d.engine.interact.cmd;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.LookAtCmd;
import com.j3d.engine.interact.cmd.commands.TeleportCmd;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.engine.EngineCmd;
import com.j3d.engine.interact.cmd.commands.orbit.OrbitCmd;
import com.j3d.engine.interact.cmd.commands.ExplodeCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.engine.interact.cmd.commands.uicmd.UICmd;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class for all top-level commands.
 *
 * <p>This class holds a single instance of each concrete {@link Command} implementation.
 * This allows commands to be constructed once and then reused, avoiding repeated
 * object creation. The instances are then registered with the {@link CommandsManager}.
 *
 * @author Lehlogonolo Poole
 * @see Command
 * @see CommandsManager
 */
public class Commands {
    public DebugCmd debug = new DebugCmd();
    public TransformCmd transform = new TransformCmd();
    public LookAtCmd lookAt = new LookAtCmd();
    public TeleportCmd teleport = new TeleportCmd();
    public OrbitCmd orbit = new OrbitCmd();
    public EngineCmd engine = new EngineCmd();
    public ExplodeCmd explodeCmd = new ExplodeCmd();
    public UICmd uiCmd = new UICmd();

    /**
     * Default (empty) constructor
     */
    public Commands() {
        Static.getLog().cmdPrintln("Commands populated with " + getCommands().size() + " commands.");
        Static.getLog().cmdPrintln(
                "(" + getCommands()
                        .stream()
                        .map((c) -> c.aliases.getFirst())
                        .reduce((a, b) -> a + ", " + b)
                + ")"
        );
    }

    /**
     * Returns a list of all top-level command instances.
     *
     * @return an {@link ArrayList} containing all registered {@link Command} instances
     */
    public ArrayList<Command> getCommands() {
        return new ArrayList<>(List.of(
                debug, transform, lookAt,
                teleport, orbit, engine,
                explodeCmd, uiCmd
        ));
    }
}
