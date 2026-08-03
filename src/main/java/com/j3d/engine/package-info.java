/**
 * Provides the core components and functionalities for the J3D engine.
 * <p>
 *     The engine is made up of many different smaller sub systems which all work together.
 *     <uL>
 *         <li>
 *             {@link com.j3d.engine.geometry} holds pure geometry definitions. Such as a line segment or a triangle.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.interact} holds all "interaction" based definitions. How the user can interact
 *             with the engine.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.math} holds any related math utility or algorithm which itself is not tied to geometry.
 *             Such as a {@code Vector3} or {@code CartesianPoint}
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.react} holds all reactive subsystems. Being the Actions (undo/redo) and event handling.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.scene} holds all scene related components and subsystems. How the engine actually
 *             "displays" itself to the user
 *         </li>
 *     </uL>
 * </p>
 * @author Lehlogonolo Poole
 */
package com.j3d.engine;