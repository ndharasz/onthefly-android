package com.example.noah.onthefly.widgets;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private int numColumns;
    private int numSeats;
    private List<Double> rowArms;
    private Animation animation;
    private PassengerViewAdapter passengerAdapter;

    private PassengerRemovedListener passengerRemovedListener =  new PassengerRemovedListener() {
        @Override
        public void onPassengerRemoved(int seat) {
            return;
        }
    };

    private PassengerAddedListener passengerAddedListener = new PassengerAddedListener() {
        @Override
        public void onPassengerAdded(int seat, Passenger passenger) {
            return;
        }
    };

    public interface PassengerRemovedListener {
        public void onPassengerRemoved(int seat);
    }

    public interface PassengerAddedListener {
        public void onPassengerAdded(int seat, Passenger passenger);
    }

    private class PassengerDragListener implements OnDragListener {
        private Passenger passenger;
        private int position;

        private boolean dropped = false;

        public PassengerDragListener(int position, Passenger passenger) {
            this.position = position;
            this.passenger = passenger;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                dropped = false;
                return true;
            } else if (event.getAction() == DragEvent.ACTION_DROP) {
                // The ClipData holds the index of the originally dragged object.
                int newPos = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                Passenger replace = passengerAdapter.getItem(newPos);

                // Update Database
                if (!passenger.equals(Passenger.EMPTY)) {
                    passengerRemovedListener.onPassengerRemoved(position);
                }
                passengerRemovedListener.onPassengerRemoved(newPos);
                if (!passenger.equals(Passenger.EMPTY)) {
                    passengerAddedListener.onPassengerAdded(newPos, passenger);
                }
                passengerAddedListener.onPassengerAdded(position, replace);

                // Update views
                Passenger.swap(passenger, replace);
                passengerAdapter.refreshView();
                dropped = true;
                return true;
            } else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                switchAnimation(false);
                v.setAlpha(1);
                v.clearAnimation();
                getChildAt(position).setBackgroundResource(R.drawable.passenger_box);
                return true;

            }
            return false;
        }
    }

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
                view = newPassenger(pos);
            }

            ((TextView) view.findViewById(R.id.passenger_name)).setText(getItem(currPos).getName());

            if (getItem(pos).equals(Passenger.EMPTY)) {
                view.findViewById(R.id.seat).setVisibility(INVISIBLE);
                ((TextView) view.findViewById(R.id.passenger_weight)).setText("");
            } else {
                view.findViewById(R.id.seat).setVisibility(VISIBLE);
                ((TextView) view.findViewById(R.id.passenger_weight)).setText(String.valueOf(getItem(currPos).getWeight()));
            }
            return view;
        }

        private View newPassenger(int pos) {
            animation = AnimationUtils.loadAnimation(getContext(),R.anim.shake);

            LayoutInflater inflater = LayoutInflater.from(context);
            final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_passenger_view, null);
            final ImageView seat = (ImageView) layout.findViewById(R.id.seat);

            // All the code for drag and drop is below
            PassengerDragListener passengerDragListener = new PassengerDragListener(pos, getItem(pos));
            layout.setOnDragListener(passengerDragListener);

            return layout;
        }

        private void refreshView() {
            notifyDataSetChanged();
        }

        private Passenger viewToPassenger(View view) {
            try {
                String name = ((TextView) view.findViewById(R.id.passenger_name)).getText().toString();
                double weight = Double.parseDouble(((TextView) view.findViewById(R.id.passenger_weight)).getText().toString());
                return new Passenger(name, weight);
            } catch (Exception e) {
                // to avoid crashes, if a view is bad, return an empty passenger.
                Log.d(TAG, e.getMessage());
                return Passenger.deepCopy(Passenger.EMPTY);
            }
        }

        public void setPassenger(int a, Passenger passenger) {
            getItem(a).setName(passenger.getName());
            getItem(a).setWeight(passenger.getWeight());
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
        this.numSeats = numSeats;
        this.numColumns = columnsPerRow;


        ArrayList<Passenger> passengers = new ArrayList<>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            passengers.add(new Passenger("Add Passenger", 0));
        }
        passengerAdapter = new PassengerViewAdapter(context, R.layout.item_passenger_view, passengers);
        this.setAdapter(passengerAdapter);

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int tInt = position;
                final Passenger passenger = (Passenger) parent.getItemAtPosition(position);

                View promptView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_passenger_info, null);
                // pull up dialog to enter passenger names
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                alertDialogBuilder.setView(promptView);
                ((TextView)promptView.findViewById(R.id.dialog_title))
                        .setText("New Passenger at Seat " + (position + 1));
                final EditText name = (EditText) promptView.findViewById(R.id.passName);
                final EditText weight = (EditText) promptView.findViewById(R.id.passWeight);

                if (!parent.getItemAtPosition(position).equals(Passenger.EMPTY)) {
                    name.setText(passenger.getName());
                    weight.setText(String.valueOf(passenger.getWeight()));
                } else {
                    name.setText("Passenger");
                }

                alertDialogBuilder
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                passengerRemovedListener.onPassengerRemoved(tInt);
                                Passenger.swap(passenger, Passenger.deepCopy(Passenger.EMPTY));
                                passengerAdapter.refreshView();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Log.d(TAG, "Clicked on pos = " + tInt);
                                    String passName = name.getText().toString().trim();
                                    double passWeight = Double.parseDouble(weight.getText().toString().trim());

                                    passenger.setWeight(passWeight);
                                    passenger.setName(passName);


                                    if (passName != "Add Passenger") {
                                        if (getChildAt(tInt) != null) {
                                            ImageView iv = (ImageView) getChildAt(tInt).findViewById(R.id.seat);
                                            iv.setVisibility(VISIBLE);
                                        }
                                    }

                                    passengerAddedListener.onPassengerAdded(tInt, new Passenger(passName, passWeight));
                                    passengerAdapter.refreshView();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "One or more fields were input incorrectly.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alert.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alert.show();
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.colorPrimary));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.colorPrimary));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorAccent));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
        });

        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Long click at: " + position);

                Passenger passenger = passengerAdapter.viewToPassenger((RelativeLayout) view);
                if (!passenger.equals(Passenger.EMPTY)) {
                    ClipData data = ClipData.newPlainText("Position", String.valueOf(position));
                    ImageDragShadowBuilder shadowBuilder = new ImageDragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);

                    switchAnimation(true);
                    return true;
                } else {
                    return false;
                }
            }
        });

        this.setNumColumns(columnsPerRow);
        this.setVerticalSpacing(40);
        this.setHorizontalSpacing(20);
        this.setPadding(20, 20, 20, 20);
    }

    public void setOnPassengerRemovedListener(PassengerRemovedListener passengerRemovedListener) {
        this.passengerRemovedListener = passengerRemovedListener;
    }

    public void setOnPassengerAddedListener(PassengerAddedListener passengerAddedListener) {
        this.passengerAddedListener = passengerAddedListener;
    }

    public void setPassenger(int loc, Passenger passenger) {
        ((PassengerViewAdapter) getAdapter()).setPassenger(loc, passenger);
    }

    private void switchAnimation(boolean turnOn) {

        for (int i = 0; i < getChildCount(); i++) {
            if (turnOn) {
                getChildAt(i).startAnimation(animation);
            } else {
                animation.cancel();
            }
        }
    }
}
