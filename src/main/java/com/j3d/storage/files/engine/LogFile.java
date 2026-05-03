package com.j3d.storage.files.engine;

import com.j3d.Static;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogFile {
    File logFolder = Path.of(EngineFiles.engineFolder.toString(),"logs").toFile();
    File file;
    PrintWriter pw;

    public LogFile() {
        if (!logFolder.exists())
            logFolder.mkdirs();
        file = logFolder.toPath().resolve(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("HH-mm-ss_dd-MMM-yyyy")
        )+ "_j3drun.txt").toFile();

        if (!file.exists())
            file.getParentFile().mkdirs();

        try {
            pw = new PrintWriter(new FileWriter(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    Static.getLog().println("Engine shutdown.");
                    pw.close();
                })
        );

    }

    public void writeLn(String ln) {
        pw.println(ln);
    }
}
