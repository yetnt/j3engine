package com.j3d.engine.interact.macros;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.selection.SelectionQuery;
import com.j3d.engine.interact.selection.SelectionType;
import com.j3d.engine.interact.selection.SelectionUI;
import com.j3d.engine.interact.selection.SelectionUtils;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.rot.Rotation;
import com.j3d.engine.react.events.EventListener;
import com.j3d.engine.react.events.EventPayload;
import com.j3d.engine.react.events.EventType;
import com.j3d.engine.react.events.payloads.StatefulCommandCompletedPayload;
import com.j3d.utility.generic.tuple.SamePair;
import com.j3d.utility.generic.tuple.Triple;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MacroRunner implements EventListener {

    private Macro macro;
    private ArrayDeque<MacroLine> deque = new ArrayDeque<>();
    private boolean running = false;
    private boolean expectEvent = false;

    public void run(String name) {
        macro = StaticRefs.getMacroUtils().getMacros().get(name);
        if (macro == null) return;
        running = true;
        deque = new ArrayDeque<>(macro.getMacroLines());

        popOne();
    }

    public void clean() {
        // purge ALL
        running = false;
        expectEvent = false;
        macro = null;
        deque = new ArrayDeque<>();
    }

    private void popOne() {
        MacroLine line = deque.poll();
        if (line == null) {
            clean();
            return;
        }

        boolean continueExec = true;

        switch (line.instructionType()) {
            case CAMERA -> cameraLine(line.instruction());
            case SELECTION -> selectLine(line.instruction());
            case COMMAND_EXEC -> {
                continueExec = commandLine(line.instruction());
            }
            case MOUSE_MOVE -> throw new UnsupportedOperationException(); // no.
        }

        if (continueExec)
            popOne();
    }

    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.STATEFUL_COMMAND_COMPLETED && expectEvent) {
            StatefulCommandCompletedPayload payload = (StatefulCommandCompletedPayload) properties;
            if (payload.getCompletionType() == StatefulCommandCompletedPayload.CompletionType.ENTER) {
                expectEvent = false;
                popOne();
            }
        }
    }

    private boolean commandLine(String line) {
        CommandParser cmdP = StaticRefs.getCommandParser();
        cmdP.setInputField(line);
        ArrayList<CmdToken> tokens = new ArrayList<>(cmdP.getTokens());
        ArrayList<TaggedArgValue<?>> tagged = new ArrayList<>(cmdP.getTaggedArguments());
        cmdP.setInputField("");

        ArrayList<Object> objs = tokens.stream().map(CmdToken::getParsedValue).collect(Collectors.toCollection(ArrayList::new));

        String cmdName = (String)objs.removeFirst(); // command name

        Command command = StaticRefs.getCommandManager().commandsAliasMap.get(cmdName);

        if (command == null) return true;

        cmdP.runMacro(command, objs, tagged); // run ze command

        if (command instanceof SemiStatefulCommand && CommandsManager.currentStatefulCommand.getClass() == command.getClass()) {
            // usually just checking whether its stateful or not is fine
            // however some that only implement semistateful may only do so to be stateful sometimes menaing they
            // never fire an event
            expectEvent = true;
            return false;
        }

        return true;
    }

    private void selectLine(String line) {
        String[] parts = line.split("\\|");

        String properties = parts[0].trim();
        SelectionUtils.InferredSelectionType inferredSelectionType = SelectionUtils.InferredSelectionType.valueOf(
                parts[1].trim()
        );

        String[] selection = properties.split("->");

        String fromStr =  selection[0].trim();
        String toStr = selection[1].trim();

        SamePair<String> pair = new SamePair<>(fromStr, toStr);

        SamePair<ScreenPoint> points =
                pair
                        .map((string) -> string.substring(1, string.length() - 1).trim())
                        .map((nums) -> {
                            String[] ns = nums.split(";");
                            return new SamePair<>(
                                    ns[0].trim(),
                                    ns[1].trim()
                            );
                        })
                        .map((s) -> s
                                    .map(Integer::parseInt)
                                    .mapTo(ScreenPoint::new)
                        );

        StaticRefs.getSceneManager().select(
                new SelectionQuery(
                        points.first, points.second,
                        switch (inferredSelectionType) {
                            case UNION -> SelectionType.UNION;
                            case SUBTRACT -> SelectionType.SUBTRACT;
                            case NONE ->
                                    SelectionUI.isStrict(new ScreenPoint[]{points.first, points.second})
                                            ? SelectionType.BOUNDS_STRICT : SelectionType.BOUNDS_SOFT;
                        }
                )
        );
    }

    private void cameraLine(String line) {
        String[] parts = line.split("\\|");
        String xyz = parts[0].replace("pos_xyz", "").trim();
        String xyzS = xyz.substring(1,  xyz.length() - 1).trim();
        String ypr = parts[1].replace("rot_pyr", "").trim();
        String yprS = ypr.substring(1,  ypr.length() - 1).trim();

        // already did this dumbass shit in Jaiva
        Triple<String> numsXyz = Triple.from(Token.splitByTopLevelComma(xyzS));
        Triple<String> numsYpr = Triple.from(Token.splitByTopLevelComma(yprS));

        Vector3  position = numsXyz.map(Double::parseDouble).mapTo(Vector3::new);
        Rotation rotation = numsYpr.map(Double::parseDouble).mapTo(Rotation::new);

        StaticRefs.getCamera().setPosition(position);
        StaticRefs.getCamera().setRotation(rotation);
    }

    public boolean isRunning() {
        return running;
    }
}
