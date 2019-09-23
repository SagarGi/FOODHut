package com.example.pravinewa.foodhut;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref{

    //this java code is specially is used for the management of the night or day theme of our android application
    SharedPreferences mySharedPref;

    public SharedPref(Context context){
        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }


    public void setLightModeState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("LightMode",state);
        editor.commit();
    }

    public Boolean loadLightModeState(){
        Boolean state = mySharedPref.getBoolean("LightMode",false);
        return state;
    }

    public void setDarkModeState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("DarkMode",state);
        editor.commit();
    }

    public Boolean loadDarkModeState(){
        Boolean state = mySharedPref.getBoolean("DarkMode",false);
        return state;
    }
}
