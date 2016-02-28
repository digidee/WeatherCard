package com.shortcut.weather.weathercard;

/**
 * Created by digi on 26.02.2016.
 */


import com.shortcut.weather.weathercard.POJO.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface WeatherService {
    String SERVICE_ENDPOINT = "http://api.openweathermap.org";
    String APPID = "347873d6b85f422d90c399b008d51be8";

    @GET("/data/2.5/weather")
    Call<Model> getWeatherDetailed(@Query("q") String city, @Query("appid") String appid, @Query("units") String units);

    @GET("/data/2.5/weather")
    Observable<Model> getWeatherReport(@Query("q") String city, @Query("appid") String appid, @Query("units") String units);


    //http://api.openweathermap.org/data/2.5/group?id=3143244,3137115,3161733,3133880&appid=347873d6b85f422d90c399b008d51be8&units=metric


    @GET("/data/2.5/group?id=3143244,3137115,3161733,3133880&appid=347873d6b85f422d90c399b008d51be8&units=metric")
    Call<Model> getAllMetric();


    @GET("/data/2.5/weather?id=3143244&appid=347873d6b85f422d90c399b008d51be8&units=metric")
    Call<Model> getOsloMetric();

    //http://api.openweathermap.org/data/2.5/group?id=3143244,3137115,3161733,3133880&appid=347873d6b85f422d90c399b008d51be8&units=imperial
    @GET("/data/2.5/group?id=3143244,3137115,3161733,3133880&appid=347873d6b85f422d90c399b008d51be8&units=imperial")
    Call<Model> getAllImperial();


    @GET("/data/2.5/group?id=3143244,3137115,3161733,3133880&appid=347873d6b85f422d90c399b008d51be8&units=metric")
    Call<List<Model>> getAllMetricList();

}
