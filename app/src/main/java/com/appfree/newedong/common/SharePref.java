package com.appfree.newedong.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharePref {


    public static SharedPreferences sharedPreferences;
    public static SharePref sharePref;
    public static final String NUMBER_eDong = "NUMBER";
    public static final String MD5_PHONE_eDong = "MD5_PHONE";
    public static final String MD5_USER_ID_eDong = "MD5_USER_ID";
    public static final String USER_ID_eDong = "USER_ID";
    public static final String NAME_USER_eDong = "NAME_USER";

    public static final String DONE_STEP_1_eDong = "DONE_STEP_1";
    public static final String DONE_STEP_2_eDong = "DONE_STEP_2";
    public static final String DONE_STEP_3_eDong = "DONE_STEP_3";
    public static final String FIRST_FINISH_STEP_1 = "FIRST_FINISH_STEP_1";
    public static final String FIRST_FINISH_STEP_2 = "FIRST_FINISH_STEP_2";
    public static final String FIRST_FINISH_STEP_3 = "FIRST_FINISH_STEP_3";
    public static final String DONE = "DONE";
    public static final String CMND_eDong = "CMND";
    public static final String VERIFY_ID_eDong = "VERIFY_ID";
    public static final String VERIFY_VIDEO_eDong = "VERIFY_VIDEO";
    public static final String VERIFY_REPAY_BILL_eDong = "VERIFY_REPAY_BILL";

    public static final String LOAN_MONEY_eDong = "LOAN_MONEY";
    public static final String LOAN_INTEREST_eDong = "LOAN_INTEREST";
    public static final String LOAN_FEE_eDong = "LOAN_FEE";
    public static final String LOAN_ACTURAL_RECEIVED_eDong = "LOAN_ACTURAL_RECEIVED";
    public static final String LOAN_DAY_eDong = "LOAN_DAY";
    public static final String LOAN_FINAL_VALUE_eDong = "LOAN_FINAL_VALUE";
    public static final String SHOW_TUTORIAL_eDong = "SHOW_TUTORIAL";


    private SharePref() {

    }

    public static void init(Context context) {
        if (sharedPreferences == null)
            Log.d(Common.TAG_eDong, "init SharePref");
            sharedPreferences = context.getSharedPreferences("SharePref", Activity.MODE_PRIVATE);
    }

    public static void initNew(Context context) {
        if (sharedPreferences != null){
            Log.d(Common.TAG_eDong, "Cleare and init New SharePref");
            sharedPreferences = context.getSharedPreferences("SharePref", Activity.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }

    }

    public static String read(String key, String defValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defValue);
        }
        return null;
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static int readInt(String key, int defValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, defValue);
        }
        return 0;
    }

    public static void writeInt(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

}
