package cn.iselab;


import java.io.File;
import java.util.List;
import java.util.ArrayList;

import cn.iselab.mutant.process.MutantFinder;
import cn.iselab.mutant.process.ProjectPackaging;
import cn.iselab.mutant.generating.GenMutantsNoCommandArgs;
import cn.iselab.mutant.process.FernflowerDecompiler;
import cn.iselab.codeparse.JavaFileParser;
import cn.iselab.codeparse.MethodCallAnalysis;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "";
        if(args.length == 1){
            path = args[0];
        } else if (args.length > 1) {
            System.out.println("Too many arguments");
            return;
        } else {
            path = System.getProperty("user.dir") + File.separator + "projUT";
//            System.out.println("Please input the project path as the first argument");

        }

        List<String> projectDirs = getProjectDirs(path);


        for (String projectDir : projectDirs) {
            System.out.printf("==========Packaging project %s==========%n", projectDir);
            ProjectPackaging.packageProjectToJar(projectDir);
            List<String> jarFiles = ProjectPackaging.findJarFiles(projectDir);
            String jarFilePath = jarFiles.get(0);
            System.out.printf("jar file path is: %s%n", jarFilePath);

            System.out.printf("==========Parsing ast from java files in project %s==========%n", projectDir);
            JavaFileParser.parseJavaFiles(projectDir);

            System.out.printf("==========Analyzing method calls in project %s==========%n", projectDir);
            MethodCallAnalysis.analyzeAllMethodCallsAndSaveToJson(projectDir);

            System.out.printf("==========Generating mutants for project %s==========%n", projectDir);
            List<String> sourceJarPaths = new ArrayList<>();
            sourceJarPaths.add(jarFilePath);
            GenMutantsNoCommandArgs.generateMutantsAndOutput(sourceJarPaths,
                    projectDir + File.separator + "target" + File.separator + "mutants");

            System.out.printf("==========Decompiling class files in %s==========%n", projectDir);
            FernflowerDecompiler.deCompileAllClassesWithFF(projectDir);

            System.out.printf("==========Analyzing mutants in project %s==========%n", projectDir);
            MutantFinder.saveDetailsOfMuAndOriToJson(projectDir);
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
