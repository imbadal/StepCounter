package com.example.stepcounter.retrofit;

import com.example.stepcounter.model.ModelCity;
import com.example.stepcounter.model.ModelSearch;
import com.example.stepcounter.model.ModelWeather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface WeatherClient {

    @GET("location/search/")
    Call<List<ModelCity>> getCity(@Query("lattlong") String lattlong);

    @GET("location/search/")
    Call<List<ModelSearch>> searchCity(@Query("query") String city);

    @GET("location/{woeid}")
    Call<ModelWeather> getWeather(@Path("woeid") long wid);


}
