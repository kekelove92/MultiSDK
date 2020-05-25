package com.appfree.newedong.model;

import java.util.ArrayList;
import java.util.List;

public class DayModel {
    private String day;
    private List<Double> listFee;

    public DayModel() {
    }

    public DayModel(String day, List<Double> listFee) {
        this.day = day;
        this.listFee = listFee;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Double> getListFee() {
        return listFee;
    }

    public void setListFee(List<Double> listFee) {
        this.listFee = listFee;
    }
}
