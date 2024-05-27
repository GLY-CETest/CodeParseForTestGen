import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationGPTTestGeneration\\projUT\\Triangle\\src\\main\\java\\net\\mooctest\\Triangle.java";
        String sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));

        // Parse the source code
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

        // Convert to JSON
        Map<String, Object> jsonMap = nodeToJson(compilationUnit);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

        // Output the JSON string
        System.out.println(jsonString);
    }

    private static Map<String, Object> nodeToJson(Node node) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("nodeType", node.getClass().getSimpleName());
        jsonMap.put("begin", node.getBegin().map(Object::toString).orElse(null));
        jsonMap.put("end", node.getEnd().map(Object::toString).orElse(null));
        jsonMap.put("children", new ArrayList<>());

        List<Map<String, Object>> children = (List<Map<String, Object>>) jsonMap.get("children");
        for (Node child : node.getChildNodes()) {
            children.add(nodeToJson(child));
        }

        return jsonMap;
    }
}
