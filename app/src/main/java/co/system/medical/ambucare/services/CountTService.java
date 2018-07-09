package co.system.medical.ambucare.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import co.system.medical.ambucare.Tapplication;
import co.system.medical.ambucare.auxiliary.Data;
import co.system.medical.ambucare.auxiliary.Database;
import co.system.medical.ambucare.auxiliary.StoredProcedure;
import co.system.medical.ambucare.auxiliary.Values;
import co.system.medical.ambucare.dataformate.TParam;
//import me.leolin.shortcutbadger.ShortcutBadger;

public class CountTService extends Service {
    Timer timerObj;

    public CountTService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //super.onCreate();

        timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                Log.v("mango", "testing testing ........");
                List<TParam> params = new ArrayList<>();
                params.add(new TParam("@id", Data.getUserID()));
                JSONObject json = Tapplication.intiJson(StoredProcedure.count_approval_type, Database.SCM, params);
                Log.v("mango", json.toString());
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Values.ApiGetData, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("mango", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            Data.nCount.clear();
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject j = jsonArray.getJSONObject(i);
                                    Data.setNCount(j.getString("NAME"), j.getInt("COUNT"));
                                }
                            }

                        } catch (Exception e) {

                        }
                        int total = 0;
                        try {
                            for (Map.Entry<String, Integer> entry : Data.nCount.entrySet()
                                    ) {
                                  total = total +  entry.getValue();
                            }
                            Log.v("mango","total"+ total);
                           // ShortcutBadger.applyCount(Tapplication.getContext(), total);
//                            if (total>0)
//                            {
//                                ShortcutBadger.applyCount(Tapplication.getContext(), total);
//                            }
//                            else {
//                                ShortcutBadger.removeCount(Tapplication.getContext());
//                            }
                        } catch (Exception e) {
                            Log.e("mango","Error"+ e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                Tapplication.getInstance().cancelPendingRequests("COUNT");
                Tapplication.getInstance().addToRequestQueue(req, "COUNT");
            }
        };
        timerObj.schedule(timerTaskObj, 0, 5000);

    }
}
