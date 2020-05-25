package com.appfree.newedong.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.appfree.newedong.R;

import dmax.dialog.SpotsDialog;

public class LoadingCustom {
    Activity activity;
    AlertDialog dialog;

    public LoadingCustom(Activity activity) {
        this.activity = activity;
    }

    public void showDialog(){

        dialog = new SpotsDialog.Builder()
                .setContext(activity)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
//                .setTheme(R.style.Custom)
                .build();
        dialog.show();

    }

    public void hideDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public boolean isShow(){
        if (dialog != null && dialog.isShowing()){
            return true;
        }
        return false;
    }
}
