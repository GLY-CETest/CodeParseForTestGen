package cn.iselab.mutant.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * pacakge a certain maven project to a jar file
 * @Author: YGL
 */
public class ProjectPackaging {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 指定maven项目的根目录
        String projectDir = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday";
        packageProjectToJar(projectDir);
        findJarFiles(projectDir);
    }


    /**
     * 执行Packaging命令
     *
     * @param command 要执行的命令
     * @param projectDir 命令执行的工作目录
     */
    public static int executePackagingCommand(String[] command, String projectDir) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // 设置工作目录为maven项目的根目录
        processBuilder.directory(new java.io.File(projectDir));

        try {
            // 启动进程
            Process process = processBuilder.start();
            // 获取命令行输出
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            // 等待进程结束并获取退出值
            int exitCode = process.waitFor();
//            System.out.println("Exited with code: " + exitCode);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 执行系统命令
     *
     * @param command 要执行的命令
     * @param projectDir 命令执行的工作目录
     */
    public static void executeCommand(String[] command, String projectDir) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        // 设置工作目录为maven项目的根目录
        processBuilder.directory(new java.io.File(projectDir));

        try {
            // 启动进程
            Process process = processBuilder.start();
            // 获取命令行输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待进程结束并获取退出值
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Package project to jar
     * @param projectDir projectDir
     * @throws IOException
     * @throws InterruptedException
     */
    public static void packageProjectToJar(String projectDir) throws IOException, InterruptedException {
        // 指定要执行的命令
        String[] command = {"cmd.exe", "/c", "mvn clean package"};

        int exitCode = executePackagingCommand(command, projectDir);
        if (exitCode == 0) {
            System.out.println("Project packaged successfully.");
        }
        else {
            System.out.println("Failed to package project.");
        }
    }

    /**
     * 递归查找指定目录及其子目录中的所有jar文件
     *
     * @param directoryPath 要查找的目录路径
     * @return
     */
    public static List<String> findJarFiles(String directoryPath) {
        List<String> jarFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            findJarFilesRecursive(directory, jarFiles);
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }

        return jarFiles;
    }

    /**
     * 递归方法，用于查找jar文件
     *
     * @param directory 当前目录
     * @param jarFiles 用于存储找到的jar文件路径的列表
     */
    private static void findJarFilesRecursive(File directory, List<String> jarFiles) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findJarFilesRecursive(file, jarFiles); // 递归遍历子目录
                } else if (file.isFile() && file.getName().endsWith(".jar")) {
                    jarFiles.add(file.getAbsolutePath()); // 添加.class文件路径到列表中
                }
            }
        }
    }
}
