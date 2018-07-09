package co.system.medical.ambucare.auxiliary;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.system.medical.ambucare.dataformate.Employee;


/**
 * Created by TahmidH_MIS on 12/14/2016.
 */

public class Data {
    public static Map<String, Integer> nCount = new HashMap<String, Integer>();
    public static List<Employee> employees = new ArrayList<Employee>();

    public static List<Employee> getEmployees() {
//        if (employees.size()==0)
//        {
//           String s1 =  Tapplication.Pref().getString(Values.pref_emp_List,"");
//            if (!s1.isEmpty() && s1 !="" )
//            {
//                Data.setEmployees(new Gson().fromJson(s1,employees.getClass()));
//            }
//        }
        return employees;
    }

    public static void setEmployees(List<Employee> employees) {
        Data.employees = employees;
    }

    public static Response.ErrorListener genericErrorListener(final ProgressDialog pDialog, final Context context) {
        return new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                try {
                    Toast.makeText(context, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    if (error instanceof NoConnectionError) {
//
//
//                        //    ((TextView) findViewById(R.id.tv_login_status)).setText("No Connection");
//
//                    } else if (error instanceof NetworkError) {
//
//                        //    ((TextView) findViewById(R.id.tv_login_status)).setText("Network Error");
//                    } else if (error instanceof ServerError) {
//                        //   ((TextView) findViewById(R.id.tv_login_status)).setText("Server Errpr");
//                    } else if (error instanceof TimeoutError) {
//                        //   ((TextView) findViewById(R.id.tv_login_status)).setText("Timneout");
//                    } else if (error instanceof VolleyError) {
//                        try {
//                            //       ((TextView) findViewById(R.id.tv_login_status)).setText("Volley Error");
//                        } catch (Exception e) {
//
//                        }
//                    }

                } catch (Exception e) {
                    //  ((TextView) findViewById(R.id.tv_login_status)).setText("Error");
//                    Toast.makeText(OperationActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        };
    }

    public static JSONObject LoginData;

    public static String getUserID() {
        try {
            if (LoginData == null) {

               // LoginData = new JSONObject(Pref().getString(Values.pref_login_data, ""));
            }
            return LoginData.getJSONArray("data").getJSONObject(0).getString("EMP_ID");

        } catch (Exception e) {
            return "";
        }


    }

    public static void setNCount(String name, int isUp) {
        nCount.put(name, isUp);

    }

}
