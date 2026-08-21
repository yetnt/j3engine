package com.j3d.storage;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public record JarPath(String path) {
    public <T> T readAs(Function<InputStream, T> func) throws IOException {
        try (InputStream s = JarPath.class.getResourceAsStream(path)) {
            return func.apply(s);
        }
    }
}
