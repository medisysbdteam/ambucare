package co.system.medical.ambucare.DB;

import android.provider.BaseColumns;

/**
 * Created by TahmidH_MIS on 12/24/2016.
 */

public final  class LocationContract {
    LocationContract()
    {

    }

    public static class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String COLUMN_NAME_TIME = "ltime";
        public static final String COLUMN_NAME_EMPID = "empid";
        public static final String COLUMN_NAME_DEVICEID = "deviceid";
    }

}
