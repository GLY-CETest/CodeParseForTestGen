package cn.iselab.mutationgen;

import cn.iselab.mutationgen.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Load classes to be mutated from a jar.
 *
 * @author Adian Qian
 */
public class SourceJarLoader extends URLClassLoader {

    private final List<JarFile> jarFiles = new ArrayList<>();

    /**
     * Map class fully qualified name to source path.
     */
    private final Map<String, String> fqnToResourcePath = new HashMap<>();

    // ---------- Maps to Get Relevant Classes ----------

    private final Map<String, String> innerToClass = new HashMap<>();

    private final Map<String, Set<String>> classToInners = new HashMap<>();

    public SourceJarLoader(File...files) throws IOException {
        super(filesToURLs(files));
        for (File file : files) {
            jarFiles.add(new JarFile(file));
        }
        // Prepare path to name map.
        prepareResourceFQNMap();
        // Prepare map from (1) an anonymous class to its declaring class
        // and (2) a class to its anonymous classes.
        prepareInnerClassMap();
    }

    private static URL[] filesToURLs(File...files) throws MalformedURLException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    /**
     * Prepare {@link SourceJarLoader#fqnToResourcePath} map.
     */
    private void prepareResourceFQNMap() {
        for (JarFile jarFile : this.jarFiles) {
            Enumeration<JarEntry> jeIter = jarFile.entries();
            while (jeIter.hasMoreElements()) {
                JarEntry je = jeIter.nextElement();
                String resourcePath = je.getName();
                if (je.isDirectory() ||
                    !resourcePath.endsWith(FileUtils.CLASS_SUFFIX)) {
                    continue;
                }
                this.fqnToResourcePath.put(toFQN(resourcePath), resourcePath);
            }
        }
    }

    private String toFQN(String entryName) {
        return entryName.replace("/", ".")
            .replace(FileUtils.CLASS_SUFFIX, "");
    }


    /**
     * Prepare {@link SourceJarLoader#innerToClass} and {@link SourceJarLoader#classToInners}.
     */
    private void prepareInnerClassMap() {
        Set<String> classFQNs = this.fqnToResourcePath.keySet();

        // Build map from class to its inner classes.
        // We firstly map each fqn to a list and discard the map items
        // whose value sets are still empty at last.
        Map<String, Set<String>> tempMap = new HashMap<>();
        for (String classFQN : classFQNs) {
            tempMap.put(classFQN, new HashSet<>());
            // Add relevant anonymous classes.
            for (String innerClassFQN : classFQNs) {
                if (isRelevantInner(classFQN, innerClassFQN))
                    tempMap.get(classFQN).add(innerClassFQN);
            }
        }
        tempMap.forEach((dClass, innerClasses) -> {
            if (!innerClasses.isEmpty())
                this.classToInners.put(dClass, innerClasses);
        });


        // Then, build map from inner class to declaring class, which
        // is a one-to-one relation.
        this.classToInners.forEach((dClass, innerClasses) -> {
            for (String innerClass : innerClasses) {
                this.innerToClass.put(innerClass, dClass);
            }
        });
    }

    /**
     * For example: A$1 and A$B are relevant inner classes to class A,
     * whereas $A$1 is not.
     */
    private boolean isRelevantInner(String classFQN, String anonymousClassFQN) {
        return anonymousClassFQN.length() > classFQN.length() &&
               anonymousClassFQN.replace(classFQN, "").startsWith("$");
    }

    // ---------- Outer Helpers ----------

    /**
     * Parse fully qualified names for loaded classes.
     * @return fully qualified names for loaded classes.
     */
    public List<String> getLoadedClassFQNs() {
        return new ArrayList<>(this.fqnToResourcePath.keySet());
    }

    /**
     * Whether a class an anonymous class.
     * @param classFQN class fully qualified name.
     * @return {@code true} if the class is inner class, else
     *         {@code false}.
     */
    public boolean isInnerClass(String classFQN) {
        return this.innerToClass.containsKey(classFQN);
    }

    /**
     * Whether a non-inner class has inner classes.
     */
    public boolean hasInnerClasses(String classFQN) {
        return this.classToInners.containsKey(classFQN);
    }

    /**
     * Get a class resource input stream.
     * @param classFQN class fully qualified name.
     * @return it input stream.
     */
    public InputStream getResourceInputStreamByFQN(String classFQN) {
        String resourcePath = this.fqnToResourcePath.get(classFQN);
        assert resourcePath != null : String.format("Resource path for class %s is null!", classFQN);
        return this.getResourceAsStream(resourcePath);
    }

    /**
     * Get fully qualified name of its declaring class for an inner class
     */
    public String getDeclaringClass(String classFQN) {
        assert isInnerClass(classFQN) : String.format("Class %s must be an inner class!", classFQN);
        return this.innerToClass.get(classFQN);
    }

    /**
     * Get fully qualified names of inner classes for a non-inner class.
     * @param classFQN a non-inner class
     * @return fully qualified names of the inner classes.
     */
    public Set<String> getInnerClasses(String classFQN) {
        assert !isInnerClass(classFQN) : String.format("Class %s is an inner class!", classFQN);
        // Return a duplicate to avoid polluting the map.
        return new HashSet<>(this.classToInners.get(classFQN));
    }



}
