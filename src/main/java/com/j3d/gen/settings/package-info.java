/**
 * Settings generation logic.
 * <p>
 *     This is a top-level panel, hence it's UI lives within {@link com.j3d.ui.settings}
 * </p>
 * <h1>Settings</h1>
 * <p>
 *     The way that settings work is a singular {@link com.j3d.gen.settings.Setting}, knows how to render itself and
 *     update itself whether the code or user updates it. Otherwise there can be "sections" of settings implementing
 *     {@link com.j3d.gen.settings.SettingsParent} under other sections which can hold any {@link com.j3d.gen.settings.SettingsChild}.
 * </p>
 * <p>
 *     The user need only create the {@link com.j3d.gen.settings.SettingsParent} frame once and all settings will be shown
 *     and editable. The code however needs to go through {@link com.j3d.gen.settings.Settings} statically to acces the
 *     current value of said setting.
 *     <p>
 *         e.g. To access {@link com.j3d.gen.settings.classes.CameraProperties#focalLength}, the code would access
 *         {@link com.j3d.gen.settings.Settings#cameraProperties} and then {@link com.j3d.gen.settings.classes.CameraProperties#focalLength}
 *     </p>
 * </p>
 * <h1>Structure</h1>
 * <h2>Classes</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.settings.SettingsChild}, an interface for a singular setting "base"
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.settings.SettingsParent}, an implementor of {@link com.j3d.gen.settings.SettingsChild}
 *         which represents a "folder" of settings be it concrete settings or sub-folders.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.settings.Setting}, the base class for all concrete settings, holding a value and
 *         providing mechanisms for UI interaction and persistence.`
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.settings.Settings}, the main entry point for all top-level settings,
 *         acting as the root container for all other settings and their categories.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.settings.PrefsGenException}, the base exception for anything that went wrong during
 *         generation of settings.
 *     </li>
 * </ul>
 * <h2>Sub-packages</h2>
 * <ul>
 *     <li>
 *         {@link com.j3d.gen.settings.types} holds all specific setting types that a singular setting can be.
 *     </li>
 *     <li>
 *         {@link com.j3d.gen.settings.classes} holds specific categories of settings, such as camera properties,
 *         scene properties, etc.
 *     </li>
 * </ul>
 * @author Lehlogonolo Poole
 */
package com.j3d.gen.settings;