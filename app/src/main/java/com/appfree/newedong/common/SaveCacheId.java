package com.appfree.newedong.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SaveCacheId {

    public static SharedPreferences sharedPreferences;

    public static final String INIT_ID_OLD = "INIT_ID_OLD";
    public static final String INIT_ID_NEW = "INIT_ID_NEW";

    private SaveCacheId() {

    }

    public static void init(Context context) {
        if(sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences("SaveCacheId", Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        if (sharedPreferences != null){
            return sharedPreferences.getString(key, defValue);
        }
        return null;
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }


}
