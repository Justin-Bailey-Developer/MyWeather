package com.weatherpack.myweatherv2.ui.main.model;

import com.weatherpack.myweatherv2.ui.main.model.DailyForecasts;
import com.weatherpack.myweatherv2.ui.main.model.Headline;

import java.util.List;

public class FiveDayForecast
{
    private com.weatherpack.myweatherv2.ui.main.model.Headline Headline;
    private List<com.weatherpack.myweatherv2.ui.main.model.DailyForecasts> DailyForecasts;

    public void setHeadline(Headline Headline){
        this.Headline = Headline;
    }
    public Headline getHeadline(){
        return this.Headline;
    }

    public void setDailyForecasts(List<DailyForecasts> DailyForecasts){
        this.DailyForecasts = DailyForecasts;
    }
    public List<DailyForecasts> getDailyForecasts(){
        return this.DailyForecasts;
    }
}