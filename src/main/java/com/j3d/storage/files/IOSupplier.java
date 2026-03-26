package com.j3d.storage.files;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface IOSupplier<T> {
    T accept(DataInputStream dis) throws Exception;
}