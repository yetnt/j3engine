package com.j3d.gen.settings;


import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * An interface for a folder of settings.
 * <p>
 * This interface allows for the creation of a hierarchical structure of settings, where each folder can contain
 * other folders or individual settings. This is useful for organizing a large number of settings in a logical way.
 * </p>
 */
public interface SettingsParent extends SettingsChild {
    ArrayList<SettingsChild> getAllChildren();
    @Override
    default ArrayList<String> serialize() {
        return
                getAllChildren()
                        .stream()
                        .flatMap(s -> s.serialize().stream())
                        .map(s -> serializedName() + "." + s)
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    default void deserialize(ArrayList<String> leftover) {
        if (leftover.isEmpty()) return;
        ArrayList<String> leftover2 =
                leftover
                        .stream()
                        .filter(s -> s.startsWith(serializedName()))
                        .map(s -> s.substring(serializedName().length()+1))
                        .collect(Collectors.toCollection(ArrayList::new));
        if (leftover2.isEmpty()) return;
        getAllChildren()
                .forEach(s -> s.deserialize(leftover2));
    };
}