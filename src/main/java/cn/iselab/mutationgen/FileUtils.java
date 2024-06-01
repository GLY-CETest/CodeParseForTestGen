package cn.iselab.mutationgen;

import java.io.*;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Util class provide static method of IO operations.
 *
 * @author Adian Qian
 */
public class FileUtils {

    private FileUtils() {}

    public static final String JAVA_SUFFIX = ".java";
    public static final String CLASS_SUFFIX = ".class";
    public static final String DOT_SUFFIX = ".dot";
    public static final String TXT_SUFFIX = ".txt";
    public static final String LOG_SUFFIX = ".log";
    public static final String CSV_SUFFIX = ".csv";
    public static final String JSON_SUFFIX = ".json";
    public static final String NEW_LINE = System.lineSeparator();

    public static String defaultCharset = "UTF-8";

    public static String writeBytesIntoClassFile(byte[] bytes, String path) {
        return writeBytesIntoClassFile(bytes, new File(path));
    }

    public static String writeBytesIntoClassFile(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return file.getAbsolutePath();
    }

    public static void createNewFile(File file) throws IOException {
        if (!file.exists()) {
            System.out.println("File has already exists: " + file);
            return;
        }
        if (file.isDirectory())
            createNewDirectories(file);
        else {
            boolean newFile = file.createNewFile();
            if (newFile)
                System.out.println("Create file: " + file);
        }

    }

    public static void createNewDirectories(String dirPath) {
        createNewDirectories(new File(dirPath));
    }

    public static void createNewDirectories(File dir) {
        boolean mkdirs = dir.mkdirs();
        if (mkdirs)
            System.out.println("Make directory: " + dir.getAbsolutePath());
    }

    public static void deleteAndCreateNewDir(File dir) {
        if (dir.exists())
            deleteRecursively(dir);
        createNewDirectories(dir);
    }

