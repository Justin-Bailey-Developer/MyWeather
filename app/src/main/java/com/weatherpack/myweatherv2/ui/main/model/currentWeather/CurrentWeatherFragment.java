package com.weatherpack.myweatherv2.ui.main.model.currentWeather;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weatherpack.myweatherv2.BuildConfig;
import com.weatherpack.myweatherv2.R;
import com.weatherpack.myweatherv2.ui.main.retrofit.IAccuWeather;
import com.weatherpack.myweatherv2.ui.main.retrofit.RetrofitClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentWeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    //retrofit (RxJava)
    private IAccuWeather weatherService;
    private CompositeDisposable compositeDisposable;

    private TextView tv_weatherText;
    private TextView tv_temperatureValue;
    private List<CurrentWeather> currentWeatherList;

    public CurrentWeatherFragment() {
        // Required empty public constructor

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofit();
        weatherService = retrofit.create(IAccuWeather.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentWeatherFragment newInstance(String param1, String param2) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        tv_weatherText = view.findViewById(R.id.tv_weatherText);
        tv_temperatureValue = view.findViewById(R.id.tv_temperatureValue);


        // Make the call using Retrofit and RxJava
        compositeDisposable.add(weatherService.getCurrentConditions
                (
                        "305605",
                        BuildConfig.ACCUWEATHER_API_KEY
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CurrentWeather>>() {
                    @Override
                    public void accept(List<CurrentWeather> currentWeatherList) throws Exception {
                        displayData(currentWeatherList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("MYERROR", "accept: " + throwable.getMessage());
                    }
                }));

        return view;
    }


    private void displayData(List<CurrentWeather> currentWeatherList) {

        CurrentWeather today = currentWeatherList.get(0);
        String temp = Double.toString(today.getTemperature()
                .getMetric().getValue());

        tv_weatherText.setText(currentWeatherList.get(0).getWeatherText());
        tv_temperatureValue.setText(temp + " C");
    }
}

