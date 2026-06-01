import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写工具类
 * 使用 Java 对象序列化，将 List 整体写入 .dat 文件或从中读取
 *
 * 写入采用"先写临时文件，再替换原文件"的策略，防止写入过程中崩溃导致数据丢失。
 */
public class FileUtil {

    /**
     * 将列表保存到文件（带备份保护）
     * 步骤：写入 .tmp 临时文件 → 替换原文件
     */
    public static <T> void saveData(List<T> list, String fileName) {
        Path tempPath = Paths.get(fileName + ".tmp");
        Path targetPath = Paths.get(fileName);

        try {
            // 第一步：写入临时文件
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(tempPath.toFile()))) {
                oos.writeObject(list);
            }

            // 第二步：用临时文件替换原文件
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("【错误】保存数据失败: " + fileName + "，原因: " + e.getMessage());
            // 清理可能残留的临时文件
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
        }
    }

    /**
     * 从文件读取列表
     * 如果文件不存在或损坏，返回空列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadData(String fileName) {
        File file = new File(fileName);

        // 文件不存在 → 返回空列表（正常情况，首次启动时）
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("【警告】读取数据失败: " + fileName + "，原因: " + e.getMessage());
            System.err.println("将使用空数据启动，原有数据文件可能已损坏。");
            return new ArrayList<>();
        }
    }
}
