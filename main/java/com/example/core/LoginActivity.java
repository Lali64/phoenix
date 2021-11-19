package com.example.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vpos.apipackage.PosApiHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

        CardView cardview, cardView1, cardView2, cardView3;
//    SharedPref sharedPref;
    TextView staff_name;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cardview = (CardView) findViewById(R.id.card);
        cardView1 = (CardView) findViewById(R.id.card1);
        cardView2 = (CardView) findViewById(R.id.card2);
        cardView3 = (CardView) findViewById(R.id.card3);
        staff_name = (TextView) findViewById(R.id.part);

        cardview.setOnClickListener(this);
        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);

            if (!SharedPref.getInstance(this).isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

            //getting logged in staff_id
            String loggedStaff = SharedPref.getInstance(this).LoggedInUser();
            staff_name.setText(loggedStaff);


    }

    @Override
    public void onClick(View v) {

        Intent i;

        switch (v.getId()) {
            case R.id.card:
                i = new Intent(this, TicketActivity.class);
                startActivity(i);

                break;

            case R.id.card1:
                i = new Intent(this, ChairActivity.class);
                startActivity(i);

                break;

            case R.id.card2:
                i = new Intent(this, PermitActivity.class);
                startActivity(i);
                break;


        }


        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.out:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(this, "logged out ", Toast.LENGTH_SHORT).show();
                SharedPref.getInstance(getApplicationContext()).logout();
                break;


            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        //set Power ON
        PosApiHelper.getInstance().SysSetPower(1);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PosApiHelper.getInstance().SysSetPower(0);


    }


}

