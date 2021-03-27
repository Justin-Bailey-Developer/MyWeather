package com.weatherpack.myweatherv2.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weatherpack.myweatherv2.ui.main.model.DailyForecasts;
import com.weatherpack.myweatherv2.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDailyForecastsRecyclerViewAdapter extends RecyclerView.Adapter<MyDailyForecastsRecyclerViewAdapter.ViewHolder> {

    private final List<DailyForecasts> mValues;

    public MyDailyForecastsRecyclerViewAdapter(List<DailyForecasts> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        DailyForecasts item = mValues.get(position);

        holder.mItem = item;
        holder.tvDate.setText(item.getDate().substring(0, 10));
        holder.tvMinimum.setText(item.getTemperature().getMinimum().getValue() +
                " " + item.getTemperature().getMinimum().getUnit());
        holder.tvMaximum.setText(item.getTemperature().getMaximum().getValue() +
                " " + item.getTemperature().getMaximum().getUnit());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView tvDate;
        public final TextView tvMinimum;
        public final TextView tvMaximum;
        public DailyForecasts mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvMinimum = (TextView) view.findViewById(R.id.tv_minimum);
            tvMaximum = (TextView) view.findViewById(R.id.tv_maximum);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvDate.getText() + "'";
        }
    }

}