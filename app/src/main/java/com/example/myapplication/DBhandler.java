package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBhandler {
    private myDbHelper db;
    private Context context;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public DBhandler(Context context) {
        this.context = context;
        db = new myDbHelper(context);
    }

    public void addNewScheduleRoute(ScheduledRoute route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.KEY_ID, route.scheduledTime);
        contentValues.put(myDbHelper.KEY_REPEATED, route.repeated);

        if (route.repeated) {
            contentValues.put(myDbHelper.KEY_SUNDAY, route.days.contains(0));
            contentValues.put(myDbHelper.KEY_MONDAY, route.days.contains(1));
            contentValues.put(myDbHelper.KEY_TUESDAY, route.days.contains(2));
            contentValues.put(myDbHelper.KEY_WEDNESDAY, route.days.contains(3));
            contentValues.put(myDbHelper.KEY_THURSDAY, route.days.contains(4));
        }

        contentValues.put(myDbHelper.KEY_ARRIVAL, dateFormat.format(route.route.arrivalTime));
        contentValues.put(myDbHelper.KEY_DEPARTURE, dateFormat.format(route.route.departureTime));
        contentValues.put(myDbHelper.KEY_DESTINATION, route.route.destStation);
        contentValues.put(myDbHelper.KEY_ORIGIN, route.route.orignStation);
        contentValues.put(myDbHelper.KEY_TRAIN_NUM, route.route.trainNum);
        contentValues.put(myDbHelper.KEY_DEST_PLATFORM, route.route.destPlatform);
        contentValues.put(myDbHelper.KEY_IS_DIRECT, route.route.isDirectTrain);
        contentValues.put(myDbHelper.KEY_IS_ORDERED, route.isOrdered);
        long id = dbb.insert(myDbHelper.TABLE_schedule_routes, null , contentValues);
    }

    public void deleteScheduleRoute(ScheduledRoute route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        String[] whereArgs ={String.valueOf(route.scheduledTime)};

        int count = dbb.delete(myDbHelper.TABLE_schedule_routes ,myDbHelper.KEY_ID+" = ?",whereArgs);
    }

    public void scheduledRouteWasOrdered(ScheduledRoute route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        String[] whereArgs ={String.valueOf(route.scheduledTime)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.KEY_IS_ORDERED, true);

        dbb.update(myDbHelper.TABLE_schedule_routes, contentValues, myDbHelper.KEY_ID+" = ?", whereArgs);
    }

    public void updateRepeatedRoutesDates(ScheduledRoute route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        String[] whereArgs ={String.valueOf(route.scheduledTime)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.KEY_DEPARTURE, dateFormat.format(route.route.departureTime));
        contentValues.put(myDbHelper.KEY_ARRIVAL, dateFormat.format(route.route.arrivalTime));

        dbb.update(myDbHelper.TABLE_schedule_routes, contentValues, myDbHelper.KEY_ID+" = ?", whereArgs);
    }

    public List<ScheduledRoute> getAllScheduledRoutes() {
        ArrayList<ScheduledRoute> scheduledRoutes = new ArrayList<>();
        Date today = new Date();
        SQLiteDatabase dbb = db.getWritableDatabase();

        String query = "SELECT * FROM "+ myDbHelper.TABLE_schedule_routes;
        Cursor cursor = dbb.rawQuery(query,null);
        while (cursor.moveToNext()){
            try {
                long id = cursor.getLong(cursor.getColumnIndex(myDbHelper.KEY_ID));
                boolean repeat = cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_REPEATED)) != 0;
                ArrayList<Integer> days = null;
                if (repeat) {
                    days = new ArrayList<>();
                    if (cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_SUNDAY)) != 0) {
                        days.add(0);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_MONDAY))!= 0) {
                        days.add(1);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_TUESDAY)) != 0) {
                        days.add(2);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_WEDNESDAY)) != 0) {
                        days.add(3);
                    }
                    if (cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_THURSDAY)) != 0) {
                        days.add(4);
                    }
                }
                Date arrival = dateFormat.parse(cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_ARRIVAL)));
                Date departure = dateFormat.parse(cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DEPARTURE)));
                String dest = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DESTINATION));
                String origin = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_ORIGIN));
                String trainNum = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_TRAIN_NUM));
                String destPlatform = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DEST_PLATFORM));
                boolean isDirect = cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_IS_DIRECT)) != 0;
                boolean isOrdered = cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_IS_ORDERED)) != 0;

                RailRoute route = new RailRoute(arrival, departure, dest, origin, trainNum, destPlatform, isDirect);
                ScheduledRoute scheduledRoute = new ScheduledRoute(route, days, id, isOrdered);
                if (!repeat && (today.after(arrival) || isOrdered)) {
                    this.deleteScheduleRoute(scheduledRoute);
                } else {
                    scheduledRoutes.add(scheduledRoute);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        return  scheduledRoutes;
    }

    public void addNewQR(QRRouteItem route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.KEY_ID, route.scheduledTime);
        contentValues.put(myDbHelper.KEY_QR_REF, route.QRref);
        contentValues.put(myDbHelper.KEY_BARCODE, route.barcode);
        contentValues.put(myDbHelper.KEY_ORIGIN, route.route.orignStation);
        contentValues.put(myDbHelper.KEY_ARRIVAL, dateFormat.format(route.route.arrivalTime));
        contentValues.put(myDbHelper.KEY_DEPARTURE, dateFormat.format(route.route.departureTime));
        contentValues.put(myDbHelper.KEY_DESTINATION, route.route.destStation);
        contentValues.put(myDbHelper.KEY_ORIGIN, route.route.orignStation);
        contentValues.put(myDbHelper.KEY_TRAIN_NUM, route.route.trainNum);
        contentValues.put(myDbHelper.KEY_DEST_PLATFORM, route.route.destPlatform);
        contentValues.put(myDbHelper.KEY_IS_DIRECT, route.route.isDirectTrain);
        long id = dbb.insert(myDbHelper.TABLE_QR_codes, null , contentValues);
    }

    public void deleteQR(QRRouteItem route) {
        SQLiteDatabase dbb = db.getWritableDatabase();
        String[] whereArgs ={String.valueOf(route.scheduledTime)};

        int count =dbb.delete(myDbHelper.TABLE_QR_codes ,myDbHelper.KEY_ID+" = ?",whereArgs);
    }

    public List<QRRouteItem> getAllQR() {
        ArrayList<QRRouteItem> QRItems = new ArrayList<>();
        long today = new Date().getTime();
        SQLiteDatabase dbb = db.getWritableDatabase();

        String query = "SELECT * FROM "+ myDbHelper.TABLE_QR_codes;
        Cursor cursor = dbb.rawQuery(query,null);
        while (cursor.moveToNext()){
            try {
                long id = cursor.getLong(cursor.getColumnIndex(myDbHelper.KEY_ID));
                Date arrival = dateFormat.parse(cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_ARRIVAL)));
                Date departure = dateFormat.parse(cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DEPARTURE)));
                String dest = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DESTINATION));
                String origin = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_ORIGIN));
                String trainNum = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_TRAIN_NUM));
                String destPlatform = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_DEST_PLATFORM));
                boolean isDirect = cursor.getInt(cursor.getColumnIndex(myDbHelper.KEY_IS_DIRECT)) != 0;
                String qrRef = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_QR_REF));
                String barcode = cursor.getString(cursor.getColumnIndex(myDbHelper.KEY_BARCODE));

                RailRoute route = new RailRoute(arrival, departure, dest, origin, trainNum, destPlatform, isDirect);
                QRRouteItem QRItem = new QRRouteItem(route, barcode, qrRef, id);

                long howOld = today - id;
                int days = (int) (howOld / (1000*60*60*24));
                if (days > 6) {
                    this.deleteQR(QRItem);
                } else {
                    QRItems.add(new QRRouteItem(route, barcode, qrRef, id));
                }
            }
            catch (Exception e) {
                // Ignore
            }
        }

        return  QRItems;
    }

    static class myDbHelper extends SQLiteOpenHelper   {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "israelrails.db";
        private static final String TABLE_schedule_routes = "scheduleroutes";
        private static final String TABLE_QR_codes = "qrcodes";

        private static final String KEY_ID = "id";
        private static final String KEY_REPEATED = "repeated";
        private static final String KEY_SUNDAY = "sunday";
        private static final String KEY_MONDAY = "monday";
        private static final String KEY_TUESDAY = "tuesday";
        private static final String KEY_WEDNESDAY = "wednesday";
        private static final String KEY_THURSDAY = "thursday";
        private static final String KEY_ARRIVAL = "arrival";
        private static final String KEY_DEPARTURE = "departure";
        private static final String KEY_DESTINATION = "destination";
        private static final String KEY_ORIGIN = "origin";
        private static final String KEY_TRAIN_NUM = "trainnum";
        private static final String KEY_DEST_PLATFORM = "destplatform";
        private static final String KEY_TRANSFORMS = "transforms";
        private static final String KEY_IS_DIRECT = "isdirect";
        private static final String KEY_QR_REF = "qrref";
        private static final String KEY_BARCODE = "barcode";
        private static final String KEY_IS_ORDERED = "is_ordered";


        public myDbHelper(Context context){
            super(context,DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            String CREATE_TABLE = "CREATE TABLE " + TABLE_schedule_routes + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_REPEATED + " INTEGER not NULL,"
                    + KEY_SUNDAY + " INTEGER,"
                    + KEY_MONDAY + " INTEGER,"
                    + KEY_TUESDAY + " INTEGER,"
                    + KEY_WEDNESDAY + " INTEGER,"
                    + KEY_THURSDAY + " INTEGER,"
                    + KEY_TRAIN_NUM + " TEXT not NULL,"
                    + KEY_ARRIVAL + " DATE not NULL,"
                    + KEY_DEPARTURE + " DATE not NULL,"
                    + KEY_DESTINATION + " TEXT not NULL,"
                    + KEY_ORIGIN + " TEXT not NULL,"
                    + KEY_TRANSFORMS + " INTEGER,"
                    + KEY_DEST_PLATFORM + " TEXT not NULL,"
                    + KEY_IS_DIRECT + " INTEGER" +")";
            db.execSQL(CREATE_TABLE);

            CREATE_TABLE = "CREATE TABLE " + TABLE_QR_codes + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_QR_REF + " TEXT not NULL,"
                    + KEY_BARCODE + " TEXT,"
                    + KEY_ARRIVAL + " DATE not NULL,"
                    + KEY_DEPARTURE + " DATE not NULL,"
                    + KEY_DESTINATION + " TEXT not NULL,"
                    + KEY_ORIGIN + " TEXT not NULL,"
                    + KEY_TRAIN_NUM + " TEXT not NULL,"
                    + KEY_TRANSFORMS + " INTEGER,"
                    + KEY_DEST_PLATFORM + " TEXT not NULL,"
                    + KEY_IS_DIRECT + " INTEGER" +")";
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_schedule_routes);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QR_codes);
            onCreate(db);
        }
    }
}