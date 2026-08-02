package com.j3d.gen.settings;

import com.j3d.utility.Parsing;

import java.awt.*;
import java.util.ArrayList;

/**
 * An interface for a child of a settings folder.
 * <p>
 * This interface allows for the creation of a hierarchical structure of settings, where each folder can contain
 * other folders or individual settings. This is useful for organizing a large number of settings in a logical way.
 * </p>
 * <p>
 *     This interface is implemented by {@link SettingsParent} and {@link Setting}.
 * </p>
 */
public interface SettingsChild {
    String getDescription();
    String getName();
    default String serializedName() {
        return Parsing.toCamelCase(getName());
    }
    ArrayList<String> serialize();
    void deserialize(ArrayList<String> leftover);
    <R extends Component> R panel();
}
