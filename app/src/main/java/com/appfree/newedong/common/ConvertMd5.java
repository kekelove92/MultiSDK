package com.appfree.newedong.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConvertMd5 {
    public ConvertMd5() {
    }

    public static String convertByteToHex(byte[] data) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xff & data[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // get MD5 code userID + phone
    public static String getMD5(String input, String typeConvert) {
        try {
            switch (typeConvert) {
                case Common.MD5_TYPE_PHONE:
                    input = input + Common.ANDROID_TYPE + Common.MD5_KEY_eDong;
                    break;
                case Common.MD5_TYPE_USER_ID:
                    input = input + Common.MD5_KEY_eDong;
                    break;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // get MD5 code repay bill
    public static String getRepayBillMd5(String imageRepayName, String userId) {
        String input = imageRepayName + userId + Common.MD5_KEY_eDong;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
