package com.example.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import static android.provider.Contacts.SettingsColumns.KEY;

public class SharedPref {
    //Storage File
    public static final String SHARED_PREF_NAME = "mypreferences";
    private static final String KEY_STAFF_NAME = "staff_name";
    private static final String CUSTOMER_KEY = "customer_name";
    private static final String KEY_STAFF_ID = "staff_id";

    public static SharedPref mInstance;

    public static Context mCtx;


    public SharedPref(Context context) {
        mCtx = context;
    }


    public static synchronized SharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPref(context);
        }
        return mInstance;
    }



    //method to store user data

    public void storeUserName(String staff_name) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STAFF_NAME, staff_name);
        editor.commit();
    }

    //check if user is logged in
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STAFF_NAME, null) != null;
    }

    //find logged in user
    public String LoggedInUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STAFF_NAME, "");

    }

    public void storeStaff(String staff_id) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STAFF_ID, staff_id);
        editor.commit();
    }

//    public String LoggedInStaff() {
//        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(KEY_STAFF_ID, "");
//
//    }


    public void saveName(String customer_name) {
        SharedPreferences.Editor editor = mCtx.getSharedPreferences(KEY + customer_name, Context.MODE_PRIVATE).edit();
        editor.putString(CUSTOMER_KEY, customer_name);
        editor.commit();
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }


}


