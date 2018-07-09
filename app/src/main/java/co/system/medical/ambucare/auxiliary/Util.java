package co.system.medical.ambucare.auxiliary;

/**
 * Created by ARMAN on 14-Dec-16.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");

    public static String getCurrentTime() {
        return timeFormat.format(Calendar.getInstance().getTime());
    }

    public static String getCurrentDate() {
        return dateFormat.format(Calendar.getInstance().getTime());
    }


}