/**
 * Holds all possible Argument definitions that a command can use (except {@link com.j3d.engine.interact.cmd.args.TaggedArgUtil})
 * <h1>Argument Definitions</h1>
 * <p>
 *     Arguments are more so used to provide information to the user about what the Command expects
 *     more than it is type checking, as the Command still has to do type checking of the concrete objects
 *     before proceeding. All arguments implement the {@link com.j3d.engine.interact.cmd.args.Argument}
 *     base interface marking them as an argument.
 *     <p>
 *         The optionality of arguments are defined by the command in the defintion (except for {@link com.j3d.engine.interact.cmd.args.Subcommand}
 *         which is always required), however an optional argument cannot be between required arguments, they can
 *         only show up at the end of Command definitions.
 *     </p>
 *     The following are valid arguments:
 *     <ul>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.args.TypedArg} a generic argument which can take
 *             either a single type or multiple types. If it takes multiple types it creates a tree
 *             structure of the command for possible usage types. e.g.:
 *             <pre>{@code
 *             "engine" () {
 *                 new TypedArg(boolean),
 *                 new TypedArg(string)
 *             }
 *             // becomes
 *             engine <boolean> <string>
 *             }</pre>
 *             whereas if i had multiple types
 *             <pre>{@code
 *             "engine" () {
 *                 new TypedArg(boolean, Vector3, number),
 *                 new TypedArg(string)
 *             }
 *             // becomes
 *             engine <boolean> <string>
 *             engine <Vector3> <string>
 *             engine <number> <string>
 *             }</pre>
 *             This doesn't actually affect the command itself, but this is the output
 *             shown to the user.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.args.ArgSet} is a special type of argument which defines
 *             a closed list of string values that the argument can take. This functions almost identical
 *             to a {@link com.j3d.engine.interact.cmd.args.TypedArg} that takes a {@code string} however
 *             differs in that it tells the user there's a specific set that they are allowed to input instead
 *             of any string. A rigid argument.These arguments are denoted by
 *             the square bracket syntax followed by a list of strings. e.g.
 *             <pre>{@code
 *             "engine" () {
 *                 new ArgSet("clear", "remove"),
 *                 new TypedArg(boolean)
 *             }
 *             // becomes
 *             engine [clear|remove] <boolean>
 *             }</pre>
 *             This argument is special as it also provides a method to check if a given input
 *             matches the rigid list, so usually instead of defining this argument within the
 *             constructor of a Command, it's better to have it be a private field for validation.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.args.Subcommand} is probably the most important
 *             type of argument, as it is itself a Command, functions exactly like a Command, except
 *             it is only called by its head Command or Subcommand and is itself an Argument, allowing
 *             complex chaining of commands. e.g.
 *             If we have the following subcommands:
 *             <pre>{@code
 *             "clear" () {
 *                 new TypedArg(boolean),
 *                 new ArgSet("all", "selected")
 *             }
 *             clear <boolean> [all|selected]
 *             "close" () {} // no arguments
 *             close
 *             "recalc" () {
 *                 new TypedArg(Vector3, number)
 *                 new TypedArg(number)
 *             }
 *             recalc <Vector3> <number>
 *             recalc <number> <number>
 *             }</pre>
 *             and the head command:
 *             <pre>{@code
 *             "engine" () {
 *                 new ClearSubcommand(),
 *                 new CloseSubcommand(),
 *                 new RecalcSubcommand()
 *             }
 *             }</pre>
 *             The usage strings of the subcommands themselves are generated first, and simply appended
 *             to the head command/subcommand and goes up the chain until we get to the base Command.
 *             <pre>{@code
 *             engine clear <boolean> [all|selected]
 *             engine close
 *             engine recalc <Vector3> <number>
 *             engine recalc <number> <number>
 *             }</pre>
 *         </li>
 *     </ul>
 * </p>
 * <h2>Tagged Arguments</h2>
 * <p>
 *     This is a special type of argument, which is not part of the main argument list and can be
 *     typed by the user at any time. All Commands accept a variadic amount of tagged arguments, and
 *     there is a specific set of tagged arguments which a user can use, defined within {@link com.j3d.engine.interact.cmd.args.TaggedArgUtil}.
 *     See documentation in {@link com.j3d.engine.interact.cmd.args.TaggedArgUtil} and {@link com.j3d.engine.interact.cmd.args.TaggedArgValue}
 *     for more information.
 * </p>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.interact.cmd.args;