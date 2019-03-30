package com.example.stepcounter.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelWeather {

    @SerializedName("consolidated_weather")
    private List<ModelConsolidatedWeather> weatherDtails;
    private String time;
    private String sun_rise;
    private String sun_set;
    private Parent parent;
    private List<Sources> sources;
    private String title;
    private String location_type;
    private String latt_long;
    private String timezone;
    private long woeid;

    public ModelWeather() {

    }

    public ModelWeather(List<ModelConsolidatedWeather> weatherDtails, String time, String sun_rise, String sun_set, Parent parent, List<Sources> sources, String title, String location_type, String latt_long, String timezone, long woeid) {
        this.weatherDtails = weatherDtails;
        this.time = time;
        this.sun_rise = sun_rise;
        this.sun_set = sun_set;
        this.parent = parent;
        this.sources = sources;
        this.title = title;
        this.location_type = location_type;
        this.latt_long = latt_long;
        this.timezone = timezone;
        this.woeid = woeid;
    }

    public List<ModelConsolidatedWeather> getWeatherDtails() {
        return weatherDtails;
    }

    public void setWeatherDtails(List<ModelConsolidatedWeather> weatherDtails) {
        this.weatherDtails = weatherDtails;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSun_rise() {
        return sun_rise;
    }

    public void setSun_rise(String sun_rise) {
        this.sun_rise = sun_rise;
    }

    public String getSun_set() {
        return sun_set;
    }

    public void setSun_set(String sun_set) {
        this.sun_set = sun_set;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public List<Sources> getSources() {
        return sources;
    }

    public void setSources(List<Sources> sources) {
        this.sources = sources;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public long getWoeid() {
        return woeid;
    }

    public void setWoeid(long woeid) {
        this.woeid = woeid;
    }
}
