package com.example.administrator.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inflate fragment, where the list of cities with temperatures is deployed
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    // adding Fragment into the container layout (activity_main.xml)
                    .add(R.id.activity_main_view, new FragmentForecast())
                    .commit();
        }

    }


}
