package com.example.myapplication;

import android.content.IntentFilter;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class RailUtils {
    private Map<Integer, TrainStation> stationsMap = new HashMap<>();
    private String[] stationsNames;
    private TrainStation[] stationsList;
    private SMSReceiver smsReceiver;
    private DBhandler db;
    private RailVoucherActivity activity;

    public final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public final String getRoutesUrl = "https://www.rail.co.il/apiinfo/api/Plan/GetRoutes?OId=%d&TId=%d&Date=%s&Hour=%s&isGoing=true&c=%d";
    public final String getTokenURLTemplate = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?mobile=%s&userId=%s&method=getToken&type=sms";
    public final String authAndOrderUrl = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?numSeats=1&method=MakeVoucherSeatsReservation&IsSendEmail=true&source=1&typeId=1&token=%s";
    public final String sendSMSURL = "https://www.rail.co.il/taarif//_layouts/15/SolBox.Rail.FastSale/ReservedPlaceHandler.ashx?Generatedref=%s&typeId=1&method=SendSms";
    public String getTokenURL;

    RailUtils(DBhandler db, String mobile, String userId ) {
        this.db = db;
        getTokenURL = String.format(getTokenURLTemplate, mobile, userId);
        initTrainsData();
    }

    RailUtils(RailVoucherActivity activity, DBhandler db) {
        this.activity = activity;
        this.db = db;
        getTokenURL = String.format(getTokenURLTemplate, activity.userMobile, activity.userId);
        initTrainsData();
    }

    public RailRoute[] getTrainRoutes(int orignStationNum, int destStationNum, Date timeToFind) {
        String date = getDateString(timeToFind);
        String hour = getHourString(timeToFind);
        String url = String.format(getRoutesUrl, orignStationNum, destStationNum, date, hour, timeToFind.getTime());

        try {
            String routes = new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url).get();
            JSONObject routesJSON = new JSONObject(routes);
            JSONObject routesData = routesJSON.getJSONObject("Data");
            int startIndex = routesData.getInt("StartIndex");
            JSONArray routesArr = routesData.getJSONArray("Routes");

            int routesSize = Math.min(routesArr.length(), startIndex + 5);
            RailRoute[] railRoutes = new RailRoute[routesSize - startIndex];

            for (int i = startIndex; i < routesSize; i++) {
                railRoutes[i-startIndex] = new RailRoute(routesArr.getJSONObject(i));
            }
            return railRoutes;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public TrainStation[] getStationsList() {
        return stationsList;
    }

    public TrainStation getStationDataById(String id) {
        int num_id = Integer.parseInt(id);

        return this.stationsMap.get(num_id);
    }

    private String getDateString(Date date) {
        String year = Integer.toString(date.getYear() + 1900);
        String month = addTrailingZeroIfNeeded(date.getMonth() + 1);
        String day = addTrailingZeroIfNeeded(date.getDate());

        return year + month + day;
    }

    private String getHourString(Date date) {
        String hours = addTrailingZeroIfNeeded(date.getHours());
        String minutes = addTrailingZeroIfNeeded(date.getMinutes());

        return hours + minutes;
    }

    private String addTrailingZeroIfNeeded(int num) {
        if (num < 10) {
            return "0" + Integer.toString(num);
        }

        return Integer.toString(num);
    }

    private void initTrainsData() {
        stationsMap.put(3700, new TrainStation(3700, "1", "Tel Aviv-Center"));
        stationsMap.put(3500, new TrainStation(3500, "2", "Hertsliyya"));
        stationsMap.put(3400, new TrainStation(3400, "3", "Bet Yehoshu''a"));
        stationsMap.put(3300, new TrainStation(3300, "4", "Netanya"));
        stationsMap.put(3100, new TrainStation(3100, "5", "Hadera- West"));
        stationsMap.put(2800, new TrainStation(2800, "6", "Binyamina"));
        stationsMap.put(2820, new TrainStation(2820, "7", "Caesarea P,h"));
        stationsMap.put(2500, new TrainStation(2500, "8", "Atlit"));
        stationsMap.put(2200, new TrainStation(2200, "9", "Haifa-Bat Gallim"));
        stationsMap.put(1300, new TrainStation(1300, "10", "Hutsot HaMifrats"));
        stationsMap.put(700, new TrainStation(700, "11", "Kiryat Hayyim"));
        stationsMap.put(1400, new TrainStation(1400, "12", "Kiryat Motzkin"));
        stationsMap.put(1500, new TrainStation(1500, "13", "Acre"));
        stationsMap.put(2300, new TrainStation(2300, "14", "Haifa H.HaKarmell"));
        stationsMap.put(8700, new TrainStation(8700, "15", "Kfar Sava-Nordau"));
        stationsMap.put(1600, new TrainStation(1600, "16", "Nahariyya"));
        stationsMap.put(6500, new TrainStation(6500, "17", "Jerusalem-Biblical Zoo"));
        stationsMap.put(6300, new TrainStation(6300, "18", "Bet Shemesh"));
        stationsMap.put(7000, new TrainStation(7000, "19", "Kiryat Gat"));
        stationsMap.put(5000, new TrainStation(5000, "20", "Lod"));
        stationsMap.put(7300, new TrainStation(7300, "21", "B.S. North"));
        stationsMap.put(4800, new TrainStation(4800, "22", "Kfar Habbad"));
        stationsMap.put(4600, new TrainStation(4600, "23", "Tel Aviv - HaShalom"));
        stationsMap.put(2100, new TrainStation(2100, "25", "Haifa Center"));
        stationsMap.put(5010, new TrainStation(5010, "26", "Ramla"));
        stationsMap.put(8800, new TrainStation(8800, "27", "Rosh Ha''ayin North"));
        stationsMap.put(5300, new TrainStation(5300, "28", "Be''er Ya''akov"));
        stationsMap.put(5200, new TrainStation(5200, "29", "Rehovot"));
        stationsMap.put(5410, new TrainStation(5410, "30", "Yavne East"));
        stationsMap.put(9100, new TrainStation(9100, "31", "R.HaRishonim"));
        stationsMap.put(5800, new TrainStation(5800, "32", "Ashdod- Ad Halom"));
        stationsMap.put(4250, new TrainStation(4250, "34", "Petah Tikva-Sgulla"));
        stationsMap.put(4100, new TrainStation(4100, "35", "Bne Brak"));
        stationsMap.put(3600, new TrainStation(3600, "36", "Tel Aviv University T\"U"));
        stationsMap.put(7320, new TrainStation(7320, "37", "Beer Sheva- Center"));
        stationsMap.put(1220, new TrainStation(1220, "38", "HaMifrats Central Station"));
        stationsMap.put(4900, new TrainStation(4900, "39", "Tel Aviv HaHagana"));
        stationsMap.put(8600, new TrainStation(8600, "40", "Ben Gurion Airport"));
        stationsMap.put(6700, new TrainStation(6700, "41", "Jerusalem - Malha"));
        stationsMap.put(5900, new TrainStation(5900, "42", "Ashkelon"));
        stationsMap.put(7500, new TrainStation(7500, "43", "Dimona"));
        stationsMap.put(9200, new TrainStation(9200, "44", "H.Sharon Sokolov"));
        stationsMap.put(4170, new TrainStation(4170, "45", "P.T. Kiryat Arye"));
        stationsMap.put(5150, new TrainStation(5150, "46", "Lod-Ganne Aviv"));
        stationsMap.put(8550, new TrainStation(8550, "47", "Lehavim-Rahat"));
        stationsMap.put(300, new TrainStation(300, "48", "Pa'ate Modi'in"));
        stationsMap.put(400, new TrainStation(400, "49", "Modi'in-Center M\"C"));
        stationsMap.put(4640, new TrainStation(4640, "50", "Holon Junction"));
        stationsMap.put(4660, new TrainStation(4660, "51", "Holon Wolfson"));
        stationsMap.put(4680, new TrainStation(4680, "52", "Bat Yam-Yoseftal"));
        stationsMap.put(4690, new TrainStation(4690, "53", "Bat.Y-Komemiyyut"));
        stationsMap.put(9800, new TrainStation(9800, "54", "R.Moshe-Dayan"));
        stationsMap.put(9000, new TrainStation(9000, "55", "Yavne West"));
        stationsMap.put(9600, new TrainStation(9600, "56", "Sderot"));
        stationsMap.put(9650, new TrainStation(9650, "57", "Netivot"));
        stationsMap.put(9700, new TrainStation(9700, "58", "Ofakim"));
        stationsMap.put(3310, new TrainStation(3310, "59", "NETANYA-SAPIR"));
        stationsMap.put(1240, new TrainStation(1240, "60", "YOKNE'AM-KFAR YEHOSHU'A"));
        stationsMap.put(1250, new TrainStation(1250, "61", "MIGDAL HA'EMEK-KFAR BARUKH"));
        stationsMap.put(1260, new TrainStation(1260, "62", "Afula R.Eitan"));
        stationsMap.put(1280, new TrainStation(1280, "63", "BEIT SHE'AN"));
        stationsMap.put(1820, new TrainStation(1820, "64", "Ahihud"));
        stationsMap.put(1840, new TrainStation(1840, "65", "Karmiel"));
        stationsMap.put(2940, new TrainStation(2940, "66", "Ra'anana West"));
        stationsMap.put(2960, new TrainStation(2960, "67", "Ra'anana South"));
        stationsMap.put(6150, new TrainStation(6150, "68", "Kiryat Malakhi  Yoav"));
        stationsMap.put(680, new TrainStation(680, "69", "Jerusalem - Yitzhak Navon"));
        stationsMap.put(6900, new TrainStation(6900, "70", "Mazkeret Batya"));

        stationsNames = new String[stationsMap.size()];
        stationsList = new TrainStation[stationsMap.size()];
        int i = 0;
        for (Map.Entry<Integer, TrainStation> entry : stationsMap.entrySet()) {
            stationsNames[i] = entry.getValue().EN;
            stationsList[i] = entry.getValue();
            i++;
        }

        Arrays.sort(stationsNames);
        Arrays.sort(stationsList);
    }

    public void orderNext24HrsVouchers() {
        Date today = new Date();
        db.getAllScheduledRoutes().forEach((scheduledRoute -> {
            long diff = scheduledRoute.route.departureTime.getTime() - today.getTime();
            int hoursGap = (int) diff / (1000*60*60);
            if (hoursGap < 24) {
                executeVoucherMaker(scheduledRoute);
            }
        }));
    }

    public void executeVoucherMaker(ScheduledRoute scheduledRoute) {
        RailRoute route = scheduledRoute.route;
        smsReceiver = new SMSReceiver() {
            @Override
            public void afterAuth(String code) throws ExecutionException, InterruptedException, JSONException {
                String ticketBody = getTicketBody(route);
                String ticketUrl = String.format(authAndOrderUrl, code);
                String ticket = new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ticketUrl, ticketBody).get();

                JSONObject ticketJSON = new JSONObject(ticket);
                String generatedReference = ticketJSON.getJSONObject("voutcher").getString("GeneretedReferenceValue");
                String barcodeString = ticketJSON.getString("BarcodeImage");
                activity.addQRItem(new QRRouteItem(route, barcodeString, generatedReference));

                String url = String.format(sendSMSURL, generatedReference);

                new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, ticketBody).get();
                activity.unregisterReceiver(smsReceiver);
                updateDBAfterOrder(scheduledRoute);
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        activity.registerReceiver(smsReceiver, intentFilter);
        new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getTokenURL);
    }

    public void updateDBAfterOrder(ScheduledRoute scheduledRoute) {
        if (scheduledRoute.repeated) {
            // Update route dates
            int newDay = scheduledRoute.route.departureTime.getDay() + 1;
            Date departureNextDay = getNextXday(scheduledRoute.route.departureTime, newDay);
            Date arrivalNextDay = getNextXday(scheduledRoute.route.arrivalTime, newDay);
            while (!scheduledRoute.days.contains(newDay)) {
                newDay = (newDay + 1) % 5;
            }
            scheduledRoute.route.departureTime = getNextXday(departureNextDay, newDay);
            scheduledRoute.route.arrivalTime = getNextXday(arrivalNextDay, newDay);

            db.updateRepeatedRoutesDates(scheduledRoute);
        } else {
            db.scheduledRouteWasOrdered(scheduledRoute);
        }
    }

    private Date getNextXday(Date date , int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        while (date.getDay() != day) {
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }

        return cal.getTime();
    }

    private String getTicketBody(RailRoute route) {
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
            TrainStation fromStation = getStationDataById(route.orignStation);
            TrainStation toStation = getStationDataById(route.destStation);
            Date trainDate = new Date(route.departureTime.getTime());
            trainDate.setHours(0);
            trainDate.setMinutes(0);
            trainJson.put("TrainDate", dateFormat.format(trainDate));
            trainJson.put("destinationStationId", toStation.id);
            trainJson.put("destinationStationHe", "");
            trainJson.put("orignStationId", fromStation.id);
            trainJson.put("orignStationHe", "");
            trainJson.put("trainNumber", Integer.valueOf(route.trainNum));
            trainJson.put("departureTime", dateFormat.format(route.departureTime));
            trainJson.put("arrivalTime", dateFormat.format(route.arrivalTime));

            trainJson.put("orignStation", fromStation.EN);
            trainJson.put("destinationStation", toStation.EN);
            trainJson.put("orignStationNum", Integer.valueOf(route.orignStation));
            trainJson.put("destinationStationNum", Integer.valueOf(route.destStation));
            trainJson.put("DestPlatform", Integer.valueOf(route.destPlatform));
            trainJson.put("TrainOrder", 1);

            json.put("smartcard", activity.userId);
            json.put("mobile", activity.userMobile);
            json.put("email", activity.userEmail);
            trainArray.put(trainJson);
            json.put("trainsResult", trainArray);

        } catch (Exception e) {
            // ignore
        }

        return json.toString();
    }

}

