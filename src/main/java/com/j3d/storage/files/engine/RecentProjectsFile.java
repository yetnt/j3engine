package com.j3d.storage.files.engine;

import com.j3d.Static;
import com.j3d.ui.engine.J3DPanel;
import com.j3d.utility.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class RecentProjectsFile {
    File recentsFolder = Path.of(EngineFiles.engineFolder.toString(),"recents").toFile();
    File file = EngineFiles.engineFolder.toPath().resolve("recents.txt").toFile();

    public RecentProjectsFile() {
        if (!recentsFolder.exists())
            recentsFolder.mkdirs();
        if (!file.exists())
            file.getParentFile().mkdirs();
    }

    public ArrayList<Pair<File, File>> readRecents() throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        ArrayList<Pair<File, File>> recents = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            if (parts.length != 2) continue;
            File projectImage = Path.of(parts[0]).toFile();
            File projectFile = Path.of(parts[1]).toFile();
            recents.add(new Pair<>(projectImage, projectFile));
        }
        return recents;
    }

    public void writeProj(File project) {
        try {
            File image = recentsFolder.toPath().resolve(project.getName() + ".png").toFile();
            ((J3DPanel)Static.mainPanel).exportAs("png", image);

            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            pw.println(image.getAbsolutePath() + ";" + project.getAbsolutePath());
            pw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
