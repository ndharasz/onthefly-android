package com.example.noah.onthefly.widgets;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Passenger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 2/21/17.
 */

public class PlaneView extends GridView {
    private static final String TAG = "PlaneView";

    private class PassengerViewAdapter extends ArrayAdapter<Passenger> {
        private Context context;

        public PassengerViewAdapter(Context context, int resourceId, //resourceId=your layout
                                     List<Passenger> items) {
            super(context, resourceId, items);
            this.context = context;
        }

        public View getView(int pos, final View convertView, final ViewGroup parent) {
            final int currPos = pos;
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.item_passenger_view, null);
                ((TextView) layout.findViewById(R.id.passenger_name)).setText("Add Passenger");
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View promptView = LayoutInflater.from(context).inflate(R.layout.passenger_info_dialog, null);
                        // pull up dialog to enter passenger names
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                        alertDialogBuilder.setView(promptView);
                        alertDialogBuilder.setTitle("New Passenger");
                        final EditText name = (EditText) promptView.findViewById(R.id.passName);
                        final EditText weight = (EditText) promptView.findViewById(R.id.passWeight);
                        if (!getItem(currPos).getName().equals("Add Passenger")) {
                            name.setText(getItem(currPos).getName());
                            weight.setText(getItem(currPos).getWeight());
                        }

                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            final String passName = name.getText().toString().trim();
                                            final int passWeight = Integer.parseInt(weight.getText().toString().trim());
                                            getItem(currPos).setWeight(passWeight);
                                            getItem(currPos).setName(passName);
                                            name.setText(passName);
                                            weight.setText(String.valueOf(passWeight));
                                        } catch (Exception e) {
                                            Toast.makeText(context, "One or more fields were input incorrectly.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();
                    }
                });
                layout.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String passengerName = ((TextView)view.findViewById(R.id.passenger_name)).getText().toString();
                        if (!passengerName.equals("Add Passenger")) {
                            ClipData data = ClipData.newPlainText("Dragged Object", passengerName);
                            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                            view.startDrag(data, shadowBuilder, view, 0);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                layout.setOnDragListener(new OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                            Log.d(TAG, "Pos: " + currPos);
                            return true;
                        } else if (event.getAction() == DragEvent.ACTION_DROP) {
                            // Find view at location
                            float endX = v.getX();
                            float endY = v.getY();
                            Log.d(TAG, "End (X, Y): (" + endX + ", " + endY + ")");
                            GridView owner = (GridView) v.getParent();
                            for (int i = 0; i < owner.getChildCount(); i++) {
                                View passengerView = owner.getChildAt(i);
                                float left = passengerView.getLeft();
                                float top = passengerView.getTop();
                                if (left == endX && top == endY) {
                                    String nameDragged = event.getClipData().getItemAt(0).getText().toString();
                                    swapViews(nameDragged, i);
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                });
                view = layout;
            }

            ((TextView) view.findViewById(R.id.passenger_name)).setText(getItem(currPos).getName());
            ((TextView) view.findViewById(R.id.passenger_weight)).setText(String.valueOf(getItem(currPos).getWeight()));
            return view;
        }

        private void swapViews(String nameDragged, int b) {
            int a = getPosition(new Passenger(nameDragged, 0));
            Passenger first = getItem(a);
            Passenger second = getItem(b);
            Passenger.swap(first, second);
            notifyDataSetChanged();
        }
    }
    /*
     * This is merely a drag and drop grid where you can click on each
     * passenger and drag them to a different seat.
     */
    public PlaneView(Context context, int columnsPerRow, int numSeats) {
        super(context);

        ArrayList<Passenger> passengers = new ArrayList<>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            passengers.add(new Passenger("Add Passenger", 0));
        }
        PassengerViewAdapter passengerAdapter = new PassengerViewAdapter(context, R.layout.item_passenger_view, passengers);
        this.setAdapter(passengerAdapter);

        this.setNumColumns(columnsPerRow);
        this.setVerticalSpacing(10);
        this.setHorizontalSpacing(20);
        this.setPadding(20, 20, 20, 20);
    }

}
