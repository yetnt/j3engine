/**
 * Package that holds all classes and interfaces related to deep copying
 * {@link com.j3d.engine.geometry.geo2d.graphics.GObject} references.
 * <h1>Copying</h1>
 * <p>
 *     GObjects aren't usually copied but in the event that they do need to this package provides
 *     the utilities needed to do so.
 *</p>
 * <h2>Process</h2>
 * <p>
 *     If generic copying via selection is needed, rather
 *     <ol>
 *         <li>Select the GObjects in the scene</li>
 *         <li>Call {@link com.j3d.engine.interact.cmd.commands.clipboard.CopyCmd}</li>
 *         <li>Call {@link com.j3d.engine.interact.cmd.commands.clipboard.PasteCmd}</li>
 *     </ol>
 *     <br>
 *     Otherwise the setup is as follows:
 *     <ol>
 *         <li>
 *             Collect all GObjects which need to be copied.
 *         </li>
 *         <li>
 *             Create a new {@link com.j3d.engine.geometry.geo2d.copy.CopyProperties}.
 *         </li>
 *         <li>
 *             Call {@link com.j3d.engine.geometry.geo2d.graphics.GObject#copy(com.j3d.engine.geometry.geo2d.copy.CopyProperties)}.
 *             <br>
 *             <p>
 *                 Make sure to call on the points first, then the liens then the tris to ensure
 *                 that all dependencies are copied before their dependents, preventing
 *                 {@link com.j3d.engine.geometry.geo2d.copy.InvalidCopyException} due to missing
 *                 references.
 *             </p>
 *             <p>
 *                 This should be wrapped in a try-catch block to catch {@link com.j3d.engine.geometry.geo2d.copy.InvalidCopyException}
 *                 and hence clear all references that got made in the process before the exception occured.
 *                 Otherwise, this exception will be rethrown by {@link com.j3d.StaticRefs#getErrs()}.
 *             </p>
 *         </li>
 *     </ol>
 * </p>
 * <h2>Classes</h2>
 *     <ul>
 *         <li>
 *             {@link com.j3d.engine.geometry.geo2d.copy.CanCopy}, the interface which enforces that implementing
 *             objects provide a {@link com.j3d.engine.geometry.geo2d.copy.CanCopy#copy(com.j3d.engine.geometry.geo2d.copy.CopyProperties)}
 *             method with their own implementation. This is usually only used by GObjects anyways.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.geometry.geo2d.copy.CopyProperties}, the properties of a copy operation
 *             which hold booleans related to the copy operation, the current list of copied objects linked to their
 *             original object's UUID and management of fetching and getting. A single {@link com.j3d.engine.geometry.geo2d.copy.CopyProperties}
 *             instance should be made and passed into the multiple GObjects that would like to be copied in a single
 *             operation.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.geometry.geo2d.copy.Copy}, a record that encapsulates an original
 *             object's {@link java.util.UUID} and its corresponding copied
 *             {@link com.j3d.engine.geometry.geo2d.graphics.GObject} instance. It's used internally by
 *             {@link com.j3d.engine.geometry.geo2d.copy.CopyProperties} to track which original objects
 *             have been copied and to what new instances.
 *             <br>
 *             <p>
 *                 {@link com.j3d.engine.geometry.geo2d.copy.Copy#equals(java.lang.Object)} and
 *                 {@link com.j3d.engine.geometry.geo2d.copy.Copy#hashCode()} only use
 *                 {@link com.j3d.engine.geometry.geo2d.copy.Copy#original()} (the original object's UUID).
 *             </p>
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.geometry.geo2d.copy.CopyPropertiesBuilder}, a builder class for creating
 *             {@link com.j3d.engine.geometry.geo2d.copy.CopyProperties} instances. It provides an
 *             API to configure various properties related to the copying of {@link com.j3d.engine.geometry.geo2d.graphics.GObject}
 *             instances.
 *        </li>
 *        <li>
 *            {@link com.j3d.engine.geometry.geo2d.copy.InvalidCopyException}, an exception thrown when an attempt to copy objects fails, typically indicating an issue during the deep copying process or with missing dependencies.
 *        </li>
 *     </ul>
 * @author Lehlogonolo Poole
 * @see com.j3d.engine.geometry.geo2d.copy.CanCopy
 * @see com.j3d.engine.geometry.geo2d.copy.Copy
 * @see com.j3d.engine.geometry.geo2d.copy.CopyProperties
 * @see com.j3d.engine.geometry.geo2d.copy.CopyPropertiesBuilder
 * @see com.j3d.engine.geometry.geo2d.copy.InvalidCopyException
 */
package com.j3d.engine.geometry.geo2d.copy;