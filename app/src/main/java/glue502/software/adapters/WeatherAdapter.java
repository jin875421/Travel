package glue502.software.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.search.weather.WeatherSearchForecasts;

import java.util.List;

import glue502.software.R;

public class WeatherAdapter extends ArrayAdapter<WeatherSearchForecasts> {
    public WeatherAdapter(Context context, List<WeatherSearchForecasts> forecasts) {
        super(context, 0, forecasts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, parent, false);
        }

        WeatherSearchForecasts forecast = getItem(position);
        TextView tvDate = convertView.findViewById(R.id.tv_date);
        TextView tvWeather = convertView.findViewById(R.id.tv_weather);
        TextView tvTemperature = convertView.findViewById(R.id.tv_temperature);

        tvDate.setText(forecast.getDate());
        tvWeather.setText(forecast.getPhenomenonDay());
        tvTemperature.setText(forecast.getHighestTemp() + "℃~" + forecast.getLowestTemp()+"℃");

        return convertView;
    }
}
