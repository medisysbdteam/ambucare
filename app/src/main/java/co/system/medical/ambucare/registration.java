package co.system.medical.ambucare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.collect.Range;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import co.system.medical.ambucare.DB.TDbHelper;
import co.system.medical.ambucare.auxiliary.Data;
import co.system.medical.ambucare.auxiliary.Values;
import co.system.medical.ambucare.dataformate.TParam;
import co.system.medical.ambucare.dataformate.TRequest;
import co.system.medical.ambucare.services.LocationTService;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class registration extends AppCompatActivity implements View.OnClickListener {
    private EditText birthDateEditText, editTextMobileNoId, EditTextFirstNameId, EditTextLastNameId;
    private DatePickerDialog datePickerDialog;
    private AutoCompleteTextView editTextNationalityId;
    private ArrayAdapter<Nationality> adapter;
    private ArrayAdapter<Religion> religionAdapter;
    private ArrayAdapter<Meritial> meritialAdapter;
    private Spinner ReligionSpinner, MaritialSpinner;
    private Button btnRegistrationId;
    private ProgressDialog pDialog;
    private String nationalityCode;
    private RadioButton male, female, unknownGender;
    private String sex_Status_Code;
    private String EMP_ID;
    private AwesomeValidation awesomeValidation;
    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        EditTextFirstNameId = findViewById(R.id.EditTextFirstNameId);
        EditTextLastNameId = findViewById(R.id.EditTextLastNameId);
        birthDateEditText = findViewById(R.id.editTextBirthDateId);
        editTextNationalityId = findViewById(R.id.editTextNationalityId);
        ReligionSpinner = findViewById(R.id.spinnerReligionId);
        MaritialSpinner = findViewById(R.id.spinnerMaritialId);
        btnRegistrationId = findViewById(R.id.btnRegistrationId);
        editTextMobileNoId = findViewById(R.id.editTextMobileNoId);
        genderRadioGroup = findViewById(R.id.gender_radiogroup);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        unknownGender = findViewById(R.id.unknownGender);
        //Get Nationality
        GetData("GET_M_NATIONALITIES");
        GetData("GET_M_RELIGION");
        GetData("GET_M_MERITIAL");

        //adding validation to edittexts
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation = new AwesomeValidation(COLORATION);
        awesomeValidation.setColor(R.color.ambucareColor);
      /*  awesomeValidation.addValidation(this, R.id.EditTextFirstNameId, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.first_name_error);
        awesomeValidation.addValidation(this, R.id.EditTextLastNameId, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.last_name_error);
        awesomeValidation.addValidation(this, R.id.editTextEmailId, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.editTextNationalityId, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nationalityerror);
        *///awesomeValidation.addValidation(this,R.id.gender_radiogroup,)
        //Set listeners of views
        setViewActions();

        //Create DatePickerDialog to show a calendar to user to select birthdate
        prepareDatePickerDialog();
    }

    private void prepareDatePickerDialog() {
        //Get current date
        Calendar calendar = Calendar.getInstance();

        //Create datePickerDialog with initial date which is current and decide what happens when a date is selected.
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //When a date is selected, it comes here.
                //Change birthdayEdittext's text and dismiss dialog.
                birthDateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                datePickerDialog.dismiss();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setViewActions() {
        birthDateEditText.setOnClickListener(this);
        btnRegistrationId.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        unknownGender.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTextBirthDateId:
                datePickerDialog.show();
                break;
            case R.id.btnRegistrationId:
                if (editTextMobileNoId.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter Mobile No", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (birthDateEditText.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter Birth Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (birthDateEditText.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter Birth Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitForm();
                break;
            case R.id.male:
                sex_Status_Code = "1";
                break;
            case R.id.female:
                sex_Status_Code = "2";
                break;
            case R.id.unknownGender:
                sex_Status_Code = "3";
                break;
        }
    }

    private Response.Listener<JSONObject> nationalitiesListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Log.d(data.toString(), "onResponse: ");
                    if (data.length() > 0) {
                        ArrayList<Nationality> nationalityList = new ArrayList<Nationality>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                String T_NTNLTY_CODE = data.getJSONObject(i).getString("T_NTNLTY_CODE");
                                String T_NTNLTY_NAME = data.getJSONObject(i).getString("T_NTNLTY_NAME");
                                String T_PHONE_CODE = data.getJSONObject(i).getString("T_PHONE_CODE");
                                Nationality nationlity = new Nationality(T_NTNLTY_CODE, T_NTNLTY_NAME, T_PHONE_CODE);
                                nationalityList.add(nationlity);
                            }
                        }

                        adapter = new ArrayAdapter<Nationality>(registration.this, android.R.layout.simple_list_item_1, nationalityList);
                        editTextNationalityId.setThreshold(1);
                        editTextNationalityId.setAdapter(adapter);

                        editTextNationalityId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object item = parent.getItemAtPosition(position);
                                if (item instanceof Nationality) {
                                    Nationality nationality = (Nationality) item;
                                    editTextMobileNoId.setText(nationality.T_PHONE_CODE);
                                    nationalityCode = nationality.T_NTNLTY_CODE;
                                }
                            }
                        });
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

    private Response.Listener<JSONObject> religionListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Log.d(data.toString(), "onResponse: ");
                    if (data.length() > 0) {
                        ArrayList<Religion> religionList = new ArrayList<Religion>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                String T_RLGN_CODE = data.getJSONObject(i).getString("T_RLGN_CODE");
                                String T_RLGN_NAME = data.getJSONObject(i).getString("T_RLGN_NAME");
                                Religion religion = new Religion(T_RLGN_CODE, T_RLGN_NAME);
                                religionList.add(religion);
                            }
                        }
                        religionAdapter = new ArrayAdapter<Religion>(registration.this, R.layout.spinner_layout, R.id.spinnerTextViewSampleId, religionList);
                        ReligionSpinner.setAdapter(religionAdapter);
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

    private Response.Listener<JSONObject> maritialListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Log.d(data.toString(), "onResponse: ");
                    if (data.length() > 0) {
                        ArrayList<Meritial> meritialList = new ArrayList<Meritial>();
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                String T_MRTL_STATUS_CODE = data.getJSONObject(i).getString("T_MRTL_STATUS_CODE");
                                String T_MRTL_STATUS_NAME = data.getJSONObject(i).getString("T_MRTL_STATUS_NAME");
                                Meritial meritial = new Meritial(T_MRTL_STATUS_CODE, T_MRTL_STATUS_NAME);
                                meritialList.add(meritial);
                            }
                        }
                        meritialAdapter = new ArrayAdapter<Meritial>(registration.this, R.layout.spinner_layout, R.id.spinnerTextViewSampleId, meritialList);
                        MaritialSpinner.setAdapter(meritialAdapter);
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

    private void GetData(String procedureName) {
        try {
            JSONObject json = new JSONObject();
            TRequest tRequest = new TRequest();
            tRequest.setSp(procedureName);
            tRequest.setDb(getString(R.string.DB_PatientCare));
            List<TParam> tParamList = new ArrayList<TParam>();
            Gson gson = new Gson();
            json = new JSONObject(gson.toJson(tRequest, TRequest.class));
            Log.v(Values.ApiGetDataParam, json.toString());
            if (procedureName == "GET_M_NATIONALITIES") {
                Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiGetData, json, nationalitiesListener(), genericErrorListener()));
            } else if (procedureName == "GET_M_RELIGION") {
                Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiGetData, json, religionListener(), genericErrorListener()));
            } else if (procedureName == "GET_M_MERITIAL") {
                Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiGetData, json, maritialListener(), genericErrorListener()));
            }
            //   Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Method.POST, getString(R.string.api_v1_get_data), json, loginListener(), genericErrorListener()));

        } catch (Exception ex) {
            Log.v("Fail..", ex.getMessage());
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String GetDate(String stringDate) {
        DateFormat inputFormat = new SimpleDateFormat("dd/mm/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("DD-MMM-YY");
        String inputDateStr = stringDate;
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        if (awesomeValidation.validate()) {
            Insert_T74046();
            Insert_T74M01();
            Toast.makeText(getApplicationContext(),"Register successfully",Toast.LENGTH_SHORT).show();

        }
    }

    private void Insert_T74046()
    {
        //Toast.makeText(this, "Registration Successfull", Toast.LENGTH_LONG).show();

        //process the data further
        try {
            JSONObject json = new JSONObject();
            TRequest tRequest = new TRequest();
            tRequest.setSp("T74046_M_INSERT");
            tRequest.setDb(getString(R.string.DB_PatientCare));
            List<TParam> tParamList = new ArrayList<TParam>();
            tParamList.add(new TParam("@P_FIRST_LANG2_NAME", ((EditText) findViewById(R.id.EditTextFirstNameId)).getText().toString()));
            tParamList.add(new TParam("@P_FAMILY_LANG2_NAME", ((EditText) findViewById(R.id.EditTextLastNameId)).getText().toString()));
            String birthDate = GetDate(birthDateEditText.getText().toString());
            tParamList.add(new TParam("@P_BIRTH_DATE", birthDate));
            tParamList.add(new TParam("@P_EMAIL_ID", ((EditText) findViewById(R.id.editTextEmailId)).getText().toString()));
            tParamList.add(new TParam("@P_NTNLTY_ID", nationalityCode));
            tParamList.add(new TParam("@P_MOBILE_NO", ((EditText) findViewById(R.id.editTextMobileNoId)).getText().toString()));
            Religion religion =(Religion) ReligionSpinner.getSelectedItem();
            String T_RLGN_CODE = religion.getId();
            tParamList.add(new TParam("@P_RLGN_CODE",T_RLGN_CODE));
            tParamList.add(new TParam("@P_SEX_CODE",sex_Status_Code));
            Meritial maritial =(Meritial) MaritialSpinner.getSelectedItem();
            String T_MRTL_STATUS_CODE = maritial.getId();
            tParamList.add(new TParam("@P_MRTL_STATUS",T_MRTL_STATUS_CODE));
            tRequest.setDict(tParamList);
            Gson gson = new Gson();
            json = new JSONObject(gson.toJson(tRequest, TRequest.class));
            Log.v(Values.ApiSetData,json.toString());
            //Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, registrationListener(), genericErrorListener()));
            Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, registrationListener(),genericErrorListener()));
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
    private void Insert_T74M01()
    {
        String password = GetPassword();
        try {
            JSONObject json = new JSONObject();
            TRequest tRequest = new TRequest();
            tRequest.setSp("T74M01_M_INSERT");
            tRequest.setDb(getString(R.string.DB_PatientCare));
            List<TParam> tParamList = new ArrayList<TParam>();
            tParamList.add(new TParam("@P_IS_ACTIVE", "1"));
            tParamList.add(new TParam("@P_USER_TYPE", "Patient"));
            tParamList.add(new TParam("@P_PASSWORD", password));
            tRequest.setDict(tParamList);
            Gson gson = new Gson();
            json = new JSONObject(gson.toJson(tRequest, TRequest.class));
            Log.v(Values.ApiSetData, json.toString());
            //Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, registrationListener(), genericErrorListener()));
            Tapplication.getInstance().addToRequestQueue(new JsonObjectRequest(Request.Method.POST, Values.ApiSetData, json, registrationListener(), genericErrorListener()));
        } catch (Exception ex) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Log.v("Fail..", ex.getMessage());
            Toast.makeText(getApplicationContext(),
                    ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    public class Nationality {

        private String T_NTNLTY_CODE;
        private String T_NTNLTY_NAME;
        private String T_PHONE_CODE;

        public Nationality(String id, String name, String code) {
            this.T_NTNLTY_CODE = id;
            this.T_NTNLTY_NAME = name;
            this.T_PHONE_CODE = code;
        }


        public String getId() {
            return T_NTNLTY_CODE;
        }

        public void setId(String id) {
            this.T_NTNLTY_CODE = id;
        }

        public String getName() {
            return T_NTNLTY_NAME;
        }

        public void setName(String name) {
            this.T_NTNLTY_NAME = name;
        }

        public String getCode() {
            return T_PHONE_CODE;
        }

        public void setCode(String code) {
            this.T_PHONE_CODE = code;
        }

        //to display object as a string in spinner
        @Override
        public String toString() {
            return T_NTNLTY_NAME;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Nationality) {
                Nationality c = (Nationality) obj;
                if (c.getName().equals(T_NTNLTY_NAME) && c.getId() == T_NTNLTY_CODE && c.getCode() == T_PHONE_CODE)
                    return true;
            }

            return false;
        }

    }

    public class Religion {

        private String T_RLGN_CODE;
        private String T_RLGN_NAME;

        public Religion(String id, String name) {
            this.T_RLGN_CODE = id;
            this.T_RLGN_NAME = name;
        }


        public String getId() {
            return T_RLGN_CODE;
        }

        public void setId(String id) {
            this.T_RLGN_CODE = id;
        }

        public String getName() {
            return T_RLGN_NAME;
        }

        public void setName(String name) {
            this.T_RLGN_NAME = name;
        }


        //to display object as a string in spinner
        @Override
        public String toString() {
            return T_RLGN_NAME;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Nationality) {
                Nationality c = (Nationality) obj;
                if (c.getName().equals(T_RLGN_CODE) && c.getId() == T_RLGN_NAME) return true;
            }

            return false;
        }

    }

    public class Meritial {

        private String T_MRTL_STATUS_CODE;
        private String T_MRTL_STATUS_NAME;

        public Meritial(String id, String name) {
            this.T_MRTL_STATUS_CODE = id;
            this.T_MRTL_STATUS_NAME = name;
        }


        public String getId() {
            return T_MRTL_STATUS_CODE;
        }

        public void setId(String id) {
            this.T_MRTL_STATUS_CODE = id;
        }

        public String getName() {
            return T_MRTL_STATUS_NAME;
        }

        public void setName(String name) {
            this.T_MRTL_STATUS_NAME = name;
        }


        //to display object as a string in spinner
        @Override
        public String toString() {
            return T_MRTL_STATUS_NAME;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Meritial) {
                Meritial c = (Meritial) obj;
                if (c.getName().equals(T_MRTL_STATUS_CODE) && c.getId() == T_MRTL_STATUS_NAME)
                    return true;
            }

            return false;
        }

    }

    private Response.Listener<JSONObject> registrationListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Log.d(data.toString(), "onResponse: ");
                    if (data.length() > 0) {
                        Toast.makeText(getApplicationContext(),
                                "Save data successfully",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Invalid Credentials",
                                Toast.LENGTH_LONG).show();
                        //((TextView) findViewById(R.id.tv_login_status)).setText("Invalid Credentials");
                    }

                } catch (Exception e) {
                    Log.v("mango", e.getMessage());
                    //((TextView) findViewById(R.id.tv_login_status)).setText(e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener genericErrorListener() {
        return new Response.ErrorListener() {

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

    private String GetUserName(String firstName, String lastName) {
        // Declare variables
        Scanner sc = new Scanner(System.in);
        String first, last, username;

        // Get input
        first = firstName;
        last = lastName;

        // Create random generator
        Random generator = new Random();
        int randomNumber = generator.nextInt(90) + 10;

        // Generate username
        username = first.charAt(0) + last.substring(0, 5) + randomNumber;
        Log.v(null, username);
        String password = GetPassword();
        Log.v(null, password);
        return username;
    }

    private String GetPassword() {
        String password = generateRandomPassword(5, true, true, true, false);
        return password;
    }

    static String generateRandomPassword(int max_length, boolean upperCase, boolean lowerCase, boolean numbers, boolean specialCharacters) {
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String numberChars = "0123456789";
        String specialChars = "!@#$%^&*()_-+=<>?/{}~|";
        String allowedChars = "";

        Random rn = new Random();
        StringBuilder sb = new StringBuilder(max_length);

        //this will fulfill the requirements of atleast one character of a type.
        if (upperCase) {
            allowedChars += upperCaseChars;
            sb.append(upperCaseChars.charAt(rn.nextInt(upperCaseChars.length() - 1)));
        }

        if (lowerCase) {
            allowedChars += lowerCaseChars;
            sb.append(lowerCaseChars.charAt(rn.nextInt(lowerCaseChars.length() - 1)));
        }

        if (numbers) {
            allowedChars += numberChars;
            sb.append(numberChars.charAt(rn.nextInt(numberChars.length() - 1)));
        }

        if (specialCharacters) {
            allowedChars += specialChars;
            sb.append(specialChars.charAt(rn.nextInt(specialChars.length() - 1)));
        }


        //fill the allowed length from different chars now.
        for (int i = sb.length(); i < max_length; ++i) {
            sb.append(allowedChars.charAt(rn.nextInt(allowedChars.length())));
        }

        return sb.toString();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}


