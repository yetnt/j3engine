package com.j3d.engine.interact.macros;

import com.yetnt.utils.tuple.Pair;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * A single macro with a name and multiple {@link MacroLine}s
 *
 * @see MacroLine
 * @see MacroRunner
 * @see MacroRecorder
 * @see MacroUtils
 * @author Lehlogonolo Poole
 */
public class Macro extends Pair<String, ArrayList<MacroLine>> {

    /**
     * Default Constructor
     * @param name The name of the macro
     * @param macroLines The lines of the macro
     */
    public Macro(String name, ArrayList<MacroLine> macroLines) {
        super(name, macroLines);
    }

    /**
     * Gets the name of the macro
     * @return The name
     */
    public String getName(){
        return first;
    }

    /**
     * Gets the lines of the macro
     * @return The lines arraylist
     */
    public ArrayList<MacroLine> getMacroLines(){
        return second;
    }
}
