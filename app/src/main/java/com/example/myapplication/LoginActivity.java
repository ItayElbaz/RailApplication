package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login fr = new Login();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_place, fr).commit();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST);
    }
}