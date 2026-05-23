package com.j3d.engine.interact.cmd.complete;

import java.util.ArrayList;

public class Suggestion {
    private String[] usages;
    private ArrayList<Object> args;
    private String currentArg;

    private Suggestion(String[] usages, ArrayList<Object> args, String currentArg) {
        this.usages = usages;
        this.args = args;
        this.currentArg = currentArg;
    }

    public static Suggestion getInstance(String[] usages, ArrayList<Object> args, String currentArg) {
        return new Suggestion(usages, args, currentArg);
    }
}
