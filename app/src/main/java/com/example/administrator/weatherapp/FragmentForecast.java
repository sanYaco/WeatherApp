package com.example.administrator.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FragmentForecast extends Fragment {
    public FragmentForecast(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        // dummy data to populate Fragment list view
        String[] data = {
                "Moscow - 35C",
                "Tashkent - 45C",
        };

        List<String> dummyData = new ArrayList<String>(Arrays.asList(data));

        // create adapter
        ArrayAdapter<String> mAppAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_view_items,
                R.id.textView_for_listView,
                dummyData);


        // inflate fragment_forecast layout
        View rootView = inflater.inflate(R.layout.fragment_forecast,container,false);

        // bind adapter to the ListView
        ListView listView = (ListView) rootView.findViewById(R.id.fragment_listview);
        listView.setAdapter(mAppAdapter);

        // open list of cities by clicking add button

        Button button = (Button) rootView.findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationList.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // update weather method
//    private void updateWeather(){
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
//        String location = Utility.getPreferredLocation(getActivity());
//        weatherTask.execute(location);
//    }

    // update weather on app start
//    @Override
//    public void onStart(){
//        super.onStart();
//        updateWeather();
//    }

}
