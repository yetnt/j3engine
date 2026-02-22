package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Vector3;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Handle {
    HandleType handleType;
    Vector3 position;
    Vector3 previewPostion;
    boolean drawPreview = false;
    BiConsumer<Graphics2D, ScreenPoint> draw;


    public Handle(HandleType handleType, Vector3 position, BiConsumer<Graphics2D, ScreenPoint> draw) {
        this.handleType = handleType;
        this.position = position;
        this.draw = draw;
    }

    public void setPreview(Vector3 previewPosition) {
        this.previewPostion = previewPosition;
        drawPreview = true;
    }

    public void disablePreview() {
        drawPreview = false;
        previewPostion = null;
    }

    public Handle draw(Graphics2D g) {
        draw.accept(g, toSp());
        if (drawPreview && previewPostion != null) {
            // set 50% transparency
            Color oldColor = g.getColor();
            g.setColor(new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), 50));
            drawCopy(g, previewPostion);
            g.setColor(oldColor);
        }
        return this;
    }

    public ScreenPoint toSp() {
        return position.toPoint(Static.camera).toScreen(Static.renderer);
    }

    private Handle drawCopy(Graphics2D g, Vector3 p) {
        draw.accept(g, p.toPoint(Static.camera).toScreen(Static.renderer));
        return this;
    }
}
