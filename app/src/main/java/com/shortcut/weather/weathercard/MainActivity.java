package com.shortcut.weather.weathercard;
/**
 * Created by digi on 26.02.2016.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shortcut.weather.weathercard.POJO.Model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "CardViewActivity";
    private RecyclerView mRecyclerView;
    private CardAdapter mCardAdapter;
    private WeatherService service;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences SP;
    boolean imperial;
    String type = "metric";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(android.R.style.Animation_Toast); //Fade animation
        setContentView(R.layout.activity_main);

        //Get Preferences Data
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mode();
        /**
         * Setting up CardView/RecycleView
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mCardAdapter = new CardAdapter(this);
        mRecyclerView.setAdapter(mCardAdapter);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(WeatherService.class);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createCityList();
            }
        });

        createCityList();
    }

    public String mode() {
        imperial = SP.getBoolean("imperial", false);
        if (imperial) type = "imperial";
        else type = "metric";
        return type;
    }


    public void createCityList() {
        mCardAdapter.clear();

        //Multithreaded often returns HTTP429 - too bad :/
        for (String city : Cities.citiList) {
            service.getWeatherReport(city, WeatherService.APPID, mode())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Model>() {
                        @Override
                        public final void onCompleted() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public final void onError(Throwable e) {
                            Log.e("City", e.getMessage());
                        }

                        @Override
                        public final void onNext(Model response) {
                            mCardAdapter.addData(response);
                        }
                    });
        }

/*        Call<List<Model>> call2 = service.getAllMetricList();
        call2.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                mSwipeRefreshLayout.setRefreshing(false);
               // mCardAdapter.addData(response.body());
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {

            }
        });*/

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
                createCityList();
                return true;

            case R.id.refresh:
                Toast.makeText(this, "Refreshing",
                        Toast.LENGTH_SHORT).show();
                createCityList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
