package com.j3d.engine.interact.input.mouse;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Mouse {
    int x, y;
    public Mouse(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public Mouse setX(int x) {
        this.x = x;
        return this;
    }
    public Mouse setY(int y) {
        this.y = y;
        return this;
    }

    public Mouse addX(int x) {
        this.x += x;
        return this;
    }
    public Mouse addY(int y) {
        this.y += y;
        return this;
    }

    public void add(Point e, Point old) {
        x += e.x - old.x;
        y += e.y - old.y;
    }

    public void reset() {
        x = 0;
        y = 0;
    }

    @Override
    public String toString() {
        return "Mouse{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
