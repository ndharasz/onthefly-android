package com.example.noah.onthefly.activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.zip.Inflater;

import static com.example.noah.onthefly.R.layout.item_flight;

public class ActivityFlightList extends AppCompatActivity {
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;
    private TableLayout flightTable;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference flightListReference;

    View expandedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
        flightTable = (TableLayout) findViewById(R.id.flight_table);

        firebaseDatabase = FirebaseDatabase.getInstance();
        flightListReference = firebaseDatabase.getReference("flights");
        flightListReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Flight flight = (Flight) dataSnapshot.getValue(Flight.class);
                flight.setKey(dataSnapshot.getKey());
                // ignore this data if it doesn't belong to this user
                if (!flight.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    return;
                }

                final TableRow v = (TableRow) LayoutInflater.from(ActivityFlightList.this).inflate(item_flight, null);

                ((Button)v.findViewById(R.id.upcoming_flight_edit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActivityFlightList.this, ActivityEditFlight.class);
                        intent.putExtra("FlightDetails", flight);
                        startActivity(intent);
                    }
                });

                ((TextView) v.findViewById(R.id.upcoming_flight_date))
                        .setText(flight.getDate());
                ((TextView) v.findViewById(R.id.upcoming_flight_arrival))
                        .setText(flight.getArriveAirport());
                ((TextView) v.findViewById(R.id.upcoming_flight_depart))
                        .setText(flight.getDepartAirport());
                ((TextView)v.findViewById(R.id.dept_time))
                        .setText(flight.getTime());
                ((TextView)v.findViewById(R.id.duration))
                        .setText(flight.getFlightDuration() + " m");
                ((TextView)v.findViewById(R.id.ac_num))
                        .setText(flight.getPlane());


                v.findViewById(R.id.item_wrapper).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        expandCollapseItem(v);
                    }
                });
                flightTable.addView(v);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Changing a child can only happen in another screen
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Flight flight = (Flight) dataSnapshot.getValue(Flight.class);
                if (!flight.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    return;
                }
                // Also can't happen unless there's a delete button in the list
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void expandCollapseItem(View v) {
        if(expandedItem == null) {
            expandedItem = v;
            expandItem(v);
        } else {
            collapseItem(expandedItem);
            if(expandedItem == v) {
                expandedItem = null;
            } else {
                expandedItem = v;
                expandItem(v);
            }
        }
    }

    protected void expandItem(final View v) {
        v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int startHeight = v.getMeasuredHeight();
        ((TableRow)v.getParent()).setBackgroundColor(getResources().getColor(R.color.colorPrimarySemiDark));
        v.findViewById(R.id.label_wrapper1).setVisibility(View.VISIBLE);
        v.findViewById(R.id.label_wrapper2).setVisibility(View.VISIBLE);
        v.findViewById(R.id.detail_wrapper).setVisibility(View.VISIBLE);
        v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int targetHeight = (int)(v.getMeasuredHeight()*.75);
        animate(startHeight, targetHeight, v);
    }

    protected void collapseItem(final View v) {
        v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int startHeight = (int)(v.getMeasuredHeight()*.75);
        ((TableRow)v.getParent()).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        v.findViewById(R.id.label_wrapper1).setVisibility(View.GONE);
        v.findViewById(R.id.label_wrapper2).setVisibility(View.GONE);
        v.findViewById(R.id.detail_wrapper).setVisibility(View.GONE);
        v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int targetHeight = v.getMeasuredHeight();
        animate(startHeight, targetHeight, v);
    }

    protected void animate(int start, int end, final View v) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    // takes in the database date and displays it
    public String getDisplayDate(String date) {
        if (date.contains("-")) {
            return date.substring(0, date.lastIndexOf('-'));
        } else {
            return date;
        }
    }

    protected void logout(View v) {
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();
        loginPrefsEditor.putBoolean("saveLogin", false);
        loginPrefsEditor.commit();
        Intent logoutIntent = new Intent(this, ActivityLogin.class);
        this.startActivity(logoutIntent);
    }

    protected void createFlight(View v) {
        Intent createFlightIntent = new Intent(this, ActivityCreateFlight.class);
        this.startActivity(createFlightIntent);
    }
}
