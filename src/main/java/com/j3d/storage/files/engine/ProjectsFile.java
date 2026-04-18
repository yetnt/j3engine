package com.j3d.storage.files.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public class ProjectsFile {
    File projectsFolder = Path.of(EngineFiles.engineFolder.toString(),"projects").toFile();
    File file;
    public ProjectsFile(String name) {
        if (!projectsFolder.exists())
            projectsFolder.mkdirs();
        file = projectsFolder.toPath().resolve(name).toFile();
        if (!file.exists())
            file.getParentFile().mkdirs();
    }

    public boolean existsInFile(File txtFile, File project) {
        try {
            Scanner sc = new Scanner(txtFile);
            while (sc.hasNextLine())
                if (sc.nextLine().contains(project.getAbsolutePath()))
                    return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
