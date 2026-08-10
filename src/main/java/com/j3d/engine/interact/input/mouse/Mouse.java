package com.j3d.engine.interact.input.mouse;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Mouse {
    int x, y, deltaX, deltaY;
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

//    public Mouse setX(int x) {
//        this.x = x;
//        return this;
//    }
//    public Mouse setY(int y) {
//        this.y = y;
//        return this;
//    }

    public Mouse addX(int x) {
        this.x += x;
        this.deltaX = x;
        return this;
    }
    public Mouse addY(int y) {
        this.y += y;
        this.deltaY = y;
        return this;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void reset() {
        x = 0;
        y = 0;
    }

    public void moveAndReset(int x, int y) {
        this.x = x;
        this.y = y;
        this.deltaX = 0;
        this.deltaY = 0;
    }

    @Override
    public String toString() {
        return "Mouse{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
