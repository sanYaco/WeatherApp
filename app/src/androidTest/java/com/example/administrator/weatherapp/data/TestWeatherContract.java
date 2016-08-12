package com.example.administrator.weatherapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Administrator on 31.07.2016.
 */
public class TestWeatherContract extends AndroidTestCase{
    // global variables to perform test
    private static final String TEST_WEATHER_LOCATION = "/North Pole";



    // test of weather location function
    public void testBuildWeatherLocation() {
        Uri locationUri = AppContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.example.administrator.weatherapp/weather/%2FNorth%20Pole");
    }
}
