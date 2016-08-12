package com.example.administrator.weatherapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.administrator.weatherapp.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 31.07.2016.
 */
public class TestUtilities extends AndroidTestCase {

    // global variables
    static final String TEST_LOCATION = "2997076";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(AppContract.LocationEntry.COLUMN_CITY_CODE, "2997076");
        testValues.put(AppContract.LocationEntry.COLUMN_CITY_NAME, "XXXXXXX");
        testValues.put(AppContract.LocationEntry.COLUMN_COUNTRY_CODE, "YY");
        testValues.put(AppContract.LocationEntry.COLUMN_STATUS, 2);
        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the AppContract as well as the AppDbHelper.
     */
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        AppDbHelper dbHelper = new AppDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(AppContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }
    /*
            Students: The functions we provide inside of TestProvider use this utility class to test
            the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
            CTS tests.

            Note that this only tests that the onChange function is called; it does not test that the
            correct Uri is returned.
         */
        /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    // for weather tables
    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(AppContract.WeatherEntry.COLUMN_CITY_KEY, locationRowId);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_TEMP, 75);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(AppContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(AppContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }



}
