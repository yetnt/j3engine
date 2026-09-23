/**
 * Macros. Another insane use of {@link com.j3d.engine.react.events.EventListener}s
 * <h1>What?</h1>
 * <p>
 *     A Macro is defined to be a list of instructions that a user can do which is to be automated by the engine
 *     so the user doesn't have to do it themselves. So far the following can be macro instructions:
 *     <ul>
 *         <li>Command Invocations</li>
 *         <li>Selections</li>
 *         <li>Camera deltas</li>
 *     </ul>
 *     others like key presses or mouser movement <i>might</i> come soon. if so i'll update the doc.
 * </p>
 * <p>
 *     Macros can either be used simply to define a key press to execute a certain command such as to execute commands faster
 *     or for multiple actions to set up a complex scene with a key press.
 * </p>
 * <h1>Subclasses in this</h1>
 * <p>
 *     {@link MacroLine} is a singular instruction that a macro can execute. it's a record of a line which is usually
 *     in a human readable format and an instruction type
 * </p>
 * <p>
 *     {@link InstructionType} is the type of macro instruction the line is. This is usually serialized with the macro
 *     line itself as a 3 character prefix with a colon.
 * </p>
 * <p>
 *     {@link Macro} is a singular Macro with multiple {@link MacroLine}s. it has no other capabilities other than storing
 *     the name of a macro and it's instruction set together
 * </p>
 * <p>
 *     {@link MacroUtils} is the manager of macros for the engine. A single instance of it can be found within
 *     {@link com.j3d.StaticRefs} and its how the rest of the engine can manage query and update the current macros within
 *     the engine.
 * </p>
 * <p>
 *     {@link MacroRecorder} is a stateful, single-instantiated class which has the purpose of recording user input
 *     to save as a macro. This is where the user actions are serialized into string format and saved.
 * </p>
 * <p>
 *     {@link MacroRunner} is a stateful, single-instantiated class with the purposes of running a {@link Macro} and all
 *     the nuances of how to call the next instruction.
 * </p>
 */
package com.j3d.engine.interact.macros;