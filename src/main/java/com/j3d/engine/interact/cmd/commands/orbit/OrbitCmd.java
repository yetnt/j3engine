package com.j3d.engine.interact.cmd.commands.orbit;

import com.j3d.Static;
import com.j3d.engine.geometry.geo3d.rot.Rotation;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.layer.Layer;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.base.StatefulCommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.generic.CursorManager;
import com.j3d.ui.generic.CursorNames;
import com.j3d.ui.generic.J3DTheme;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.utility.generators.JLabelRichText;

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
public class OrbitCmd extends Command implements StatefulCommand<Rotation> {
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
        this.aliases("o", "rot").parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (!CommandsManager.isCurrentStatefulRunning(this)) return;

        run(this, "orbitCmd", Static.camera.getRotation().copy(), logLabel);
    }

    @Override
    public void onStart(Rotation object, SafeJLabel label) {
        orbitMouseOwner.requestOwnership();
        CursorManager.set(CursorNames.HAND_GRAB);
        Static.mainPanel.repaint();
        Static.sceneManager.scheduleOverlap(orbitCmdUUID, c -> label.setText(
                "Use the mouse to orbit the camera around. | "+SafeJLabel.EMPH+": "
                        + SafeJLabel.EMPH + SafeJLabel.EMPH,
                "Sensitivity",
                new JLabelRichText(Settings.cameraProperties.orbitSensitivity.getValue().toString())
                        .font(J3DTheme.TEXT_SECONDARY.color().brighter(), "8"),
                " units per mouse drag"
        ));
        Static.sceneManager.layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().add(orbitCmdUUID, (t2, d) -> {
                    Static.camera.lookAt(t2.getCentroid());
                }));
    }

    public void cleanup(SafeJLabel label) {
        EngineFrame.setMouseOwner(null);
        CursorManager.setDefault();
        Static.sceneManager.removeOverlap(orbitCmdUUID);
        label.clear();
        CommandsManager.clearCurrent();
        Static.sceneManager.layers.stream()
                .flatMap(Layer::usableLayersStream)
                .forEach(t -> t.getIdentity().remove(orbitCmdUUID));
    }

    @Override
    public void onEnter(ActionEvent e, Rotation object, SafeJLabel label) {
        cleanup(label);
        Static.getLog().println(
                "Camera was rotated from: " + object.toLogString() + " to " + Static.camera.getRotation().toLogString()
        );
        // done
    }

    @Override
    public  void onEsc(ActionEvent e, Rotation object, SafeJLabel label) {
        Static.camera.setRotation((Rotation) object);
        cleanup(label);
    }

}
