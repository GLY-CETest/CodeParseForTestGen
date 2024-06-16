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

//        String classPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\classes\\net\\mooctest\\Day.class";
//        String outputDir = "C:\\YGL\\Projects\\CodeParse\\libs";
//        excuteFernflower(classPath, outputDir);
        String projectPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday";
        deCompileAllClassesWithFF(projectPath);

    }


    public static void deCompileAllClassesWithFF(String projectPath) throws Exception {
        Path mutantsClassPath = Paths.get(projectPath, "target/mutants");
        Path sourceClassesPath = Paths.get(projectPath, "target/classes");

//        String directoryPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\classes"; // 替换为你的目录路径

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

        // 打印命令以供调试
        System.out.println("Executing command: " + Arrays.toString(command));

        // 使用ProcessBuilder执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 设置工作目录
        processBuilder.directory(new java.io.File("C:\\YGL\\Projects\\CodeParse\\libs"));

        try {
            Process process = processBuilder.start();

            // 获取命令行输出（如果有）
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

            // 获取命令行错误输出（如果有）
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

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归查找指定目录及其子目录中的所有.class文件
     *
     * @param directoryPath 要查找的目录路径
     * @return .class文件的完整路径列表
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
     * 递归方法，用于查找.class文件
     *
     * @param directory 当前目录
     * @param classFiles 用于存储找到的.class文件路径的列表
     */
    private static void findClassFilesRecursive(File directory, List<String> classFiles) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassFilesRecursive(file, classFiles); // 递归遍历子目录
                } else if (file.isFile() && file.getName().endsWith(".class")) {
                    classFiles.add(file.getAbsolutePath()); // 添加.class文件路径到列表中
                }
            }
        }
    }


}
