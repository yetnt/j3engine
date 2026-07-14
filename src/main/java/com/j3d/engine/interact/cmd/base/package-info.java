/**
 * The base for defining a command and how it executes.
 * <h1>Commands</h1>
 * <p>
 *     A {@link com.j3d.engine.interact.cmd.base.Command} by itself, is a stateless side effect producing
 *     base class, where concrete implementations override {@link com.j3d.engine.interact.cmd.base.Command#run(com.j3d.ui.SafeJLabel, java.lang.String, java.lang.Object[], java.util.ArrayList)}
 *     to do their specialised logic. Commands define their arguments within it's constructor super call
 *     and any aliases that relate to said command. The arguments are purely used for usage strings which
 *     form an important part of UX, giving user's a way to know the multiple ways a command can execute itself.
 * </p>
 * <p>
 *     Specific argument definitions can be found within {@link com.j3d.engine.interact.cmd.args}. However
 *     to summarise, arguments are split within 2 distinct roles:
 *     <ol>
 *         <li>A generic argument, so this is any argument that takes in some form of a type.</li>
 *         <li>A subcommand, this is a {@link com.j3d.engine.interact.cmd.base.Command} who is
 *         itself also an argument defined by {@link com.j3d.engine.interact.cmd.args.Subcommand}</li>
 *     </ol>
 *     These 2 argument definitions are mutually exclusive in context of the head Command, in that:
 *     <p>
 *         A command who defines an argument to take a type, cannot also define that same argument
 *         to be a subcommand. e.g.: If we have the command "engine"
 *         <pre>{@code
 *         "engine" () {
 *             new TypedArg(boolean),
 *             new CloseSubcommand() // usually a concrete subcommand defintion
 *         }
 *         }</pre>
 *         this creates the two different possible usage "paths" for the "engine" command:
 *         <pre>{@code
 *         engine <boolean>
 *         engine close
 *         // or
 *         engine <boolean> close
 *         }</pre>
 *         The reason this is, is because actual arguments and subcommands make the command's usage
 *         path slightly different. actual arguments create:
 *         <pre>{@code
 *         "engine" () {
 *             new TypedArg(boolean|Vector3),
 *             new TypedArg(string),
 *             new ArgSet("clear", "remove")
 *         }
 *
 *         engine <boolean> <string> [clear|remove] // typed args become a single possible usage.
 *         engine <Vector3> <string> [clear|remove] // for the other possible type of the first arg.
 *         }</pre>
 *         Whereas subcommands become:
 *         <pre>{@code
 *         "engine" () {
 *             new ResourceSubCommand(),
 *             new ClearSubCommand(),
 *             new RemoveSubCommand()
 *         }
 *
 *         engine resource
 *         engine clear
 *         engine remove
 *         }</pre>
 *         In the subcommand example, the command itself usually becomes a dispatcher to it's
 *         subcommands.
 *     </p>
 * </p>
 * <h2>Interfaces</h2>
 * <p>
 *     More than being a stateless nothing, there are more complicated interfaces a concrete Command can
 *     implement to enable certain functionality. These include:
 *     <ul>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.base.SemiStatefulCommand} an interface, which labels this command
 *             as one that has to disable usage of other commands while it is active. More info in it's own
 *             documentation
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.base.StatefulCommand} is a specialised version of {@link com.j3d.engine.interact.cmd.base.SemiStatefulCommand}
 *             which allows command access to temporary {@link java.awt.event.KeyEvent#VK_ESCAPE} and {@link java.awt.event.KeyEvent#VK_ENTER} to
 *             clear or commit any state that the command has made. StatefulCommands also print output to
 *             the command line labels mentioning this, clearing telling the user they've entered a stateful
 *             command and need to interact with these keys to exit.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.base.KeyedStatefulCommand} is a further
 *             specialisation of {@link com.j3d.engine.interact.cmd.base.StatefulCommand} that
 *             allows for custom key bindings to interact with the command's state, beyond the
 *             default ESCAPE and ENTER keys. This enables more complex and interactive command
 *             workflows. It allows any key to be set but has specialised methods for setting the
 *             UP, DOWN, LEFT and RIGHT keys. It also includes a "gear" key to change input sizes
 *             for various commands.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact.cmd.base.PreCommandExecution} an interface that allows a command to define logic
 *             that has to first pass before the command can continue execution. These are usually called
 *             "conditions" and live within {@link com.j3d.engine.interact.cmd.base.conditions}. These conditions
 *             are usually stored within the command itself via composition, and only run within the own command's
 *             {@link com.j3d.engine.interact.cmd.base.Command#run(com.j3d.ui.SafeJLabel, java.lang.String, java.lang.Object[], java.util.ArrayList)}.
 *             These conditions work by using {@link com.j3d.engine.react.events.EventEmitter} to continue
 *             command execution if the condition passes. (Commands with these conditions usually also implement {@link com.j3d.engine.interact.cmd.base.SemiStatefulCommand}
 *             as the command can continue at any time and other commands need to be blocked.)
 *         </li>
 *     </ul>
 * </p>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.interact.cmd.base;