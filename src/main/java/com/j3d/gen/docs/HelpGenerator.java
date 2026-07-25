package com.j3d.gen.docs;

import com.j3d.StaticRefs;
import com.j3d.gen.docs.reader.DocsGenException;
import com.j3d.ui.help.HelpFrame;

import java.util.HashMap;

/**
 * Beginnings of the Helpogenerator for the later help UI frame describing the app and anything to do with or in relation
 * with it to the user. This isnt for the code the Javadoc is for that, this is for the user. (F1 menu.)
 * TODO: continbyue i9mplement
 */
public class HelpGenerator {

    private final HashMap<String, HelpFrame> frameHashMap = new HashMap<>();
    private final HelpFrame main = new HelpFrame();

    public HelpGenerator() {

    }

    public HelpFrame getMain() {
        return main;
    }

    public HelpFrame getHelpFrame(String id) {
        HelpFrame f = frameHashMap.get(id);
        if (f == null) {
            if (StaticRefs.getEngineFiles().docsFolder.getFileHashMap().get(id) == null) {
                StaticRefs.getErrs().handle(
                        new DocsGenException(
                                "Attempt to get help frame for non existent file: " + id
                        )
                );
            }
            // create it.
            f = new HelpFrame(id);
            frameHashMap.put(id, f);
        }
        return f;
    }
}
