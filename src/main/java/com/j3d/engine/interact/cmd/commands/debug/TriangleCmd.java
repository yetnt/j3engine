package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.StaticConfig;
import com.j3d.ui.SafeJLabel;

import java.util.ArrayList;
import java.util.List;

import static com.j3d.StaticRefs.getSceneManager();

/**
 * A no-arg subcommand of {@link DebugCmd} which simply toggles the normal of a selected triangle or triangles.
 * <p>
 *     Aliases: {@code tri}, {@code triangle}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     debug tri     - Toggles the normal of a selected triangle
 *     dbg triangle  - Toggles the normal of a selected triangle
 *     }</pre>
 * </p>
 * @see DebugCmd
 * @see Subcommand
 * @see GTri
 * @author Lehlogonolo Poole
 */
public class TriangleCmd extends Subcommand {
    public TriangleCmd() {
        super("tri", "Toggles the normal of a selected triangle or triangles.");
        aliases("triangle").parseUsages().noArgs();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);

        if (sceneManager.getSelected().isEmpty()) {
            logLabel.setText("No objects selected. Normals were cleared instead");
            StaticConfig.setShowNormals(false);
            return;
        }

        List<GTri> selected = sceneManager.getSelected()
                .stream()
                .filter(t -> t instanceof GTri)
                .map(t -> (GTri)t)
                .toList();
        if (selected.isEmpty()) {
            logLabel.setText("No triangles selected.");
            StaticConfig.setShowNormals(false);
            return;
        }

        selected.forEach(
                tri -> tri.showNorm = true
        );

    }
}
