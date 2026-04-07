package com.j3d.engine.interact.cmd.commands.transform.handlers;

import com.j3d.Static;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.utility.Pair;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Handle {
    HandleType handleType;
    Vector3 position;
    boolean selected = false;
    BiConsumer<Graphics2D, ScreenPoint> draw;
    Consumer<Graphics2D> extraDetail;

    public Handle(HandleType handleType, Vector3 position, BiConsumer<Graphics2D, ScreenPoint> draw) {
        this.handleType = handleType;
        this.position = position;
        this.draw = draw;
    }

    public Handle extraDetail(Consumer<Graphics2D> extraDetail) {
        this.extraDetail = extraDetail;
        return this;
    }

    public Handle draw(Graphics2D g) {
        draw.accept(g, toSp());
        if (selected) {
            if (extraDetail != null)
                extraDetail.accept(g);
            Static.renderer.drawText3D(
                    g, position.sub(new Vector3(0, 5, 0)),
                    handleType.name(), Static.camera,
                    new Color(0x0000000),
                    Color.WHITE
            );
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

    public HandleType handleType() {
        return handleType;
    }

    public void setPos(Vector3 newPos) {
        position = newPos;
    }

    public void unselect() {
        selected = false;
    }

    public void selected() {
        selected = true;
    }

    public Vector3 getPos() {
        return position;
    }
}
