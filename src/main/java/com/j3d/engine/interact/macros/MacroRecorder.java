package com.j3d.engine.interact.macros;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.commands.macro.RecordCmd;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.CameraUpdatedEventPayload;
import com.j3d.engine.react.events.payloads.CommandFiredPayload;
import com.j3d.engine.react.events.payloads.SelectionEventPayload;
import com.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;

/**
 * A single-instantiated class with the purpose of recording engine events to become a single {@link Macro}.
 * <p>
 *     How it works is, it listens for the following events:
 *     <ul>
 *         <li>{@link EventType#CAMERA_MOVED} to track camera changes</li>
 *         <li>{@link EventType#COMMAND_FIRED} to track command invocations (only the base invocation since it'll have the subcommand as arguments)</li>
 *         <li>{@link EventType#X_SELECTED} to track selection</li>
 *     </ul>
 *     and simply formats them. The reverse of {@link MacroRunner}
 * </p>
 * @see Macro
 * @see MacroLine
 * @see MacroRunner
 * @see MacroUtils
 * @see EventType#COMMAND_FIRED
 * @see EventType#CAMERA_MOVED
 * @see EventType#X_SELECTED
 *
 * @author Lehlogonolo Poole
 */
public class MacroRecorder implements EventListener {

    /**
     * The ordered list of instructions.
     */
    private ArrayList<MacroLine> instructions = new ArrayList<>();
    /**
     * The name of the macro provided by {@link RecordCmd}
     */
    private String name = null;
    /**
     * Boolean indicating whether the macro is running or not
     */
    private boolean running = false;

    /**
     * Package-private default constructor (Only {@link MacroUtils} can instantiate it)
     */
    MacroRecorder() {}

    /**
     * Begins recording engine events to save as macro instructions. This serves as the start of recording a macro.
     * @implSpec Ensure the class is not already running a macro or else this will cause issues.
     * @param name The name of the new macro
     */
    public void record(String name) {
        running = true;
        this.name = name;
        StaticRefs.getLog().println("[MR] Started.");
    }

    /**
     * Stops recording the macro, clears all state such that a new macro can be recorded and returns a macro of the recorded instructions.
     * @return The recorded macro, unless the class was never running then null.
     */
    public Macro stop() {
        if (!running) return null;
        running = false;
        String n = name;
        name = null;
        ArrayList<MacroLine> result = new ArrayList<>(instructions);
        instructions.clear();
        StaticRefs.getLog().println("[MR] Stopped.");
        return new Macro(n, result);
    }

    /**
     * Listens for the 3 events listed in the {@link MacroRecorder} doc and applies human readable formatting if the recorded class instance
     * does not provide a specialized macro-line method.
     * @param event The type of event
     * @param properties The given event payload
     * @param <K> K.
     *
     * @see EventType#X_SELECTED
     * @see EventType#CAMERA_MOVED
     * @see EventType#COMMAND_FIRED
     */
    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (!running) return;

        if (properties instanceof CommandFiredPayload payload) {
            Invoker invoker = payload.getInvoker();

            if (MacroUtils.commandsToIgnore.contains(payload.getCommand().getClass()))
                return;

            if (invoker.isUser() || invoker.isEngine()) {

                String line = CmdToken.toStr(payload.getArgsCopy());
                MacroLine mLine = new MacroLine(InstructionType.COMMAND_EXEC, (payload.emitter.aliases.getFirst() + " " + line).trim());

                instructions.add(mLine);

                StaticRefs.getLog().println("\t -> [*MR-CMD] " + mLine);
            }
        } else if (properties instanceof SelectionEventPayload selectionEventPayload) {
            SamePair<ScreenPoint> points = selectionEventPayload.getScreenPoints();

            String line = points.getFirst().toMacroPoint() + " -> " + points.getSecond().toMacroPoint() + " | " + selectionEventPayload.getInferredSelectionType().name();
            MacroLine mLine = new MacroLine(InstructionType.SELECTION, line);

            instructions.add(mLine);

            StaticRefs.getLog().println("\t -> [*MR-SEL] " + mLine);
        } else if (properties instanceof CameraUpdatedEventPayload cameraUpdatedEventPayload) {
            // pos_xyz(x, y, z)
            String pos = "pos_xyz" + cameraUpdatedEventPayload.getPosition().toCommandPaletteString();
            // rot_pyr(y, p, r)
            String rot = "rot_pyr" + cameraUpdatedEventPayload.getRotation().toMacroString();

            String line = pos + " | " + rot;
            MacroLine macroLine = new MacroLine(InstructionType.CAMERA, line);

            instructions.add(macroLine);

            StaticRefs.getLog().println("\t -> [*MR-CAM] " + macroLine);
        }
    }

    public boolean isRecording() {
        return running;
    }
}
