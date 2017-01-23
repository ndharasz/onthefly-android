package com.example.noah.onthefly.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.noah.onthefly.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityCreateFlight extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private class ArrayAdapterWithHint<T> extends ArrayAdapter {
        public ArrayAdapterWithHint(
                Context c, int resource, List<T> objects) {
            super(c, resource, objects);
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == 0) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            } else {
                return true;
            }
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if (position == 0) {
                // Set the hint text color gray
                tv.setTextColor(Color.GRAY);
            } else {
                tv.setTextColor(Color.BLACK);
            }
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acivity_create_flight);

        Spinner plane_spinner = (Spinner) findViewById(R.id.choose_plane_spinner);
        String[] planes = new String[]{
                "Choose a plane", "Plane 1", "Plane 2"
        };
        final List<String> planesList = new ArrayList<>(Arrays.asList(planes));

        Spinner dept_loc_spinner = (Spinner) findViewById(R.id.choose_dept_airport_spinner);
        String[] dept_locs = new String[] {
                "Departure location", "ATL", "SEA", "MIA"
        };
        final List<String> deptList = new ArrayList<>(Arrays.asList(dept_locs));

        Spinner arr_loc_spinner = (Spinner) findViewById(R.id.choose_arr_airport_spinner);
        String[] arr_locs = new String[] {
                "Arrical location", "ATL", "SEA", "MIA"
        };
        final List<String> arrList = new ArrayList<>(Arrays.asList(arr_locs));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, planesList);
        final ArrayAdapter<String> deptArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, deptList);
        final ArrayAdapter<String> arrArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, arrList);
        plane_spinner.setAdapter(planeArrayAdapter);
        dept_loc_spinner.setAdapter(deptArrayAdapter);
        arr_loc_spinner.setAdapter(arrArrayAdapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ActivityCreateFlight Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
