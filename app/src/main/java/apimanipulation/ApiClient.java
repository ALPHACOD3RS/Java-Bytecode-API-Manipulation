package apimanipulation;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "https://api.gataama.com/healthz";
    private static final String AUTH_TOKEN = "okhttp";

    public static void main(String[] args) {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create a request with authorization header
        Request request = new Request.Builder()
                .url(BASE_URL)
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Print the response body
                System.out.println(response.body().string());
            } else {
                System.out.println("Request failed: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
