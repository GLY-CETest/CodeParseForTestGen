package cn.iselab.mutant.process;

import org.benf.cfr.reader.Main;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.JsonObject;

import cn.iselab.utils.Utils;


public class MutantFinder {
    public static void main(String[] args) throws Exception {
//        String className = getClassName("C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants\\2\\net\\mooctest");
//        System.out.println(className);
        String projectPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday";
        saveDetailsOfMuAndOriToJson(projectPath);
    }


    public static void saveDetailsOfMuAndOriToJson(String projectPath) throws Exception {
        String mutantsDir = projectPath + File.separator + "target" + File.separator + "mutants";
        long mutantnumber = Utils.folderCounter(mutantsDir);

        for (int i = 1; i <= mutantnumber; i++) {
            String mutantDetailsPath = mutantsDir + File.separator + i + File.separator + "details.json";
            String className = getClassName(mutantsDir + File.separator + i);
            System.out.println("className: " + className);
            if (className != null) {
                String astPath = projectPath + File.separator + "target" + File.separator + "parsefiles" + File.separator + "ast_json" + File.separator + className + ".json";
                System.out.println("astPath: " + astPath);
                JsonObject jsonObject = new JsonObject();
                int lineNumber = mutantLineNumberFinder(mutantDetailsPath);
                System.out.println("mutant_linenumber: " + lineNumber) ;
                jsonObject.addProperty("mutant_lineNumber: ", lineNumber);
                String methodName = mutantMethodNameFinder(astPath, lineNumber);
                System.out.println("mutant_method_name: " + methodName);
                jsonObject.addProperty("mutant_method_name: ", methodName);
                if (methodName != null) {

                }
            }
            else continue;
//            if (className != null) {
//                String astPath = projectPath + File.separator + "target" + File.separator + "parsefiles" + File.separator + "ast_json" + File.separator + className + ".json";
//                JsonObject jsonObject = new JsonObject();
//                int lineNumber = mutantLineNumberFinder(mutantDetailsPath);
//                System.out.println("mutant_linenumber: " + lineNumber) ;
//                jsonObject.addProperty("mutant_lineNumber: ", lineNumber);
//                String methodName = mutantMethodNameFinder(astPath, lineNumber);
//                System.out.println("mutant_method_name: " + methodName);
//                jsonObject.addProperty("mutant_method_name: ", methodName);
//
//                if (methodName != null) {
//                    String originCodeFilePath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + className + ".java";
//                    String mutationCodeFilePath = projectPath + File.separator + "target" + File.separator + "mutants"
//                            + File.separator + i + File.separator + "net" + File.separator + "mooctest" + File.separator + className + ".java";
//                    String methodSourceCode = originCodeFinderWithMethodName(originCodeFilePath, methodName);
//                    String methodMutationCode = mutationCodeFinderWithMethodName(mutationCodeFilePath, methodName);
//                    System.out.println("method_original_code:\n" + methodSourceCode);
//                    jsonObject.addProperty("method_original_code:", methodSourceCode);
//                    System.out.println("method_mutated_code:\n" + methodMutationCode);
//                    jsonObject.addProperty("method_mutated_code:", methodMutationCode);
//                    findDetailedCodeDifferences(methodSourceCode, methodMutationCode);
//                    String jsonPath = mutantsDir + File.separator + i + File.separator + "mutantDetails.json";
//                    Utils.saveJsonToFile(jsonPath, jsonObject.getAsString());
//                }
//                else continue;
//            }
//            else continue;


//        String mutantDetailsPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants\\1\\details.json";
//        String astPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\parsefiles\\ast_json\\Day.json";

//        String originCodeFilePath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\classes\\net\\mooctest\\Day.java";
//        String mutationCodeFilePath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants\\1\\net\\mooctest\\Day.java";
//        String methodName = "diffOfBorders";
        }
    }


