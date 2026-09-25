package com.j3d.utility;

import java.awt.*;

public abstract class Colors {
    public static Color alphaChannel(Color original, int alpha) {
        return new Color(original.getRed(), original.getGreen(), original.getBlue(), alpha);
    }
}
