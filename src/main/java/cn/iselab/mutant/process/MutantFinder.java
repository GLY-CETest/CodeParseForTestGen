package cn.iselab.mutant.process;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MutantFinder {
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\YGL\\Projects\\pythonProject\\MutationTestGEN-LLM\\projUT\\Triangle\\target\\mutants\\1\\details.json";
        int lineNumber = mutantLineNumberFinder(filePath);
        System.out.println("Mutant lineNumber: " + lineNumber);
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

//    public static String mutantMethodFinder(String path) throws Exception {
//    }
}