    /**
     * get the lineNumber of a mutant where the mutation operator is applied
     * @param path
     * @return lineNumber
     * @throws Exception
     */
    public static int mutantLineNumberFinder(String path) throws Exception {
//        File file = new File(path);
        int lineNumber = 0;
        try {
            // 读取文件内容到字符串
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));
            // 解析JSON字符串
            JSONObject jsonObject = new JSONObject(jsonString);
            // 获取lineNumber的值
            lineNumber = jsonObject.getInt("lineNumber");
            // 打印lineNumber的值
//            System.out.println("lineNumber: " + lineNumber);
            return lineNumber;
        } catch (IOException e) {
            e.printStackTrace();
            return lineNumber;
        }
    }


    /**
     * get the methodName of a mutant
     * @param path
     * @param lineNumber
     * @return methodName
     * @throws Exception
     */
    public static @Nullable String mutantMethodNameFinder(String path, int lineNumber) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 读取 JSON 文件
            File jsonFile = new File(path);
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            // 确保读取的是数组
            if (jsonNode.isArray()) {
                // 遍历数组中的每个元素
                for (JsonNode elementNode : jsonNode) {
                    // 处理每个 JSON 对象
                    String name = elementNode.get("name").asText();
                    String type = elementNode.get("type").asText();
                    if (type.equals("method")) {
                        int start_line = elementNode.get("start_line").asInt();
                        int end_line = elementNode.get("end_line").asInt();
                        if (lineNumber >= start_line && lineNumber <= end_line)
                        {
//                            System.out.println("name: " + name);
                            return name;
                        }
                        else
                            continue;
                    }
                    else
                        continue;
                }
            } else {
                System.out.println("Root is not an array.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * get the sourceCode of a method
     * @param sourcefilePath
     * @param methodName
     * @return
     * @throws Exception
     */
    public static @Nullable String originCodeFinderWithMethodName(String sourcefilePath, String methodName) throws Exception {
//        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\classes\\net\\mooctest\\Triangle.java"; // 替换为你的.java文件路径
//        String methodName = "diffOfBorders";

        try {
            FileInputStream in = new FileInputStream(new File(sourcefilePath));
            CompilationUnit cu = StaticJavaParser.parse(in);

            MethodVisitor methodVisitor = new MethodVisitor(methodName);
            methodVisitor.visit(cu, null);

            Optional<MethodDeclaration> methodOpt = methodVisitor.getMethod();
            if (methodOpt.isPresent()) {
//                System.out.println("Found method source code:");
//                System.out.println(methodOpt.get().toString());
                return methodOpt.get().toString();
            } else {
//                System.out.println("Method not found.");
                return null;
            }
//            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * find the detailed code differences between two methods, return the differences and line numbers
     * @param original
     * @param mutated
     */
    public static JsonObject findDetailedCodeDifferences(String original, String mutated) {
        JsonObject jsonObject = new JsonObject();
        String[] originalLines = original.split("\n");
        String[] mutatedLines = mutated.split("\n");

        int maxLines = Math.max(originalLines.length, mutatedLines.length);

        System.out.println("Differences found:");

        for (int i = 0; i < maxLines; i++) {
            String originalLine = i < originalLines.length ? originalLines[i] : "";
            String mutatedLine = i < mutatedLines.length ? mutatedLines[i] : "";

            if (!originalLine.equals(mutatedLine)) {
                System.out.println("linenum_in_method: " + i);
                System.out.println("line_original: " + originalLine);
                System.out.println("line_mutated : " + mutatedLine);
                jsonObject.addProperty("linenum_in_method", i);
                jsonObject.addProperty("line_original", originalLine);
                jsonObject.addProperty("mutated", mutatedLine);
            }
        }
        return jsonObject;
    }


    /**
     * get the code of a mutant
     * @param mutantFilePath
     * @param methodName
     * @return
     * @throws Exception
     */
    public static @Nullable String mutationCodeFinderWithMethodName(String mutantFilePath, String methodName) throws Exception {
//        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\mutants\\1\\net\\mooctest\\Triangle.java"; // 替换为你的.java文件路径
//        String methodName = "diffOfBorders";

        try {
            FileInputStream in = new FileInputStream(new File(mutantFilePath));
            CompilationUnit cu = StaticJavaParser.parse(in);

            MethodVisitor methodVisitor = new MethodVisitor(methodName);
            methodVisitor.visit(cu, null);

            Optional<MethodDeclaration> methodOpt = methodVisitor.getMethod();
            if (methodOpt.isPresent()) {
//                System.out.println("Found method mutation code:");
//                System.out.println(methodOpt.get().toString());
                return methodOpt.get().toString();
            } else {
//                System.out.println("Method not found.");
                return null;
            }
//            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        private final String methodName;
        private MethodDeclaration method;

        public MethodVisitor(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            if (md.getNameAsString().equals(methodName)) {
                method = md;
            }
        }

        public Optional<MethodDeclaration> getMethod() {
            return Optional.ofNullable(method);
        }
    }


    /**
     * get the class name of a mutant
     * @param dir the dir of the mutant file
     * @return the class name without the .java extension
     */
    public static @Nullable String getClassName(String dir){
        File directory = new File(dir);
        List<String> javaFilesPath = new ArrayList<>();
        Utils.searchJavaFiles(new File(dir), javaFilesPath);
//        System.out.println("javaFilesPath: " + javaFilesPath);
        File file = new File(javaFilesPath.get(0).toString());
//        File[] files = directory.listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".java"));
        String className = file.getName();
        // 获取最后一个点号的位置，它是文件名和扩展名的分隔符
        int dotIndex = className.lastIndexOf('.');
        // 检查点号是否存在且不是文件名的第一个字符
        if (dotIndex > 0) {
            // 使用 substring() 方法截取文件名
            String baseName = className.substring(0, dotIndex);
            // 打印文件名
//            System.out.println("文件名（无扩展名）: " + baseName);
            className = baseName;
        } else {
            // 如果文件没有扩展名，直接打印文件名
//            System.out.println("文件名（无扩展名）: " + className);
            className = null;
        }
//        System.out.println("className: " + className);
        return className;

    }

}
