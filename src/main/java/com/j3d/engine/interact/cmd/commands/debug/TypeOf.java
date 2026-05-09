package com.j3d.engine.interact.cmd.commands.debug;

import com.j3d.Static;
import com.j3d.engine.interact.cmd.Any;
import com.j3d.engine.interact.cmd.args.Subcommand;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.util.SafeJLabel;

import java.util.ArrayList;

/**
 * A subcommand of {@link DebugCmd} which logs the type of the given argument
 * <p>
 *     Provides an optional second (third in context of its parent command) {@link TypedArg} which accepts
 *     any valid type given {@link Any}
 * </p>
 * <p>
 *     Aliases: {@code typeof}, {@code t}, {@code tof}, {@code type}, {@code ty}
 * </p>
 * <p>
 *     {@code typeof} also has the unique feature of, (if no concrete argument is provided) but a
 *     {@link TaggedArgValue} was provided, it will instead print the type of the (first) tagged argument
 *     provided
 * </p>
 * <p>
 *     Typical Usage:
 *     <pre>{@code
 *     debug tof 10             - Type: Integer
 *     dbg typeof #23:43:12#    - Type: Colour
 *     debug t huh              - Type: String
 *     d typeof yes             - Type: Boolean
 *     debug typeof (0, 0, 1)   - Type: Vector3
 *     dbg typeof v:(0, 0, 2)   - Type: TaggedArgValue<Vector3>
 *     dbg tof cond:false       - Type: TaggedArgValue<Boolean>
 *     d tof x:10 layer:"def"   - Type: TaggedArgValue<Integer>
 *     }</pre>
 * </p>
 * @see DebugCmd
 * @see Subcommand
 * @see TaggedArgValue
 * @see TypedArg
 * @see Any
 * @author Lehlogonolo Poole
 */
public class TypeOf extends Subcommand {
    public TypeOf() {
        super("typeof", "Returns the type of the input argument.");
        this.aliases("t", "tof", "type", "ty").args(
                new TypedArg("input", "The input to check the type of", false, Any.class)
        ).parseUsages();
    }

    @Override
    public void run(SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(logLabel, aliasUsed, args, taggedArgs);
        if (args.length != 1 && taggedArgs.isEmpty()) {
            logLabel.setText("Invalid arguments. Usage: typeof <input>");
            return;
        }
        Object input = args.length == 1 ? args[0] : taggedArgs.getFirst();
        String typeName = input.getClass().getSimpleName();
        if (input instanceof TaggedArgValue<?> g)
            typeName = typeName + "<" + g.type.getSimpleName() + ">";

        logLabel.setText("Type: " + typeName);
        Static.getLog().println("Type: " + typeName);
    }
}