class TrainStation implements Comparable<TrainStation> {
    int code;
    String id;
    String EN;
    String HE;

    public TrainStation(int code, String id, String EN) {
        this.code = code;
        this.id = id;
        this.EN = EN;
    }

    public TrainStation(int code, String id, String EN, String HE) {
        this.code = code;
        this.id = id;
        this.EN = EN;
        this.HE = HE;
    }

    @NotNull
    public String toString() {
        return this.EN;
    }

    @Override
    public int compareTo(TrainStation o) {
        return this.EN.compareTo(o.EN);
    }
}

class ScheduledRoute {
    public RailRoute route;
    public boolean repeated;
    public ArrayList<Integer> days;
    public long scheduledTime;
    public boolean isOrdered;

    public ScheduledRoute(RailRoute route, ArrayList<Integer> days){
        this.scheduledTime = System.currentTimeMillis();
        this.route = route;
        this.isOrdered = false;
        if (days != null) {
            this.repeated = true;
            this.days = days;
        } else {
            this.repeated = false;
        }
    }

    public ScheduledRoute(RailRoute route, ArrayList<Integer> days, long scheduledTime, boolean isOrdered){
        this.scheduledTime = scheduledTime;
        this.isOrdered = isOrdered;
        this.route = route;
        if (days != null) {
            this.repeated = true;
            this.days = days;
        } else {
            this.repeated = false;
        }
    }
}

