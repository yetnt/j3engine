package com.j3d.engine.interact.cmd;

import com.j3d.engine.interact.cmd.args.Argument;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.interact.cmd.args.TypedArg;
import com.j3d.ui.engine.CommandPalette;

/**
 * A marker interface representing a generic type.
 * This is only used by arguments (Specifically {@link TypedArg}) to signify that they can accept any type.
 * It does not contain any methods or fields.
 * @author Lehlogonolo Poole
 * @see TypedArg
 * @see Argument
 * @see Command
 * @see CommandPalette
 * @see CommandParser
 */
public interface Any {
}
