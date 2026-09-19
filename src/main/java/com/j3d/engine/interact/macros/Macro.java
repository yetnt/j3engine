package com.j3d.engine.interact.macros;

import com.j3d.utility.generic.tuple.Pair;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Macro extends Pair<String, ArrayList<MacroLine>> {

    public Macro(String name, ArrayList<MacroLine> macroLines) {
        super(name, macroLines);
    }

    public String getName(){
        return first;
    }

    public ArrayList<MacroLine> getMacroLines(){
        return second;
    }
}