class QRRouteItem {
    public RailRoute route;
    public String QRref;
    public String barcode;
    public long scheduledTime;

    public QRRouteItem(RailRoute route, String barcode, String QRref) {
        this.route = route ;
        this.barcode = barcode;
        this.QRref = QRref;
        this.scheduledTime = System.currentTimeMillis();
    }

    public QRRouteItem(RailRoute route, String barcode, String QRref, long scheduledTime) {
        this.route = route ;
        this.barcode = barcode;
        this.QRref = QRref;
        this.scheduledTime = scheduledTime;
    }
}


class RailRoute implements Serializable {
    public Date arrivalTime;
    public Date departureTime;
    public String destStation;
    public String orignStation;
    public String trainNum;
    public String destPlatform;
    public int namOfTransforms;
    public boolean isDirectTrain;
    public boolean isFullTrain;

    public RailRoute(JSONObject railData) {
        try {
            JSONArray trains = railData.getJSONArray("Train");
            this.namOfTransforms = trains.length();
            JSONObject firstTrain = trains.getJSONObject(0);
            JSONObject lastTrain;
            if (namOfTransforms > 1) {
                lastTrain = trains.getJSONObject(namOfTransforms - 1);
            } else {
                lastTrain = firstTrain;
            }

            this.arrivalTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(lastTrain.getString("ArrivalTime"));
            this.departureTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(firstTrain.getString("DepartureTime"));
            this.destStation = lastTrain.getString("DestinationStation");
            this.orignStation = firstTrain.getString("OrignStation");
            this.trainNum = firstTrain.getString("Trainno");
            this.destPlatform = lastTrain.getString("DestPlatform");
            this.isDirectTrain = namOfTransforms == 1;
            this.isFullTrain = firstTrain.getBoolean("isFullTrain");
        } catch (Exception e) {
            // ignore
        }
    }

    public RailRoute(Date arrivalTime, Date departureTime, String destStation, String orignStation, String trainNum,
            String destPlatform, boolean isDirectTrain) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.destStation = destStation;
        this.orignStation = orignStation;
        this.trainNum = trainNum;
        this.destPlatform = destPlatform;
        this.isDirectTrain = isDirectTrain;
    }

    public String toString() {
        return this.trainNum;
    }
}