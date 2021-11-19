package com.example.core;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vpos.apipackage.PosApiHelper;

public class TicketActivity extends AppCompatActivity {

    Spinner spinner;
    Dialog dialog;
    ArrayList<String> customerList = new ArrayList<>();
    ArrayAdapter<String> customerAdapter;
    RequestQueue requestQueue;
    ListView listView;
    TextView textView, textView1;
    EditText editText, editText1;

    String park_id, division_id;
    RelativeLayout relativeLayout, relativeLayout2;

    EditText reg_name, reg_contact;
    Button button;

    //    final int act_type = '2';
    final String RegisterURL = "http://192.168.137.1/revcom/cus_register.php";
    String url = "http://192.168.137.1/revcom/customer_name.php";
    final String WalkURL = "http://192.168.137.1/revcom/exist_registration.php";
    final String URL = "http://192.168.137.1/revcom/walk_registration.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        relativeLayout = (RelativeLayout)findViewById(R.id.linearinvisible);
        relativeLayout2 = (RelativeLayout)findViewById(R.id.invisible);
        spinner = findViewById(R.id.customIconSpinner);
        textView = findViewById(R.id.Textsearch);
        textView1 = findViewById(R.id.act_type);

        button = findViewById(R.id.log);
        listView = findViewById(R.id.listsearch);
        editText = findViewById(R.id.editsearch);
        editText1 = findViewById(R.id.editquantity);

        requestQueue = Volley.newRequestQueue(this);

        reg_name = findViewById(R.id.user);
        reg_contact = findViewById(R.id.contact);


