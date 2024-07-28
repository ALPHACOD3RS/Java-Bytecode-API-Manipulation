package apimanipulation;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "https://api.gataama.com/healthz";
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .header("Authorization", "Bearer " + "okhttp")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            } else {
                System.out.println("request failed: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
