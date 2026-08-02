/**
 * Gen. Any UI which has to be generated in some way or another lives here within the generation package.
 * <h1>Generation</h1>
 * <p>
 *     UI counts as generation if it's entire children have to be made from code and isn't itself completely coded
 *     in NetBeans. The UI or other UI subclasses may still live within {@link com.j3d.ui} and other packages below, but
 *     the actual logic to wire it together can be found here.
 * </p>
 * <h2>Classes</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.GenException}, the base exception for anything that went wrong during generation
 *         of content.
 *     </li>
 * </ul>
 * <h2>Sub-packages</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.settings}. Preferences (JFrame) generation backend. Where all preferences are stored and can be
 *         referenced by code, and is loaded by the system.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties}. Properties (JPanel (FloatingPanel)), where selection properties panel is
 *         built from.
 *     </li>
 *     <li>
 *          {@link com.j3d.gen.docs}. Documentation generation backend. Handles parsing and rendering of markdown
 *          custom documentation files.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.gen;