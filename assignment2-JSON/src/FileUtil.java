import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * read file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<Weather> readFile(String file) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(file));
        List<Weather> weatherList = new ArrayList<>();
        List<String> lineTmps = new ArrayList<>();
        boolean hasId = false;
        for (String line : lines) {
            line = line.strip();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equals("entry")) {
                if (hasId && lineTmps.size() > 0) {
                    weatherList.add(JsonUtil.parse(lineTmps, false));
                }
                lineTmps.clear();
                hasId = false;
                continue;
            }

            lineTmps.add(line);
            if (line.startsWith("id")) {
                hasId = true;
            }
        }

        if (hasId && lineTmps.size() > 0) {
            weatherList.add(JsonUtil.parse(lineTmps, false));
        }
        return weatherList;
    }

    /**
     * save file
     *
     * @param weatherList
     * @param file
     * @throws IOException
     */
    public static void saveFile(List<Weather> weatherList, String file) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < weatherList.size(); i++) {
                Weather weather = weatherList.get(i);
                bufferedWriter.write(JsonUtil.toString(weather, true).trim());
                bufferedWriter.write(System.lineSeparator());
                if (i != weather.getWind_spd_kmh() - 1) {
                    bufferedWriter.write("entry");
                    bufferedWriter.write(System.lineSeparator());
                }
            }
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
