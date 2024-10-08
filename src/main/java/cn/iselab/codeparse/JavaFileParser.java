package cn.iselab.codeparse;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.CommentsCollection;
import com.github.javaparser.printer.PrettyPrinter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.io.FileWriter;

import java.util.List;
import java.util.stream.Collectors;

import cn.iselab.utils.Utils;


/**
 * 目前使用这个进行解析
 * Parse Java files and extract method information
 */
public class JavaFileParser {


    public static void main(String[] args) throws IOException {
        String projectPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday";
//        List<String> fileNames = searchFiles(new File(projectPath + '/' + "src/main/java"));
        parseJavaFiles(projectPath);

//        String srcPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Nextday\\src\\main";
//        searchFiles(new File(srcPath));

//        String codefilePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\src\\main\\java\\net\\mooctest\\Triangle.java";
//        File file = new File(codefilePath);
//
//        JavaParser javaParser = new JavaParser();
//        ParseResult<CompilationUnit> parse = javaParser.parse(file);
//
//        Optional<CompilationUnit> optionalCompilationUnit = parse.getResult();
//        Optional<CommentsCollection> commentsCollection = parse.getCommentsCollection();
//        if (optionalCompilationUnit.isPresent()) {
//            CompilationUnit compilationUnit = optionalCompilationUnit.get();
//
//            // 遍历所有的类
//            for (ClassOrInterfaceDeclaration c : compilationUnit.findAll(ClassOrInterfaceDeclaration.class)) {
//                JSONArray jsonArray = new JSONArray();
//                JSONObject classJson = new JSONObject();
//
//                try {
//                    classJson.put("name", c.getName().asString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    classJson.put("code", c.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                if (c instanceof ClassOrInterfaceDeclaration) {
//                    classJson.put("type", "class");
//                }
//
//
//                jsonArray.put(classJson);
//
//                // 遍历类中的所有方法
//                for (MethodDeclaration m : c.findAll(MethodDeclaration.class)) {
//                    JSONObject methodJson = new JSONObject();
//                    try {
//                        methodJson.put("name", m.getNameAsString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (m instanceof MethodDeclaration) {
//                        methodJson.put("type", "method");
//                    }
//
//
//                    try {
//                        methodJson.put("code", m.getTokenRange().get().toString());
//                        System.out.println("code" + m.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        if (m.getChildNodes().toString() != null) {
//                            methodJson.put("children", m.getChildNodes().get(1));
//                            System.out.println("children" + m.getChildNodes().get(1));
//                        } else {
//                            methodJson.put("children", "");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    m.getParentNode().ifPresent(parent -> {
//                        if (parent instanceof ClassOrInterfaceDeclaration) {
//                            ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) parent;
//                            String parentName = parentClass.getNameAsString();
////                            String parentType =
//                            methodJson.put("parent", parentName);
//                            methodJson.put("parentType", "class");
//                            System.out.println("Method: " + m.getNameAsString() + " is in class/interface: " + parentName);
//                        }
//                        if (parent instanceof MethodDeclaration) {
//                            MethodDeclaration parentMethod = (MethodDeclaration) parent;
//                            String parentName = parentMethod.getNameAsString();
//                            methodJson.put("parent", parentName);
//                            methodJson.put("parentType", "method");
//                        }
//                    });
//
//                    if (m.getParameters().toString() != null) {
//                        methodJson.put("parameters", m.getParameters().toString());
//                        System.out.println("Parameter: " + m.getParameters().toString());
//                    } else {
//                        methodJson.put("parameters: ", "");
//                    }
//
//                    if (m.getComment().toString() != null) {
//
//                        Optional<String> comment = m.getComment().map(commentNode -> commentNode.getContent());
//
//                        methodJson.put("comment", comment.orElse(""));
//                        comment.ifPresent((String co) -> System.out.println("comment: " + co));
//                    } else {
//                        methodJson.put("comment: ", "");
//                    }
//
//                    List<String> parameterTypes = m.getParameters().stream()
//                            .map(p -> p.getType().asString())
//                            .collect(Collectors.toList());
//                    methodJson.put("signature", m.getName().toString() + "(" + String.join(", ", parameterTypes) + ")");
//
//
//                    jsonArray.put(methodJson);
//                    String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json";
//                    saveToFile(filePath, jsonArray.toString());
//                    System.out.println("saved to: " + "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json");
//
//                }
//
//            }
//
//        } else {
//            System.out.println("Failed to parse the file.");
//        }
    }


