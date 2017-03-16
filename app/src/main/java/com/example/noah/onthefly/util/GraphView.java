package com.example.noah.onthefly.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

/**
 * Created by Alex on 3/16/17.
 */



public class GraphView extends View {

    class Pt {
        float x, y;

        Pt(float _x, float _y) {
            x = _x;
            y = 1000 - _y;
        }
    }

        Pt[] boundaries = {
                new Pt(300, 0),
                new Pt(300, 400),
                new Pt(600, 600),
                new Pt(1200, 600)
        };

        Pt[] flight = {
                new Pt(700, 300),
                new Pt(800, 500)
        };

        Pt[] axes = {
                new Pt(200, 800),
                new Pt(200, 0),
                new Pt(1200, 0)

        };

        Pt[] gridLinesHoriz = {
                new Pt(200, 0),
                new Pt(1200, 0),
                new Pt(1200, 200),
                new Pt(200, 200),
                new Pt(1200, 200),
                new Pt(1200, 400),
                new Pt(200, 400),
                new Pt(200, 600),
                new Pt(1200, 600)
        };

        Pt[] gridLinesVert = {
            new Pt(200, 0), new Pt(200, 800),
            new Pt(400, 800),
            new Pt(400, 0),
            new Pt(600, 0),
            new Pt(600, 800),
                new Pt(800, 800),
                new Pt(800, 0),
                new Pt(1000, 0),
                new Pt(1000, 800),
                new Pt(1200, 800),
                new Pt(1200, 0),
                new Pt(1200, 800),
                new Pt(200, 800)
        };


    public GraphView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(60);
        text.setStrokeWidth(2);
        text.setStyle(Paint.Style.STROKE);


        Path gridVert = new Path();
        gridVert.moveTo(gridLinesVert[0].x, gridLinesVert[0].y);
        for (int i = 1; i < gridLinesVert.length; i++){
            gridVert.lineTo(gridLinesVert[i].x, gridLinesVert[i].y);
        }
        canvas.drawPath(gridVert, text);

        Path gridHoriz = new Path();
        gridHoriz.moveTo(gridLinesHoriz[0].x, gridLinesHoriz[0].y);
        for (int i = 1; i < gridLinesHoriz.length; i++){
            gridHoriz.lineTo(gridLinesHoriz[i].x, gridLinesHoriz[i].y);
        }
        canvas.drawPath(gridHoriz, text);

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
        path.moveTo(flight[0].x, flight[0].y);
        for (int i = 1; i < flight.length; i++){
            path.lineTo(flight[i].x, flight[i].y);
        }
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


        canvas.drawText("1800", 0, 1000, text);
        canvas.drawText("2000", 0, 800, text);
        canvas.drawText("2200", 0, 600, text);
        canvas.drawText("2400", 0, 400, text);
        canvas.drawText("2600", 0, 200, text);

        canvas.drawText("70", 200, 1100, text);
        canvas.drawText("74", 400, 1100, text);
        canvas.drawText("78", 600, 1100, text);
        canvas.drawText("82", 800, 1100, text);
        canvas.drawText("84", 1000, 1100, text);



        }
    }

