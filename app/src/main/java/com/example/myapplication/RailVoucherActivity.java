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

import android.view.View;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RailVoucherActivity extends AppCompatActivity {
    public RailUtils utils;
    public List<ScheduledRoute> schedulesRoutes = new ArrayList<>();
    public MutableLiveData<Integer> listner = new MutableLiveData<>();

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private SMSReceiver smsReceiver;
    String getTokenURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?mobile=0544615463&userId=311240196&method=getToken";
    String makeVoucherURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?numSeats=1&smartCard=311240196&mobile=0544615463&userEmail=itayelbaz7@gmail.com&method=MakeVoucherSeatsReservation&IsSendEmail=true&source=1&typeId=1";
    String sendSMSURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?Generatedref=%s&typeId=1&method=SendSms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_voucher);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        utils = new RailUtils();
        listner.setValue(schedulesRoutes.size());

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

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
        listner.setValue(schedulesRoutes.size());
    }

    public void removeScheduleRoute(ScheduledRoute route) {
        this.schedulesRoutes.remove(route);
        listner.setValue(schedulesRoutes.size());
    }

    private String getTicketBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("destinationStationId", "23");
            json.put("destinationStationHe", "");
            json.put("orignStationId", "21");
            json.put("orignStationHe", "");
            json.put("trainNumber", 46);
            json.put("departureTime", "07/12/2020 18:01:00");
            json.put("arrivalTime", "07/12/2020 19:16:00");
            json.put("orignStation", "באר שבע- צפון/אוניברסיטה");
            json.put("destinationStation", "תל אביב - השלום");
            json.put("orignStationNum", 7300);
            json.put("destinationStationNum", 4600);
            json.put("DestPlatform", 1);
            json.put("TrainOrder", 1);
        } catch (Exception e) {
            // ignore
        }

        return "[" + json.toString() + "]";
    }
}