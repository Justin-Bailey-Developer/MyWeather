package com.weatherpack.myweatherv2.ui.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.weatherpack.myweatherv2.BuildConfig;
import com.weatherpack.myweatherv2.R;
import com.weatherpack.myweatherv2.ui.main.model.FiveDayForecast;
import com.weatherpack.myweatherv2.ui.main.retrofit.IAccuWeather;
import com.weatherpack.myweatherv2.ui.main.retrofit.RetrofitClient;

import java.io.IOException;
import java.net.URL;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A fragment representing a list of Items.
 */
public class DailyForecastsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView weatherDataList;

    //retrofit (RxJava)
    private IAccuWeather weatherService;
    private CompositeDisposable compositeDisposable;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DailyForecastsFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofit();
        weatherService = retrofit.create(IAccuWeather.class);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DailyForecastsFragment newInstance(int columnCount) {
        DailyForecastsFragment fragment = new DailyForecastsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            weatherDataList = recyclerView;
            /*URL url = NetworkUtil.buildURLForWeather();
            new FetchWeatherData().execute(url);*/

            // Make the call using Retrofit and RxJava
            compositeDisposable.add(weatherService.getFiveDayForecast
                    (
                            "305605",
                            BuildConfig.ACCUWEATHER_API_KEY,
                            true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<FiveDayForecast>()
                    {
                        @Override
                        public void accept(FiveDayForecast forecast) throws Exception {
                            displayData(forecast);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            Log.d("MYERROR", "accept: " + throwable.getMessage());
                        }
                    }));

                        /*URL url = NetworkUtil.buildURLForWeather();
                        new FetchWeatherData().execute(url);*/

            //recyclerView.setAdapter(new MyDailyForecastsRecyclerViewAdapter(DummyContent.ITEMS));
        }
        return view;
    }

    /**
     * Display the data from the FiveDayForcast by creating a view adapter
     * and setting it onto the RecyclerView.
     * @param fiveDayForecast The forecast to display.
     *
     */
    private void displayData(FiveDayForecast fiveDayForecast) {
        weatherDataList.setAdapter(new MyDailyForecastsRecyclerViewAdapter(
                                        fiveDayForecast.getDailyForecasts()));
    }



    /**
     * Asynchronous task that requests weather data.
     *//*
    private class FetchWeatherData extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL weatherURL = urls[0];
            String weatherData = null;

            try {
                weatherData = NetworkUtil.getResponseFromHttpUrl(weatherURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("doInBackground", ": " + weatherData);
            return weatherData;
        }

        @Override
        protected void onPostExecute(String weatherData) {

            if (weatherData != null) {
                consumeJson(weatherData);
            }
            super.onPostExecute(weatherData);
        }

        protected void consumeJson(String weatherJSON) {
            if (weatherJSON != null) {
                Gson gson = new Gson();
                FiveDayForecast weatherData = gson.fromJson(weatherJSON, FiveDayForecast.class);
                weatherDataList.setAdapter(new MyDailyForecastsRecyclerViewAdapter(
                        weatherData.getDailyForecasts()));
            }
        }

    }*/
}