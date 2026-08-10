package com.j3d.engine.interact.cmd.commands.orbit;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.math.rot.Rotation;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.scene.nodes.layer.Layer;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.theme.CursorManager;
import com.j3d.ui.theme.CursorNames;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.generators.JLabelRichText;
import com.j3d.utility.generic.Pair;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A no-args stateful command which orbits the camera around (itself) using {@link OrbitMouseOwner} to enable dragging
 * capabilities.
 * <p>
 *     As a {@link StatefulCommand}, {@code orbit} registers {@link KeyEvent#VK_ENTER} to finalise the rotation change
 *     and {@link KeyEvent#VK_ESCAPE} to abort the rotation change.
 * </p>
 * <p>
 *     Aliases: {@code orbit}, {@code rot}, {@code o}
 * </p>
 * @see OrbitMouseOwner
 * @see StatefulCommand
 * @see CommandsManager
 * @see Command
 * @author Lehlogonolo Poole
 */
public class OrbitCmd extends Command implements StatefulCommand<Pair<Vector3, Rotation>> {
    /**
     * The mouse owner for {@link OrbitCmd} to function
     */
    public static OrbitMouseOwner orbitMouseOwner = new OrbitMouseOwner();
    /**
     * The UUID which the orbit command uses to identify stuff registered by it. i.e. events or keybinds
     */
    public UUID orbitCmdUUID = UUID.randomUUID();

    public OrbitCmd() {
        super("orbit", "Orbits the camera around itself");
        this.aliases("o", "rot").parseUsages().addNoArgUsage();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);
        if (!CommandsManager.isCurrentStatefulRunning(this)) return;

        run(this, "orbitCmd",
                new Pair<>(
                        StaticRefs.getCamera().getPosition().copy(),
                        StaticRefs.getCamera().getRotation().copy()
                ), logLabel);
    }

    @Override
    public void onStart(Pair<Vector3, Rotation> object, SafeJLabel label) {
        orbitMouseOwner.requestOwnership();
        orbitMouseOwner.sendMouseToCentre();
        CursorManager.set(CursorNames.HAND_GRAB);
        StaticRefs.getMainPanel().repaint();
        StaticRefs.getSceneManager().scheduleOverlap(orbitCmdUUID, c -> label.setText(
                "Use the mouse to orbit the camera around. | "+SafeJLabel.EMPH+": "
                        + SafeJLabel.EMPH + SafeJLabel.EMPH,
                "Sensitivity",
                new JLabelRichText(Settings.cameraProperties.orbitSensitivity.getValue().toString())
                        .font(J3DTheme.TEXT_SECONDARY.color().brighter(), "8"),
                " units per mouse drag"
        ));
        StaticRefs.getSceneManager().layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().add(orbitCmdUUID, (t2, d) -> {
                    StaticRefs.getCamera().lookAt(t2.getCentroid());
                }));
    }

    public void cleanup(SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        CursorManager.setDefault();
        StaticRefs.getSceneManager().removeOverlap(orbitCmdUUID);
        label.clear();
        CommandsManager.clearCurrent();
        StaticRefs.getSceneManager().layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().remove(orbitCmdUUID));
    }

    @Override
    public void onEnter(ActionEvent e, Pair<Vector3, Rotation> object, SafeJLabel label) {
        cleanup(label);
        StaticRefs.getLog().println(
                "Camera was rotated from: pos-" + object.first.toCommandPaletteString() + " rot-" + object.second.toLogString() + " to " + StaticRefs.getCamera().getRotation().toLogString()
        );
        // done
    }

    @Override
    public  void onEsc(ActionEvent e, Pair<Vector3, Rotation> object, SafeJLabel label) {
        StaticRefs.getCamera().setPosition(object.first);
        StaticRefs.getCamera().setRotation(object.second);
        cleanup(label);
    }

}
