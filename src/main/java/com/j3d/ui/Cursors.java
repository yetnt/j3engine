package com.j3d.ui;

import com.j3d.Main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cursors {
    private static final Map<String, Cursor> cursors = new HashMap<>();
    private static Component defaultTarget;

    public static void init(Component defaultComponent) {
        defaultTarget = defaultComponent;
        loadCursors();
    }

    private static void loadCursors() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        cursors.put("default", createScaledCursor("/cursors/default.png", "default"));
//            cursors.put("hand", Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cursors.put("selectSoft", createScaledCursor("/cursors/selectSoft.png", "selectSoft"));
        cursors.put("selectStrict", createScaledCursor("/cursors/selectStrict.png", "selectStrict"));
        cursors.put("selectInvert", createScaledCursor("/cursors/selectInvert.png", "selectInvert"));
    }

    private static Cursor createScaledCursor(String path, String name) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource(path)));
        Image image = icon.getImage();
        Image scaled = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        return Toolkit.getDefaultToolkit().createCustomCursor(scaled, new Point(0, 0), name);
    }

    public static void set(String name) {
        set(name, defaultTarget);
    }

    public static void set(String name, Component target) {
        Cursor cursor = cursors.getOrDefault(name, Cursor.getDefaultCursor());
        if (target != null) target.setCursor(cursor);
    }

    public static void setDefault() {
        set("default");
    }
}
