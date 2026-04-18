package com.j3d.storage.files.engine;

import com.j3d.storage.files.util.ProjectImagePair;
import com.j3d.utility.Pair;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class PinnedProjectsFile extends ProjectsFile {
    public PinnedProjectsFile() {
        super("pinned.txt");
    }

    public ArrayList<ProjectImagePair> readPinned() {
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

    public int amtLines() {
        return readPinned().size();
    }

    public void writeProj(File project, File image) {
        try {
            if (existsInFile(file, project)) return;
            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            pw.println(image.getAbsolutePath() + ";" + project.getAbsolutePath());
            pw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(File project) {
        try {
            ArrayList<ProjectImagePair> pinned = readPinned();
            pinned.removeIf(p -> p.second.getAbsolutePath().equals(project.getAbsolutePath()));
            PrintWriter pw = new PrintWriter(new FileWriter(file, false)); // Overwrite the file
            for (ProjectImagePair p : pinned)
                pw.println(p);
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeProjs(HashSet<ProjectImagePair> pinned) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, false)); // Overwrite the file
            for (ProjectImagePair pair : pinned) {
                File project = pair.getProjectFile();
                File image = pair.getProjectImage();
//                if (existsInFile(file, project)) continue;
                pw.println(image.getAbsolutePath() + ";" + project.getAbsolutePath());
            }
            pw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
