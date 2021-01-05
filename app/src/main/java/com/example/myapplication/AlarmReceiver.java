package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class AlarmReceiver extends BroadcastReceiver {
    String userId;
    String userMobile;
    String userEmail;
    private SMSReceiver smsReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.getUserDetails(context);
        DBhandler db = new DBhandler(context);
        Date today = new Date();
        RailUtils utils = new RailUtils(db, userMobile, userId);
        db.getAllScheduledRoutes().forEach((scheduledRoute -> {
            long diff = scheduledRoute.route.departureTime.getTime() - today.getTime();
            int hoursGap = (int) diff / (1000*60*60);
            if (hoursGap < 24) {
                RailRoute route = scheduledRoute.route;
                smsReceiver = new SMSReceiver() {
                    @Override
                    public void afterAuth(String code) throws JSONException, ExecutionException, InterruptedException {
                        String ticketBody = getTicketBody(route, utils);
                        String ticketUrl = String.format(utils.authAndOrderUrl, code);

                        String ticket = new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ticketUrl, ticketBody).get();

                        JSONObject ticketJSON = new JSONObject(ticket);
                        String generatedReference = ticketJSON.getJSONObject("voutcher").getString("GeneretedReferenceValue");
                        String barcodeString = ticketJSON.getString("BarcodeImage");
                        db.addNewQR(new QRRouteItem(route, barcodeString, generatedReference));

                        String url = String.format(utils.sendSMSURL, generatedReference);

                        new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, ticketBody).get();
                        context.getApplicationContext().unregisterReceiver(smsReceiver);
                        utils.updateDBAfterOrder(scheduledRoute);
                    }
                };
                IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                intentFilter.setPriority(999);
                context.getApplicationContext().registerReceiver(smsReceiver, intentFilter);
                new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, utils.getTokenURL);
            }
        }));
    }

    private void getUserDetails(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput("usersData.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            this.userId = bufferedReader.readLine();
            this.userMobile = bufferedReader.readLine();
            this.userEmail = bufferedReader.readLine();
        } catch (Exception e) {
            // ignore
        }
    }

    private String getTicketBody(RailRoute route,RailUtils utils) {
        JSONObject trainJson = new JSONObject();
        JSONArray trainArray = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            /** EXAMPLE
             * json.put("destinationStationId", "23");
             * json.put("destinationStationHe", "");
             * json.put("orignStationId", "21");
             * json.put("orignStationHe", "");
             * json.put("trainNumber", 46);
             * json.put("departureTime", "07/12/2020 18:01:00");
             * json.put("arrivalTime", "07/12/2020 19:16:00");
             * json.put("orignStation", "באר שבע- צפון/אוניברסיטה");
             * json.put("destinationStation", "תל אביב - השלום");
             * json.put("orignStationNum", 7300);
             * json.put("destinationStationNum", 4600);
             * json.put("DestPlatform", 1);
             * json.put("TrainOrder", 1);
             ***/
            TrainStation fromStation = utils.getStationDataById(route.orignStation);
            TrainStation toStation = utils.getStationDataById(route.destStation);
            Date trainDate = route.departureTime;
            trainDate.setHours(0);
            trainDate.setMinutes(0);
            trainJson.put("TrainDate", utils.dateFormat.format(trainDate));
            trainJson.put("destinationStationId", toStation.id);
            trainJson.put("destinationStationHe", "");
            trainJson.put("orignStationId", fromStation.id);
            trainJson.put("orignStationHe", "");
            trainJson.put("trainNumber", Integer.valueOf(route.trainNum));
            trainJson.put("departureTime", utils.dateFormat.format(route.departureTime));
            trainJson.put("arrivalTime", utils.dateFormat.format(route.arrivalTime));

            trainJson.put("orignStation", fromStation.EN);
            trainJson.put("destinationStation", toStation.EN);
            trainJson.put("orignStationNum", Integer.valueOf(route.orignStation));
            trainJson.put("destinationStationNum", Integer.valueOf(route.destStation));
            trainJson.put("DestPlatform", Integer.valueOf(route.destPlatform));
            trainJson.put("TrainOrder", 1);

            json.put("smartcard", userId);
            json.put("mobile", userMobile);
            json.put("email", userEmail);
            trainArray.put(trainJson);
            json.put("trainsResult", trainArray);
        } catch (Exception e) {
            // ignore
        }

        return json.toString();
    }
}