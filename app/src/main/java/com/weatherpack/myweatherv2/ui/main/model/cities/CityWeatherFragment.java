package com.weatherpack.myweatherv2.ui.main.model.cities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.weatherpack.myweatherv2.BuildConfig;
import com.weatherpack.myweatherv2.R;
import com.weatherpack.myweatherv2.ui.main.model.currentWeather.CurrentWeather;
import com.weatherpack.myweatherv2.ui.main.model.location.AccuWeatherLocation;
import com.weatherpack.myweatherv2.ui.main.retrofit.IAccuWeather;
import com.weatherpack.myweatherv2.ui.main.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityWeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HashMap<String, String> citiesHashMap;

    TextView temperature, description;
    MaterialSearchBar materialSearchBar;

    //retrofit (RxJava)
    private IAccuWeather weatherService;
    private CompositeDisposable compositeDisposable;

    public CityWeatherFragment() {

        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofit();
        weatherService = retrofit.create(IAccuWeather.class);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CityWeatherFragment newInstance(String param1, String param2) {
        CityWeatherFragment fragment = new CityWeatherFragment();
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

        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_city_weather, container, false);

        materialSearchBar = view.findViewById(R.id.sb_city_name);
        temperature = view.findViewById(R.id.tv_temperatureValue);
        description = view.findViewById(R.id.tv_weatherText);

        // Make the call using Retrofit and RxJava
        compositeDisposable.add(weatherService.getTopCities
                (
                        BuildConfig.ACCUWEATHER_API_KEY
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TopCities>>() {
                    @Override
                    public void accept(List<TopCities> topCitiesList) throws Exception {
                        displayData(topCitiesList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("MYERROR", "accept: " + throwable.getMessage());
                    }
                }));

        return view;
    }

    private void populateSearchData(List<TopCities> topCities) {
        citiesHashMap.clear();
        for (TopCities city : topCities) {
            citiesHashMap.put(city.getLocalizedName(), city.getKey());
        }
        
        materialSearchBar.setEnabled(true);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String city : citiesHashMap.keySet()) {
                    if (city.toLowerCase().contains(
                            materialSearchBar.getText().toLowerCase()))
                        suggest.add(city);
                }
                Collections.sort(suggest);
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            /*@Override
            public void afterTextChanged(Editable editable) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {
            }*/
        });
        materialSearchBar.setOnSearchActionListener(
                new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                    }
                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        Log.d("Search:", text + "");
                        getWeatherByCityName(text.toString());
                    }
                    @Override
                    public void onButtonClicked(int buttonCode) {
                    }
                });
        ArrayList suggestionsList = new ArrayList(citiesHashMap.keySet());
        Collections.sort(suggestionsList);
        materialSearchBar.setLastSuggestions(suggestionsList);
    }

    private void getWeatherByCityName(String toString) {

    }

    private void displayData(List<TopCities> topCitiesList) {

//        TopCities city = topCitiesList.get(0);
//        String temp = Double.toString(city.getTemperature()
//                .getMetric().getValue());

//        tv_weatherText.setText(currentWeatherList.get(0).getWeatherText());
//        tv_temperatureValue.setText(temp + " C");
    }

}