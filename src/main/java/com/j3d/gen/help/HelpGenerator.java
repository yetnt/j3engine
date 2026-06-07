package com.j3d.gen.help;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Beginnings of the Helpogenerator for the later help UI frame describing the app and anything to do with or in relation
 * with it to the user. This isnt for the code the Javadoc is for that, this is for the user. (F1 menu.)
 * TODO: continbyue i9mplement
 */
public class HelpGenerator {
    private HashMap<Section, Set<HelpProperties>> helpMap = new HashMap<>();

    public HelpGenerator() {

    }

    /**
     * Adds property text or otherwise to a section. if the section does not exist within the map already, it's
     * added.
     * @param section The section to add.
     * @param helpProperties The properties.
     */
    public void addProperties(Sections section, HelpProperties... helpProperties) {
        // If the properties doesn't exist, add an empty HashSet
        Set<HelpProperties> properties = helpMap.computeIfAbsent(section.getSection(), k -> new HashSet<>());

        properties.addAll(
                List.of(helpProperties)
        );
    }

    /**
     * Returns the set of properties within the given section.
     * @param section The section to return it's properties
     * @return The set of properties, otherwise null.
     */
    public Set<HelpProperties> getProperties(Sections section) {
        return helpMap.get(section.getSection());
    }

    /**
     * Sets the properties to the given set by replacing it.
     * @param section The section to replace it's properties.
     * @param properties The properties to replace with.
     * @return The old properties because why not
     */
    public Set<HelpProperties> setProperties(Sections section, Set<HelpProperties> properties) {
        Set<HelpProperties> old =  helpMap.get(section.getSection());
        helpMap.put(section.getSection(), properties);
        return old;
    }

    public void create() {
        // TODO: Method to create the help JFrame, by adding each section. do it twin.
    }

    /**
     * Enumeration of sections.
     */
    public enum Sections {

        BASICS(new Section("Basics", "The basics of the app. From start to finish")),
        COMMON_TERMS(new Section("Common Terms", "The terms, words and any other common phrases used in the app.")),
        TRIANGLE_SORT_METHOD(new Section("Triangle Sort Methods", "These describe the use and purpose of the different sorting methods for triangles used.")),
        ERRORS(new Section("Errors", "Error codes and descriptions"));

        Sections(Section section) {
            this.section = section;
        }

        private Section section;

        public Section getSection() {
            return section;
        }
    }
}
