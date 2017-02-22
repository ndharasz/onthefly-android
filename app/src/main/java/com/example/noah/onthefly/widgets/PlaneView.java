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
import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * Created by brian on 2/21/17.
 */

public class PlaneView extends GridView {
    private static final String TAG = "PlaneView";
    private List<Double> rowArms;

    // This is the majority of the PlaneView.
    // Basically the PassengerViewAdapter is a mapping from Passengers to views which
    //   are displayed on screen.
    private class PassengerViewAdapter extends ArrayAdapter<Passenger> {
        private Context context;

        public PassengerViewAdapter(Context context, int resourceId, //resourceId=your layout
                                     List<Passenger> items) {
            super(context, resourceId, items);
            this.context = context;
        }

        // Converts a Passenger to a View
        @Override
        public View getView(int pos, final View convertView, final ViewGroup parent) {
            final int currPos = pos;
            View view = convertView;

            if (view == null) {
                view = createNewView(pos);
            }

            ((TextView) view.findViewById(R.id.passenger_name)).setText(getItem(currPos).getName());
            if (getItem(currPos).getWeight() != 0) {
                ((TextView) view.findViewById(R.id.passenger_weight)).setText(String.valueOf(getItem(currPos).getWeight()));
            } else {
                ((TextView) view.findViewById(R.id.passenger_weight)).setText("");
            }
            return view;
        }

        private View createNewView(final int pos) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.item_passenger_view, null);

            // OnClickListener for each passenger, to pull up a dialog and input information
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
                    if (!getItem(pos).equals(Passenger.EMPTY)) {
                        name.setText(getItem(pos).getName());
                        weight.setText(String.valueOf(getItem(pos).getWeight()));
                    }

                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        final String passName = name.getText().toString().trim();
                                        final int passWeight = Integer.parseInt(weight.getText().toString().trim());
                                        getItem(pos).setWeight(passWeight);
                                        getItem(pos).setName(passName);
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

            // This listener starts the drag and drop.
            layout.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Passenger passenger = viewToPassenger((LinearLayout) view);
                    if (!passenger.equals(Passenger.EMPTY)) {
                        ClipData data = ClipData.newPlainText("Dragged Object", passenger.toString());
                        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            // All the code for drag and drop is below
            layout.setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                        return true;
                    } else if (event.getAction() == DragEvent.ACTION_DROP) {
                        // Find view at location
                        float endX = v.getX();
                        float endY = v.getY();
                        GridView owner = (GridView) v.getParent();
                        for (int i = 0; i < owner.getChildCount(); i++) {
                            View passengerView = owner.getChildAt(i);
                            float left = passengerView.getLeft();
                            float top = passengerView.getTop();
                            if (left == endX && top == endY) {
                                String passengerDragged = event.getClipData().getItemAt(0).getText().toString();
                                swapViews(passengerDragged, i);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
            return layout;
        }

        private Passenger viewToPassenger(LinearLayout view) {
            try {
                String name = ((TextView) view.findViewById(R.id.passenger_name)).getText().toString();
                int weight = Integer.parseInt(((TextView) view.findViewById(R.id.passenger_weight)).getText().toString());
                return new Passenger(name, weight);
            } catch (Exception e) {
                // to avoid crashes, if a view is bad, return an empty passenger.
                Log.d(TAG, e.getMessage());
                return Passenger.EMPTY;
            }
        }

        private void swapViews(String passengerDragged, int b) {
            int a = getPosition(Passenger.reconstructPassenger(passengerDragged));
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
    public PlaneView(Context context, int columnsPerRow, int numSeats, List<Double> rowArms) {
        super(context);

        if (rowArms.size() != numSeats / columnsPerRow) {
            throw new IllegalArgumentException("Number of row arms != number of rows.");
        }
        this.rowArms = rowArms;

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

    public double calculateMoment() {
        // iterate through the rows and multiply the corresponding moment
        return 0;
    }

}
