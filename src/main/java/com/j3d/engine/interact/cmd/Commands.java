package com.j3d.engine.interact.cmd;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.commands.*;
import com.j3d.engine.interact.cmd.commands.camera.CameraCmd;
import com.j3d.engine.interact.cmd.commands.clipboard.*;
import com.j3d.engine.interact.cmd.commands.create.CreateCmd;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.engine.EngineCmd;
import com.j3d.engine.interact.cmd.commands.join.JoinCmd;
import com.j3d.engine.interact.cmd.commands.measure.MeasureCmd;
import com.j3d.engine.interact.cmd.commands.transform.qtrans.QuickTranslateCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.engine.interact.cmd.commands.uicmd.UICmd;
import com.j3d.engine.interact.cmd.payloads.CommandFiredPayload;
import com.j3d.engine.interact.cmd.payloads.StatefulCommandCompletedPayload;
import com.j3d.engine.react.events.EventEmitter;
import com.j3d.engine.react.events.EventType;

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
public class Commands extends EventEmitter {
    public DebugCmd debug = new DebugCmd();
    public TransformCmd transform = new TransformCmd();
    public EngineCmd engine = new EngineCmd();
    public ExplodeCmd explodeCmd = new ExplodeCmd();
    public UICmd uiCmd = new UICmd();
    public HelpCmd helpCmd = new HelpCmd();
    public PrismCmd prismCmd = new PrismCmd();
    public MeasureCmd measureCmd = new MeasureCmd();
    public ClipboardCmd clipboardCmd = new ClipboardCmd();
    public SelectCmd selectCmd = new SelectCmd();
    public QuickTranslateCmd quickTranslateCmd = new QuickTranslateCmd();
    public CameraCmd camera = new CameraCmd();
    public CreateCmd createCmd = new CreateCmd();
    public JoinCmd joinCmd = new JoinCmd();

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
                debug, transform, engine,
                explodeCmd, uiCmd, helpCmd,
                prismCmd, measureCmd, clipboardCmd,
                selectCmd, quickTranslateCmd, camera,
                createCmd, joinCmd
        ));
    }

    public void firedEvent(
            Command cmd,
            Invoker invoker,
            String aliasUsed,
            Object[] args,
            ArrayList<TaggedArgValue<?>> taggedArgs
    ) {
        broadcast(
                EventType.COMMAND_FIRED,
                new CommandFiredPayload(cmd, invoker, aliasUsed, args, taggedArgs)
        );
    }

    public void statefulCompleted(
            StatefulCommand<?> statefulCommand,
            boolean userHitEnter
    ) {
        broadcast(
                EventType.STATEFUL_COMMAND_COMPLETED,
                new StatefulCommandCompletedPayload(statefulCommand, userHitEnter)
        );
    }

}
