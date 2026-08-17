/**
 * Ugh. i hate mice. not the little guy tho i mean the computer mice thanks to this.
 * <h1>MouseOwner</h1>
 * <h2>What?</h2>
 * <p>
 *     {@link com.j3d.engine.interact.input.mouse.MouseOwner} is exactly what it describes itself. It's a class which owns
 *     all input of the user's mouse. It's registered within {@link com.j3d.ui.engine.EngineFrame}
 *     to listen for motion, mouseWheel and clicks. (Literally anything and everything)
 * </p>
 * <p>
 *     Generally speaking each mouse owner is actually responsible for checking whether it is actually the active owner
 *     of the input, since EngineFrame goes ahead and registers all of them. This means that a mouse owner who does not
 *     check, can technically always run regardless. Although this breaks the "trust" system of mouse owner. The only exception
 *     is {@link com.j3d.engine.interact.input.mouse.AlwaysMouseOwner} who has been given the privilege of running regardless.
 * </p>
 * <h2>Why?</h2>
 * <p>
 *     {@link com.j3d.engine.interact.input.mouse.MouseOwner} has multiple more features than the standrd abstract
 *     {@link java.awt.event.MouseAdapter} gives. which includes:
 *     <ul>
 *         <li>
 *             Having a click delay threshold since the smallest movement counts as a drag/move to awt. This is a configurable
 *             property such as to allow small human err
 *         </li>
 *         <li>
 *             Wrapping the mouse from left to right so it just scrolls over to the user view. However, this has alot more state
 *             to it and requires owners to handle a bit more state.
 *         </li>
 *         <li>
 *             Event emitting capabilities. Why? why not.
 *         </li>
 *     </ul>
 * </p>
 * <h1>Classes</h1>
 * <p>
 *     Most MouseOwners can only be found where they are used. e.g. {@link com.j3d.engine.interact.cmd.commands.camera.orbit.OrbitMouseOwner}
 *     can only be found within that command's sub-package within the commands package and do not live here. Only the generic owners
 *     live here.
 * </p>
 * <ul>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.MouseOwner}, the main guy. extends {@link java.awt.event.MouseAdapter} and implements
 *         {@link com.j3d.engine.react.events.EventEmitterInterface}, and is the base class for all other mouse owners.
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.Mouse}, a simple object holding an x, y and deltas. Any MouseOwner which
 *         plans on using mouse wrapping will make use of this mouse and not {@link java.awt.event.MouseEvent} deltas.
 *         See {@link com.j3d.engine.interact.input.mouse.MouseOwner#wrap(java.awt.event.MouseEvent)}
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.MOwner} an enum that defines the different types of mouse owners in the engine.
 *         usually each concrete owner has it's own enum entry defining itself.
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.NoMouseOwner}, a special {@link com.j3d.engine.interact.input.mouse.MouseOwner}
 *         that represents the absence of an active mouse owner. The only thing this provides, is wrapping the mouse form left to
 *         right. generally this isnt used elsewhere other than testing or making so no other input is given
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.AlwaysMouseOwner}, a special {@link com.j3d.engine.interact.input.mouse.MouseOwner}
 *         that always executes its mouse event methods, regardless of whether it is the active mouse owner.
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.input.mouse.SnapMouseOwner}, a special type of mouse owner which by itself
 *         is not a concrete owner, but when extended allows for snapping mouse movements to certain geometry. be it end points
 *         mid points, etc..
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.react.events.payloads.SnapPayload} , a payload class used to carry information about a snap
 *         event, such as the snapped point and the type of snap.
 *     </li>
 * </ul>
 * <h2>Other important mouse owners</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner}, An extremely complex mouse owner which
 *         is the base class for the 3 transform mouse owners. This allows its extenders to draw transform handles beyond
 *         what MouseOwner alone can do.
 *         <p>
 *             An extender {@link com.j3d.engine.interact.cmd.commands.transform.mouse.TransformMouseOwner}, extends this base class
 *             and also implements {@link com.j3d.engine.interact.input.mouse.SnapMouseOwner}, making it allow snapping
 *             too.
 *         </p>
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.cmd.commands.camera.orbit.OrbitMouseOwner}, the most interactive, is how the {@code orbit}
 *         command works by turning drag input into rotating the global camera.
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.cmd.commands.transform.qtrans.QTranslateMouseOwner}, another interactive, and also
 *         linear algebra heavy one, which acts as a quicker form of transform to just displace a group selection by a
 *         mouse movement delta.
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.interact.selection.SelectionMouseOwner} the mouse owner responsible for the most important
 *         user feature. selection and selection feedback. Although most of its logic is displaced elsewhere and not
 *         within the owner itself (intersection checks, union selection, etc)
 *     </li>
 * </ul>
 */
package com.j3d.engine.interact.input.mouse;