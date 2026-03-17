package com.j3d.ui;

import com.j3d.ui.engine.EngineFrame;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CursorManager {
    private static final Map<String, Cursor> cursors = new HashMap<>();
    private static Component defaultTarget;

    static {

        cursors.put("default", createScaledCursor("/cursors/default.png", "default"));
//            cursors.put("hand", CursorNames.getPredefinedCursor(CursorNames.HAND_CURSOR));
        cursors.put("selectSoft", createScaledCursor("/cursors/selectSoft.png", "selectSoft"));
        cursors.put("selectStrict", createScaledCursor("/cursors/selectStrict.png", "selectStrict"));
        cursors.put("selectSubtract", createScaledCursor("/cursors/selectSubtract.png", "selectSubtract"));
        cursors.put("selectAdd", createScaledCursor("/cursors/selectAdd.png", "selectAdd"));
        cursors.put("grab", createScaledCursor("/cursors/drag.png", "grab"));
        cursors.put("grabbing", createScaledCursor("/cursors/drag-held.png", "grabbing"));
        cursors.put("pointer", createScaledCursor("/cursors/pointer.png", "pointer"));
    }

    public static void init(Component defaultComponent) {
        defaultTarget = defaultComponent;
    }

    private static Cursor createScaledCursor(String path, String name) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(EngineFrame.class.getResource(path)));
        Image image = icon.getImage();
        Image scaled = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return Toolkit.getDefaultToolkit().createCustomCursor(scaled, new Point(0, 0), name);
    }

    public static void set(CursorNames cursor) {
        set(cursor, defaultTarget);
    }

    public static void set(CursorNames c, Component target) {
        Cursor cursor = cursors.getOrDefault(c.getValue(), Cursor.getDefaultCursor());
        if (target != null) target.setCursor(cursor);
    }

    public static void setDefault() {
        set(CursorNames.DEFAULT);
    }

    public static Cursor get(CursorNames c) {
        return cursors.getOrDefault(c.getValue(), cursors.get("default"));
    }
}
