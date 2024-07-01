package cn.iselab.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void main(String[] args){
        String folderPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants"; // 替换为你的文件夹路径
        long count = folderCounter(folderPath);
        File file = new File("C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\parsefiles\\ast_json\\Day.json");
        System.out.println(file.getName());
        List<String> fileList = new ArrayList<>();
        String dir = "C:\\Users\\dell\\Desktop\\100000\\4271";
        searchJavaFiles(new File(dir), fileList);
        System.out.printf("Number of java files: %d\n", fileList.size());
    }

    public static long folderCounter(String path)
    {
        long folderCount = 0;
        try {
            folderCount = Files.list(Paths.get(path))
                    .filter(Files::isDirectory)
                    .count();
            System.out.println("Number of folders: " + folderCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderCount;
    }


    /**
     * 将字符串保存到文件
     * @param filePath
     * @param string
     */
    public static void saveJsonToFile(String filePath, String string) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if(!parentDir.exists()){
            if (parentDir.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(string);
            fileWriter.close();
            System.out.println("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     * Search all java files in the directory and its subdirectories
     * @param directory
     * @param javaFilesPath 用于报错javafiles路径的列表，调用该方法前需要先定义一个List<String> sourceFilesPath变量
     */
    public static void searchJavaFiles(File directory, List<String> javaFilesPath) {
        // get all files and directories in the directory
        File[] files = directory.listFiles();
//        List<String> sourceFilesPath = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
//                    System.out.println("File: " + file.getAbsolutePath());
                    javaFilesPath.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    // 递归查找子目录
                    searchJavaFiles(file, javaFilesPath);
                }
            }
        }
//        System.out.println(javaFilesPath);
//        return sourceFilesPath;
    }


    /**
     * 递归查找目录中的所有json文件
     * @param directory
     * @param jsonFilesPath 用于保存所有json文件路径的列表，调用该方法前需要先定义一个List<String> jsonFilesPath变量
     */
    public static String searchJsonFiles(File directory, List<String> jsonFilesPath) {
        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
//        List<String> sourceFilesPath = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    // 打印文件路径
//                    System.out.println("File: " + file.getAbsolutePath());
                    jsonFilesPath.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    // 递归查找子目录
                    searchJavaFiles(file, jsonFilesPath);
                }
            }
        }
        return jsonFilesPath.get(0).toString();
//        System.out.println(javaFilesPath);
//        return sourceFilesPath;
    }


    /**
     * given a class name and source file directory, find the corresponding java file
     * @param className
     * @param sourceFileDir
     * @return
     */
    public static String searchOriJavaFile(String className, String sourceFileDir) {
        List<String> javaFilesPath = new ArrayList<>();
        searchJavaFiles(new File(sourceFileDir), javaFilesPath);
        for (String path : javaFilesPath){
            File file = new File(path);
            String fileName = file.getName();
            if (file.getName().endsWith(".java") && fileName.equals(className + ".java")) return file.getPath();
        }
        return null;
    }


    /**
     *
     * @param className
     * @param mutantFileDir
     * @return
     */
    public static String searchMutantJavaFile(String className, String mutantFileDir) {
        List<String> javaFilesPath = new ArrayList<>();
        searchJavaFiles(new File(mutantFileDir), javaFilesPath);
        for (String path : javaFilesPath) {
            File file = new File(path);
            String fileName = file.getName();
            if (file.getName().endsWith(".java") && fileName.equals(className + ".java")) return file.getPath();
        }
        return null;
    }


    /**
     * replace java files with decompiled java files
     * @param projectPath
     */
    public static void javaFileReplace(String projectPath){

    }

}
