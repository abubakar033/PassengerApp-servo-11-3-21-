package com.junaid_condition_sets;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreference {

    public static Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor et;

    //shared Preferences NAMES
    //app_local_data

    public SharedPreference(Context context, String name){

        this.context = context;
        sp = context.getSharedPreferences(name,MODE_PRIVATE);
        et = sp.edit();
    }


    public void saveBooleanValue(String key, boolean value){et.putBoolean(key,value).apply();}
    public boolean getBooleanValue(String key){return sp.getBoolean(key,false);}


    public void saveIntValue(String key, int value){et.putInt(key,value).apply();}
    public int getIntValue(String key){return sp.getInt(key,0);}

    public void saveStringValue(String key, String value){
        et.putString(key,value).apply();
    }

    public String getStringValue(String key){
        return sp.getString(key,"");
    }

    public void removeValue(String key){et.remove(key).apply();}

    public SharedPreferences getSp(){
        return sp;
    }

    public SharedPreferences.Editor getEt(){
        return et;
    }

    public void clearAll(){et.clear().apply();}

    public boolean containKey(String key){
        if(sp.contains(key)){ return true;}
        else {return false;}
    }



}
