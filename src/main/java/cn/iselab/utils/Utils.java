package cn.iselab.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static void main(String[] args){
        String folderPath = "C:\\YGL\\Projects\\CodeParse\\projUT\\Nextday\\target\\mutants"; // 替换为你的文件夹路径
        long count = folderCounter(folderPath);
    }

    public static long folderCounter(String path)
    {
        long folderCount = 0;
        try {
            folderCount = Files.list(Paths.get(path))
                    .filter(Files::isDirectory)
                    .count();
            System.out.println("Number of folders: " + folderCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderCount;
    }
}
