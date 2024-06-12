package cn.iselab.mutant.process;

import org.json.JSONObject;

import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MutantFinder {
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\mutants\\1\\details.json";
        String astPath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\parsefiles\\ast_json\\Triangle.json";
        int lineNumber = mutantLineNumberFinder(filePath);
        System.out.println("Mutant lineNumber: " + lineNumber);
        mutantMethodFinder(astPath, lineNumber);
    }

    public static int mutantLineNumberFinder(String path) throws Exception {
//        File file = new File(path);
        int lineNumber = 0;
        try {
            // 读取文件内容到字符串
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));
            // 解析JSON字符串
            JSONObject jsonObject = new JSONObject(jsonString);
            // 获取lineNumber的值
            lineNumber = jsonObject.getInt("lineNumber");
            // 打印lineNumber的值
//            System.out.println("lineNumber: " + lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return lineNumber;
        }
    }

    public static String mutantMethodFinder(String path, int lineNumber) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 读取 JSON 文件
            File jsonFile = new File(path);
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            // 确保读取的是数组
            if (jsonNode.isArray()) {
                // 遍历数组中的每个元素
                for (JsonNode elementNode : jsonNode) {
                    // 处理每个 JSON 对象
                    String name = elementNode.get("name").asText();
                    String type = elementNode.get("type").asText();
                    if (type.equals("method")) {
                        int start_line = elementNode.get("start_line").asInt();
                        int end_line = elementNode.get("end_line").asInt();
                        // ... 根据需要读取更多字段
                        if (lineNumber >= start_line && lineNumber <= end_line)
                        {
                            System.out.println("name: " + name);
                            return name;
                        }
                        else
                            continue;
                    }
                    else
                        continue;
                }
            } else {
                System.out.println("Root is not an array.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
