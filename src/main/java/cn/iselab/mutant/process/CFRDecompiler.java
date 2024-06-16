package cn.iselab.mutant.process;

import org.benf.cfr.reader.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


/**
 * 使用CFR进行反编译，由于反编译出的代码与源代码差别较大，暂时放弃不用
 */
public class CFRDecompiler {
    public static void main(String[] args) throws Exception
    {
        String projectPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Nextday";
        deCompileAllClasses(projectPath);

    }


    /**
     * deCompile all classes in the project, including source classes and mutant classes
     * @param projectPath the path of the project
     * @throws Exception
     */
    public static void deCompileAllClasses(String projectPath) throws Exception {
        Path mutantsClassPath = Paths.get(projectPath, "target/mutants");
        Path sourceClassesPath = Paths.get(projectPath, "target/classes");

//        String directoryPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\classes"; // 替换为你的目录路径

        List<String> sourceClassesFiles = findClassFiles(sourceClassesPath.toString());
        List<String> mutantsClassesFiles = findClassFiles(mutantsClassPath.toString());
//        System.out.println("Found .class files:");
        System.out.println("-----Decompiling source classes-----");
        for (String filePath : sourceClassesFiles) {
            deCompileWithCFR(filePath, filePath.replace(".class", ".java"));
        }
        System.out.println("-----Decompiling mutant classes-----");
        for (String filePath : mutantsClassesFiles) {
            deCompileWithCFR(filePath, filePath.replace(".class", ".java"));
        }
    }


    /**
     * deCompile a class file to a java file
     *
     * @param classFilePath the path of the class file
     * @param outputPath the path of the output file
     * @throws Exception
     */
    public static void deCompileWithCFR(String classFilePath, String outputPath) throws Exception {
//        String classFilePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\mutants\\1\\net\\mooctest\\Triangle.class"; // 替换为你的.class文件路径
//        String outputPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\mutants\\1\\net\\mooctest\\Triangle.java";  // 替换为你想要输出的.java文件路径

        // 准备CFR反编译器的参数
        String[] cfrArgs = {classFilePath};

        // 捕获反编译输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        // 执行反编译
        Main.main(cfrArgs);

        // 恢复标准输出流
        System.out.flush();
        System.setOut(oldOut);

        // 获取反编译后的内容
        String decompiledContent = baos.toString();

        // 将反编译后的内容写入文件
        try {
            Files.write(Paths.get(outputPath), decompiledContent.getBytes());
            System.out.println("Decompiled file written to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     * 递归查找指定目录及其子目录中的所有.class文件
     *
     * @param directoryPath 要查找的目录路径
     * @return .class文件的完整路径列表
     */
    public static List<String> findClassFiles(String directoryPath) {
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
