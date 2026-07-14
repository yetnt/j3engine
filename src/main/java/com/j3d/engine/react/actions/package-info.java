/**
 * Stores all Undo/Redo related classes, labelled as "Actions"
 * <p>
 *     Generally, an {@link com.j3d.engine.react.actions.Action} is any piece of code which can undo itself
 *     and redo itself if undone. If an action cannot undo itself to a 1:1 state as it was before then
 *     it is usually marked via {@link com.j3d.engine.react.actions.Action#isReversible()}.
 * </p>
 * <p>
 *     If a method {@code foo()} defines a new {@link com.j3d.engine.react.actions.Action}, it is {@code foo()}'s
 *     responsibility to add this Action to the {@link com.j3d.engine.react.history.History} and then after
 *     {@code foo()} has to explicitly call {@link com.j3d.engine.react.actions.Action#run()}.
 *     The {@link com.j3d.engine.react.history.History} panel does not handle the first invocation
 *     of the action. Only it's undo and redo after it has been invoked.
 * </p>
 * <h1>Action Classes & Interfaces</h1>
 * <p>
 *     There are many different interfaces one can implement to encapsulate undoable code, all with
 *     different semantics.
 * </p>
 * <ul>
 *     <li>
 *         A generic {@link com.j3d.engine.react.actions.Action} is one that when done, returns a
 *         value. This is the base of all Actions.
 *     </li>
 *     <li>
 *         A {@link com.j3d.engine.react.actions.VoidAction} is an Action who only produces side effects.
 *         Has the same effect as defining an {@link com.j3d.engine.react.actions.Action} who's {@code <T>}
 *         value is {@link java.lang.Void}
 *     </li>
 *     <li>
 *         A {@link com.j3d.engine.react.actions.DirtyAction} is an Action, who implements {@link com.j3d.engine.react.actions.CleanableAction}
 *         meaning when it is no longer required to live anywhere, it's resources get cleaned up via
 *         {@link com.j3d.engine.react.actions.CleanableAction#cleanup()}
 *     </li>
 *     <li>
 *         A {@link com.j3d.engine.react.actions.DirtyVoidAction} is a {@link com.j3d.engine.react.actions.VoidAction}
 *         who implements {@link com.j3d.engine.react.actions.CleanableAction} meaning when it is no longer required to live anywhere,
 *         it's resources get cleaned up via {@link com.j3d.engine.react.actions.CleanableAction#cleanup()}.
 *     </li>
 *     <li>
 *         An {@link com.j3d.engine.react.actions.AbstractAction} is an abstract class implementing
 *         {@link com.j3d.engine.react.actions.VoidAction}. This class exists such that concrete
 *         implementations of {@link com.j3d.engine.react.actions.VoidAction} can extend it and only
 *         override the methods they need, providing default behaviour for {@code undo()},
 *         {@code isReversible()}, {@code getDescription()}, and {@code getTime()}.
 *     </li>
 *     <li>
 *         A {@link com.j3d.engine.react.actions.ConstructorAction} is an action, which has already been
 *         run (via a class' defining constructor). This allows actions to still define a way to undo
 *         (and redo) this action even though it has already been executed. This also implements
 *         {@link com.j3d.engine.react.actions.CleanableAction} requiring explicit resource cleanup.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.react.actions;