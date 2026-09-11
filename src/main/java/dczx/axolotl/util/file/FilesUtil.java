package dczx.axolotl.util.file;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/23 12:49
 */
public class FilesUtil {

    public static boolean createFile(Path parentPath, Path fileName, boolean isDirectory) throws IOException {
        Path file = parentPath.resolve(fileName); // 组合路径
        if (Files.exists(file)) {
            return false;
        }
        Files.createDirectories(file.getParent());
        if (isDirectory) {
            Files.createDirectories(file); // 自动创建多级目录
        } else {
            Files.createFile(file);
        }
        return true;
    }

    public static boolean createFile(String parentPath, String fileName, boolean isDirectory) throws IOException {
        return createFile(Paths.get(parentPath), Paths.get(fileName), isDirectory);
    }

    /**
     * 确保指定文件存在，不存在则创建（包括父目录）
     *
     * @param filePath 文件路径
     * @return Path 对象
     * @throws IOException 创建失败时抛出
     */
    public static Path keepFileExists(Path filePath) throws IOException {
        Path parent = filePath.getParent();

        // 创建父目录
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // 创建文件
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        } else if (Files.isDirectory(filePath)) {
            throw new IOException("目标路径已存在但为目录，无法创建文件: " + filePath);
        }

        return filePath;
    }

    /**
     * 重载：接受 String 路径
     */
    public static File keepFileExists(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        keepFileExists(path); // 调用核心方法
        return path.toFile();
    }

    /**
     * 确保指定目录存在，不存在则创建（包括父目录）
     *
     * @param folderPath 目录路径
     * @return Path 对象
     * @throws IOException 创建失败时抛出
     */
    public static Path keepFolderExists(Path folderPath) throws IOException {
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        } else if (!Files.isDirectory(folderPath)) {
            throw new IOException("路径已存在但不是目录: " + folderPath);
        }
        return folderPath;
    }

    /**
     * 重载：接受 String 路径
     */
    public static File keepFolderExists(String folderName) throws IOException {
        Path path = Paths.get(folderName);
        keepFolderExists(path); // 调用核心方法
        return path.toFile();
    }

    /**
     * 复制文件或目录（支持递归复制目录）
     *
     * 请使用copy("./a/abc.txt", "./b/abc") 或 copy("./a/abc.txt", "./b") 形式调用
     *
     * @param source 源路径
     * @param target 目标路径
     * @throws IOException 复制失败时抛出
     */
    public static void copy(Path source, Path target) throws IOException {
        if (Files.isDirectory(source)) {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 重载：接受 String 路径
     */
    public static void copy(String source, String target) throws IOException {
        copy(Paths.get(source), Paths.get(target));
    }


    /**
     * 获取目录下所有文件（递归）
     *
     * @param dir 目录路径
     * @return 文件 Path 列表
     * @throws IOException 遍历失败时抛出
     */
    public static List<Path> listAllFiles(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("路径不是目录: " + dir);
        }

        List<Path> fileList = new ArrayList<>();
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                fileList.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    /**
     * 重载：接受 File 对象（兼容旧代码）
     */
    public static List<File> listAllFiles(File dir) throws IOException {
        List<Path> paths = listAllFiles(dir.toPath());
        return paths.stream().map(Path::toFile).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 重载：接受 String 路径
     */
    public static List<Path> listAllFiles(String dir) throws IOException {
        return listAllFiles(Paths.get(dir));
    }

    /**
     * 移动文件或目录
     *
     * @param source 源路径
     * @param target 目标路径
     * @throws IOException 移动失败时抛出
     */
    public static void move(Path source, Path target) throws IOException {
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 重载：接受 String 路径
     */
    public static void move(String source, String target) throws IOException {
        move(Paths.get(source), Paths.get(target));
    }


    /**
     * 计算文件或目录大小（字节）
     *
     * @param path 路径
     * @return 大小（字节）
     * @throws IOException 遍历失败时抛出
     */
    public static long calculateSize(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }

        final long[] size = {0};
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
        return size[0];
    }

    /**
     * 重载：接受 String 路径
     */
    public static long calculateSize(String path) throws IOException {
        return calculateSize(Paths.get(path));
    }

    /**
     * 获取文件或目录的基本信息
     *
     * @param path 路径
     * @return FolderDataNoFiles 对象
     * @throws IOException 获取失败时抛出
     */
    public static FolderDataNoFiles getFolderDataNoFiles(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("路径不是目录: " + path);
        }
        // 使用原子类型确保线程安全
        final AtomicLong size = new AtomicLong(0);
        final AtomicInteger fileCount = new AtomicInteger(0);
        final AtomicInteger folderCount = new AtomicInteger(0);
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 统计目录数量（除了根目录）
                folderCount.incrementAndGet();

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 统计文件大小和数量
                size.addAndGet(attrs.size());
                fileCount.incrementAndGet();
                return FileVisitResult.CONTINUE;
            }
        });
        folderCount.decrementAndGet();//-1 因为不计数自身
        return new FolderDataNoFiles(path, size.get(), fileCount.get(), folderCount.get());

    }

}
