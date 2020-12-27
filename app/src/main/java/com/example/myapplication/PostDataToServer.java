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

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(File... files) {

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", files[0].getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),files[0]))
                .build();

        Request request = new Request.Builder()
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
