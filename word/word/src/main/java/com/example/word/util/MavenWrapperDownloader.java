package com.example.word.util;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Maven Wrapper 下载器
 * @author DaY1zz
 */
public class MavenWrapperDownloader {

    private static final String WRAPPER_VERSION = "3.1.0";
    private static final String WRAPPER_URL = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/" + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

    /**
     * 下载 Maven Wrapper
     */
    public static void downloadWrapper() {
        try {
            System.out.println("正在下载 Maven Wrapper...");
            
            Path wrapperDir = Paths.get(".mvn/wrapper");
            if (!Files.exists(wrapperDir)) {
                Files.createDirectories(wrapperDir);
            }

            Path wrapperJar = wrapperDir.resolve("maven-wrapper.jar");
            
            if (Files.exists(wrapperJar)) {
                System.out.println("Maven Wrapper 已存在，跳过下载");
                return;
            }

            URL url = new URI(WRAPPER_URL).toURL();
            try (var inputStream = url.openStream()) {
                Files.copy(inputStream, wrapperJar, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("Maven Wrapper 下载完成");
            
        } catch (Exception e) {
            System.err.println("下载 Maven Wrapper 失败: " + e.getMessage());
        }
    }

    /**
     * 检查 Wrapper 是否存在
     */
    public static boolean wrapperExists() {
        return Files.exists(Paths.get(".mvn/wrapper/maven-wrapper.jar"));
    }

    /**
     * 清理 Wrapper
     */
    public static void cleanWrapper() {
        try {
            Path wrapperDir = Paths.get(".mvn");
            if (Files.exists(wrapperDir)) {
                try (Stream<Path> paths = Files.walk(wrapperDir)) {
                    paths.sorted((a, b) -> b.compareTo(a))
                         .forEach(path -> {
                             try {
                                 Files.deleteIfExists(path);
                             } catch (Exception e) {
                                 System.err.println("删除文件失败: " + path + " - " + e.getMessage());
                             }
                         });
                }
                Files.deleteIfExists(wrapperDir);
            }
            System.out.println("Maven Wrapper 清理完成");
        } catch (Exception e) {
            System.err.println("清理 Maven Wrapper 失败: " + e.getMessage());
        }
    }
}