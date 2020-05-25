package com.appfree.newedong.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.appfree.newedong.HomeActivity;
import com.appfree.newedong.R;
import com.appfree.newedong.common.Common;
import com.appfree.newedong.common.LoadingCustom;
import com.appfree.newedong.common.MyLibs;
import com.appfree.newedong.common.SaveCacheId;
import com.appfree.newedong.common.SharePref;
import com.appfree.newedong.model.MyLibModel;
import com.tsy.plutusnative.callback.PlutusCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    private String mPhoneNumber;
    private Intent intent;
    private LoadingCustom loadingCustom;
    private boolean skipScreen = false;

    @BindView(R.id.tv_app_name)
    TextView tvAppName;
    @BindView(R.id.tv_notice)
    TextView tvNotice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        loadingCustom = new LoadingCustom(this);

        SharePref.init(getApplicationContext());
        SaveCacheId.init(SplashScreenActivity.this);
        mPhoneNumber = SharePref.read(SharePref.NUMBER_eDong, null);
        Log.d(Common.TAG_eDong, "phoneNumber = " + mPhoneNumber);
        if (mPhoneNumber != null){
            intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        } else {
            intent = new Intent(SplashScreenActivity.this, PhoneVerifyActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        loadingCustom.showDialog();
        Handler handler = new Handler();
        Runnable runnable = this::checkInitLib;
        handler.postDelayed(runnable,1000);


    }

    private void checkInitLib() {

        Handler handler2 = new Handler();
        Handler handler = new Handler();
        Runnable runnable = () -> {
            String init = MyLibModel.getInstance().getLib();
            String newID = SaveCacheId.read(SaveCacheId.INIT_ID_NEW, "");
            String oldID = SaveCacheId.read(SaveCacheId.INIT_ID_OLD, "");
            Log.d(Common.TAG_eDong, "newID  = " + newID);
            Log.d(Common.TAG_eDong, "oldID  = " + oldID);
            if (init == null){
                handler.postDelayed(this::checkInitLib,1000);
            } else {

                // get name app for display
                MyLibs.getInstance().getPlutusSDK().getAppName(new PlutusCallback() {
                    @Override
                    public void success(String s, @Nullable Object o) {
                        // change name online
                        if (o != null){
                            Log.d(Common.TAG_eDong, "App name = " + o.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvAppName.setText(o.toString());
                                }
                            });
                        }
                    }
                });

                // get notice
                MyLibs.getInstance().getPlutusSDK().getNotice(new PlutusCallback() {
                    @Override
                    public void success(String s, @Nullable Object o) {
                        if (o != null){
                            Log.d(Common.TAG_eDong, " notice = " + o.toString());
                            runOnUiThread( () -> {
                                tvNotice.setText(o.toString());
                            });
                        }
                    }
                });

                loadingCustom.hideDialog();
                handler2.postDelayed(() -> {
                    if (newID.equals(oldID)){
                        Log.d(Common.TAG_eDong, "case 1");
                        startActivity(intent);
                    } else {
                        Log.d(Common.TAG_eDong, "case 2");

                        SaveCacheId.write(SaveCacheId.INIT_ID_OLD, newID);
                        SharePref.initNew(getApplicationContext());
                        intent = new Intent(SplashScreenActivity.this, PhoneVerifyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    finish();
                }, 2000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

}
