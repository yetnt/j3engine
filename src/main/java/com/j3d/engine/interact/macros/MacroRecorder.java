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
import com.j3d.utility.generic.tuple.SamePair;

import java.util.ArrayList;

// TODO: document
public class MacroRecorder implements EventListener {

    ArrayList<MacroLine> instructions = new ArrayList<>();
    String name = null;
    boolean running = false;

    public MacroRecorder() {
        StaticRefs.getLog().println("Macro Recorder Instance created.");
    }

    public void record(String name) {
        running = true;
        this.name = name;
        StaticRefs.getLog().println("[MR] Started.");
    }

    public ArrayList<MacroLine> stop() {
        running = false;
        ArrayList<MacroLine> result = new ArrayList<>(instructions);
        instructions.clear();
        StaticRefs.getLog().println("[MR] Stopped.");
        return result;
    }

    public String getName() {
        String n = name;
        name = null;
        return n;
    }

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

            String line = points.first.toMacroPoint() + " -> " + points.second.toMacroPoint() + " | " + selectionEventPayload.getInferredSelectionType().name();
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
