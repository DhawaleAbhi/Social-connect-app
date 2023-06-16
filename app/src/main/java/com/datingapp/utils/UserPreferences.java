package com.datingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserPreferences {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public UserPreferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constant.USER,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setCredentials(String phone){
        editor.putString(Constant.phoneNumber,phone);
        editor.commit();
    }
    public void setName(String name){
        editor.putString(Constant.NAME,name);
        editor.commit();
    }


    public HashMap<String, String> getData(){
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.phoneNumber,preferences.getString(Constant.phoneNumber,null));
        map.put(Constant.NAME,preferences.getString(Constant.NAME,null));
        return map;
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }
}
