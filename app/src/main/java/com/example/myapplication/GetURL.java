package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetURL extends AsyncTask<String, Void, String> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... urls) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Request request;
        if (urls.length > 1) {
            RequestBody body = RequestBody.create(urls[1], JSON);
            request = new Request.Builder()
                    .url(urls[0])
                    .post(body)
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(urls[0])
                    .build();
        }

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
