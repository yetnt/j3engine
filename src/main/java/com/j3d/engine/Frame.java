package com.j3d.engine;

import com.j3d.engine.geometry.GObject;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Queue;

public class Frame {
    public ArrayDeque<GObject> deque;

    public Frame(ArrayDeque<GObject> d) {
        deque = d;
    }
}
