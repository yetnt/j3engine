/**
 * Package containing every class related to the actual viewport scene the user sees.
 * <h1>Da Scene</h1>
 * <p>
 *     The entire idea is that, everything should be made up from smaller pieces before constructing the entire huge piece.
 *     Which ties into the idea of a {@code SceneObject}. In the simplest terms, it represents any node that can participate
 *     within the scene.
 * </p>
 * <p>
 *     All {@code SceneObject}s are extremely fundamental in that, many subsystems have to explicit use, check or apply them
 *     multiple times. These subsystems are: properties, the entirety of the command line interface.
 * </p>
 * <h2>Node Structure</h2>
 * <p>
 *     The "simplest" set of similar {@code SceneObject}s are {@code GObject}(s) (Graphics Objects), which store state about
 *     simple shapes/geometry that the user can edit directly. E.g. a point, a line, a triangle, (future) a polyline, etc...
 *     GObjects tend to reference each other, say a {@code GTri} references 3 {@code GLine} + 3 {@code GPoint}.
 * </p>
 * <p>
 *     The next, is a container for multiple {@code GObject}s, being a {@code Thing}. This does have extra logic such as
 *     transforming multiple {@code GObject}s in unison, but other than that no {@code GObject} can actually be drawn or
 *     interacted with unless it is part of a parent {@code Thing}
 * </p>
 * <p>
 *     And lastly, a {@code Layer} hodls multiple {@code Thing}s. Layers at the moment are really for organisation and just
 *     handling multiple {@code Thing}s at once. It's a glorified {@link java.util.ArrayList}
 * </p>
 * <h1>Package Structure</h1>
 * <h2>Classes</h2>
 * <p>
 *     <ul>
 *     <li>
 *         {@link com.j3d.engine.scene.SceneManager}, the most important class to even think about interacting with anything
 *         related to the scene. It holds all the state about the scene being: the entire scene graph, the history, and more.
 *         The main instance link any other single-instance property should be accessed via {@link com.j3d.StaticRefs}
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.scene.Camera}, which controls the viewpoint and projection of the 3D scene, determining
 *         what is visible to the user. Similar to {@link com.j3d.engine.scene.SceneManager}, the user's main camera can
 *         be accessed via {@link com.j3d.StaticRefs}
 *     </li>
 *     <li>
 *         {@link com.j3d.engine.scene.SceneObject}, base interface which all scene elements implement to participate i
 *         n the 3D environment.
 *     </li>
 *     </ul>
 * </p>
 * <h2>Sub-packages</h2>
 * <p>
 *     <ul>
 *         <li>
 *             {@link com.j3d.engine.scene.nodes} contains all the classes and interfaces relating to the scene graph's
 *             fundamental building blocks, including {@code Thing}s,
 *             {@code Layer}s, and various {@code GObject}s.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.scene.find} contains everything you could possibly need to find a specific instance without
 *             the scene graph without having to traverse it yourself.
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.scene.copy} contains the classes responsible for copy operations relating to
 *             {@code GObject}s in particular for CTRL+C and CTRL+V
 *         </li>
 *         <li>
 *             {@link com.j3d.engine.scene.draw} contains classes and interfaces responsible for the actual rendering and
 *             drawing of scene elements onto the display.
 *         </li>
 *     </ul>
 * </p>
 * @see com.j3d.engine.scene.SceneObject
 * @see com.j3d.engine.scene.nodes.SceneObjectList
 * @see com.j3d.engine.scene.SceneManager
 * @see com.j3d.engine.scene.draw.SceneRenderer
 * @see com.j3d.engine.scene.copy.CopyProperties
 * @see com.j3d.engine.scene.nodes.geometry.GObject
 * @see com.j3d.engine.scene.nodes.Thing
 * @see com.j3d.engine.scene.nodes.layer.Layer
 * @author Lehlogonolo Poole
 */
package com.j3d.engine.scene;