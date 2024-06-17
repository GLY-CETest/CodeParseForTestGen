package cn.iselab.mutant.process;

import org.benf.cfr.reader.Main;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MutantFinder {
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants\\1\\details.json";
        String astPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\parsefiles\\ast_json\\Day.json";
        int lineNumber = mutantLineNumberFinder(filePath);
        System.out.println("Mutant lineNumber: " + lineNumber);
        String methodName = mutantMethodNameFinder(astPath, lineNumber);
        System.out.println("Mutant method name: " + methodName);

        String sourceCodeFilePath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\classes\\net\\mooctest\\Day.java"; // 替换为你的.java文件路径
        String mutationCodeFilePath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants\\1\\net\\mooctest\\Day.java";
//        String methodName = "diffOfBorders";
        if (methodName != null){
            String methodSourceCode = sourceCodeFinderWithMethodName(sourceCodeFilePath, methodName);
            String methodMutationCode = mutationCodeFinderWithMethodName(mutationCodeFilePath, methodName);
            System.out.println("Method Source Code:\n" + methodSourceCode);
            System.out.println("Method Mutation Code:\n" + methodMutationCode);
            findDetailedCodeDifferences(methodSourceCode, methodMutationCode);
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
    public static @Nullable String sourceCodeFinderWithMethodName(String sourcefilePath, String methodName) throws Exception {
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
    public static void findDetailedCodeDifferences(String original, String mutated) {
        String[] originalLines = original.split("\n");
        String[] mutatedLines = mutated.split("\n");

        int maxLines = Math.max(originalLines.length, mutatedLines.length);

        System.out.println("Differences found:");

        for (int i = 0; i < maxLines; i++) {
            String originalLine = i < originalLines.length ? originalLines[i] : "";
            String mutatedLine = i < mutatedLines.length ? mutatedLines[i] : "";

            if (!originalLine.equals(mutatedLine)) {
                System.out.println("Line " + (i + 1) + ":");
                System.out.println("Original: " + originalLine);
                System.out.println("Mutated : " + mutatedLine);
                System.out.println();
            }
        }
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

}