    public static void deleteRecursively(String path) {
        deleteRecursively(new File(path));
    }
    public static void deleteRecursively(File dir) {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            if(files != null)
                for (File file : files)
                    deleteRecursively(file);
        }
        boolean flag = dir.delete();
        if(flag)
            System.out.println("Delete " + dir.getAbsolutePath());
    }


    /**
     * Get all files that has a suffix as <param>suffix</param>
     * under directory <param>directory</param> recursively.
     *
     * @param directory A directory.
     * @param suffix The type of target files. suffix should start with a '.'.
     * @return A List of target files.
     * @throws IllegalArgumentException When dir doesn't represent a directory.
     *
     */
    public static List<File> getAllFilesBySuffix(File directory, String suffix) {
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " should be a directory!");
        }
        // Give out warning when suffix doesn't start with '.'.
        if(suffix.charAt(0) != '.') {
            System.err.println(
                    "Warning: suffix \"" + suffix + "\" should start with a \".\", system have added for you automatically."
            );
            suffix = "." + suffix;
        }
        // Get all files end with suffix recursively.
        List<File> targetFiles = new ArrayList<>();
        File[] allFiles = directory.listFiles();
        if(allFiles != null) {
            // Add files end with the target suffix recursively.
            for (File file : allFiles) {
                if(file.isDirectory()) {
                    targetFiles.addAll(getAllFilesBySuffix(file.getAbsolutePath(), suffix));
                } else {
                    if(suffixOf(file).equals(suffix)) {
                        targetFiles.add(file);
                    }
                }
            }
        }
        return targetFiles;
    }

    /**
     *  Get all files that has a suffix as <param>suffix</param>
     *  under directory <param>dir</param> recursively.
     *
     * @param dirPath An absolute path of a directory.
     * @param suffix The type of target files. suffix should start with a '.'.
     * @return A List of target files.
     * @throws IllegalArgumentException When dir doesn't represent a directory.
     *
     */
    public static List<File> getAllFilesBySuffix(String dirPath, String suffix) {
        File directory = new File(dirPath);
        return getAllFilesBySuffix(directory, suffix);
    }



    /**
     *
     * @param path A path of a file.
     * @param content The content needed be written into the file.
     * @return The absolute path of written file.
     * @throws IOException when write wrongly.
     *
     */
    public static String writeContentIntoFile(String path, String content) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        File file = new File(path);
        return writeContentIntoFile(file, content);
    }

    public static String writeContentIntoFile(File file, String content) throws IOException {
        return writeContentIntoFile(file, content, false);
    }

    public static String writeContentIntoFile(String filePath, String content, boolean appendOn) throws IOException {
        return writeContentIntoFile(new File(filePath), content, appendOn, defaultCharset);
    }

    public static String writeContentIntoFile(File file, String content, boolean appendOn) throws IOException {
        return writeContentIntoFile(file, content, appendOn, defaultCharset);
    }

    public static String writeContentIntoFile(File file, String content, String charset) throws IOException {
        return writeContentIntoFile(file, content, false, charset);
    }

    public static String writeContentIntoFile(String path, String content, String charset) throws IOException {
        return writeContentIntoFile(new File(path), content, false, charset);
    }

    public static void setDefaultCharset(String charset) {
        defaultCharset = charset;
        System.out.println("[From IOUtil.setDefaultCharset] Now default charset is " + charset);
    }

    public static String writeContentIntoFile(File file, String content, boolean appendOn, String charset)
            throws IOException {
        if(!file.exists()) {
            boolean newFile = file.createNewFile();
            if(newFile) {
                System.out.println("Create new file: " + file.getAbsolutePath());
            } else {
                throw new RuntimeException("Create new file failed!");
            }
        }
        if(!file.canWrite()) {
            throw new IllegalArgumentException(file + ": cannot be written");
        }
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }

        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, appendOn), charset));
        bw.write(content);
        bw.newLine();

        bw.close();
        return file.getAbsolutePath();
    }

    public static String writeContentsIntoFile(File outputFile, List<String> contents) throws IOException {
        StringBuilder builder = new StringBuilder(contents.size() * 100);
        contents.forEach((content) -> builder.append(content).append(System.lineSeparator()));
        return writeContentIntoFile(outputFile, builder.toString());
    }

    public static String writeContentsIntoFile(String path, List<String> contents) throws IOException {
        return writeContentsIntoFile(new File(path), contents);
    }

    /**
     * Read properties from experimental configurations.
     */
    public static Properties readProperties(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }

    /**
     * Read all content from a readable file.
     *
     * @param file is a readable file.
     * @param charset charset of the file.
     * @return content of the file.
     * @throws IOException if read wrongly.
     *
     */
    public static String readAllContent(File file, String charset) throws IOException {
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid file. Please input a path of file.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + ": cannot be read");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            contentBuilder.append(line).append(NEW_LINE);
        }
        br.close();
        return contentBuilder.toString();
    }

    /**
     * Read all content from a readable file. Use default charset: UTF-8.
     *
     * @param file is a readable file.
     * @return content of the file.
     * @throws IOException if read wrongly.
     *
     */
    public static String readAllContent(File file) throws IOException {
        return readAllContent(file, "UTF-8");
    }

    public static String readAllContent(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        return readAllContent(new File(path));
    }


    /**
     * Read content from a txt file, one line for one item.
     *
     * @param file A readable file.
     * @return A List of parsing result.
     *
     */
    public static List<String> readContentsLineByLine(File file) throws IOException {
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid file. Please input a path of file.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + ": cannot be read");
        }

        List<String> contents = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null) {
            contents.add(line);
        }

        br.close();
        return contents;
    }
    /**
     * Read content from a txt file, one line for one item.
     *
     * @param path A path of a property file, written in a txt file.
     * @return A List of parsing result.
     *
     */
    public static List<String> readContentsLineByLine(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        File file = new File(path);
        return readContentsLineByLine(file);
    }

    public static String suffixOf(String filePath) {
        return suffixOf(new File(filePath));
    }

    public static String suffixOf(File file) {
        if(file.isDirectory())
            return "";

        String fileName = file.getName();
        int loc = fileName.lastIndexOf('.');
        if(loc != -1)
            return fileName.substring(loc);

        return "";
    }

    public static boolean isClassFile(File file) {
        return CLASS_SUFFIX.equals(suffixOf(file));
    }

    public static String simpleName(String filePath) {
        return simpleName(new File(filePath));
    }

    public static String simpleName(File file) {
        if(file.isDirectory()) {
            return file.getName();
        }
        return file.getName().replace(suffixOf(file), "");
    }

    public static List<File> listFilesOrEmpty(String dirPath){
        return listFilesOrEmpty(new File(dirPath));
    }

    /**
     * Wrap content files as a list
     *
     * @param dir a directory file
     * @return empty list if <code>dir</code> not a directory or
     *         listed files are <code>null</code>.
     */
    public static List<File> listFilesOrEmpty(File dir) {
        if(!dir.isDirectory())
            return new ArrayList<>();

        File[] files = dir.listFiles();
        if(files == null)
            return new ArrayList<>();
        return Arrays.asList(files);
    }

    public static List<File> listFiles(String dirPath) throws NotDirectoryException {
        return listFiles(new File(dirPath));
    }

    public static List<File> listFiles(File dir) throws NotDirectoryException {
        if(!dir.isDirectory())
            throw new NotDirectoryException(dir.getAbsolutePath());

        File[] files = dir.listFiles();
        if(files == null)
            return new ArrayList<>();
        return Arrays.asList(files);
    }

    public static List<String> listFilesAsStrings(String dirPath) throws NotDirectoryException {
        return listFilesOrEmpty(new File(dirPath)).stream().map(File::getAbsolutePath).collect(Collectors.toList());
    }

    public static int listFileSize(File dir) {
        File[] files = dir.listFiles();
        if (files == null)
            return 0;
        return files.length;
    }

}
