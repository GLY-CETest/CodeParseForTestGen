package cn.iselab.mutant.process;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;


public class FernflowerDecompiler {
    public static void main(String[] args) throws Exception {
        String projectPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday";
        deCompileAllClassesWithFF(projectPath);
    }


    /**
     * Decompile all classes of a project to java files with Fernflower.
     * @param projectDir
     * @throws Exception
     */
    public static void deCompileAllClassesWithFF(String projectDir) throws Exception {
        Path mutantsClassPath = Paths.get(projectDir, "target/mutants");
        Path sourceClassesPath = Paths.get(projectDir, "target/classes");


        List<String> sourceClassesFiles = findClassFiles(sourceClassesPath.toString());
        List<String> mutantsClassesFiles = findClassFiles(mutantsClassPath.toString());
//        System.out.println("Found .class files:");
        System.out.println("-----Decompiling source classes-----");
        for (String filePath : sourceClassesFiles) {
            decompileWithFernflower(filePath, new File(filePath).getParent().toString());
        }
        System.out.println("-----Decompiling mutant classes-----");
        for (String filePath : mutantsClassesFiles) {
            decompileWithFernflower(filePath, new File(filePath).getParent().toString());
        }
    }


    /**
     * Execute Fernflower to decompile a class file to a java file and save it.
     * @param classFilePath
     * @param outputDir
     */
    public static void decompileWithFernflower(String classFilePath, String outputDir){
        // Build the command
        String[] command = {
                "java",
                "-jar",
                System.getProperty("user.dir") + File.separator + "libs" + File.separator + "fernflower.jar",
                classFilePath,
                outputDir
        };
        // Print the command to console
        System.out.println("Executing command: " + Arrays.toString(command));
        // Execute the command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // Set the working directory
//        processBuilder.directory(new java.io.File(System.getProperty("user.dir") + File.separator + "libs"));
        try {
            Process process = processBuilder.start();
            // Get the command output
            new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            // Get the command error
            new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            // Wait for the command to finish
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Recursively search for all .class files in a specified directory and its subdirectories.
     *
     * @param directoryPath the directory path to search
     * @return a list of all paths of .class files in the specified directory and its subdirectories
     */
    public static @NotNull List<String> findClassFiles(String directoryPath) {
        List<String> classFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            findClassFilesRecursive(directory, classFiles);
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }

        return classFiles;
    }


    /**
     *
     * Recursively search for .class files recursively
     *
     * @param directory
     * @param classFiles
     */
    private static void findClassFilesRecursive(File directory, List<String> classFiles) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassFilesRecursive(file, classFiles);
                } else if (file.isFile() && file.getName().endsWith(".class")) {
                    classFiles.add(file.getAbsolutePath());
                }
            }
        }
    }
}
