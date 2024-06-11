package cn.iselab.mutant.generating;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MutationParser {
    public static void main(String[] args) {
        try {
            File file = new File("target/pit-reports/mutations.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("mutation");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                org.w3c.dom.Node nNode = nList.item(temp);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                    String mutatedClass = eElement.getElementsByTagName("mutatedClass").item(0).getTextContent();
                    String mutatedMethod = eElement.getElementsByTagName("mutatedMethod").item(0).getTextContent();
                    String lineNumber = eElement.getElementsByTagName("lineNumber").item(0).getTextContent();
                    String mutator = eElement.getElementsByTagName("mutator").item(0).getTextContent();

                    System.out.println("Class: " + mutatedClass);
                    System.out.println("Method: " + mutatedMethod);
                    System.out.println("Line: " + lineNumber);
                    System.out.println("Mutator: " + mutator);
                    System.out.println("-----------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
