import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.CommentsCollection;
import com.github.javaparser.ast.comments.JavadocComment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.util.List;
import java.util.stream.Collectors;

public class JavaFileParser {
    public static void saveToFile(String filePath, String string){
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(string);
            fileWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
//        if (args.length
        String codefilePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\projUT\\Triangle\\src\\main\\java\\net\\mooctest\\Triangle.java";
        File file = new File(codefilePath);
//        if (!file.exists()) {
//            System.out.println("File does not exist: " + filePath);
//            return;
//        }

        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> parse = javaParser.parse(file);

        Optional<CompilationUnit> optionalCompilationUnit = parse.getResult();
        Optional<CommentsCollection> commentsCollection = parse.getCommentsCollection();
        if (optionalCompilationUnit.isPresent()) {
            CompilationUnit compilationUnit = optionalCompilationUnit.get();

            // 遍历所有的类
            for (ClassOrInterfaceDeclaration c : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
                JSONArray jsonArray = new JSONArray();
                JSONObject classJson = new JSONObject();

                try {
                    classJson.put("name", c.getName().asString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    classJson.put("code", c.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (c instanceof ClassOrInterfaceDeclaration){
                    classJson.put("type", "class");
                }


                jsonArray.put(classJson);

                // 遍历类中的所有方法
                for (MethodDeclaration m : c.findAll(MethodDeclaration.class)) {
                    JSONObject methodJson = new JSONObject();
                    try {
                        methodJson.put("name", m.getNameAsString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(m instanceof MethodDeclaration){
                        methodJson.put("type", "method");
                    }


                    try {
                        methodJson.put("code", m.getTokenRange().get().toString());
                        System.out.println("code" + m.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (m.getChildNodes().toString() != null) {
                            methodJson.put("children", m.getChildNodes().get(1));
                            System.out.println("children" + m.getChildNodes().get(1));
                        } else {
                            methodJson.put("children", "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    m.getParentNode().ifPresent(parent -> {
                        if (parent instanceof ClassOrInterfaceDeclaration) {
                            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) parent;
                            String parentName = parentClass.getNameAsString();
//                            String parentType =
                            methodJson.put("parent", parentName);
                            methodJson.put("parentType", "class");
                            System.out.println("Method: " + m.getNameAsString() + " is in class/interface: " + parentName);
                        }
                        if (parent instanceof MethodDeclaration){
                            MethodDeclaration parentMethod = (MethodDeclaration) parent;
                            String parentName = parentMethod.getNameAsString();
                            methodJson.put("parent", parentName);
                            methodJson.put("parentType", "method");
                        }
                    });

                    if (m.getParameters().toString() != null) {
                        methodJson.put("parameters", m.getParameters().toString());
                        System.out.println("Parameter: " + m.getParameters().toString());
                    } else {
                        methodJson.put("parameters: ", "");
                    }

                    if (m.getComment().toString() != null) {

                        Optional<String> comment = m.getComment().map(commentNode -> commentNode.getContent());

                        methodJson.put("comment", comment.orElse(""));
                        comment.ifPresent((String co) -> System.out.println("comment: " + co));
                    } else {
                        methodJson.put("comment: ", "");
                    }

                    List<String> parameterTypes = m.getParameters().stream()
                            .map(p -> p.getType().asString())
                            .collect(Collectors.toList());
                    methodJson.put("signature", m.getName().toString() + "(" + String.join(", ", parameterTypes) + ")");


                    jsonArray.put(methodJson);
                    String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json";
                    saveToFile(filePath, jsonArray.toString());
                    System.out.println("saved to: " + "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json");

                }


            }

        } else {
            System.out.println("Failed to parse the file.");
        }
    }
}


