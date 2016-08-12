package com.example.administrator.weatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.weatherapp.data.AppContract.LocationEntry;
import com.example.administrator.weatherapp.data.AppContract.WeatherEntry;

/**
 * Created by Administrator on 10.08.2016.
 */
public class AppDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "cityWeather.db";

    // constructor
    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // instantiate SQL query string to create location table
    final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
            LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            LocationEntry.COLUMN_CITY_CODE + " INTEGER UNIQUE NOT NULL, " +
            LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
            LocationEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
            LocationEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0" +
            " );";
    // instantiate SQL query string to create location table
// instantiate SQL query string to create location table
    final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
            WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeatherEntry.COLUMN_CITY_KEY + " INTEGER NOT NULL, " +
            WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
            WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
            WeatherEntry.COLUMN_TEMP + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + WeatherEntry.COLUMN_CITY_KEY + ") REFERENCES " +
            LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

            // To assure the application have just one weather
            // per location, it's created a UNIQUE constraint with REPLACE strategy
            " UNIQUE (" + WeatherEntry.COLUMN_CITY_KEY + ") ON CONFLICT REPLACE);";

    // create db tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
