package com.appfree.newedong.common;

import com.tsy.plutusnative.model.UserInfoResult;

public class CustomInfo {

   public  static CustomInfo customInfo;
   public static UserInfoResult userInfoResult;


    public static CustomInfo getInstance() {
        if (customInfo == null) {
            customInfo = new CustomInfo();
        }
        return (customInfo);
    }

}
