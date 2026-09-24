package com.j3d.ui.engine.floating.grid2d;

import com.j3d.engine.math.Dim;
import com.j3d.ui.theme.J3DTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Grid extends JPanel {

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
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        consumer.accept((Graphics2D) g);
    }
}
