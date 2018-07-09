package co.system.medical.ambucare.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import co.system.medical.ambucare.DB.LocationContract;
import co.system.medical.ambucare.DB.TDbHelper;
import co.system.medical.ambucare.Tapplication;
import co.system.medical.ambucare.auxiliary.Data;
import co.system.medical.ambucare.auxiliary.Database;
import co.system.medical.ambucare.auxiliary.StoredProcedure;

import co.system.medical.ambucare.auxiliary.Values;
import co.system.medical.ambucare.dataformate.TParam;
import co.system.medical.ambucare.dataformate.TRequest;

/**
 * Created by TahmidH_MIS on 12/3/2016.
 */

public class LocationTService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final String TAG = "location-updates-sample";
    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();


    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

//        // Sets the desired interval for active location updates. This interval is
//        // inexact. You may not receive updates at all if no location sources are available, or
//        // you may receive them slower than requested. You may also receive updates faster than
//        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//
//        // Sets the fastest rate for active location updates. This interval is exact, and your
//        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        //   mLocationRequest.setSmallestDisplacement(10);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);

            } else {
                Toast.makeText(this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        try {
            if (mCurrentLocation == null) {


                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        //  mCurrentLocation = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,)
                    }
                } else {
                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    //    LocationServices.FusedLocationApi.
                }
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                //  updateUI();
                Toast.makeText(this, "Inside IF",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Inside ESLE",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }


        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (true) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        Toast.makeText(this, mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(),
//                Toast.LENGTH_SHORT).show();
        try {
            TRequest tRequest = new TRequest();
            tRequest.setDb(Database.PC);
            tRequest.setSp(StoredProcedure.set_location_bulk);
            final List<TParam> tParamList = new ArrayList<TParam>();
            String empID = "";
            try {
                empID = Data.getUserID();
            } catch (Exception e) {

            }

            tParamList.add(new TParam("@P_USER_ID", empID));
            tParamList.add(new TParam("@P_DEVICES", Tapplication.ID()));

            tParamList.add(new TParam("@P_LAT", Double.toString(mCurrentLocation.getLatitude())));
            tParamList.add(new TParam("@P_LON", Double.toString(mCurrentLocation.getLongitude())));
            //new Date().getTime()
            // tParamList.add(new TParam("@Time", String.valueOf(new Date().getTime())));
            //   tParamList.add(new TParam("@Time", String.valueOf(DateFormat.getDateTimeInstance().format(new Date()))));
            tRequest.setDict(tParamList);
            String urls = Values.ApiSetData;//"http://192.168.2.72/TWebApiSearch/api/v1/TService/SetData";
            JSONObject json = new JSONObject();
            json = new JSONObject(new Gson().toJson(tRequest, TRequest.class));
            Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, loginListener(), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    double EMPIDD = 0;
                    if (Data.getUserID() != null && Data.getUserID() != "") {
                        EMPIDD = Double.valueOf(Data.getUserID());
                    }
                    //   Toast.makeText(LocationTService.this, ""+Double.valueOf(mCurrentLocation.getLatitude()), Toast.LENGTH_SHORT).show();
                    long iddd = TDbHelper.insertLocation(TDbHelper.setLocatioContent(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()), Double.valueOf(new Date().getTime()), Tapplication.ID(), Double.valueOf(EMPIDD)));
                    //   Toast.makeText(LocationTService.this, "Inserted " + iddd + " "+mCurrentLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                }
            }));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        tRequest.setDb(getResources().getString(android.R.string.DB_SCM));
//        updateUI();
//        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        // super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate() {
        // super.onCreate();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        try {
            //   Thread.sleep(5000);
        } catch (Exception e) {

        }

        //  startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Response.ErrorListener genericErrorListener() {
        return new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {

                try {
                    if (error instanceof NoConnectionError) {


                    } else if (error instanceof NetworkError) {


                    } else if (error instanceof ServerError) {

                    } else if (error instanceof TimeoutError) {

                    } else if (error instanceof VolleyError) {
                        try {

                        } catch (Exception e) {

                        }
                    }

                } catch (Exception e) {

                }
            }

        };
    }

    private Response.Listener<JSONObject> loginListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String[] projection = {
                            LocationContract.LocationEntry._ID,
                            LocationContract.LocationEntry.COLUMN_NAME_EMPID,
                            LocationContract.LocationEntry.COLUMN_NAME_DEVICEID,
                            LocationContract.LocationEntry.COLUMN_NAME_LAT,
                            LocationContract.LocationEntry.COLUMN_NAME_LON,
                            LocationContract.LocationEntry.COLUMN_NAME_TIME
                    };
                    List<List<TParam>> tParamListS = new ArrayList<List<TParam>>();
                    Cursor cursor = Tapplication.ReadDB().query(
                            LocationContract.LocationEntry.TABLE_NAME,                     // The table to query
                            projection,                               // The columns to return
                            null,                                // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
                    );

                    final List<Long> tss = new ArrayList<Long>();
                    final List itemIds = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        long itemId = cursor.getLong(
                                cursor.getColumnIndexOrThrow(LocationContract.LocationEntry._ID));
                        itemIds.add(itemId);
                        List<TParam> tinnerParam = new ArrayList<TParam>();
                        String lt = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME_LAT));
                        String ln = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME_LON));
                        long tm = cursor.getLong(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME_TIME));
                        long eid = 0;
                        try {
                            eid = cursor.getLong(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME_EMPID));
                        } catch (Exception e) {

                        }

                        String did = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_NAME_DEVICEID));
                        tinnerParam.add(new TParam("@P_USER_ID", String.valueOf(eid)));
                        tinnerParam.add(new TParam("@P_DEVICES", String.valueOf(did)));
                        tinnerParam.add(new TParam("@P_LAT", String.valueOf(lt)));
                        tinnerParam.add(new TParam("@P_LON", String.valueOf(ln)));
                        tParamListS.add(tinnerParam);


                    }
                    cursor.close();

                    if (tParamListS.size() > 0) {
                        TRequest tRequest = new TRequest();
                        tRequest.setDb(Database.PC);
                        tRequest.setSp(StoredProcedure.set_location_bulk);
                        tRequest.setDictList(tParamListS);
                        JSONObject json = new JSONObject();
                        json = new JSONObject(new Gson().toJson(tRequest, TRequest.class));
                        Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(LocationTService.this, response.toString(), Toast.LENGTH_SHORT).show();
                                String selection = LocationContract.LocationEntry._ID + " = ?";
                                String[] addddd = new String[itemIds.size()];
                                for (int i = 0; i < itemIds.size(); i++
                                        ) {
                                    String[] a = new String[1];
                                    a[0] = String.valueOf(itemIds.get(i));

                                    addddd[i] = String.valueOf(Tapplication.WriteDB().delete(LocationContract.LocationEntry.TABLE_NAME, selection, a));
                                }
                                String dddsdsd = "asdasd";

                                //     Tapplication.WriteDB().delete(LocationContract.LocationEntry.TABLE_NAME, selection, addddd);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LocationTService.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
                    }

                } catch (Exception e) {
                    Log.v("mango", e.getMessage());

                }
            }
        };
    }
}