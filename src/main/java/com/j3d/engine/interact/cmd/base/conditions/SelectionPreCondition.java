package com.j3d.engine.interact.cmd.base.conditions;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.base.PreCommandExecution;
import com.j3d.engine.interact.cmd.base.SemiStatefulCommand;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.engine.interact.input.keyboard.DefaultKeys;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.engine.interact.selection.SelectionManager;
import com.j3d.engine.react.events.*;
import com.j3d.ui.util.SafeJLabel;
import com.j3d.utility.generators.JLabelRichText;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

/**
 * A concrete implementation of {@link PreCommandExecution} which forces the user to have made
 * a selection before the command can execute.
 * <p>
 *     This implementation provides:
 *     <ul>
 *         <li>{@link KeyEvent#VK_ENTER} and {@link KeyEvent#VK_ESCAPE} to abort the selection requirement</li>
 *         <li>a direct implementation of {@link #getPassListener()}, {@link #getFailListener()}, {@link #getCondition()}, {@link #getLogText()} and {@link #getEventEmitterToAttachTo()}</li>
 *         <li>a singleton {@link Runnable} which executes the code when this passes</li>
 *         <li>a singleton {@link #commandEndCleanup} Runnable to otherwise provide extra cleanup that is required by the command</li>
 *     </ul>
 * </p>
 * @implSpec
 *      As noted in {@link PreCommandExecution}, implementors should make use of {@link SemiStatefulCommand}.
 *      <p>
 *          Implementors also need to call {@link #finaliseCleanup()} only when {@link #execute(SafeJLabel)} returns true,
 *          as to allow cleanup if the condition was met from the start.
 *      </p>
 * @see PreCommandExecution
 * @see SemiStatefulCommand
 * @see TransformCmd
 * @author Lehlogonolo Poole
 */
public class SelectionPreCondition implements PreCommandExecution {

    private final EventReactor passListener;
    private final EventReactor failListener;
    private final Runnable cleanup;
    private final Runnable commandEndCleanup;

    /**
     * Constructs a new {@code SelectionPreCondition}.
     *
     * @param success A {@link Runnable} to be executed when the selection condition is met.
     * @param extraCleanup A {@link Runnable} to be executed for additional clean-up when the condition check is finalised.
     */
    public SelectionPreCondition(Runnable success, Runnable extraCleanup) {
        passListener = new EventReactor() {
            @Override
            public <K> void onEvent(EventType event, EventPayload<K> properties) {
                if (getCondition().get()) {
                    cleanup.run();
                    success.run();
                } else failListener.onEvent(event, properties);
            }
        };
        failListener = new EventReactor() {
            @Override
            public <K> void onEvent(EventType event, EventPayload<K> properties) {
                cleanup.run();
                Static.commandParser.safeJLabel().clearHigher();
                Static.hoverLabel.error("No selection was made. Command Aborted");
            }
        };
        AbstractAction quit =
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        failListener.onEvent(null, null);
                    }
                };
        J3Key key = new J3Key("quitEnter", true).setKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)
        ).setAction(quit);
        Static.keybinds.registerJ3Key(key);
        DefaultKeys.DEFOCUS_COMMAND_PALETTE.getKey().replaceAction(quit);

        cleanup = () -> {
            extraCleanup.run();
            Static.keybinds.removeJ3Key(key.getId());
            DefaultKeys.DEFOCUS_COMMAND_PALETTE.getKey().resetAction();
        };
        commandEndCleanup = () -> {
            getEventEmitterToAttachTo().detach(failListener);
            getEventEmitterToAttachTo().detach(passListener);
        };
    }

    /**
     * Finalises the clean-up process by detaching the pass and fail listeners from the event emitter.
     * This should be called when the command has either successfully proceeded or been aborted.
     */
    public void finaliseCleanup() {
        commandEndCleanup.run();
    }

    @Override
    public EventReactor getPassListener() {
        return passListener;
    }

    @Override
    public EventReactor getFailListener() {
        return failListener;
    }

    @Override
    public EventEmitterInterface getEventEmitterToAttachTo() {
        return SelectionManager.selectionMouseOwner;
    }

    @Override
    public String getLogText() {
        return JLabelRichText.htmlOf(
                new JLabelRichText("Make a selection then "),
                new JLabelRichText("left click to continue").bold().underline(),
                new JLabelRichText("this command. "),
                new JLabelRichText("(Or, hit escape to abort)").underline()
        );
    }

    @Override
    public Supplier<Boolean> getCondition() {
        return new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !Static.sceneManager.getSelected().isEmpty();
            }
        };
    }
}
