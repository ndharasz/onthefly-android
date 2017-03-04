package com.example.noah.onthefly.widgets;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.util.ImageDragShadowBuilder;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
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
    private int replace;

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
            if (getItem(currPos).getWeight() != 0) {
                ((TextView) view.findViewById(R.id.passenger_weight)).setText(String.valueOf(getItem(currPos).getWeight()));
            } else {
                ((TextView) view.findViewById(R.id.passenger_weight)).setText("");
            }
            return view;
        }

        private View newPassenger(int pos) {
            animation = AnimationUtils.loadAnimation(getContext(),R.anim.shake);
            final int tInt = pos;

            LayoutInflater inflater = LayoutInflater.from(context);
            final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_passenger_view, null);
            final ImageView seat = (ImageView) layout.findViewById(R.id.seat);

            // OnClickListener for each passenger, to pull up a dialog and input information
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View temp = v;
                    View promptView = LayoutInflater.from(context).inflate(R.layout.passenger_info_dialog, null);
                    // pull up dialog to enter passenger names
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                    alertDialogBuilder.setView(promptView);
                    ((TextView)promptView.findViewById(R.id.dialog_title))
                            .setText("New Passenger at Seat " + (tInt + 1));
                    final EditText name = (EditText) promptView.findViewById(R.id.passName);
                    final EditText weight = (EditText) promptView.findViewById(R.id.passWeight);

                    if (!getItem(tInt).equals(Passenger.EMPTY)) {
                        name.setText(getItem(tInt).getName());
                        weight.setText(String.valueOf(getItem(tInt).getWeight()));
                    }

                    alertDialogBuilder
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Log.d(TAG, "Clicked on pos = " + tInt);
                                        replace = tInt;
                                        String passName = name.getText().toString().trim();
                                        double passWeight = Double.parseDouble(weight.getText().toString().trim());

                                        getItem(tInt).setWeight(passWeight);
                                        getItem(tInt).setName(passName);


                                        if (passName != "Add Passenger") {
                                            if (getChildAt(tInt) != null) {
                                                ImageView iv = (ImageView) getChildAt(tInt).findViewById(R.id.seat);
                                                iv.setVisibility(VISIBLE);
                                                if (iv.getVisibility() == INVISIBLE) {
                                                    iv.setVisibility(VISIBLE);
                                                    refreshView();
                                                }
                                            }
                                        }

                                        refreshView();
                                        temp.setAlpha(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "One or more fields were input incorrectly.", Toast.LENGTH_SHORT).show();
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

            // This listener starts the drag and drop.
            layout.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Passenger passenger = viewToPassenger((RelativeLayout) view);
                    if (!passenger.equals(Passenger.EMPTY)) {
                        replace = tInt;
                        ClipData data = ClipData.newPlainText("Dragged Object", passenger.toString());
                        ImageDragShadowBuilder shadowBuilder = new ImageDragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);

                        switchAnimation(true);
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
                    GridView owner = (GridView) v.getParent();
                    if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                        return true;
                    } else if (event.getAction() == DragEvent.ACTION_DROP) {
                        // Find view at location

                        int endX = (int) v.getX();
                        int endY = (int) v.getY();

                        for (int i = 0; i < owner.getChildCount(); i++) {
                            View passengerView = owner.getChildAt(i);
                            int left = (int)passengerView.getLeft();
                            int top = (int)passengerView.getTop();

                            String passengerDragged = event.getClipData().getItemAt(0).getText().toString();

                            if (left == endX && top == endY) {
                                passengerView.setAlpha(1);
                                swapViews(passengerDragged, i);

                                for (int j = 0; j < getChildCount(); j++) {
                                    getChildAt(j).setVisibility(VISIBLE);
                                    getChildAt(j).setAlpha(1);
                                }
                                return true;

                            }
                        }

                        return true;
                    } else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                        switchAnimation(false);
                        v.setAlpha(1);
                        v.clearAnimation();

                        if (!event.getResult()) {
                            getChildAt(replace).setBackgroundResource(R.drawable.passenger_box);
                            getChildAt(replace).findViewById(R.id.seat).setVisibility(INVISIBLE);
                            Passenger.swap(new Passenger("Add Passenger", 0), getItem(replace));
                            refreshView();
                        } else {
                            getChildAt(replace).setBackgroundResource(R.drawable.passenger_box);
                        }

                    } else if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
                        getChildAt(replace).setBackgroundColor(Color.RED);
                    } else if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
                        getChildAt(replace).setBackgroundResource(R.drawable.passenger_box);
                    }
                    return false;
                }
            });
            return layout;
        }

        private void refreshView() {
            notifyDataSetChanged();
        }
        private Passenger viewToPassenger(RelativeLayout view) {
            try {
                String name = ((TextView) view.findViewById(R.id.passenger_name)).getText().toString();
                double weight = Double.parseDouble(((TextView) view.findViewById(R.id.passenger_weight)).getText().toString());
                return new Passenger(name, weight);
            } catch (Exception e) {
                // to avoid crashes, if a view is bad, return an empty passenger.
                Log.d(TAG, e.getMessage());
                return Passenger.EMPTY;
            }
        }

        private void swapViews(String passengerDragged, int b) {
            int a = getPosition(Passenger.reconstructPassenger(passengerDragged));
            // A value less than a signals that a view was dragged outside its boundaries
            //   so we'll just ignore that request
            if (a < 0)
                return;
            Passenger first = getItem(a);
            Passenger second = getItem(b);
          
            if (second.getName() == "Add Passenger") {
                getChildAt(a).findViewById(R.id.seat).setVisibility(INVISIBLE);
                getChildAt(b).findViewById(R.id.seat).setVisibility(VISIBLE);
            }
            if (first.getName() == "Add Passenger") {
                getChildAt(b).findViewById(R.id.seat).setVisibility(INVISIBLE);
                getChildAt(a).findViewById(R.id.seat).setVisibility(VISIBLE);
            }

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
        this.numSeats = numSeats;
        this.numColumns = columnsPerRow;

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
        double totalMoment = 0.0;
        for (int row = 0; row < numSeats; row++) {
            for (int col = 0; col < numColumns; col++) {
                Passenger curPassenger =  ((PassengerViewAdapter) getAdapter()).getItem(row * numColumns + col);
                totalMoment += rowArms.get(row) * curPassenger.getWeight();
            }
        }
        return 0;
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
