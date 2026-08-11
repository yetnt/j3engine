package com.j3d.storage.files.engine;

import com.j3d.storage.files.FilesUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UserFile {
    File userFile = Path.of(EngineFiles.engineFolder.toString(),"user.txt").toFile();
    public static int NONE_FOUND = -1;

    public UserFile() throws IOException {
        if (!userFile.exists())
            userFile.createNewFile();
    }

    public int read() {
        AtomicBoolean empty = new AtomicBoolean(true);
        AtomicInteger id = new AtomicInteger();
        FilesUtility.readFromFile(
                userFile.getAbsolutePath(),
                scanner -> {
                    while (scanner.hasNextLine()) {
                        try {
                            id.set(scanner.nextInt());
                            empty.set(false);
                        } catch (NoSuchElementException e) {
                            if (empty.get()) id.set(NONE_FOUND);
                        }
                    }
                }
        );
        if (empty.get()) return NONE_FOUND;
        return id.get();
    }

    public void write(int id) {
        FilesUtility.writeToFile(
                EngineFiles.engineFolder.getAbsolutePath() + "/",
                userFile.getName(),
                printWriter -> printWriter.println(id)
        );
    }

    public void clear() {
        FilesUtility.writeToFile(
                EngineFiles.engineFolder.getAbsolutePath() + "/",
                userFile.getName(),
                printWriter -> { /* write nothing */ }
        );
    }

    public boolean exists() {
        // TODO: new user gets a login?????????????????
        return read() != NONE_FOUND;
    }
}
