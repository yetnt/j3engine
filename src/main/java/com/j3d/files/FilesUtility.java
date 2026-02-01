package com.j3d.files;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Consumer;

public class FilesUtility {

    /**
     * Reads the content of a file and processes it using the provided consumer.
     * This method uses UTF-8 as the default character set.
     *
     * @param path     The path to the file to be read.
     * @param consumer A Consumer that processes the Scanner object for the file content.
     */
    public static void readFromFile(String path, Consumer<Scanner> consumer) {
        readFromFile(path, consumer, null);
    }

    /**
     * Reads the content of a file and processes it using the provided consumer.
     *
     * @param path     The path to the file to be read.
     * @param consumer A Consumer that processes the Scanner object for the file content.
     * @param csn      The character set to use for reading the file. If null, UTF-8 is used.
     */
    public static void readFromFile(String path, Consumer<Scanner> consumer, Charset csn) {
        try {
            csn = csn == null ? StandardCharsets.UTF_8 : csn;
            Scanner scanner = new Scanner(new java.io.File(path), csn);
            consumer.accept(scanner);
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes content to a file using the provided consumer.
     * This method uses UTF-8 as the default character set.
     *
     * @param path     The directory path where the file will be created.
     * @param name     The name of the file to be created.
     * @param consumer A Consumer that writes content to the PrintWriter object.
     */
    public static void writeToFile(String path, String name, Consumer<PrintWriter> consumer) {
        writeToFile(path, name, consumer, null);
    }

    /**
     * Writes content to a file using the provided consumer and character set.
     *
     * @param path     The directory path where the file will be created.
     * @param name     The name of the file to be created.
     * @param consumer A Consumer that writes content to the PrintWriter object.
     * @param csn      The character set to use for writing the file. If null, UTF-8 is used.
     */
    public static void writeToFile(String path, String name, Consumer<PrintWriter> consumer, Charset csn) {
        try {
            csn = csn == null ? StandardCharsets.UTF_8 : csn;
            FileWriter w = new FileWriter(path + name, csn);
            PrintWriter writer = new PrintWriter(w, true);
            consumer.accept(writer);
            writer.close();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String fileChooser(Consumer<JFileChooser> chooserConfigure) {
        JFileChooser chooser = new JFileChooser();
        chooserConfigure.accept(chooser);

        Frame frame = new Frame();

        int result = chooser.showOpenDialog(null);
        frame.dispose();
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().getAbsolutePath() : null;
    }

    public static String folderChooser() {
        return fileChooser(chooser -> {
            chooser.setDialogTitle("Select Folder Big Dawgg");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
        });
    }
}
