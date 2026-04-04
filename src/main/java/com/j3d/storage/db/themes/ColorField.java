package com.j3d.storage.db.themes;

import com.j3d.storage.db.api.RecordField;

import java.awt.*;

public class ColorField extends RecordField<Color> {
    public ColorField(String name, Color value, String tblName) {
        super(name, value,
                (t) -> String.format("%02x%02x%02x", t.getRed(), t.getGreen(), t.getBlue())
        );
    }
}
