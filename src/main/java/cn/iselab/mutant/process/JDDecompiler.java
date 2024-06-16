package cn.iselab.mutant.process;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.jd.core.v1.api.Decompiler;
import org.jetbrains.annotations.Nullable;

//import org.jd.core.v1.api.Processor;
//import org.jd.core.v1.service.loader.ClassPathLoader;
//import org.jd.core.v1.service.loader.FileLoader;
//import org.jd.core.v1.service.printer.PlainTextPrinter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;




public class JDDecompiler {

    public static void main(String[] args) throws Exception {
        Loader loader = new Loader() {
            @Override
            public byte @Nullable [] load(String internalName) throws LoaderException {
                InputStream is = this.getClass().getResourceAsStream("/" + internalName + ".class");



                if (is == null) {
                    return null;
                } else {
                    try (InputStream in=is; ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int read = in.read(buffer);

                        while (read > 0) {
                            out.write(buffer, 0, read);
                            read = in.read(buffer);
                        }

                        return out.toByteArray();
                    } catch (IOException e) {
                        throw new LoaderException(e);
                    }
                }
            }

            @Override
            public boolean canLoad(String internalName) {
                return this.getClass().getResource("/" + internalName + ".class") != null;
            }
        };

        Printer printer = new Printer() {
            protected static final String TAB = "  ";
            protected static final String NEWLINE = "\n";

            protected int indentationCount = 0;
            protected StringBuilder sb = new StringBuilder();

            @Override public String toString() { return sb.toString(); }

            @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}
            @Override public void end() {}

            @Override public void printText(String text) { sb.append(text); }
            @Override public void printNumericConstant(String constant) { sb.append(constant); }
            @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }
            @Override public void printKeyword(String keyword) { sb.append(keyword); }
            @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }
            @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

            @Override public void indent() { this.indentationCount++; }
            @Override public void unindent() { this.indentationCount--; }

            @Override public void startLine(int lineNumber) { for (int i=0; i<indentationCount; i++) sb.append(TAB); }
            @Override public void endLine() { sb.append(NEWLINE); }
            @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

            @Override public void startMarker(int type) {}
            @Override public void endMarker(int type) {}
        };

        ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();

        String path = "C:\\YGL\\Projects\\CodeParse\\target\\classes\\cn\\iselab\\Test.class";
        File file = new File(path);
        System.out.println(file.exists());
//        if (!file.exists()){
//            System.out.println("Not exist.");
//        }

        decompiler.decompile(loader, printer, "C:\\YGL\\Projects\\CodeParse\\target\\classes\\cn\\iselab\\Test.class");

        String source = printer.toString();
    }


//    public static void deCompileWithJD(String classFilePath) throws Exception {
//
//
//    }
}
