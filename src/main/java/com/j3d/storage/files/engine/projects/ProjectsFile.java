package com.j3d.storage.files.engine.projects;

import com.j3d.storage.files.engine.EngineFiles;
import com.j3d.storage.files.util.ProjectImagePair;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectsFile {
    File projectsFolder = Path.of(EngineFiles.engineFolder.toString(),"projects").toFile();
    File file;
    public File NO_IMAGE;
    public ProjectsFile(String name) throws IOException {
        if (!projectsFolder.exists())
            projectsFolder.mkdirs();
        file = projectsFolder.toPath().resolve(name).toFile();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        NO_IMAGE = projectsFolder.toPath().resolve("NO_IMAGE_SET.png").toFile();
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

    public void remove(File project, ArrayList<ProjectImagePair> list) {
        try {
            list.removeIf(p -> p.second.getAbsolutePath().equals(project.getAbsolutePath()));
            PrintWriter pw = new PrintWriter(new FileWriter(file, false)); // Overwrite the file
            for (ProjectImagePair p : list)
                pw.println(p);
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(ProjectImagePair identity, ArrayList<ProjectImagePair> list) {
        remove(identity.getProjectFile(), list);

        File image = identity.getProjectImage();

        if (image.getName().contains("NO_IMAGE_SET")) return;

        // delete the image
        if (image.exists())
            image.delete();
    }
}
