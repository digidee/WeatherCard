package com.shortcut.weather.weathercard;

/**
 * Created by digi on 26.02.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shortcut.weather.weathercard.POJO.Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context mcon;

    List<Model> mItems;

    SharedPreferences SP;
    boolean imperial;
    String type = "metric", fOrC = "°C";

    public CardAdapter(Context mcon) {
        super();
        this.mcon = mcon;
        mItems = new ArrayList<Model>();
        //Get Preferences Data
        SP = PreferenceManager.getDefaultSharedPreferences(mcon);
    }

    public void addData(Model city) {
        mItems.add(city);
        notifyDataSetChanged();
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final Model city = mItems.get(i);
        viewHolder.city.setText(Math.round(city.getMain().getTemp()) + mode() + " in " + city.getName().toString() + ", " + city.getSys().getCountry().toString());


        String imgURL = "http://openweathermap.org/img/w/";
        Picasso.with(mcon).load(imgURL + city.getWeather().get(0).getIcon().toString() + ".png").resize(200, 200).into(viewHolder.weather);


        viewHolder.temp.setText(city.getWeather().get(0).getMain());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mI = new Intent(mcon, DetailedWeather.class);
                mI.putExtra("city", city.getName().toString());
                mcon.startActivity(mI);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView city;
        public ImageView weather;
        public TextView temp;

        public ViewHolder(View itemView) {
            super(itemView);
            city = (TextView) itemView.findViewById(R.id.city);
            weather = (ImageView) itemView.findViewById(R.id.iv_weather);
            temp = (TextView) itemView.findViewById(R.id.temp);
        }
    }

    public String mode() {
        imperial = SP.getBoolean("imperial", false);
        if (imperial) {

            fOrC = "°F";
        } else {
            fOrC = "°C";
        }
        return fOrC;
    }


}