package com.appfree.newedong.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.appfree.newedong.R;

import dmax.dialog.SpotsDialog;

public class ShowCustomDialog {
    Activity activity;
    AlertDialog alertDialog;

    public ShowCustomDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog(String message, boolean isCancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.alert_dialog);
        builder.setMessage(message);
        builder.setCancelable(isCancelable);
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void hideDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public boolean isShow() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return true;
        }
        return false;
    }
}
