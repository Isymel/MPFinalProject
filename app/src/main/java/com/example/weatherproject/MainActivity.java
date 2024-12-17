package com.example.weatherproject;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);

    SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyyMMdd");
    String getDate = simpleDateFormatDate.format(mDate);

    SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH");
    String getTime = simpleDateFormatTime.format(mDate)+"00";

    WeatherInformation wi = new WeatherInformation();
    String weatherInfor;
    {
        try {
            weatherInfor = wi.searchWeather(getDate, getTime);
        } catch (IOException e) {
            Log.i("THREE_ERROR1", e.getMessage());
        } catch (JSONException e) {
            Log.i("THREE_ERROR2", e.getMessage());
        }
    }

    String[] weatherInforArray = weatherInfor.split(" ");
}