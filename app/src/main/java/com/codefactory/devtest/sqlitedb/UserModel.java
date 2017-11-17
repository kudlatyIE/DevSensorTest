package com.codefactory.devtest.sqlitedb;

/**
 * Created by kudlaty on 13/10/2016.
 */

public class UserModel {
    private String date, time;
    private double bio, gest;
    private boolean result;



    @Override
    public String toString() {
        return date + ", " + time + ", " + bio + ", " + gest + ", " + result;
    }

    public UserModel(String date, String time, double bio, double gest,	boolean result) {
        this.date = date;
        this.time = time;
        this.bio = bio;
        this.gest = gest;
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getBio() {
        return bio;
    }

    public double getGest() {
        return gest;
    }

    public boolean isResult() {
        return result;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBio(double bio) {
        this.bio = bio;
    }

    public void setGest(double gest) {
        this.gest = gest;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
