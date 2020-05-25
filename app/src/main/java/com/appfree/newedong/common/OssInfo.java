package com.appfree.newedong.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OssInfo {

    private static OssInfo INSTANCE = null ;

    @SerializedName("keyId")
    @Expose
    public String OS_IMAGE_KEY_ID = "";

    @SerializedName("keySecret")
    @Expose
    public String OS_IMAGE_KEY_SECRET = "";

    @SerializedName("bucket")
    @Expose
    public String OS_BUCKET_NAME = "";

    @SerializedName("url")
    @Expose
    public String OS_IMAGE_ENDPOINT = "";

    public static OssInfo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OssInfo();
        }
        return(INSTANCE);
    }


    public OssInfo() {
    }

    public OssInfo(String OS_IMAGE_KEY_ID, String OS_IMAGE_KEY_SECRET, String OS_BUCKET_NAME, String OS_IMAGE_ENDPOINT) {
        this.OS_IMAGE_KEY_ID = OS_IMAGE_KEY_ID;
        this.OS_IMAGE_KEY_SECRET = OS_IMAGE_KEY_SECRET;
        this.OS_BUCKET_NAME = OS_BUCKET_NAME;
        this.OS_IMAGE_ENDPOINT = OS_IMAGE_ENDPOINT;
    }

    public String getOS_IMAGE_KEY_ID() {
        return OS_IMAGE_KEY_ID;
    }

    public void setOS_IMAGE_KEY_ID(String OS_IMAGE_KEY_ID) {
        this.OS_IMAGE_KEY_ID = OS_IMAGE_KEY_ID;
    }

    public String getOS_IMAGE_KEY_SECRET() {
        return OS_IMAGE_KEY_SECRET;
    }

    public void setOS_IMAGE_KEY_SECRET(String OS_IMAGE_KEY_SECRET) {
        this.OS_IMAGE_KEY_SECRET = OS_IMAGE_KEY_SECRET;
    }

    public String getOS_BUCKET_NAME() {
        return OS_BUCKET_NAME;
    }

    public void setOS_BUCKET_NAME(String OS_BUCKET_NAME) {
        this.OS_BUCKET_NAME = OS_BUCKET_NAME;
    }

    public String getOS_IMAGE_ENDPOINT() {
        return OS_IMAGE_ENDPOINT;
    }

    public void setOS_IMAGE_ENDPOINT(String OS_IMAGE_ENDPOINT) {
        this.OS_IMAGE_ENDPOINT = OS_IMAGE_ENDPOINT;
    }

    @Override
    public String toString() {
        return "OssInfo{" +
                "OS_IMAGE_KEY_ID='" + OS_IMAGE_KEY_ID + '\'' +
                ", OS_IMAGE_KEY_SECRET='" + OS_IMAGE_KEY_SECRET + '\'' +
                ", OS_BUCKET_NAME='" + OS_BUCKET_NAME + '\'' +
                ", OS_IMAGE_ENDPOINT='" + OS_IMAGE_ENDPOINT + '\'' +
                '}';
    }
}
