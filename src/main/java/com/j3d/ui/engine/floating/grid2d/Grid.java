package com.j3d.ui.engine.floating.grid2d;

import com.j3d.engine.geometry.Dim;
import com.j3d.engine.geometry.ScreenPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

class Grid extends JPanel {

    Consumer<Graphics2D> consumer = (g) -> {
    };

    public Grid() {
        super();
    }

    public void setConsumer(Consumer<Graphics2D> consumer) {
        this.consumer = consumer;
    }

    public Dim sizeDim() {
        return new Dim(
                this.getSize().width,
                this.getSize().height
        );
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        consumer.accept((Graphics2D) g);
    }
}
