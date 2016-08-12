package com.example.administrator.weatherapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 10.08.2016.
 */
public class AppContract {

    // Global variables to build URIs
    public static final String CONTENT_AUTHORITY = "com.example.administrator.weatherapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    // settings to build location table
    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";

        // build MIME type fot table "vnd.android.cursor.dir/com.example.administrator.weatherapp/location"
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "." + TABLE_NAME;

        //build URI to the table: "content://com.example.administrator.weatherapp/location"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        // define column names of the table
        public static final String COLUMN_CITY_CODE = "city_code";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_STATUS = "status";

        // method that appends the given ID to the end of the path "content://com.example.administrator.weather/location/id"
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }
    }

    // settings to build weather table
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        //build URI to the table: "content://com.example.administrator.weather/weather"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        // build MIME type for table "vnd.android.cursor.dir/com.example.administrator.weather/weather"
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/" +
                CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // build MIME type for row "vnd.android.cursor.item/com.example.administrator.weather/weather"
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" +
                CONTENT_AUTHORITY + "/" + TABLE_NAME;


        // define column names of the table
        // define column names
        public static final String COLUMN_CITY_KEY = "city_id";
        public static final String COLUMN_WEATHER_ID = "weather_id"; /// weather_id as returned by API, to identify the icon to be used
        public static final String COLUMN_SHORT_DESC = "short_desc"; // e.g "clear" vs "sky is clear".
        public static final String COLUMN_TEMP = "temp";// Temperature for the day (stored as floats)
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";//windspeed  mph
        public static final String COLUMN_DEGREES = "degrees";// Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.

        public static String getColumnCityCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // build URI: "content://com.example.administrator.weather/weather/locationSetting"
        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
