package cn.iselab.parse;

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
import java.util.ArrayList;
import java.util.Optional;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Paths;
import java.nio.file.Path;

public class JavaFileParser {
    /**
     * 将字符串保存到文件
     * @param filePath
     * @param string
     */
    public static void saveToFile(String filePath, String string) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if(!parentDir.exists()){
            if (parentDir.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;
            }
        }

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

    /**
     * 递归查找目录中的所有源代码
     * @param directory
     * @return
     */
    public static void searchFiles(File directory, List<String> sourceFilesPath) {
        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
//        List<String> sourceFilesPath = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    // 打印文件路径
                    System.out.println("File: " + file.getAbsolutePath());
                    sourceFilesPath.add(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    // 递归查找子目录
                    searchFiles(file, sourceFilesPath);
                }
            }
        }
        System.out.println(sourceFilesPath);
//        return sourceFilesPath;
    }

    /**
     *
     * @param dirPath 被测项目根目录
     * @throws IOException
     */
    public static void parseJavaFiles(String dirPath) throws IOException {
        File directory = new File(dirPath + '/' + "src/main/java");
        List<String> sourceFilesPath = new ArrayList<>();
        searchFiles(directory, sourceFilesPath);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        classJson.put("code", c.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    if (c instanceof ClassOrInterfaceDeclaration) {
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

                        methodJson.put("beginline", m.getBegin().get().line);
                        methodJson.put("endline", m.getEnd().get().line);

                        List<String> parameterTypes = m.getParameters().stream()
                                .map(p -> p.getType().asString())
                                .collect(Collectors.toList());
                        methodJson.put("signature", m.getName().toString() + "(" + String.join(", ", parameterTypes) + ")");


                        jsonArray.put(methodJson);
//                        String parseFilePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json";
//                        saveToFile(parseFilePath, jsonArray.toString());
//                        System.out.println("saved to: " + "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\javaparserfile\\Triangle\\ast_json\\" + c.getName() + ".json");

                    }

                    String parseFilePath = dirPath + '/' + "target/parsefiles/ast_json/" + fileName + ".json";
                    saveToFile(parseFilePath, jsonArray.toString());
                    System.out.println("saved to: " + dirPath + '/' + "target/parsefiles/ast_json/" + fileName + ".json");

                }

            } else {
                System.out.println("Failed to parse the file.");
            }
        }
    }


    public static void main(String[] args) throws IOException {
        String projectPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Nextday";
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
}


