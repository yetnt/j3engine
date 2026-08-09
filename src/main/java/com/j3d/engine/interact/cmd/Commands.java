package com.j3d.engine.interact.cmd;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.commands.*;
import com.j3d.engine.interact.cmd.commands.clipboard.*;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.engine.EngineCmd;
import com.j3d.engine.interact.cmd.commands.measure.MeasureCmd;
import com.j3d.engine.interact.cmd.commands.orbit.OrbitCmd;
import com.j3d.engine.interact.cmd.commands.qtrans.QuickTranslateCmd;
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
    public HelpCmd helpCmd = new HelpCmd();
    public PrismCmd prismCmd = new PrismCmd();
    public MeasureCmd measureCmd = new MeasureCmd();
    public ClipboardCmd clipboardCmd = new ClipboardCmd();
    public SelectCmd selectCmd = new SelectCmd();
    public QuickTranslateCmd quickTranslateCmd = new QuickTranslateCmd();

    /**
     * Default (empty) constructor
     */
    public Commands() {
        StaticRefs.getLog().cmdPrintln("Commands populated with " + getCommands().size() + " commands.");
        StaticRefs.getLog().cmdPrintln(
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
                explodeCmd, uiCmd, helpCmd,
                prismCmd, measureCmd, clipboardCmd,
                selectCmd, quickTranslateCmd
        ));
    }
}
