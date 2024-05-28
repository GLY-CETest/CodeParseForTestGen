package cn.iselab.parse;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.SourceRoot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MethodCallExtractor {

    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        private List<JsonObject> methodCalls = new ArrayList<>();

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

        public List<JsonObject> getMethodCalls() {
            return methodCalls;
        }
    }

    public static void main(String[] args) throws IOException {
        SourceRoot sourceRoot = new SourceRoot(Paths.get("C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\projUT\\Triangle\\src\\main\\java\\net\\mooctest"));
        CompilationUnit cu = sourceRoot.parse("", "Triangle.java");

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        methodCallVisitor.visit(cu, null);

        List<JsonObject> methodCalls = methodCallVisitor.getMethodCalls();
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(methodCalls).getAsJsonArray();

        try (FileWriter writer = new FileWriter("C:/YGL/Projects/pythonProject/MutationGPTTestGeneration/javaparserfile/Triangle/method_call_json/methodCalls.json")) {
            gson.toJson(jsonArray, writer);
        }
    }
}
