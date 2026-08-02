package com.j3d.storage.files.engine;

import com.j3d.StaticRefs;
import com.j3d.storage.files.FilesUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrefsFile {
    File preferencesFile = Path.of(EngineFiles.engineFolder.toString(),"prefs.txt").toFile();

    public PrefsFile() throws IOException {
        if (!preferencesFile.exists())
            preferencesFile.createNewFile();
    }

    public ArrayList<String> read() {
        ArrayList<String> lines = new ArrayList<>();
        FilesUtility.readFromFile(
                preferencesFile.getAbsolutePath(),
                scanner -> {
                    while (scanner.hasNextLine()) {
                        lines.add(scanner.nextLine());
                    }
                }
        );
        return lines;
    }

    public void write(ArrayList<String> lines) {
        FilesUtility.writeToFile(
                EngineFiles.engineFolder.getAbsolutePath() + "/",
                preferencesFile.getName(),
                printWriter -> lines.forEach(printWriter::println)
        );
    }
}