        String [] customer = {"Customer Category", "New Customer", "Existing Customer", "Walk_In Customer"};
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, customer);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                if (parent.getItemAtPosition(position).equals("New Customer"))
                {
                    switch (position){
                        case 1:
                            relativeLayout.setVisibility(View.VISIBLE);
                            relativeLayout2.setVisibility(View.INVISIBLE);

                    }


                }

                if (parent.getItemAtPosition(position).equals("Existing Customer"))
                {
                    switch (position){
                        case 2:
                            relativeLayout2.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.INVISIBLE);

                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog = new Dialog(TicketActivity.this);
                                    dialog.setContentView(R.layout.dialog_spinner);
                                    dialog.getWindow().setLayout(650, 800);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    dialog.show();

                                    final EditText editText = dialog.findViewById(R.id.editsearch);
                                    final ListView listView = dialog.findViewById(R.id.listsearch);

//                                    final String quantity = editText1.getText().toString();
//                                    final String act_type = chairText.getText().toString();

                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                    try {
                                                        JSONArray jsonArray = response.getJSONArray("tbl_customer");
                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                            String customer_name = jsonObject.optString("customer_name");

                                                            customerList.add(customer_name);
                                                            customerAdapter = new ArrayAdapter<>(TicketActivity.this, android.R.layout.simple_list_item_1, customerList);
                                                            customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            listView.setAdapter(customerAdapter);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }) {

                                    };

                                    requestQueue.add(jsonObjectRequest);

                                    editText.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            customerAdapter.getFilter().filter(s);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            textView.setText(customerAdapter.getItem(position));
                                            dialog.dismiss();

                                        }

                                    });
                                }
                            });

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customer();
                                }

                                private void customer() {
                                    final String quantity = editText1.getText().toString();
                                    final String act_type = textView1.getText().toString();
//                                    final String staff_id = stafftext.getText().toString();
                                    final String customer_name = textView.getText().toString();

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, WalkURL,
                                            new Response.Listener<String>() {


                                                @Override
                                                public void onResponse(String response) {

                                                    try {
                                                        System.out.println("response" +response);

                                                        JSONObject obj = new JSONObject(response);
                                                        String outputt = obj.getString("error");
                                                        JSONArray jsonArray = obj.getJSONArray("data");

                                                        System.out.println("output:" +outputt);
                                                        System.out.println("obj:" +obj);

                                                        if (outputt.equals("false")) {
                                                            for (int i = 0; i < jsonArray.length(); i++){
//                                                                JSONObject object = jsonArray.getJSONObject(i);
                                                                String date = jsonArray.getJSONObject(i).getString("date");
                                                                String request_id = jsonArray.getJSONObject(i).getString("request_id");
                                                                String receipt_id = jsonArray.getJSONObject(i).getString("receipt_id");
                                                                String customer_name = jsonArray.getJSONObject(i).getString("customer_name");
                                                                String number_group = jsonArray.getJSONObject(i).getString("number_group");
                                                                String details = jsonArray.getJSONObject(i).getString("details");
                                                                String quantity = jsonArray.getJSONObject(i).getString("quantity");
                                                                String amount = jsonArray.getJSONObject(i).getString("amount");
                                                                String bill_due = jsonArray.getJSONObject(i).getString("bill_due");
                                                                String rate = jsonArray.getJSONObject(i).getString("rate");

                                                                Intent intent = new Intent(TicketActivity.this, PrintActivity.class);
                                                                intent.putExtra("date", date);
                                                                intent.putExtra("request_id", request_id);
                                                                intent.putExtra("receipt_id", receipt_id);
                                                                intent.putExtra("customer_name", customer_name);
                                                                intent.putExtra("number_group", number_group);
                                                                intent.putExtra("details", details);
                                                                intent.putExtra("quantity", quantity);
                                                                intent.putExtra("amount", amount);
                                                                intent.putExtra("bill_due", bill_due);
                                                                intent.putExtra("rate", rate);
                                                                startActivity(intent);
                                                                finish();

                                                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                                            }

                                                        } else {


                                                            Toast.makeText(getApplicationContext(), "name exists", Toast.LENGTH_SHORT).show();


//                                                            SharedPref.getInstance(getApplicationContext()).storeStaff(staff_id);


                                                            //storing the customer_name in shared preferences
                                                            SharedPref.getInstance(getApplicationContext()).saveName( customer_name);
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            Toast.makeText(getApplicationContext(), "Try again Server Offline", Toast.LENGTH_SHORT).show();
                                            error.printStackTrace();

                                        }
                                    }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("customer_name", customer_name);
                                            params.put("quantity", quantity);
                                            params.put("activity_type", act_type);

                                            return params;
                                        }
                                    };
                                    VolleySingleton.getInstance(TicketActivity.this).addToRequestQueue(stringRequest);
                                }

                            });

                    }

                }

                if (parent.getItemAtPosition(position).equals("Walk_In Customer"))
                {
                    switch (position){
                        case 3:
                            relativeLayout2.setVisibility(View.INVISIBLE);
                            relativeLayout.setVisibility(View.INVISIBLE);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    customer();
                                }

                                private void customer() {
                                    final String quantity = editText1.getText().toString();
                                    final String act_type = textView1.getText().toString();


                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                                            new Response.Listener<String>() {


                                                @Override
                                                public void onResponse(String response) {

                                                    try {
                                                        System.out.println("response" +response);

                                                        JSONObject obj = new JSONObject(response);
                                                        String outputt = obj.getString("error");
                                                        JSONArray jsonArray = obj.getJSONArray("data");

                                                        System.out.println("output:" +outputt);
                                                        System.out.println("obj:" +obj);

                                                        if (outputt.equals("false")) {
                                                            for (int i = 0; i < jsonArray.length(); i++){
//                                                                JSONObject object = jsonArray.getJSONObject(i);
                                                                String date = jsonArray.getJSONObject(i).getString("date");
                                                                String request_id = jsonArray.getJSONObject(i).getString("request_id");
                                                                String receipt_id = jsonArray.getJSONObject(i).getString("receipt_id");
                                                                String customer_name = jsonArray.getJSONObject(i).getString("customer_name");
                                                                String number_group = jsonArray.getJSONObject(i).getString("number_group");
                                                                String details = jsonArray.getJSONObject(i).getString("details");
                                                                String quantity = jsonArray.getJSONObject(i).getString("quantity");
                                                                String amount = jsonArray.getJSONObject(i).getString("amount");
                                                                String bill_due = jsonArray.getJSONObject(i).getString("bill_due");
                                                                String rate = jsonArray.getJSONObject(i).getString("rate");

                                                                Intent intent = new Intent(TicketActivity.this, PrintActivity.class);
                                                                intent.putExtra("date", date);
                                                                intent.putExtra("request_id", request_id);
                                                                intent.putExtra("receipt_id", receipt_id);
                                                                intent.putExtra("customer_name", customer_name);
                                                                intent.putExtra("number_group", number_group);
                                                                intent.putExtra("details", details);
                                                                intent.putExtra("quantity", quantity);
                                                                intent.putExtra("amount", amount);
                                                                intent.putExtra("bill_due", bill_due);
                                                                intent.putExtra("rate", rate);
                                                                startActivity(intent);
                                                                finish();

                                                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                                            }

                                                        } else {


                                                            Toast.makeText(getApplicationContext(), "name exists", Toast.LENGTH_SHORT).show();
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                            Toast.makeText(getApplicationContext(), "Try again Server Offline", Toast.LENGTH_SHORT).show();
                                            error.printStackTrace();

                                        }
                                    }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();

                                            params.put("quantity", quantity);
                                            params.put("activity_type", act_type);

                                            return params;
                                        }
                                    };
                                    VolleySingleton.getInstance(TicketActivity.this).addToRequestQueue(stringRequest);
                                }

                            });

                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserData();
            }
            private void validateUserData() {

                final String customer_name = reg_name.getText().toString();
                final String customer_contact = reg_contact.getText().toString();

                if (TextUtils.isEmpty(customer_name)) {
                    reg_name.setError("Please enter customer_name");
                    reg_name.requestFocus();

                    return;
                }

                if (TextUtils.isEmpty(customer_contact)) {
                    reg_contact.setError("Please enter contact");
                    reg_contact.requestFocus();
                    return;
                }
                Customer();
            }

            private void Customer() {
                final String customer_name = reg_name.getText().toString();
                final String customer_contact = reg_contact.getText().toString();
                final String quantity = editText1.getText().toString();
                final String act_type = textView1.getText().toString();

                System.out.println("name" + customer_name);
                System.out.println("contact" + customer_contact);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, RegisterURL,
                        new Response.Listener<String>() {


                            @Override
                            public void onResponse(String response) {

                                try {
                                    System.out.println("response" +response);

                                    JSONObject obj = new JSONObject(response);
                                    String outputt = obj.getString("error");
                                    JSONArray jsonArray = obj.getJSONArray("data");

                                    System.out.println("outputt:" +outputt);
                                    System.out.println("obj:" +obj);

                                    if (outputt.equals("false")) {
                                        for (int i = 0; i < jsonArray.length(); i++){
//                                                                JSONObject object = jsonArray.getJSONObject(i);
                                            String date = jsonArray.getJSONObject(i).getString("date");
                                            String request_id = jsonArray.getJSONObject(i).getString("request_id");
                                            String receipt_id = jsonArray.getJSONObject(i).getString("receipt_id");
                                            String customer_name = jsonArray.getJSONObject(i).getString("customer_name");
                                            String number_group = jsonArray.getJSONObject(i).getString("number_group");
                                            String details = jsonArray.getJSONObject(i).getString("details");
                                            String quantity = jsonArray.getJSONObject(i).getString("quantity");
                                            String amount = jsonArray.getJSONObject(i).getString("amount");
                                            String bill_due = jsonArray.getJSONObject(i).getString("bill_due");
                                            String rate = jsonArray.getJSONObject(i).getString("rate");

                                            Intent intent = new Intent(TicketActivity.this, PrintActivity.class);
                                            intent.putExtra("date", date);
                                            intent.putExtra("request_id", request_id);
                                            intent.putExtra("receipt_id", receipt_id);
                                            intent.putExtra("customer_name", customer_name);
                                            intent.putExtra("number_group", number_group);
                                            intent.putExtra("details", details);
                                            intent.putExtra("quantity", quantity);
                                            intent.putExtra("amount", amount);
                                            intent.putExtra("bill_due", bill_due);
                                            intent.putExtra("rate", rate);
                                            startActivity(intent);
                                            finish();

                                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                        }

                                    } else {


                                        Toast.makeText(getApplicationContext(), "name exists", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Try again Server Offline", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("customer_name", customer_name);
                        params.put("customer_contact", customer_contact);
                        params.put("quantity", quantity);
                        params.put("activity_type", act_type);

                        return params;
                    }
                };
                VolleySingleton.getInstance(TicketActivity.this).addToRequestQueue(stringRequest);
            }
        });
    }

}

