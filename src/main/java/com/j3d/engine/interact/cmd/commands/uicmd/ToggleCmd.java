package com.j3d.engine.interact.cmd.commands.uicmd;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.args.ArgSet;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.react.history.History;
import com.j3d.ui.SafeJLabel;
import com.j3d.ui.engine.floating.DebugPanel;
import com.j3d.ui.engine.floating.tree.LayerTree;

import java.util.ArrayList;

/**
 * A subcommand of {@link UICmd} which toggles the visibility of various UI floating panels.
 * <p>
 *     It provides a required argument that specifies which floating panel to toggle.
 * </p>
 * <p>
 *     Aliases: {@code t}, {@code tog}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     ui toggle layers     - Toggles the visibility of the Layer Tree panel.
 *     gui t history        - Toggles the visibility of the History panel.
 *     swing toggle debug   - Toggles the visibility of the Debug panel.
 *     }</pre>
 * </p>
 * @see UICmd
 * @see Subcommand
 * @see ArgSet
 * @see LayerTree
 * @see History
 * @see DebugPanel
 * @author Lehlogonolo Poole
 */
public class ToggleCmd extends Subcommand {

    ArgSet argSet = new ArgSet("floatingPanel",
            "the floating panel to target",
            false,
            "layers", "l",
            "history", "h",
            "debug", "dbg", "d",
            "properties", "prop", "p",
            "grid2d", "grid", "g");

    public ToggleCmd() {
        super("toggle", "Toggles a floating panel to be visible or hidden.");
        this.aliases("t", "tog").args(
                argSet
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 || !(args[0] instanceof String targetPanel)) {
            logLabel.setText("Invalid arguments. Usage: ui " + aliasUsed + " " + argSet.toUseString());
            return;
        }

        switch (targetPanel) {
            case "layers", "l" ->
                StaticRefs.getLayerTree().toggleHidden();
            case "history", "h" -> History.panel.toggleHidden();
            case "debug", "dbg", "d" ->
                StaticRefs.getDebugPanel().toggleHidden();
            case "p", "properties", "prop" ->
                StaticRefs.getPropertiesPanel().toggleHidden();
            case "g", "grid", "grid2d" ->
                    StaticRefs.getGrid2DPanel().toggleHidden();
        }

    }
}
