package cn.iselab;


import java.io.File;
import java.util.List;
import java.util.ArrayList;

import cn.iselab.mutant.process.ProjectPackaging;
import cn.iselab.mutant.generating.GenMutantsNoCommandArgs;
import cn.iselab.mutant.process.FernflowerDecompiler;
import cn.iselab.codeparse.JavaFileParser;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = System.getProperty("user.dir") + File.separator + "projUT";
        List<String> projectDirs = getProjectDirs(path);

        for (String projectDir : projectDirs) {
            System.out.println(String.format("----------Packaging project %s----------", projectDir));
            ProjectPackaging.packageProjectToJar(projectDir);
            List<String> jarFiles = ProjectPackaging.findJarFiles(projectDir);
            String jarFilePath = jarFiles.get(0);
            System.out.println(String.format("Jar file path is: %s", jarFilePath));

            System.out.println(String.format("----------Parsing java files in project %s----------", projectDir));
            JavaFileParser.parseJavaFiles(projectDir);

            System.out.println(String.format("----------Generating mutants for project %s----------", projectDir));
            List<String> sourceJarPaths = new ArrayList<>();
            sourceJarPaths.add(jarFilePath);
            GenMutantsNoCommandArgs.generateMutantsAndOutput(sourceJarPaths,
                    projectDir + File.separator + "target" + File.separator + "mutants");

            System.out.println(String.format("----------Decompiling class files in %s----------", projectDir));
            FernflowerDecompiler.deCompileAllClassesWithFF(projectDir);


        }
    }



    public static List<String> getProjectDirs(String path) {
        List<String> subDirs = new ArrayList<>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        subDirs.add(file.getAbsolutePath());
                    }
                }
            }
        }

        return subDirs;
    }
}
