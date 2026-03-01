package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.Vector3;
import com.j3d.utility.Pair;

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
        {
            Pair<Integer, Integer> selectionBoundingBox = new Pair<>(5, 5);
            ScreenPoint p = toSp();
            int lowerX = p.x - selectionBoundingBox.first;
            int lowerY = p.y - selectionBoundingBox.second;
            int upperX = p.x + selectionBoundingBox.first;
            int upperY = p.y + selectionBoundingBox.second;

            int rectHeight = upperY - lowerY;
            int rectWidth = upperX - lowerX;

            int rectCentreX = rectWidth / 2;
            int rectCentreY = rectHeight / 2;

            g.setColor(new Color(23, 12, 42, 120));

            g.fillRect(rectCentreX, rectCentreY, rectWidth, rectHeight);

//            g.fillRect(lowerX, lowerY, upperX, upperY);
        }
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
