package com.example.administrator.weatherapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.administrator.weatherapp.data.AppContract.LocationEntry;
import com.example.administrator.weatherapp.data.AppContract.WeatherEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Administrator on 02.08.2016.
 */
// 2.01.1 Add netwotk call-----------------------------------------------------------------------
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public FetchWeatherTask(Context context) {
        mContext = context;
    }

    long addLocation(String locationSetting, String cityName) {

        long locationId;

        // First, check if the location with this city name exists in the db
        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,                  // URI to table
                new String[]{LocationEntry._ID}, //column
                LocationEntry.COLUMN_CITY_CODE + " = ?",    //selections
                new String[]{locationSetting},              //selectionArgs
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(LocationEntry.COLUMN_CITY_CODE, locationSetting);

            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        // Wait, that worked?  Yes!
        return locationId;
    }

    private void getWeatherDataFromJson(String forecastJSONStr, String locationSetting)
            throws JSONException {

        // JSON requesting URL  http://api.openweathermap.org/data/2.5/group?id=703448,2643743&mode=json&units=metric&
        // APPID=b04fad6abdd400029a508b53d9ce0182

        // JSON arrays
        final String OWN_ARR_LIST = "list";
        final String OWN_ARR_WEATHER = "weather";

        //JSON objects
        final String OWN_OBJ_MAIN = "main";
        final String OWN_OBJ_WIND = "wind";
        //JSON Strings
        final String OWM_STR_WEATHER_ID = "id";
        final String OWM_STR_DESCRIPTION = "main";
        final String OWM_STR_TEMPERATURE = "temp";
        final String OWM_STR_PRESSURE = "pressure";
        final String OWM_STR_HUMIDITY = "humidity";
        final String OWM_STR_WINDSPEED = "speed";
        final String OWM_STR_WIND_DIRECTION = "deg";
        final String OWN_STR_CITY_NAME = "name";

        try {
            // create JSON object
            JSONObject weatherJsonFile = new JSONObject(forecastJSONStr);
            // get 1st JSON array
            JSONArray listArray = weatherJsonFile.getJSONArray(OWN_ARR_LIST);

            // Insert the new weather information into the database
            Vector<ContentValues> OWN_vector = new Vector<ContentValues>(listArray.length());

            // get the number of cities returned
            //int cnt = forecastJson.getInt("cnt");
            //String[] resultStr = new String[cnt];

            // iterate through JSON array to get JSON strings
            for (int i = 0; i < listArray.length(); i++) {
                String weatherId;
                String weatherDescription;
                double temperature;
                double pressure;
                int humidity;
                double windSpeed;
                double windDeg;

                // get JSON object relating to a city weather
                JSONObject listArrayItem = listArray.getJSONObject(i);

                // get weather id and weather description, e.g. clear, cloudy etc.
                JSONObject weatherArray = listArrayItem.getJSONArray(OWN_ARR_WEATHER)
                        .getJSONObject(0);
                weatherId = weatherArray.getString(OWM_STR_WEATHER_ID);             //1
                weatherDescription = weatherArray.getString(OWM_STR_DESCRIPTION);   //2

                // get temperature, pressure, humidity
                JSONObject mainObject = listArrayItem.getJSONObject(OWN_OBJ_MAIN);
                temperature = mainObject.getDouble(OWM_STR_TEMPERATURE);            //3
                pressure = mainObject.getDouble(OWM_STR_PRESSURE);                  //4
                humidity = mainObject.getInt(OWM_STR_HUMIDITY);                     //5

                // get wind details
                JSONObject windObject = listArrayItem.getJSONObject(OWN_OBJ_WIND);
                windSpeed = windObject.getDouble(OWM_STR_WINDSPEED);                //6
                windDeg = windObject.getDouble(OWM_STR_WIND_DIRECTION);             //7

                // get city name and db location id
                String cityName = listArrayItem.getString(OWN_STR_CITY_NAME);       //8
                long locationId = addLocation(locationSetting, cityName);           //9

                ContentValues weatherValues = new ContentValues();
                weatherValues.put(WeatherEntry.COLUMN_CITY_KEY, locationId);             //1
                weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);              //2
                weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);              //3
                weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);           //4
                weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDeg);                //5
                weatherValues.put(WeatherEntry.COLUMN_TEMP, temperature);               //6
                weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, weatherDescription);  //7
                weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);           //8
                OWN_vector.add(weatherValues);
            }
            int inserted = 0;
            if (OWN_vector.size() > 0) {
                ContentValues[] OWN_array = new ContentValues[OWN_vector.size()];
                OWN_vector.toArray(OWN_array);
                inserted = mContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, OWN_array);
            }
            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // verify size of params
        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];
        // These two need to be declared outside the try/catch so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        String format = "json";
        String units = "metric";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/group?";
            final String QUERY_PARAM = "id";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String APPID_PARAM = "APPID";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG,"Built URI "+ url);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            getWeatherDataFromJson(forecastJsonStr, locationQuery);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error, could not get weather data ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}