package com.appfree.newedong.common;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;

import com.appfree.newedong.model.MyLibModel;
import com.tsy.plutusnative.PlutusSDK;
import com.tsy.plutusnative.callback.PlutusCallback;


public class MyLibs extends Application {

    private static MyLibs myLibs;
    private static PlutusSDK plutusSDK;
    private MyLibModel myLibModel;

    @Override
    public void onCreate() {
        super.onCreate();


        myLibs = this;
        plutusSDK = new PlutusSDK(getApplicationContext());
        plutusSDK.initialize("ScQyRYej1EyUJoCDHI5MqKIAOSzavnd2",
                false,
                new PlutusCallback() {
                    @Override
                    public void success(String s, @Nullable Object o) {
                        if (o != null) {
                            MyLibModel.getInstance().setLib(o.toString());
                            String initialize = o.toString();
                            SaveCacheId.write(SaveCacheId.INIT_ID_NEW, initialize);
                            Log.d(Common.TAG_eDong, "initialize = " + o.toString());
                        }

                    }
                });
    }

    public static synchronized MyLibs getInstance() {
        return myLibs;
    }

    public PlutusSDK getPlutusSDK() {

        return plutusSDK;
    }


}
