import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> readFile(String file) {
        try {
           return Files.readAllLines(new File(file).toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static byte[] readFileBytes(String file) {
        try {
            return Files.readAllBytes(new File(file).toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void writeFile(String file, String content, boolean append) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append));
            bufferedWriter.write(content + System.lineSeparator());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     * @param file
     * @param bytes
     */
    public static void writeFile(String file, byte[] bytes) {
        try {
            File file1 = new File(file);
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
