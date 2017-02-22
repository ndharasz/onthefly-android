package com.example.noah.onthefly.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.widgets.PlaneView;

public class FragmentPassengerView extends Fragment {
    Button tab;
    private PlaneView planeView;

    public FragmentPassengerView setTabButton(Button tab) {
        this.tab = tab;
        return new FragmentPassengerView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_passenger_view, container, false);
        LinearLayout baseLayout = (LinearLayout) v.findViewById(R.id.base_layout);
        planeView = new PlaneView(getContext(), 2, 8);
        baseLayout.addView(planeView);
        return v;
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        Log.d("Hidden called?", "YES");
//        if(hidden) {
//            tab.setBackgroundResource(R.drawable.edit_flight_tab);
//        } else {
//            tab.setBackgroundResource(R.drawable.edit_flight_tab_selected);
//        }
//    }
}
