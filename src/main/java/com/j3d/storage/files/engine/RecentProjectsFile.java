package com.j3d.storage.files.engine;

import com.j3d.StaticRefs;
import com.j3d.storage.files.util.ProjectImagePair;
import com.j3d.ui.engine.J3DPanel;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class RecentProjectsFile extends ProjectsFile {

    public static final int MAX_RECENT = 20;

    public RecentProjectsFile() {
        super("recents.txt");
    }

    public ArrayList<ProjectImagePair> readRecents() {
        try {
            Scanner sc = new Scanner(file);
            ArrayList<ProjectImagePair> recents = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(";");
                if (parts.length != 2) continue;
                File projectImage = Path.of(parts[0]).toFile();
                File projectFile = Path.of(parts[1]).toFile();
                recents.add(new ProjectImagePair(projectImage, projectFile));
            }
            return recents;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeProj(File project) {
        try {
            File image = projectsFolder.toPath().resolve(project.getName() + ".png").toFile();
            ((J3DPanel) StaticRefs.mainPanel).exportAs("png", image);

            if (existsInFile(file, project)) return;
            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            pw.println(image.getAbsolutePath() + ";" + project.getAbsolutePath());
            pw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trimTop();
    }

    public void trimTop() {
        try {
            ArrayList<ProjectImagePair> recents = readRecents();
            if (recents.size() > MAX_RECENT) {
                recents.removeFirst(); // Remove the oldest entry
                PrintWriter pw = new PrintWriter(new FileWriter(file, false)); // Overwrite the file
                for (ProjectImagePair recent : recents)
                    pw.println(recent);
                pw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
