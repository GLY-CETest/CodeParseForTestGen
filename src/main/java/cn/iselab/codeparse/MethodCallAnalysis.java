package cn.iselab.codeparse;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.MessageFormat;
import java.util.stream.Collectors;


/**
 * analysis the method calls in java files
 */
public class MethodCallAnalysis {

    public static void main(String[] args) {
        // 指定源码根目录
        String sourceRootPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday_1523352132921";
        analyzeAllMethodCallsAndSaveToJson(sourceRootPath);
    }


    public static void analyzeAllMethodCallsAndSaveToJson(String prjectDir) {
        String javaFileDir = prjectDir + File.separator + "src" + File.separator + "main" + File.separator + "java";
        try {
            // 遍历目录下的所有 Java 文件
            Files.walk(Paths.get(javaFileDir))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(MethodCallAnalysis::analyzeAndSaveMethodCalls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void analyzeAndSaveMethodCalls(Path javaFilePath) {
        try {
            // 解析 Java 文件
            CompilationUnit cu = StaticJavaParser.parse(javaFilePath);

            // 创建一个列表来存储方法调用关系
            List<Map<String, Object>> methodCallsList = new ArrayList<>();

            // 访问并分析方法调用
            cu.accept(new MethodCallVisitor(methodCallsList), null);

            // 将结果写入 JSON 文件
            saveMethodCallsToJson(javaFilePath, methodCallsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveMethodCallsToJson(Path javaFilePath, List<Map<String, Object>> methodCallsList) {
        // 创建 Gson 实例
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 构建 JSON 文件名
        String jsonFileName = javaFilePath.toString().replace(".java", ".json");
        jsonFileName = jsonFileName.replace(
                "src" + File.separator + "main" + File.separator + "java" + File.separator + "net" + File.separator + "mooctest",
                "target" + File.separator + "parsefiles" + File.separator + "method_call");
        File parentDir = new File(jsonFileName).getParentFile();
        if (!parentDir.exists()){
            if (parentDir.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;
            }
        }

        try (FileWriter writer = new FileWriter(jsonFileName)) {
            // 将方法调用关系写入 JSON 文件
            gson.toJson(methodCallsList, writer);
            System.out.println("Saved method calls to: " + jsonFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        private final List<Map<String, Object>> methodCallsList;

        public MethodCallVisitor(List<Map<String, Object>> methodCallsList) {
            this.methodCallsList = methodCallsList;
        }

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);

            // 创建一个 Map 来存储方法信息
            Map<String, Object> methodInfo = new HashMap<>();
            methodInfo.put("callerName", md.getNameAsString());
            List<String> parameterTypes = md.getParameters().stream()
                    .map(p -> p.getType().asString())
                    .collect(Collectors.toList());
//            methodInfo.put("signature", md.getName().toString() + "(" + String.join(", ", parameterTypes) + ")");
            //为了与ast匹配，使用getDeclarationAsString()
            methodInfo.put("signature", md.getDeclarationAsString());
            methodInfo.put("methodCalls", new ArrayList<Map<String, Object>>());

            // 查找方法调用
            List<MethodCallExpr> methodCalls = md.findAll(MethodCallExpr.class);
            for (MethodCallExpr mce : methodCalls) {
                Map<String, Object> callInfo = new HashMap<>();
                callInfo.put("name", mce.getNameAsString());
                PrettyPrinter prettyPrinter = new PrettyPrinter();
                callInfo.put("signature", mce.getNameAsString() + mce.getArguments().stream()
                        .map(expression -> prettyPrinter.print(expression))
                        .collect(Collectors.joining(", ", "(", ")")));
//                mce.get
                callInfo.put("methodName", mce.findAncestor(ClassOrInterfaceDeclaration.class).orElse(null).getName().toString());
                callInfo.put("line", mce.getBegin().get().line);

                ((List<Map<String, Object>>) methodInfo.get("methodCalls")).add(callInfo);
            }

            // 将方法信息添加到列表
            methodCallsList.add(methodInfo);
        }
    }
}
