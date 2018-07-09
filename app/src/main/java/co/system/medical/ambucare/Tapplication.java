package co.system.medical.ambucare;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import co.system.medical.ambucare.R;
import co.system.medical.ambucare.DB.TDbOpenHelper;
import co.system.medical.ambucare.auxiliary.LruBitmapCache;
import co.system.medical.ambucare.dataformate.TParam;
import co.system.medical.ambucare.dataformate.TRequest;

/**
 * Created by TahmidH_MIS on 11/29/2016.
 */

public class Tapplication extends Application {
    public static final String TAG = Tapplication.class.getSimpleName();
    private static final int MY_SOCKET_TIMEOUT_MS = 120000;
    public static String PrefName;
    private static Tapplication mInstance;
    private static Context mContext;
    private static SharedPreferences sharedPref;
    private RequestQueue mRequestQueue;
    private  static TDbOpenHelper tDbOpenHelper;
    private ImageLoader mImageLoader;

    public static Context getContext() {
        return mContext;
    }

    public static synchronized Tapplication getInstance() {

        return mInstance;
    }

    public static SharedPreferences Pref() {
        if (sharedPref == null) {
            sharedPref = mContext.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
        }
        return sharedPref;

    }

    public static void FullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static String ID() {
        try {
            // return Settings.Secure.ANDROID_ID;
            return Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "NA";
        }
    }

    public static ProgressDialog pleaseWait(Activity activity, String msg) {
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage(msg);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }



    @Override

    public void onCreate() {
        super.onCreate();


        mInstance = this;
        mContext = getApplicationContext();
        sharedPref = mContext.getSharedPreferences(getString(R.string.Pref), Context.MODE_PRIVATE);
        PrefName = getString(R.string.Pref);

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {

//        req.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static JSONObject intiJson(String sp, String db, List<TParam> tParamList) {

        JSONObject json = new JSONObject();
        try {

            TRequest tRequest = new TRequest();
            tRequest.setSp(sp);
            tRequest.setDb(db);
            tRequest.setDict(tParamList);
            Gson gson = new Gson();
            json = new JSONObject(gson.toJson(tRequest, TRequest.class));

        } catch (Exception e) {
            Toast.makeText(Tapplication.getContext(), "IN T :" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return json;
    }

    public static TDbOpenHelper getTDbHelper()
    {
        if (tDbOpenHelper ==null)
        {
            return new TDbOpenHelper(Tapplication.getContext());
        }
        return tDbOpenHelper;


    }
    public static SQLiteDatabase WriteDB()
    {
        return  Tapplication.getTDbHelper().getWritableDatabase();
    }
    public static SQLiteDatabase ReadDB()
    {
        return  Tapplication.getTDbHelper().getReadableDatabase();
    }



    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }



}
