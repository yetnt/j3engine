/**
 * Package that holds all classes related to command-line interaction and processing.
 * This includes parsing commands, executing them, and managing command history.
 * <h1>Command Line Interaction</h1>
 * The core is defined by {@link com.j3d.engine.interact.cmd.CommandParser},
 * {@link com.j3d.engine.interact.cmd.CommandsManager} and {@link com.j3d.engine.interact.cmd.Commands}
 * <br>
 * <br>
 * <p>
 *     The {@link com.j3d.engine.interact.cmd.CommandParser} parses all input from the
 *     {@link com.j3d.ui.engine.CommandPalette} instance into {@link com.j3d.engine.interact.cmd.CmdToken}
 *     instances for sub systems like the {@link com.j3d.engine.interact.cmd.complete} package to consume.
 *     Otherwise, the parsed objects which are stored within the tokens are passed into the invoked
 *     command once the user hits enter.
 * </p>
 * <p>
 *     The {@link com.j3d.engine.interact.cmd.Commands} class contains the single instances to all
 *     available commands, as Commands need only be instantiated once and reused multiple times.
 * </p>
 * <p>
 *     The {@link com.j3d.engine.interact.cmd.CommandsManager} handles finding a command via it's alias
 *     using a {@link java.util.HashMap} for O(1) lookup, and handles state vs stateless command flagging.
 * </p>
 * <p>
 *     {@link com.j3d.engine.interact.cmd.CmdToken} encapsulates a single parsed element of a command,
 *     holding its type and value, which can then be consumed by various subsystems or passed to the
 *     command for execution.
 * </p>
 * <h2>Sub-packages</h2>
 * <ul>
 *     <li>{@link com.j3d.engine.interact.cmd.args}</li>
 *     <li>{@link com.j3d.engine.interact.cmd.base}</li>
 *     <li>{@link com.j3d.engine.interact.cmd.commands}</li>
 *     <li>{@link com.j3d.engine.interact.cmd.complete}</li>
 * </ul>
 * <h2>Other classes within this package</h2>
 * <p>
 *     {@link com.j3d.engine.interact.cmd.RunHistory} tracks the history of executed commands, allowing
 *     for recall and re-execution.
 * </p>
 * <p>
 *     {@link com.j3d.engine.interact.cmd.Wildcard} is a special interface, which is used in Command definitions
 *     to label that a {@link com.j3d.engine.interact.cmd.args.TypedArg} can take any type.
 * </p>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.interact.cmd;