package com.j3d.storage.files;

import com.j3d.errors.ErrorHandler;
import com.j3d.storage.errs.GenericIOException;

import javax.swing.*;
import java.io.*;
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
            Scanner scanner = new Scanner(new File(path), csn);
            consumer.accept(scanner);
            scanner.close();
        } catch (IOException e) {
            ErrorHandler.handle(
                    new GenericIOException(
                            "Error reading from file:" + path,
                            e
                    )
            );
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
            ErrorHandler.handle(
                    new GenericIOException(
                            "Error writing to file:" + path + name,
                            e
                    )
            );
        }
    }


    /**
     * Shows a file chooser dialog and returns the selected file's absolute path.
     * @param chooserConfigure A consumer that configures the file chooser.
     * @return The absolute path of the selected file, or null if no file is selected.
     */
    public static File fileChooser(Consumer<JFileChooser> chooserConfigure, JFrame frameParent) {
        JFileChooser chooser = new JFileChooser();
        chooserConfigure.accept(chooser);

        int result = chooser.showOpenDialog(frameParent);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile(): null;
    }

    /**
     * Shows a folder chooser dialog and returns the selected folder's absolute path.
     * @return The absolute path of the selected folder, or null if no folder is selected.
     */
    public static File folderChooser(JFrame frameParent) {
        return fileChooser(chooser -> {
            chooser.setDialogTitle("Select Folder Big Dawgg");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
        }, frameParent);
    }

    public static void writeBinary(String path, String name, Consumer<DataOutputStream> consumer) {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(new File(path, name))))) {

            consumer.accept(out);

        } catch (IOException e) {
            ErrorHandler.handle(
                    new GenericIOException(
                            "Error writing binary to file:" + path + name,
                            e
                    )
            );
        }
    }

    public static void readBinary(String path, String name, IOSupplier<DataInputStream> consumer) throws Exception {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File(path, name))))) {

            consumer.accept(in);

        }
    }
}
