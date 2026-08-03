/**
 * The generation of object selection properties and base the bane of my existence.
 * <p>
 *     This is a {@link com.j3d.ui.engine.FloatingPanel} meaning it's UI lives within {@link com.j3d.ui.engine.floating.properties}
 * </p>
 * <h1>Properties</h1>
 * <p>
 *     "properties" are defined by any {@link com.j3d.engine.scene.nodes.geometry.GObject}., {@link com.j3d.engine.scene.nodes.Thing}
 *     or {@link com.j3d.engine.scene.nodes.layer.Layer}.
 *     They are different from fields as they are not used by code and are purely a way for the user to "see" a
 *     object's internal property and possibly edit it. The object itself defines what is a property within its constructor
 *     how the UI can access said property and any other semantics for property intersection logic.
 * </p>
 * <p>
 *     Properties themselves aren't difficult, in that they can just be thought of as a user-friendly accessor to
 *     whatever the object defines the property as. However, for a something like {@link com.j3d.engine.scene.nodes.geometry.GObject} which
 *     has concrete extendors, e.g. {@link com.j3d.engine.scene.nodes.geometry.GTri}, those extendors may inherit properties
 *     from the base class and add extra properties special to this child class. (See {@link com.j3d.gen.properties.Property} and
 *     {@link com.j3d.gen.properties.PropertyKey})
 * </p>
 * <p>
 *     Unlike other stuff within {@link com.j3d.gen}, the generated property panels don't persist and are always regenerated
 *     when the user makes a new selection. The {@link com.j3d.gen.properties.Property} instance may be the exact same
 *     that the object instance defines, but the panel itself might now hold a single property, then later multiple
 *     as the user's selection grows.
 * </p>
 * <h2>Classes</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.properties.HasProperties}, an interfaces which allows an object's instance to
 *         have properties associated with it. These generally need to be objects that the user cna interact with
 *         and not some random class
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties.Property}, represents a singular property definition. With the following:
 *         <ul>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#getName()} : The label for the property
 *             </li>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#setDescription(java.lang.String)} : The description of this
 *                 property to show to the user.
 *             </li>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#getValueSupplier()} : The supplier which fetches the current
 *                 value of the field
 *             </li>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#isConstant()} : Whether this property should only be a view
 *                 and cannot be changed
 *             </li>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#getPropertyProvider()} : The provider class of this property.
 *                 Used when multiple objects have been selected and only properties of the same property provider
 *                 get shown.
 *             </li>
 *             <li>
 *                 {@link com.j3d.gen.properties.Property#holds(java.lang.Class)} : The type the property holds. This is
 *                 exactly identical to the types that the Command Parser can intake. (See {@link com.j3d.engine.interact.cmd.CommandParser})
 *             </li>
 *         </ul>
 *         A property is defined as a property but based on its type may be assigned a different concrete panel from
 *         {@link com.j3d.ui.engine.floating.properties.panels}
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties.PropertyKey}, a record that encapsulates the identifying characteristics
 *         of a {@link com.j3d.gen.properties.Property}. This is primarily used for comparing properties across
 *         multiple selected objects to determine which properties are common and can be displayed/edited together.
 *         (See {@link com.j3d.gen.properties.PropertyKey} for {@code #equals} and {@code #hashCode} semantics)
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties.SelectionPropertiesFilter}, an enum representing the options for a combo box
 *         with options such as to allow the user to target the properties of a specific object. e.g. instead of targeting
 *         {@link com.j3d.gen.properties.SelectionPropertiesFilter#DEFAULT} which tries to finder all common properties
 *         from {@code GObject}s, it can be changed to target only specifically {@link com.j3d.gen.properties.SelectionPropertiesFilter#TRI}
 *         and likewise.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.properties.PropertiesUI}, the main class that orchestrates the generation and display
 *         of property panels for selected objects.
 *         <p>
 *             This class handles:
 *             <ul>
 *                 <li>Collecting all properties based on the filter</li>
 *                 <li>Merging similar properties into a singular panel that can be bulk edited</li>
 *                 <li>Finding the correct panel subclass that correctly identifies this property</li>
 *                 <li>Displaying this property.</li>
 *             </ul>
 *             The property panel itself is responsible for handling updates and nuances between constant
 *             and non-constant properties, and difference between receiving a single property and multiple
 *             at once.
 *         </p>
 *     </li>
 * </ul>
 */
package com.j3d.gen.properties;