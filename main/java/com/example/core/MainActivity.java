package com.example.core;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vpos.apipackage.PosApiHelper;

import static com.example.core.SharedPref.SHARED_PREF_NAME;

public class MainActivity extends AppCompatActivity  {


    EditText staffid_input,password_input;
    Button btnLogin;

    final String loginURL = "http://192.168.137.1/revcom/customer_login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        staffid_input = findViewById(R.id.user);
        password_input = findViewById(R.id.Pass);
        btnLogin = findViewById(R.id.log);

        btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            validateUserData();
        }
    });

}

    private void validateUserData() {

        //first getting the values
        final String staff_id = staffid_input.getText().toString();
        final String password = password_input.getText().toString();

        //checking if email is empty
        if (TextUtils.isEmpty(staff_id)) {
            staffid_input.setError("Please enter your staff_id");
            staffid_input.requestFocus();
            // Vibrate for 100 milliseconds

            btnLogin.setEnabled(true);
            return;
        }
        //checking if password is empty
        if (TextUtils.isEmpty(password)) {
            password_input.setError("Please enter your password");
            password_input.requestFocus();
            //Vibrate for 100 milliseconds

            btnLogin.setEnabled(true);
            return;
        }
        //validating email


        //Login User if everything is fine
        loginUser();


    }

    private void loginUser() {


        //first getting the values
        final String staff_id = staffid_input.getText().toString();
        final String password = password_input.getText().toString();

        System.out.println("staff_id:"+staff_id);
        System.out.println("password:"+password);

        //Call our volley library

        StringRequest stringRequest = new StringRequest(Request.Method.POST,loginURL,
                new Response.Listener<String>() {

//                    private Object String;

                    @Override
                    public void onResponse(String response) {
                        try {
//
                            System.out.println("Response:"+response);
//                            System.out.println("String:"+String);
                            JSONObject obj = new JSONObject(response);
                            String output = obj.getString(("error"));

                            System.out.println("obj:"+obj);

                            if (output.equals("true")){
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "successful", Toast.LENGTH_SHORT).show();

                                String staff_name = obj.getString("staff_name");
                                SharedPref.getInstance(getApplicationContext()).storeUserName(staff_name);

                            }else if(output.equals("false")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();



                            }


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Try again in a minute Server Offline", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("staff_id", staff_id);
                params.put("password", password);

                return params;
            }
        };
        VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }

}





