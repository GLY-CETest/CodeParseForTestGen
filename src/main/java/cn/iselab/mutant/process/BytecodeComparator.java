package cn.iselab.mutant.process;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BytecodeComparator {
    public static void main(String[] args) throws Exception {
        compareBytecode(
                "C:/YGL/Projects/pythonProject/MutationTestGEN-LLM/projUT/Triangle/target/mutants/1/net/mooctest/Triangle.class",
                "C:/YGL/Projects/pythonProject/MutationTestGEN-LLM/projUT/Triangle/target/classes/net/mooctest/Triangle.class");
    }

    public static void compareBytecode(String classFilePath1, String classFilePath2) throws Exception {
        String bytecode1 = getBytecode(classFilePath1);
        String bytecode2 = getBytecode(classFilePath2);

        if (!bytecode1.equals(bytecode2)) {
            System.out.println("The bytecode of the two classes is different.");
            // Find and print differences
            printDifferences(bytecode1, bytecode2);
        } else {
            System.out.println("The bytecode of the two classes is identical.");
        }
    }

    private static String getBytecode(String classFilePath) throws Exception {
        ClassReader classReader = new ClassReader(classFilePath);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        TraceClassVisitor tcv = new TraceClassVisitor(null, new ASMifier(), pw);
        classReader.accept(tcv, 0);
        return sw.toString();
    }

    private static void printDifferences(String bytecode1, String bytecode2) {
        String[] lines1 = bytecode1.split("\n");
        String[] lines2 = bytecode2.split("\n");

        for (int i = 0; i < Math.min(lines1.length, lines2.length); i++) {
            if (!lines1[i].equals(lines2[i])) {
                System.out.println("Difference at line " + (i + 1) + ":");
                System.out.println("Class 1: " + lines1[i]);
                System.out.println("Class 2: " + lines2[i]);
            }
        }

        if (lines1.length != lines2.length) {
            System.out.println("The two bytecode files have different lengths.");
        }
    }
}
