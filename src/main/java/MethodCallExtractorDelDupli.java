import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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

    public static void main(String[] args) throws IOException {
        // 解析源代码文件路径
        SourceRoot sourceRoot = new SourceRoot(Paths.get("C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\projUT\\Triangle\\src\\main\\java\\net\\mooctest"));
        CompilationUnit cu = sourceRoot.parse("", "Triangle.java");

        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
        methodCallVisitor.visit(cu, null);

        Set<JsonObject> methodCalls = methodCallVisitor.getMethodCalls();
        JsonArray jsonArray = new JsonArray();

        for (JsonObject call : methodCalls) {
            jsonArray.add(call);
        }

        // 将去重后的调用关系保存为JSON文件
        try (FileWriter writer = new FileWriter("C:/YGL/Projects/pythonProject/MutationGPTTestGeneration/javaparserfile/Triangle/method_call_json/method_calls.json")) {
            new Gson().toJson(jsonArray, writer);
        }
    }
}
