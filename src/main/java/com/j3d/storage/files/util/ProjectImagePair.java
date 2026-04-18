package com.j3d.storage.files.util;

import com.j3d.utility.Pair;

import java.io.File;

public class ProjectImagePair extends Pair<File, File> {

    public ProjectImagePair(File image, File project) {
        super(image, project);
    }

    public File getProjectImage() {
        return first;
    }

    public File getProjectFile() {
        return second;
    }

    @Override
    public String toString() {
        return getProjectImage().getAbsolutePath() + ";" + getProjectFile().getAbsolutePath();
    }

    public static boolean isCopy(ProjectImagePair pairA, ProjectImagePair pairB) {
        return pairA.first.getAbsolutePath().equals(pairB.first.getAbsolutePath()) && pairA.second.getAbsolutePath().equals(pairB.second.getAbsolutePath());
    }
}
