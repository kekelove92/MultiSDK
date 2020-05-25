package com.appfree.newedong.model;

public class ContactModel implements Comparable<ContactModel> {
    private String mName;

    private String mNumber;

    public ContactModel(String mName, String mNumber) {
        this.mName = mName;
        this.mNumber = mNumber;
    }

    public ContactModel() {
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    @Override
    public int compareTo(ContactModel o) {
        return this.mName.compareTo(o.mName);
    }
}
