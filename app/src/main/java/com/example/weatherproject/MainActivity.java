package com.example.weatherproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Logger;

class weatherInfor{
    LocalDateTime mDate = LocalDateTime.now();

    int todayOfWeek = mDate.getDayOfWeek().getValue();

    public weatherInfor(){
        init();
    }

    public weatherInfor(int targetDayOfWeek) {
        this.mDate = mDate.minusDays(todayOfWeek - targetDayOfWeek);
        Log.i("Alert", "ChangedDate: "+mDate);
    }

    DateTimeFormatter formatDatePrint = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    String getPrintDate = mDate.format(formatDatePrint);
    DateTimeFormatter formatHourPrint = DateTimeFormatter.ofPattern("HH");
    String getPrintTime = mDate.format(formatHourPrint);

    void init() {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        String getDate = mDate.format(formatDate);

        DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH00");
        String getTime = mDate.format(formatHour);

        WeatherInformation wi = new WeatherInformation();
        String weatherInfo = null;
        {
            try {
                Log.i("Alert", getDate);
                Log.i("Alert", getTime);
                weatherInfo = wi.searchWeather(getDate, getTime);
                Log.i("weatherInfoData", weatherInfo);
            } catch (IOException e) {
                Log.i("THREE_ERROR1", e.getMessage());
            } catch (JSONException e) {
                Log.i("THREE_ERROR2", e.getMessage());
            }
        }
        String[] weatherInforArray = weatherInfo.split(" ");

        this.rainPercent = weatherInforArray[0];
        this.rainState = weatherInforArray[1];
        this.cloudState = weatherInforArray[2];
        this.curTemperature = weatherInforArray[3];
        this.minTemperature = weatherInforArray[4];
        this.maxTemperature = weatherInforArray[5];
        this.windSpeed = weatherInforArray[6];
    }

    String rainPercent;
    String rainState;
    String cloudState;
    String curTemperature;
    String minTemperature;
    String maxTemperature;
    String windSpeed;

    public String showRainState(){
        if(this.rainState.equals("rainy"))
            return "비";
        else if(this.rainState.equals("rainNSnowy"))
            return "비 또는 눈";
        else if(this.rainState.equals("snowy"))
            return "눈";
        else if(this.rainState.equals("shower"))
            return "소나기";
        else
            return " ";
    }

    public String showCloudState(){
        if(this.cloudState.equals("perfectClear"))
            return "맑음";
        else if(this.cloudState.equals("veryClear"))
            return "대부분 맑음";
        else if(this.cloudState.equals("aLittleClear"))
            return "조금 흐림";
        else if(this.cloudState.equals("cloudy"))
            return "흐림";
        else return null;
    }

