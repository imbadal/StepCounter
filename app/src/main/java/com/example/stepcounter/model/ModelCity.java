package com.example.stepcounter.model;

public class ModelCity {

    private long distance;
    private long woeid;
    private String title;
    private String location_type;
    private String latt_long;

    public ModelCity() {

    }

    public ModelCity(long distance, long woeid, String title, String location_type, String latt_long) {
        this.distance = distance;
        this.woeid = woeid;
        this.title = title;
        this.location_type = location_type;
        this.latt_long = latt_long;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getWoeid() {
        return woeid;
    }

    public void setWoeid(long woeid) {
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
}
