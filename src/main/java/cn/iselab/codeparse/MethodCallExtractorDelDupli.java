package cn.iselab.codeparse;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.iselab.utils.Utils;


/**
 * 抽取类中各个方法的调用关系，并删除其中重复的调用
 */
public class MethodCallExtractorDelDupli {

    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        private Set<JsonObject> methodCalls = new HashSet<>();

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            md.getBody().ifPresent(body -> body.accept(new MethodCallExprVisitor(md.getNameAsString()), null));
        }

        private class MethodCallExprVisitor extends VoidVisitorAdapter<Void> {
            private String caller;

            public MethodCallExprVisitor(String caller) {
                this.caller = caller;
            }

            @Override
            public void visit(MethodCallExpr mce, Void arg) {
                super.visit(mce, arg);
                JsonObject call = new JsonObject();
                call.addProperty("caller", caller);
                call.addProperty("callee", mce.getNameAsString());
                methodCalls.add(call);
            }
        }

        public Set<JsonObject> getMethodCalls() {
            return methodCalls;
        }
    }

    public static void parseMethodCalls(String dirPath){
        File directory = new File(dirPath + '/' + "src/main/java");
        List<String> sourceFilesPath = new ArrayList<>();
        Utils.searchJavaFiles(directory, sourceFilesPath);

    }

    public static void main(String[] args) throws IOException {
        // 解析源代码文件路径
        SourceRoot sourceRoot = new SourceRoot(Paths.get("C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday"));
        CompilationUnit cu = StaticJavaParser.parse(Paths.get("C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\src\\main\\java\\net\\mooctest\\Year.java"));

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        methodCallVisitor.visit(cu, null);

        Set<JsonObject> methodCalls = methodCallVisitor.getMethodCalls();
        JsonArray jsonArray = new JsonArray();

        for (JsonObject call : methodCalls) {
            jsonArray.add(call);
        }

        // 将去重后的调用关系保存为JSON文件
        try (FileWriter writer = new FileWriter("C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\javaparserfile\\method_calls.json")) {
            new Gson().toJson(jsonArray, writer);
        }
    }
}
