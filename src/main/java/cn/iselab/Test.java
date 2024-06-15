package cn.iselab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Test {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        // 创建 JSON 对象
        ObjectNode json = mapper.createObjectNode();
        json.put("name", "John");
        json.put("age", 30);
        json.put("isMarried", true);

        // 嵌套对象
        ObjectNode address = mapper.createObjectNode();
        address.put("street", "123 Main St");
        address.put("city", "New York");
        json.set("address", address);

        // 输出 JSON 字符串
        try {
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
