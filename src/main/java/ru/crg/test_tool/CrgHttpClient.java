package ru.crg.test_tool;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
public class CrgHttpClient {

    private final OkHttpClient client = new OkHttpClient();

    public boolean isResourceAvailable(URL url) {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.code() == 200;
        } catch (IOException e) {
            return false;
        }
    }

}
