package com.app.rishi.yelp.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.rishi.yelp.Model.ReservationClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by HGS on 12/11/2015.
 */
public class Preference {

    private static final String FILE_NAME = "Yelp.pref";

    private static Preference mInstance = null;

    public static Preference getInstance() {
        if (null == mInstance) {
            mInstance = new Preference();
        }
        return mInstance;
    }

    public void putReservations(Context context, String key, ArrayList<ReservationClass> logEntries){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(logEntries);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<ReservationClass> getReservations(Context context, String key){

        ArrayList<ReservationClass> logEntries = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(key, "");

        if (json.isEmpty()){

            logEntries = new ArrayList<ReservationClass>();

        }else {

            Type type =  new TypeToken<ArrayList<ReservationClass>>(){}.getType();
            logEntries = gson.fromJson(json, type);
        }
        return logEntries;
    }

    public void put(Context context, String key, String value){


        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void put(Context context, String key, boolean value){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(Context context, String key, int value){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public void put(Context context, String key, long value){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);
        editor.commit();
    }

    public String getValue(Context context, String key, String defaultValue){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        try{
            return pref.getString(key, defaultValue);
        }catch (Exception e){

            return defaultValue;
        }
    }

    public int getValue(Context context, String key, int defaultValue){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        try{
            return pref.getInt(key, defaultValue);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public boolean getValue(Context context, String key, boolean defaultValue){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        try{
            return pref.getBoolean(key, defaultValue);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public long getValue(Context context, String key, long defaultValue){

        SharedPreferences pref = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

        try{
            return pref.getLong(key, defaultValue);
        }catch (Exception e){
            return defaultValue;
        }
    }
}
