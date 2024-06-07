import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Help {
    private static final double EARTH_RADIUS = 6371; // 地球半径（单位：公里）
    private static final String APP_KEY = "pDIrCkmHOFqCH2x_F8o4V3OZ2tuqEkAkzdUNbnTTDIkdwq1QhrE6X0nz-m9Q3xnV5wQ4pwrL7Kt2yuwR7Jp1dPXDoEgsw9ZJbVC7xy7XGYz3JSe2z0vhZsgq_Ls_ZXYx";
    private static final String URL_TPL = "https://api.yelp.com/v3/businesses/search?term=%s&latitude=%s&longitude=%s&sort_by=distance&limit=%s&offset=%s";
    private static Map<String, Location> locationMap = new HashMap<>();

    public static Location getLocation(String name, Location center) {
        if (locationMap.containsKey(name.toUpperCase())) {
            return locationMap.get(name.toUpperCase());
        }

        String nameTmp = URLEncoder.encode(name, StandardCharsets.UTF_8);
        OkHttpClient client = new OkHttpClient();
        int limit = 50;
        int offset = 0;
        while (true) {
            String url = String.format(URL_TPL, nameTmp, center.getLatitude(), center.getLongitude(), limit, offset);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer " + APP_KEY)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                Location location = getLocation(jsonObject.getJSONArray("businesses"), name);
                if (location != null) {
                    return location;
                }
                offset += limit;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static double getDistance(Location one, Location two) {
        double dLat = Math.toRadians(one.getLatitude() - two.getLatitude());
        double dLon = Math.toRadians(two.getLongitude() - one.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(one.getLatitude())) *
                        Math.cos(Math.toRadians(two.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000;
    }


    private static Location getLocation(JSONArray array, String name) {
        name = name.toUpperCase();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            Location location = JSONObject.parseObject(jsonObject.getString("coordinates"), Location.class);
            locationMap.put(jsonObject.getString("name").toUpperCase(), location);
            if (jsonObject.getString("name").toUpperCase().contains(name)) {
                locationMap.put(name.toUpperCase(), location);
                return location;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Location center = new Location(34.02116D, -118.287132D);
        Location location = getLocation("Momota Ramen House", center);
        System.out.println(location);
        System.out.println(getDistance(center, location));
    }
}
