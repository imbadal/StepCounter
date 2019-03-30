package com.example.stepcounter.model;

public class Parent {
    private String title;
    private String location_type;
    private String latt_long;
    private long woeid;

    public Parent() {

    }

    public Parent(String title, String location_type, String latt_long, long woeid) {
        this.title = title;
        this.location_type = location_type;
        this.latt_long = latt_long;
        this.woeid = woeid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation_type() {
        return location_type;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }

    public String getLatt_long() {
        return latt_long;
    }

    public void setLatt_long(String latt_long) {
        this.latt_long = latt_long;
    }

    public long getWoeid() {
        return woeid;
    }

    public void setWoeid(long woeid) {
        this.woeid = woeid;
    }
}
