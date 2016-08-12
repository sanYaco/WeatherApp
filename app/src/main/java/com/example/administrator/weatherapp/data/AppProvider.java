package com.example.administrator.weatherapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.administrator.weatherapp.data.AppContract.LocationEntry;
import com.example.administrator.weatherapp.data.AppContract.WeatherEntry;


/**
 * Created by Administrator on 10.08.2016.
 */
public class AppProvider extends ContentProvider {

    private AppDbHelper appDbHelper;

    // uri matcher
    static final int WEATHER = 100;
    static final int WEATHER_BY_LOCATION = 101;
    static final int LOCATION = 300;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppContract.CONTENT_AUTHORITY;
        // Uri matcher tree
        matcher.addURI(authority, AppContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, AppContract.PATH_WEATHER + "/*", WEATHER_BY_LOCATION);
        matcher.addURI(authority, AppContract.PATH_LOCATION, LOCATION);
        return matcher;
    }

    // variable used to perform query "weather by location"
    private static final String cityIdSelection = LocationEntry.TABLE_NAME+
            "." + LocationEntry._ID + " = ? ";

    // SQL query builder to use in getWeatherByLocation method
    private static final SQLiteQueryBuilder weatherByCityCodeQueryBuilder;
    static{
        weatherByCityCodeQueryBuilder = new SQLiteQueryBuilder();
        weatherByCityCodeQueryBuilder.setTables(
                WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        LocationEntry.TABLE_NAME + " ON " +
                        WeatherEntry.TABLE_NAME + "." +
                        WeatherEntry.COLUMN_CITY_KEY + " = " +
                        LocationEntry.TABLE_NAME + "." +
                        LocationEntry._ID);

    }
    // SQL query method to perform weather by location query
    private Cursor getWeatherByLocation (Uri uri, String[] columns, String orderBy){

        String city_id = WeatherEntry.getColumnCityCodeFromUri(uri);
        String[] selectionArgs = new String[]{city_id};
        String selection = cityIdSelection;
        return weatherByCityCodeQueryBuilder.query(appDbHelper.getReadableDatabase(),
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
    }

    @Override
    public boolean onCreate() {
        appDbHelper = new AppDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri,
                        String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String orderBy) {

        Cursor cursor;
        switch (uriMatcher.match(uri)){
            case WEATHER_BY_LOCATION:{
                cursor = getWeatherByLocation(uri,columns,orderBy);
                break;
            }
            case WEATHER:{
                cursor = appDbHelper.getReadableDatabase().query(
                        WeatherEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy
                );
                break;
            }
            case LOCATION:{
                cursor = appDbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER_BY_LOCATION:
                return AppContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return AppContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return AppContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValue) {//TODO
        final SQLiteDatabase db = appDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch(match){
            case WEATHER:{
                long _id = db.insert(WeatherEntry.TABLE_NAME, null, contentValue);
                if ( _id > 0 )
                    returnUri = WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION:{
                long _id = db.insert(LocationEntry.TABLE_NAME, null, contentValue);
                if ( _id > 0 )
                    returnUri = LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = appDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case WEATHER:
                rowsDeleted = db.delete(
                        WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = appDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case WEATHER:
                rowsUpdated = db.update(WeatherEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(LocationEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = appDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AppContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }


    }
    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        appDbHelper.close();
        super.shutdown();
    }


}
