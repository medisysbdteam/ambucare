package co.system.medical.ambucare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import co.system.medical.ambucare.session.SessionManager;
import co.system.medical.ambucare.R;
import co.system.medical.ambucare.auxiliary.Values;
//import co.system.medical.ambucare.approval.OperationActivity;
import co.system.medical.ambucare.dataformate.TParam;
import co.system.medical.ambucare.dataformate.TRequest;


public class MainActivity extends Activity {
    private Typeface trebuchetReqular,trebuchetBold,arialRegular;
    private TextView textViewAmbucare,textViewManagement,textViewUserTips,textViewProduct,
            useTipsLine1,useTipsLine2,useTipsLine3,ProductLine1,warningId,waringDetails,medicalId;
    private ImageButton btnRegistrationId;
    String userId;
   // Button btnLogin;
    ImageButton btnLogin;
    EditText edtUname, edtPassword;

    SessionManager session;

    private ProgressDialog aProgressDialog;

    private void InitializeComponant() {
        btnLogin = (ImageButton) findViewById(R.id.btnLogIn);
        btnRegistrationId = findViewById(R.id.btnRegistrationId);
        edtUname = (EditText) findViewById(R.id.editTextUserId);
        edtPassword = (EditText) findViewById(R.id.editTextPasswordId);
    }
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Tapplication.FullScreen(this);
        setContentView(R.layout.activity_main);
        textViewAmbucare = findViewById(R.id.textViewAmbucare);
        textViewManagement = findViewById(R.id.textViewManagement);
        textViewUserTips = findViewById(R.id.textViewAmbucare);
        textViewProduct = findViewById(R.id.textViewManagement);
        useTipsLine1 = findViewById(R.id.useTipsLine1);
        useTipsLine2 = findViewById(R.id.useTipsLine2);
        useTipsLine3 = findViewById(R.id.useTipsLine3);
        ProductLine1 = findViewById(R.id.ProductLine1);
        warningId = findViewById(R.id.warningId);
        waringDetails = findViewById(R.id.waringDetails);
        medicalId = findViewById(R.id.medicalId);
        btnRegistrationId = findViewById(R.id.btnRegistrationId);
        trebuchetBold = Typeface.createFromAsset(getAssets(),"font/trebuchet_ms_bold.ttf");
        trebuchetReqular = Typeface.createFromAsset(getAssets(),"font/trebuchet_ms.ttf");
        arialRegular = Typeface.createFromAsset(getAssets(),"font/arial_regular.ttf");
        textViewAmbucare.setTypeface(trebuchetBold);
        textViewManagement.setTypeface(trebuchetBold);
        textViewUserTips.setTypeface(trebuchetReqular);
        textViewProduct.setTypeface(trebuchetReqular);
        useTipsLine1.setTypeface(arialRegular);
        useTipsLine2.setTypeface(arialRegular);
        useTipsLine3.setTypeface(arialRegular);
        ProductLine1.setTypeface(arialRegular);
        warningId.setTypeface(arialRegular);
        waringDetails.setTypeface(arialRegular);
        medicalId.setTypeface(arialRegular);


//        String lTest = Tapplication.Pref().getString(getString(R.string.pref_login_data), "");
//        session = new SessionManager(getApplicationContext());
//        if (lTest != null || !lTest.isEmpty()) {
//
//        } else {
//
//        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        String priData = Tapplication.Pref().getString(getString(R.string.pref_login_data), "");
//        if (priData.length() > 0) {
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }
        findViewById(R.id.btnLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    JSONObject json = new JSONObject();
                    TRequest tRequest = new TRequest();
                    tRequest.setSp("GET_M_LOGIN");
                    tRequest.setDb(getString(R.string.DB_PatientCare));
                    List<TParam> tParamList = new ArrayList<TParam>();
                    tParamList.add(new TParam("@P_ID", ((EditText) findViewById(R.id.editTextUserId)).getText().toString()));
                    final boolean add = tParamList.add(new TParam("@P_PWD", ((EditText) findViewById(R.id.editTextPasswordId)).getText().toString()));
                    tRequest.setDict(tParamList);
                    Gson gson = new Gson();
                    json = new JSONObject(gson.toJson(tRequest, TRequest.class));
                    Log.v(Values.ApiGetDataParam,json.toString());
                    Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Method.POST, Values.ApiGetDataParam, json, loginListener(), genericErrorListener()));
                    //   Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Method.POST, getString(R.string.api_v1_get_data), json, loginListener(), genericErrorListener()));

                } catch (Exception ex) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    Log.v("Fail..",ex.getMessage());
                    Toast.makeText(getApplicationContext(),
                            ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegistrationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,registration.class);
                startActivity(intent);
            }
        });
    }
    private Response.ErrorListener genericErrorListener() {
        return new ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                try {
                    if (error instanceof NoConnectionError) {

                        ((TextView) findViewById(R.id.tv_login_status)).setText("No Connection");

                    } else if (error instanceof NetworkError) {

                        ((TextView) findViewById(R.id.tv_login_status)).setText("Network Error");
                    } else if (error instanceof ServerError) {
                        ((TextView) findViewById(R.id.tv_login_status)).setText("Server Errpr");
                    } else if (error instanceof TimeoutError) {
                        ((TextView) findViewById(R.id.tv_login_status)).setText("Timneout");
                    } else if (error instanceof VolleyError) {
                        try {
                            ((TextView) findViewById(R.id.tv_login_status)).setText("Volley Error");
                        } catch (Exception e) {

                        }
                    }

                } catch (Exception e) {
                    //  ((TextView) findViewById(R.id.tv_login_status)).setText("Error");
                }
            }

        };
    }

    private Response.Listener<JSONObject> loginListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    Gson Res = new Gson();

                    JSONArray data = response.getJSONArray("data");
                    Log.d(data.toString(), "onResponse: ");
                    if (data.length() > 0) {
                       // session.CreateLoginSession("001");
                        Tapplication.Pref().edit().putString(getString(R.string.pref_login_data), response.toString()).apply();
                        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Invalid Credentials",
                                Toast.LENGTH_LONG).show();
                        ((TextView) findViewById(R.id.tv_login_status)).setText("Invalid Credentials");
                    }

                } catch (Exception e) {
                    Log.v("mango", e.getMessage());
                    ((TextView) findViewById(R.id.tv_login_status)).setText(e.getMessage());
                }
            }
        };
    }
}
