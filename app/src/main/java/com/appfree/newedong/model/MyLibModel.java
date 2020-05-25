package com.appfree.newedong.model;

public class MyLibModel {
    private static MyLibModel mInstance= null;


    protected MyLibModel(){}

    public static synchronized MyLibModel getInstance() {
        if(null == mInstance){
            mInstance = new MyLibModel();
        }
        return mInstance;
    }

    private String lib;

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }
}
