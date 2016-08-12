package com.example.administrator.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.administrator.weatherapp.data.AppContract.LocationEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 10.08.2016.
 */
public class LocationList extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.location_list_container, new LocationListFragment())
                    .commit();
        }
    }

    public static class LocationListFragment extends Fragment implements
            LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = LocationListFragment.class.getSimpleName();
        private static final int URL_LOADER = 0;
        private static final String[] LOCATION_COLUMNS = {
                LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
                LocationEntry.COLUMN_CITY_CODE,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COUNTRY_CODE
        };

        public LocationListFragment(){}

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState){
            //return inflater.inflate(R.layout.location_list_fragment, container, false);

            // dummy data to populate Fragment list view
            String[] data = {
                    "Moscow - RU",
                    "Tashkent - UZ",
            };

            List<String> dummyData = new ArrayList<String>(Arrays.asList(data));

            // create adapter
            ArrayAdapter<String> mAppAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.location_list_fragment, // layout, where to-be-populated textview is located
                    R.id.textView_for_location_list, // textview id to be populated
                    dummyData);


            // inflate layout where ListView is placed
            View rootView = inflater.inflate(R.layout.location_list,container,false);

            // bind adapter to the id of ListView
            ListView listView = (ListView) rootView.findViewById(R.id.location_list_listview);
            listView.setAdapter(mAppAdapter);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(URL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    LOCATION_COLUMNS,
                    null,
                    null,
                    null
            );

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


}
