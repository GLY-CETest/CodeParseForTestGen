package cn.iselab.mutant.generating;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Load classes to be mutated.
 *
 * @author Adian Qian
 */
public class SourceProgramLoader extends URLClassLoader {

    public SourceProgramLoader(URL[] urls) {
        super(urls);
    }

    public SourceProgramLoader(String...paths) throws MalformedURLException {
        this(stringsToUrls(paths));
    }

    public SourceProgramLoader(File...files) throws MalformedURLException {
        this(filesToURLs(files));
    }

    public static URL[] filesToURLs(File[] files) throws MalformedURLException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    public static URL[] stringsToUrls(String[] paths) throws MalformedURLException {
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = new File(paths[i]).toURI().toURL();
        }
        return urls;
    }
}
