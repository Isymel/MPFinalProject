package com.example.weatherproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class WeatherInformation {
    private String rainPercent, rainState, cloudState, curTemperature, minTemperature, maxTemperature, windSpeed;

    public String suitTime(String time){
        switch(time) {
            case "0200":
            case "0300":
            case "0400":
                time = "0200";
                break;
            case "0500":
            case "0600":
            case "0700":
                time = "0500";
                break;
            case "0800":
            case "0900":
            case "1000":
                time = "0800";
                break;
            case "1100":
            case "1200":
            case "1300":
                time = "1100";
                break;
            case "1400":
            case "1500":
            case "1600":
                time = "1400";
                break;
            case "1700":
            case "1800":
            case "1900":
                time = "1700";
                break;
            case "2000":
            case "2100":
            case "2200":
                time = "2000";
                break;
            case "2300":
            case "0000":
            case "0100":
                time = "2300";
        }
        return time;
    }

    public String searchWeather(String date, String time) throws IOException, JSONException {
        String bDate = date;
        String bTime = suitTime(time);
        String type = "json";
        String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
        String sKey = "g66JTL3Nwh2wI8viyg0hpgOZ3Ih9dborl9g4oOh42VYfoi5kDmMzi%2FB%2FMVz9wO9TRv3qtqtyfEBuBqspQFeTHw%3D%3D";
        StringBuilder urlBuilder = new StringBuilder(apiURL);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")+"="+sKey);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("61", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("128", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(bDate, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(bTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {
                if (fcstValue.equals("1")) {
                    cloudState = "perfectClear ";
                } else if (fcstValue.equals("2")) {
                    cloudState = "veryClear ";
                } else if (fcstValue.equals("3")) {
                    cloudState = "aLittleClear ";
                } else if (fcstValue.equals("4")) {
                    cloudState = "cloudy ";
                }
            }

            if (category.equals("TMP")) {
                curTemperature = fcstValue + "℃ ";
            }

            if(category.equals("WSD")) {
                windSpeed = fcstValue + "m/s ";
            }

            if(category.equals("POP")) {
                rainPercent = fcstValue + "% ";
            }
            if(category.equals("PTY")) {
                if (fcstValue.equals("1")) {
                    rainState = "rainy ";
                } else if (fcstValue.equals("2")) {
                    rainState = "rainNSnowy ";
                } else if (fcstValue.equals("3")) {
                    rainState = "snowy ";
                } else if (fcstValue.equals("4")) {
                    rainState = "shower ";
                } else if (fcstValue.equals("0")){
                    rainState = "noRain ";
                }
            }
            if(category.equals("TMN")) {
                minTemperature = fcstValue + "℃ ";
            }
            if(category.equals("TMX")){
                maxTemperature = fcstValue + "℃ ";
            }

        }

        return rainPercent + rainState + cloudState + curTemperature + minTemperature + maxTemperature + windSpeed;
    }
}
