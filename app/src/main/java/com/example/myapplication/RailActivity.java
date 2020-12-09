package com.example.myapplication;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.app.Fragment;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;


public class RailActivity extends AppCompatActivity {
    public RailUtils utils;

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private SMSReceiver smsReceiver;
    GetURL getURL = new GetURL();
    String getTokenURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?mobile=0544615463&userId=311240196&method=getToken";
    String makeVoucherURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?numSeats=1&smartCard=311240196&mobile=0544615463&userEmail=itayelbaz7@gmail.com&method=MakeVoucherSeatsReservation&IsSendEmail=true&source=1&typeId=1";
    String sendSMSURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?Generatedref=%s&typeId=1&method=SendSms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_main);
        utils = new RailUtils();


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                Fragment fr = new FindRouteFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place, fr);
                fragmentTransaction.addToBackStack(null).commit();

               /*** String sDate1="06/12/2020 11:00:00";
                Date date1= null;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(sDate1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                List<RailRoute> test = utils.getTrainRoutes(7300, 4900, date1);


                smsReceiver = new SMSReceiver() {
                    @Override
                    public void afterAuth() throws ExecutionException, InterruptedException, JSONException {
                        Toast.makeText(getApplicationContext(), "got sms", Toast.LENGTH_SHORT).show();

                        String ticket = new GetURL().execute(makeVoucherURL, getTicketBody()).get();

                        JSONObject ticketJSON = new JSONObject(ticket);
                        String genertedReference = ticketJSON.getJSONObject("voutcher").getString("GeneretedReferenceValue");
                        String url = String.format(sendSMSURL, genertedReference);

                        Toast.makeText(getApplicationContext(), "Ordered voucher, getting SMS", Toast.LENGTH_SHORT).show();

                        new GetURL().execute(url, getTicketBody()).get();
                        unregisterReceiver(smsReceiver);
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                    }
                };
                IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                intentFilter.setPriority(999);
                registerReceiver(smsReceiver, intentFilter);
                new GetURL().execute(getTokenURL);
                Toast.makeText(getApplicationContext(), "waiting for sms", Toast.LENGTH_SHORT).show();***/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    public void showAddButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
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