    /**
     *
     * @param dirPath 被测项目根目录
     * @throws IOException
     */
    public static void parseJavaFiles(String dirPath) throws IOException {
        File directory = new File(dirPath + File.separator + "src/main/java");
        List<String> sourceFilesPath = new ArrayList<>();
        Utils.searchJavaFiles(directory, sourceFilesPath);
        System.out.println("sourceFilesPath: " + sourceFilesPath);
        for (String sourcefilePath : sourceFilesPath) {
            File file = new File(sourcefilePath);
            String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));

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
                        classJson.put("code", c.toString());
                        CompilationUnit cu = c.findCompilationUnit().orElse(null);
                        if (cu != null) {
                            // 从 CompilationUnit 中获取包声明
                            Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration();
                            if (packageDeclaration.isPresent()) {
                                // 打印包名称
                                System.out.println("Class " + c.getName() + " is in package: " + packageDeclaration.get().getNameAsString());
                                classJson.put("packageName", packageDeclaration.get().getNameAsString());
                            }
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (c instanceof ClassOrInterfaceDeclaration) {
                        classJson.put("type", "class");
                    }
                    jsonArray.put(classJson);
                    // 遍历类中的所有方法
                    for (MethodDeclaration m : c.findAll(MethodDeclaration.class)) {
                        JSONObject methodJson = new JSONObject();
                        try {
                            methodJson.put("name", m.getNameAsString());
                            CompilationUnit cu = c.findCompilationUnit().orElse(null);
                            if (cu != null) {
                                // 从 CompilationUnit 中获取包声明
                                Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration();
                                if (packageDeclaration.isPresent()) {
                                    // 打印包名称
                                    System.out.println("Class " + c.getName() + " is in package: " + packageDeclaration.get().getNameAsString());
                                    methodJson.put("packageName", packageDeclaration.get().getNameAsString());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (m instanceof MethodDeclaration) {
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
                            if (parent instanceof MethodDeclaration) {
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

                        methodJson.put("start_line", m.getBegin().get().line);
                        methodJson.put("end_line", m.getEnd().get().line);

                        List<String> parameterTypes = m.getParameters().stream()
                                .map(p -> p.getType().asString())
                                .collect(Collectors.toList());
//                        methodJson.put("signature", m.getName().toString() + "(" + String.join(", ", parameterTypes) + ")");
                        methodJson.put("signature", m.getDeclarationAsString());

                        PrettyPrinter prettyPrinter = new PrettyPrinter();
                        methodJson.put("short_sig", m.getNameAsString() + m.getParameters().stream()
                                .map(expression -> prettyPrinter.print(expression))
                                .collect(Collectors.joining(", ", "(", ")")));


                        jsonArray.put(methodJson);
//                        String parseFilePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json";
//                        saveToFile(parseFilePath, jsonArray.toString());
//                        System.out.println("saved to: " + "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json");

                    }

                    String parseFilePath = dirPath + File.separator + "target/parsefiles/ast_json/" + fileName + ".json";
                    Utils.saveJsonToFile(parseFilePath, jsonArray.toString());
                    System.out.println("saved to: " + dirPath + '/' + "target/parsefiles/ast_json/" + fileName + ".json");
                }

            } else {
                System.out.println("Failed to parse the file.");
            }
        }
    }
}


