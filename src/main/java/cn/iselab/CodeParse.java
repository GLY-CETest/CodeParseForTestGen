package cn.iselab;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.stmt.Statement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class CodeParse {


    public static void main(String[] args) throws IOException {
        // 从文件中读取 Java 代码
        FileInputStream in = new FileInputStream("C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\source_code\\Triangle.java");
        CompilationUnit cu = StaticJavaParser.parse(in);

        // 遍历所有方法
        cu.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
            // 输出方法名
            System.out.println("方法名: " + methodDeclaration.getName());

            // 输出方法体
            methodDeclaration.getTokenRange().ifPresent(tokens -> {
                for (JavaToken statement : tokens) {
                    System.out.println(statement);
                }
            });
        });
    }


}
