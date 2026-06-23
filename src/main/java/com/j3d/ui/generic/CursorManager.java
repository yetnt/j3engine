package com.j3d.ui.generic;

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

        register(CursorNames.POINTER);
        register(CursorNames.HOURGLASS);
        register(CursorNames.SELECT_SOFT);
        register(CursorNames.SELECT_STRICT);
        register(CursorNames.SELECT_SUBTRACT);
        register(CursorNames.SELECT_ADD);
        register(CursorNames.HAND_GRAB);
        register(CursorNames.HAND_GRABBING);
        register(CursorNames.HAND_POINTER);

//        cursors.put("cursor-pointer", createScaledCursor("/cursors/cursor-pointer.png", "cursor-pointer"));
//        cursors.put("hourglass", createScaledCursor("/cursors/hourglass.png", "hourglass"));
//        cursors.put("selectSoft", createScaledCursor("/cursors/selectSoft.png", "selectSoft"));
//        cursors.put("selectStrict", createScaledCursor("/cursors/selectStrict.png", "selectStrict"));
//        cursors.put("selectSubtract", createScaledCursor("/cursors/selectSubtract.png", "selectSubtract"));
//        cursors.put("selectAdd", createScaledCursor("/cursors/selectAdd.png", "selectAdd"));
//        cursors.put("grab", createScaledCursor("/cursors/drag.png", "grab"));
//        cursors.put("grabbing", createScaledCursor("/cursors/drag-held.png", "grabbing"));
//        cursors.put("hand-pointer", createScaledCursor("/cursors/hand-pointer.png", "hand-pointer"));
    }

    public static void register(CursorNames cursorName) {
        cursors.put(cursorName.getValue(), createScaledCursor("/cursors/" + cursorName.getValue() + ".png", cursorName.getValue()));
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
