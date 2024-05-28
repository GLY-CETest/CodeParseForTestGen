package cn.iselab.parse;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MethodCallExtractorSimple {

    public static void main(String[] args) {
        // 指定要解析的Java文件路径
        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\source_proj\\Triangle\\Triangle.java";

        // 解析Java文件
        try (FileInputStream in = new FileInputStream(filePath)) {
            CompilationUnit cu = StaticJavaParser.parse(in);

            // 创建一个VoidVisitorAdapter来访问方法声明
            cu.accept(new MethodVisitor(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 自定义的VoidVisitorAdapter来访问方法声明
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            System.out.println("Method: " + md.getName());

            // 创建一个MethodCallVisitor来访问方法调用
            md.accept(new MethodCallVisitor(), null);
        }
    }

    // 自定义的VoidVisitorAdapter来访问方法调用
    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodCallExpr mc, Void arg) {
            super.visit(mc, arg);
            System.out.println("  Called method: " + mc.getName());
        }
    }
}
