package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.SceneManager;
import com.j3d.engine.geometry.geo2d.graphics.GLine;
import com.j3d.engine.geometry.geo2d.graphics.GPoint;
import com.j3d.engine.geometry.geo2d.graphics.GTri;
import com.j3d.engine.geometry.geo3d.Thing;
import com.j3d.engine.interact.cmd.args.TaggedArgUtil;
import com.j3d.engine.layer.Layer;
import com.j3d.engine.react.actions.VoidAction;
import com.j3d.ui.SafeJLabel;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.ui.dialog.AreYouSure;
import com.j3d.utility.generators.JLabelRichText;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * A command which makes the explodes the geometry structure of whatever.
 * <p>
 *     This has positional arguments, but makes use of tagged arguments to work,
 *     specifically the {@code thing:"string"/thing="string"} or the
 *     {@code layer:"string"/layer="string"}
 * </p>
 * <p>
 *     While tagged arguments do not require a strict position, if both tagged arguments exist
 *     it will check explicit for the {@code thing} tagged argument before {@code layer}.
 *     And otherwise, if no tagged arguments are provided or tagged arguments that this command
 *     does not check for, it is treated as exploding everything.
 * </p>
 * <p>
 *     Aliases: {@code explode}, {@code expl}, {@code ex}, {@code destruct}
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     explode                      - Explodes All Things in All layers
 *     expl thing:"Cube"            - Explodes the Thing labelled "Cube"
 *     explode layer:"l"            - Explodes All the Things in the layer "l"
 *     explode layer:"a" thing="b"  - Explodes the Thing labelled "b"
 *     }</pre>
 * </p>
 * @implNote This command is undoable. Geometry destruction is one that cannot be undone, (specifically
 *     due to events being registered both ways). Triangles are destroyed and dereferenced, and so
 *    are lines.
 * @see Command
 * @see TaggedArgValue
 * @see TaggedArgUtil
 * @see Thing
 * @see Layer
 * @author Lehlogonolo Poole
 */
public class ExplodeCmd extends Command{

    public ExplodeCmd() {
        super("explode", "Destroy given geometry of triangles and lines into constituent points. (Uses tagged arguments)");
        this.aliases("expl", "ex", "destruct").parseUsages();
        this.addNoArgUsage();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        AreYouSure aysDialogue = new AreYouSure(
                StaticRefs.getMainFrame(), true // sets to modal
                , JLabelRichText.htmlOf(
                        new JLabelRichText(
                                "This command CANNOT be undone."
                                        + JLabelRichText.LINE_BREAK
                                        + "This means any previous history before this will be unreachable.")
        )
        );
        aysDialogue.setVisible(true);
        if (aysDialogue.canProceed())
            explode(logLabel, args, taggedArgs);
    }

    private void explode(SafeJLabel logLabel, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        // if there aren't any taggedArgs, explode it all
        if (taggedArgs.isEmpty()) {
            explodeAll();
            return;
        }

        // look for a specific Thing to explode via tagged argz
        TaggedArgValue<String> t1 =
                TaggedArgUtil.getTaggedArg(taggedArgs, "thing", String.class);
        if (t1 != null) {
            Thing t = StaticRefs.getSceneManager().findThing(t1.value);
            if (t == null) {
                logLabel.setText("No thing with the name \"" + t1.value + "\" exists.");
                return;
            }
            explode(t);
            addHistory("thing-" + t1.value);
            return;
        }

        // look for a specific Layer to explode things.
        TaggedArgValue<String> t2 =
                TaggedArgUtil.getTaggedArg(taggedArgs, "layer", String.class);
        if (t2 != null) {
            Layer l = StaticRefs.getSceneManager().layers.find(t2.value);
            if (l == null) {
                logLabel.setText("No layer with the name \"" + t2.value + "\" exists.");
                return;
            }
            l.forEach(this::explode);
            addHistory("layer-" + t2.value);
            return;
        }

        // otherwise, explode it all.
        explodeAll();
    }

    private void explodeAll() {
        StaticRefs.getSceneManager().layers.forEach(layer -> {
            layer.forEach(this::explode);
        });
        addHistory("all-layers");
    }

    private void explode(Thing thing) {
        HashSet<GPoint> points = new HashSet<>();
        HashSet<GLine> lines = new HashSet<>();
        new ArrayList<>(thing.getObjects())
                .stream()
                .filter(o -> o instanceof GTri)
                .map(o -> (GTri)o)
                .forEach((tri) -> {
                    lines.addAll(tri.getLegStream().collect(Collectors.toCollection(ArrayList::new)));
                            points.addAll(tri.explode(thing));
                        }
                );
        StaticRefs.getSceneManager().select(thing);
    }

    private void addHistory(String operation) {
        SceneManager.history.add(
                new VoidAction() {
                    final LocalTime now = LocalTime.now();
                    @Override
                    public Void run() {
                        return null;
                    }

                    @Override
                    public void undo() {
                    }

                    @Override
                    public boolean isReversible() {
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "explode:" + operation;
                    }

                    @Override
                    public LocalTime getTime() {
                        return now;
                    }
                }
        );
    }
}
