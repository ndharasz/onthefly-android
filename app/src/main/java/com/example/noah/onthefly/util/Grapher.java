package com.example.noah.onthefly.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import com.example.noah.onthefly.models.Flight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 3/16/17.
 */



public class Grapher {
    Flight flight;

    class Pt {
        float x, y;
        Pt(float _x, float _y) {
            x = _x;
            y = 1000 - _y;
        }
    }

    List<Pt> boundaries;
    List<Pt> axes;
    List<Pt> gridLinesHoriz;
    List<Pt> gridLinesVert;

    public Grapher(Flight flight) {
        this.flight = flight;
        boundaries = new ArrayList<Pt>();
        boundaries.add(new Pt())
    }

    protected Bitmap drawGraph() {
        int w = 800, h = 1200; //set dynamically?

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bmp);

        Paint textStyle = new Paint();
        textStyle.setColor(Color.WHITE);
        textStyle.setTextSize(60);
        textStyle.setStrokeWidth(2);
        textStyle.setStyle(Paint.Style.STROKE);

        Path gridVert = new Path();
        gridVert.moveTo(gridLinesVert[0].x, gridLinesVert[0].y);
        for (int i = 1; i < gridLinesVert.length; i++){
            gridVert.lineTo(gridLinesVert[i].x, gridLinesVert[i].y);
        }
        canvas.drawPath(gridVert, textStyle);

        Path gridHoriz = new Path();
        gridHoriz.moveTo(gridLinesHoriz[0].x, gridLinesHoriz[0].y);
        for (int i = 1; i < gridLinesHoriz.length; i++){
            gridHoriz.lineTo(gridLinesHoriz[i].x, gridLinesHoriz[i].y);
        }
        canvas.drawPath(gridHoriz, textStyle);

        Paint env = new Paint();
        env.setColor(Color.WHITE);
        env.setStrokeWidth(20);
        env.setStyle(Paint.Style.STROKE);
        Path envelope = new Path();

        envelope.moveTo(boundaries[0].x, boundaries[0].y);
        for (int i = 1; i < boundaries.length; i++){
            envelope.lineTo(boundaries[i].x, boundaries[i].y);
        }
        canvas.drawPath(envelope, env);

            //_______________________

        Paint fly = new Paint();
        fly.setColor(Color.RED);
        fly.setStrokeWidth(20);
        fly.setStyle(Paint.Style.STROKE);

        Path path = new Path();
//        path.moveTo(flight[0].x, flight[0].y);
//        for (int i = 1; i < flight.length; i++){
//            path.lineTo(flight[i].x, flight[i].y);
//        }
        canvas.drawPath(path, fly);

            //________________________

        Paint ax = new Paint();
        ax.setColor(Color.BLACK);
        ax.setStrokeWidth(10);
        ax.setStyle(Paint.Style.STROKE);

        Path axis = new Path();
        axis.moveTo(axes[0].x, axes[0].y);
        for (int i = 1; i < axes.length; i++){
            axis.lineTo(axes[i].x, axes[i].y);
        }
        canvas.drawPath(axis, ax);

            //----GRID AXES


        canvas.drawText("1800", 0, 1000, textStyle);
        canvas.drawText("2000", 0, 800, textStyle);
        canvas.drawText("2200", 0, 600, textStyle);
        canvas.drawText("2400", 0, 400, textStyle);
        canvas.drawText("2600", 0, 200, textStyle);

        canvas.drawText("70", 200, 1100, textStyle);
        canvas.drawText("74", 400, 1100, textStyle);
        canvas.drawText("78", 600, 1100, textStyle);
        canvas.drawText("82", 800, 1100, textStyle);
        canvas.drawText("84", 1000, 1100, textStyle);

        return bmp;
    }
}