    public void applyWeatherIcon(ImageView iv){
        if(this.rainState.equals("rainy"))
            iv.setImageResource(R.drawable.rainy);
        else if(this.rainState.equals("rainNSnowy"))
            iv.setImageResource(R.drawable.rainnsnowy);
        else if(this.rainState.equals("snowy"))
            iv.setImageResource(R.drawable.snowy);
        else if(this.rainState.equals("shower"))
            iv.setImageResource(R.drawable.shower);
        else if(this.rainState.equals("noRain")){
            if(this.cloudState.equals("perfectClear"))
                iv.setImageResource(R.drawable.perfectclear);
            else if(this.cloudState.equals("veryClear"))
                iv.setImageResource(R.drawable.vclear);
            else if(this.cloudState.equals("aLittleClear"))
                iv.setImageResource(R.drawable.alittleclear);
            else if(this.cloudState.equals("cloudy"))
                iv.setImageResource(R.drawable.cloudy);
        }
    }
}

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

        ImageView weatherIcon = findViewById(R.id.weatherNow);
        TextView selectedDate = findViewById(R.id.dateInfor);
        TextView rainInfo = findViewById(R.id.rainInfo);
        TextView curTemperature = findViewById(R.id.curTemperature);
        TextView windSpeed = findViewById(R.id.windSpeed);
        TextView maxTemperature = findViewById(R.id.maxTemperature);
        TextView minTemperature = findViewById(R.id.minTemperature);

        int todayOfWeekC = LocalDate.now().getDayOfWeek().getValue();
        switch (todayOfWeekC){
            case 1:
                findViewById(R.id.mon).setBackgroundColor(Color.RED);
                break;
            case 2:
                findViewById(R.id.tue).setBackgroundColor(Color.RED);
                break;
            case 3:
                findViewById(R.id.wed).setBackgroundColor(Color.RED);
                break;
            case 4:
                findViewById(R.id.thu).setBackgroundColor(Color.RED);
                break;
            case 5:
                findViewById(R.id.fri).setBackgroundColor(Color.RED);
                break;
            case 6:
                findViewById(R.id.sat).setBackgroundColor(Color.RED);
                break;
            case 7:
                findViewById(R.id.sun).setBackgroundColor(Color.RED);
                break;
        }

        HandlerThread handlerThread = new HandlerThread("mThread");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                weatherInfor todayInfo = new weatherInfor();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        todayInfo.applyWeatherIcon(weatherIcon);
                        selectedDate.setText(todayInfo.getPrintDate + " / " + todayInfo.getPrintTime + "시");
                        rainInfo.setText(todayInfo.showRainState() + "  " + todayInfo.rainPercent);
                        curTemperature.setText(todayInfo.curTemperature);
                        windSpeed.setText(todayInfo.windSpeed);
                        maxTemperature.setText(todayInfo.maxTemperature);
                        minTemperature.setText(todayInfo.minTemperature);
                    }
                });
            }
        });

        Button buttonMon = findViewById(R.id.mon);
        buttonMon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor monInfo = new weatherInfor(1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                monInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(monInfo.getPrintDate + " / " + monInfo.getPrintTime + "시");
                                rainInfo.setText(monInfo.showRainState() + "  " + monInfo.rainPercent);
                                curTemperature.setText(monInfo.curTemperature);
                                windSpeed.setText(monInfo.windSpeed);
                                maxTemperature.setText(monInfo.maxTemperature);
                                minTemperature.setText(monInfo.minTemperature);
                            }
                        });
                    }
                });

            }
        });

        Button buttonTue = findViewById(R.id.tue);
        buttonTue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                @Override
                public void run() {
                    weatherInfor tueInfo = new weatherInfor(2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Alert", "Operate");
                            tueInfo.applyWeatherIcon(weatherIcon);
                            selectedDate.setText(tueInfo.getPrintDate + " / " + tueInfo.getPrintTime + "시");
                            rainInfo.setText(tueInfo.showRainState() + "  " + tueInfo.rainPercent);
                            curTemperature.setText(tueInfo.curTemperature);
                            windSpeed.setText(tueInfo.windSpeed);
                            maxTemperature.setText(tueInfo.maxTemperature);
                            minTemperature.setText(tueInfo.minTemperature);
                        }
                    });
                }
            });
            }
        });

        Button buttonWed = findViewById(R.id.wed);
        buttonWed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor wedInfo = new weatherInfor(3);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wedInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(wedInfo.getPrintDate + " / " + wedInfo.getPrintTime + "시");
                                rainInfo.setText(wedInfo.showRainState() + "  " + wedInfo.rainPercent);
                                curTemperature.setText(wedInfo.curTemperature);
                                windSpeed.setText(wedInfo.windSpeed);
                                maxTemperature.setText(wedInfo.maxTemperature);
                                minTemperature.setText(wedInfo.minTemperature);
                            }
                        });
                    }
                });
            }
        });

        Button buttonThu = findViewById(R.id.thu);
        buttonThu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor thuInfo = new weatherInfor(4);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                thuInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(thuInfo.getPrintDate + " / " + thuInfo.getPrintTime + "시");
                                rainInfo.setText(thuInfo.showRainState() + "  " + thuInfo.rainPercent);
                                curTemperature.setText(thuInfo.curTemperature);
                                windSpeed.setText(thuInfo.windSpeed);
                                maxTemperature.setText(thuInfo.maxTemperature);
                                minTemperature.setText(thuInfo.minTemperature);
                            }
                        });
                    }
                });
            }
        });

        Button buttonFri = findViewById(R.id.fri);
        buttonFri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor friInfo = new weatherInfor(5);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(friInfo.getPrintDate + " / " + friInfo.getPrintTime + "시");
                                rainInfo.setText(friInfo.showRainState() + "  " + friInfo.rainPercent);
                                curTemperature.setText(friInfo.curTemperature);
                                windSpeed.setText(friInfo.windSpeed);
                                maxTemperature.setText(friInfo.maxTemperature);
                                minTemperature.setText(friInfo.minTemperature);
                            }
                        });
                    }
                });
            }
        });

        Button buttonSat = findViewById(R.id.sat);
        buttonSat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor satInfo = new weatherInfor(6);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                satInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(satInfo.getPrintDate + " / " + satInfo.getPrintTime + "시");
                                rainInfo.setText(satInfo.showRainState() + "  " + satInfo.rainPercent);
                                curTemperature.setText(satInfo.curTemperature);
                                windSpeed.setText(satInfo.windSpeed);
                                maxTemperature.setText(satInfo.maxTemperature);
                                minTemperature.setText(satInfo.minTemperature);
                            }
                        });
                    }
                });
            }
        });

        Button buttonSun = findViewById(R.id.sun);
        buttonSun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfor sunInfo = new weatherInfor(7);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sunInfo.applyWeatherIcon(weatherIcon);
                                selectedDate.setText(sunInfo.getPrintDate + " / " + sunInfo.getPrintTime + "시");
                                rainInfo.setText(sunInfo.showRainState() + "  " + sunInfo.rainPercent);
                                curTemperature.setText(sunInfo.curTemperature);
                                windSpeed.setText(sunInfo.windSpeed);
                                maxTemperature.setText(sunInfo.maxTemperature);
                                minTemperature.setText(sunInfo.minTemperature);
                            }
                        });
                    }
                });
            }
        });

    }


}