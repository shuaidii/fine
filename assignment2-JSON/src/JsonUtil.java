import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-15
 * Time: 22:14
 */
public class JsonUtil {

    public static Weather parse(String message) {
        String[] lines = message.split("\n");
        List<String> lineList = new ArrayList<>(Arrays.asList(lines));
        try {
            lineList.remove(0);
            lineList.remove(lineList.size() - 1);
            return parse(lineList, true);
        } catch (Exception e) {
            System.out.println("Receive error json message: " + message);
            return null;
        }
    }

    public static Weather parse(List<String> lines, boolean withQuot) {
        Weather weather = new Weather();
        if (lines == null || lines.size() == 0) {
            return null;
        }
        for (String line : lines) {
            String key = line.split(":")[0].trim();
            String value = line.split(":")[1].trim();
            if (withQuot) {
                key = key.substring(1, key.length() - 1);
            }
            checkKeyValue(key, value, weather, withQuot);
        }
        return weather;
    }

    public static String toJsonString(Weather weather) {
        StringBuilder builder = new StringBuilder();
        builder.append("{").append(System.lineSeparator());
        builder.append("    \"id\" : ").append(toString(weather.getId())).append(System.lineSeparator());
        builder.append("    \"name\" : ").append(toString(weather.getName())).append(System.lineSeparator());
        builder.append("    \"state\" : ").append(toString(weather.getState())).append(System.lineSeparator());
        builder.append("    \"time_zone\" : ").append(toString(weather.getTime_zone())).append(System.lineSeparator());
        builder.append("    \"lat\" : ").append(toString(weather.getLat())).append(System.lineSeparator());
        builder.append("    \"lon\" : ").append(toString(weather.getLon())).append(System.lineSeparator());
        builder.append("    \"local_date_time\" : ").append(toString(weather.getLocal_date_time())).append(System.lineSeparator());
        builder.append("    \"local_date_time_full\" : ").append(toString(weather.getLocal_date_time_full())).append(System.lineSeparator());
        builder.append("    \"air_temp\" : ").append(toString(weather.getAir_temp())).append(System.lineSeparator());
        builder.append("    \"apparent_t\" : ").append(toString(weather.getApparent_t())).append(System.lineSeparator());
        builder.append("    \"cloud\" : ").append(toString(weather.getCloud())).append(System.lineSeparator());
        builder.append("    \"dewpt\" : ").append(toString(weather.getDewpt())).append(System.lineSeparator());
        builder.append("    \"press\" : ").append(toString(weather.getPress())).append(System.lineSeparator());
        builder.append("    \"rel_hum\" : ").append(toString(weather.getRel_hum())).append(System.lineSeparator());
        builder.append("    \"wind_dir\" : ").append(toString(weather.getWind_dir())).append(System.lineSeparator());
        builder.append("    \"wind_spd_kmh\" : ").append(toString(weather.getWind_spd_kmh())).append(System.lineSeparator());
        builder.append("    \"wind_spd_kt\" : ").append(toString(weather.getWind_spd_kt())).append(System.lineSeparator());
        builder.append("}");
        return builder.toString();
    }

    public static String toString(Weather weather, boolean withServerContent) {
        StringBuilder builder = new StringBuilder();
        builder.append("id:").append(weather.getId()).append(System.lineSeparator());
        builder.append("name:").append(weather.getName()).append(System.lineSeparator());
        builder.append("state:").append(weather.getState()).append(System.lineSeparator());
        builder.append("time_zone:").append(weather.getTime_zone()).append(System.lineSeparator());
        builder.append("lat:").append(weather.getLat()).append(System.lineSeparator());
        builder.append("lon:").append(weather.getLon()).append(System.lineSeparator());
        builder.append("local_date_time:").append(weather.getLocal_date_time()).append(System.lineSeparator());
        builder.append("local_date_time_full:").append(weather.getLocal_date_time_full()).append(System.lineSeparator());
        builder.append("air_temp:").append(weather.getAir_temp()).append(System.lineSeparator());
        builder.append("apparent_t:").append(weather.getApparent_t()).append(System.lineSeparator());
        builder.append("cloud:").append(weather.getCloud()).append(System.lineSeparator());
        builder.append("dewpt:").append(weather.getDewpt()).append(System.lineSeparator());
        builder.append("press:").append(weather.getPress()).append(System.lineSeparator());
        builder.append("rel_hum:").append(weather.getRel_hum()).append(System.lineSeparator());
        builder.append("wind_dir:").append(weather.getWind_dir()).append(System.lineSeparator());
        builder.append("wind_spd_kmh:").append(weather.getWind_spd_kmh()).append(System.lineSeparator());
        builder.append("wind_spd_kt:").append(weather.getWind_spd_kt()).append(System.lineSeparator());
        if (withServerContent) {
            builder.append("contentServer:").append(weather.getContentServer()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static void checkKeyValue(String key, String value, Weather weather, boolean withQuot) {
        switch (key) {
            case "id" -> weather.setId(getTrueValue(value, withQuot));
            case "name" -> weather.setName(getTrueValue(value, withQuot));
            case "state" -> weather.setState(getTrueValue(value, withQuot));
            case "time_zone" -> weather.setTime_zone(getTrueValue(value, withQuot));
            case "lat" -> weather.setLat(getTrueValue(value, withQuot));
            case "lon" -> weather.setLon(getTrueValue(value, withQuot));
            case "local_date_time" -> weather.setLocal_date_time(getTrueValue(value, withQuot));
            case "local_date_time_full" -> weather.setLocal_date_time_full(getTrueValue(value, withQuot));
            case "air_temp" -> weather.setAir_temp(getDoubleValue(value));
            case "apparent_t" -> weather.setApparent_t(getDoubleValue(value));
            case "cloud" -> weather.setCloud(getTrueValue(value, withQuot));
            case "dewpt" -> weather.setDewpt(getDoubleValue(value));
            case "press" -> weather.setPress(getDoubleValue(value));
            case "rel_hum" -> weather.setRel_hum(Integer.parseInt(value));
            case "wind_dir" -> weather.setWind_dir(getTrueValue(value, withQuot));
            case "wind_spd_kmh" -> weather.setWind_spd_kmh(Integer.parseInt(value));
            case "wind_spd_kt" -> weather.setWind_spd_kt(Integer.parseInt(value));
            case "contentServer" -> weather.setContentServer(getTrueValue(value, withQuot));
            default -> throw new IllegalStateException("Unexpected value: " + key);
        }
    }

    private static String getTrueValue(String value, boolean withQuot) {
        if (!withQuot) {
            return value;
        }
        return value.substring(1, value.length() - 1);
    }

    private static double getDoubleValue(String value) {
        return Double.parseDouble(value);
    }

    private static String toString(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return value.toString();
    }

}
