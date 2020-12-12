package com.example.myapplication;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RailUtils {
    private String getRoutesUrl = "https://www.rail.co.il/apiinfo/api/Plan/GetRoutes?OId=%d&TId=%d&Date=%s&Hour=%s&isGoing=true&c=%d";
    private Map<Integer, TrainStation> stationsMap = new HashMap<>();
    private String[] stationsNames;
    private TrainStation[] stationsList;

    RailUtils() {
        initTrainsData();
    }

    public RailRoute[] getTrainRoutes(int orignStationNum, int destStationNum, Date timeToFind) {
        String date = getDateString(timeToFind);
        String hour = getHourString(timeToFind);
        String url = String.format(getRoutesUrl, orignStationNum, destStationNum, date, hour, timeToFind.getTime());

        try {
            String routes = new GetURL().execute(url).get();
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
    
    public String[] getStationsNames() {
        return this.stationsNames;
    }

    public TrainStation[] getStationsList() {
        return stationsList;
    }

    public TrainStation getStationDataById(String id) {
        int num_id = Integer.parseInt(id); // TODO: change map to string and not int

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

    // TODO: make a method
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
        return this.EN.compareTo(((TrainStation) o).EN);
    }
}

class ScheduledRoute {
    public RailRoute route;
    public boolean repeated;
    public ArrayList<Integer> days;

    public ScheduledRoute(RailRoute route, ArrayList<Integer> days){
        this.route = route;
        if (days != null) {
            this.repeated = true;
            this.days = days;
        } else {
            this.repeated = false;
        }
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

    public String toString() {
        return this.trainNum;
    }
}