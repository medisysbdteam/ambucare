package co.system.medical.ambucare.DB;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static co.system.medical.ambucare.Tapplication.WriteDB;

/**
 * Created by TahmidH_MIS on 12/24/2016.
 */

public class TDbHelper {
    public static ContentValues setLocatioContent(String lat, String lon, double time, String deviceid, double emp) {
        ContentValues values = new ContentValues();
        values.put(LocationContract.LocationEntry.COLUMN_NAME_LAT, lat);
        values.put(LocationContract.LocationEntry.COLUMN_NAME_LON, lon);
        values.put(LocationContract.LocationEntry.COLUMN_NAME_TIME, time);
        values.put(LocationContract.LocationEntry.COLUMN_NAME_EMPID, emp);
        values.put(LocationContract.LocationEntry.COLUMN_NAME_DEVICEID, deviceid);
        return values;
    }

    public static long insertLocation(ContentValues values) {
        long ret = 0;
        SQLiteDatabase sqLiteDatabase = WriteDB();
        sqLiteDatabase.beginTransaction();
        try {

            ret = sqLiteDatabase.insert(LocationContract.LocationEntry.TABLE_NAME, null, values);
            sqLiteDatabase.setTransactionSuccessful();


        } catch (Exception e) {
            Log.v("mango", "failed to insert data in sqlite " + e.getMessage());

        } finally {
            sqLiteDatabase.endTransaction();
        }

        return ret;

    }
}
