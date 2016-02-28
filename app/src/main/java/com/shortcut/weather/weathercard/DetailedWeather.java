package com.shortcut.weather.weathercard;

/**
 * Created by digi on 27.02.2016.
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shortcut.weather.weathercard.POJO.Model;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailedWeather extends AppCompatActivity {

    private WeatherService service;
    private static String LOG_TAG = "CardViewActivity";
    String url = "http://api.openweathermap.org",
            cityName = "cityName",
            imgURL = "http://openweathermap.org/img/w/";
    TextView tCity, tTemp, tWind,tWind2, tCloud, tPressure, tHumidity, tSunrise, tSunset, tCoords, tDesc, tUpdated;
    ImageView iWeather;
    String weather = "01d";
    Context mCon;
    SwipeRefreshLayout mSwipeRefreshLayout;

    SharedPreferences SP;
    boolean imperial;
    String type = "metric", fOrC="°C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(android.R.style.Animation_Toast); //Fade animation
        setContentView(R.layout.activity_detailed);

        //Get Preferences Data
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mode();

        tCity = (TextView) findViewById(R.id.tv_city);
        iWeather = (ImageView) findViewById(R.id.iv_weather);
        tTemp = (TextView) findViewById(R.id.tv_temp);

        tWind = (TextView) findViewById(R.id.tv_wind);
        tWind2 = (TextView) findViewById(R.id.tv_wind2);

        tCloud = (TextView) findViewById(R.id.tv_cloud);
        tPressure = (TextView) findViewById(R.id.tv_pressure);

        tHumidity = (TextView) findViewById(R.id.tv_humidity);

        tSunrise = (TextView) findViewById(R.id.tv_sunrise);
        tSunset = (TextView) findViewById(R.id.tv_sunset);
        tCoords = (TextView) findViewById(R.id.tv_coords);

        tDesc = (TextView) findViewById(R.id.tv_desc);
        tUpdated = (TextView) findViewById(R.id.tv_updated);

        Intent mI = getIntent();
        cityName = mI.getStringExtra("city");

        mCon = this;

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_detailed_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDetailedWeather();
            }
        });


        getDetailedWeather();

        iWeather = (ImageView) findViewById(R.id.iv_weather);


    }

    public String mode() {
        imperial = SP.getBoolean("imperial", false);
        if (imperial){
            type = "imperial";
            fOrC = "°F";
        }
        else {
            type = "metric";
            fOrC = "°C";
        }
        return type;
    }


    public void getDetailedWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherService.class);

        Call<Model> call = service.getWeatherDetailed(cityName, WeatherService.APPID, mode());


        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.isSuccess()) {
                    String city = response.body().getName().toString() + ", " + response.body().getSys().getCountry().toString();
                    weather = response.body().getWeather().get(0).getIcon().toString();
                    Double temp = response.body().getMain().getTemp();

                    String wind = "Speed "+response.body().getWind().getSpeed().toString();
                    String wind2 = "Deg  " + response.body().getWind().getDeg().toString();
                    String cloud = response.body().getWeather().get(0).getDescription().toString();
                    String pressure = response.body().getMain().getPressure().toString() + " hpa";

                    String humidity = response.body().getMain().getHumidity().toString();

                    String sunrise = response.body().getSys().getSunrise().toString();
                    String sunset = response.body().getSys().getSunset().toString();
                    String coords = "[ " + response.body().getCoord().getLon() + ", " + response.body().getCoord().getLat() + " ]";

                    String desc = response.body().getWeather().get(0).getMain();

                    tCity.setText(city);

                    tTemp.setText(Math.round(temp) + fOrC);

                    tWind.setText(wind);
                    tWind2.setText(wind2);
                    tCloud.setText(cloud);
                    tPressure.setText(pressure);

                    tHumidity.setText(humidity);

                    tSunrise.setText(sunrise);
                    tSunset.setText(sunset);
                    tCoords.setText(coords);

                    tDesc.setText(desc);

                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    tUpdated.setText("get at " + sdf.format(date));

                    Picasso.with(mCon).load(imgURL + weather + ".png").resize(200, 200).into(iWeather);
                }

            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imperial_settings:
                SharedPreferences.Editor editor = SP.edit();
                if (imperial) {
                    Toast.makeText(this, "Celsius",
                            Toast.LENGTH_SHORT).show();

                    editor.putBoolean("imperial", false);
                } else {

                    Toast.makeText(this, "Fahrenheit",
                            Toast.LENGTH_SHORT).show();

                    editor.putBoolean("imperial", true);
                }
                editor.commit();
                getDetailedWeather();
                return true;

            case R.id.refresh:
                Toast.makeText(this, "Refreshing",
                        Toast.LENGTH_SHORT).show();
                getDetailedWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}