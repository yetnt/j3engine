package com.j3d.engine.interact.macros;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.CmdToken;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.Commands;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.commands.macro.MacroCmd;
import com.j3d.engine.interact.cmd.commands.macro.RunCmd;
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
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.generic.tuple.SamePair;
import com.j3d.utility.generic.tuple.Triple;
import com.jaiva.tokenizer.tokens.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A single-instantiated class with the purpose of running a single {@link Macro} with many responsibilities
 * <p>
 *     A typical flow is:
 *     <ol>
 *         <li>Read one macro off the queue</li>
 *         <li>Execute it</li>
 *         <li>Repeat</li>
 *     </ol>
 *     However, in the case of {@link Macro} with an instruction type of {@link InstructionType#COMMAND_EXEC}, the executed command
 *     might be stateful (implement {@link SemiStatefulCommand}), meaning it may have not finished synchronously. In that case the
 *     order is as follows:
 *     <ol>
 *         <li>Read one macro off the queue</li>
 *         <li>Execute it</li>
 *         <li>
 *             Check if it was a command and it is stateful
 *             <p>
 *                 <ul>
 *                     <li>
 *                         If yes : Exit early and instead only continue if we receive the
 *                         {@link EventType#STATEFUL_COMMAND_COMPLETED} event from
 *                         {@link Commands} which denotes the command has completed, then repeat.
 *                     </li>
 *                     <li>
 *                         If no : Repeat as normal
 *                     </li>
 *                 </ul>
 *             </p>
 *         </li>
 *     </ol>
 * </p>
 * <p>
 *     This class can be accessed via {@link MacroUtils} however should only be sued by {@link MacroCmd}
 *     (specifically its recording subcommand {@link RunCmd})
 * </p>
 * @see EventListener
 * @see EventType#STATEFUL_COMMAND_COMPLETED
 * @see Macro
 * @see MacroLine
 * @see MacroRecorder
 * @see MacroUtils
 * @see MacroCmd
 * @see SemiStatefulCommand
 */
public class MacroRunner implements EventListener {

    // da fiels below get cleared once we finsihed running a macro
    /**
     * The current macro
     */
    private Macro macro;
    /**
     * The loglabel for {@link RunCmd} callback errors
     */
    private SafeJLabel logLabel;
    /**
     * The queue of instructions
     */
    private ArrayDeque<MacroLine> deque = new ArrayDeque<>();
    /**
     * Whether this macro is running or not
     */
    private boolean running = false;
    /**
     * Whether the macro should expect a {@link EventType#STATEFUL_COMMAND_COMPLETED} event to proceed execution.
     */
    private boolean expectEvent = false;

    /**
     * Package-private default constructor (Only {@link MacroUtils} can instantiate it)
     */
    MacroRunner() {}

    /**
     * Attempts to run a given macro by name. This serves as the start of running a macro and will run ther entire macro
     * to completion.
     * @implSpec Ensure the class is not already running a macro or else this will cause issues.
     * @param logLabel The log label for error purposes
     * @param name The name of the macro to run
     * @return boolean indicating whether the macro started running successfully.
     */
    public boolean run(SafeJLabel logLabel, String name) {
        macro = StaticRefs.getMacroUtils().getMacros().get(name);
        if (macro == null) return false;
        this.logLabel = logLabel;
        running = true;
        deque = new ArrayDeque<>(macro.getMacroLines());

        poll();
        return true;
    }

    /**
     * Cleans up all state to ensure {@link #run(SafeJLabel, String)} can be called for a new macro.
     */
    public void clean() {
        // purge ALL
        running = false;
        expectEvent = false;
        macro = null;
        logLabel = null;
        deque = new ArrayDeque<>();
    }

    /**
     * Retrieves the head of the instruction set and attempts to execute it.
     * @implSpec This is only called once as it is recursive and will call itself until the queue is empty.
     */
    private void poll() {
        MacroLine line = deque.poll();
        if (line == null) {
            logLabel.setText("Ran " + SafeJLabel.EMPH + " macro.", macro.getName());
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
            poll();
    }

    /**
     * Handles the event stuff of the macro. Specifically handles {@link EventType#STATEFUL_COMMAND_COMPLETED}
     * only if {@link #expectEvent} is {@code true}
     * @param event The type of event
     * @param properties The given event payload
     * @param <K> The event emitter. In this case it would be {@link Commands}
     */
    @Override
    public <K> void onEvent(EventType event, EventPayload<K> properties) {
        if (event == EventType.STATEFUL_COMMAND_COMPLETED && expectEvent) {
            expectEvent = false;
            poll();
        }
    }

    /**
     * Execute a given command-line by stealing what the command parser itself thinks the parsed arguments would be.
     * @param line The input line
     * @return true indicating that the next macro instruction invocation can be polled via {@link #poll()} otherwise false
     * indicating that {@link #expectEvent} is now true hence the next invocation will only happen once the event has been
     * emitted.
     * @see MacroRecorder
     * @see CommandParser
     * @see #expectEvent
     */
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

        if (command instanceof SemiStatefulCommand) {
            // usually just checking whether its stateful or not is fine
            // however some that only implement semistateful may only do so to be stateful sometimes menaing they
            // never fire an event

            // i wrote that then realised, subcommands will be the one who have statefulness recognized in some cases
            // so its basically imposible for us to check.
            // i mean i could go through each commands argument and see if a subcommand if a given command has stateful
            // but just for macro come on now
            if (CommandsManager.commandIsRunning()) {
                expectEvent = true;
                return false;
            }
        }

        return true;
    }

    /**
     * Parses and applies the selection
     * @param line The line input
     * @see MacroRecorder
     */
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

    /**
     * Parses and applies the camera movement. both position and rotation
     * @param line the line
     * @see MacroRecorder
     */
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

    /**
     * Whether the macro runner is itself running a macro or not
     * @return true if it's running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
