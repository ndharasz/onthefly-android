package com.example.noah.onthefly.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.noah.onthefly.models.Coordinate;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Plane;

import java.util.List;

/**
 * Created by Alex on 3/16/17.
 */



public class Grapher {
    final String TAG = "Grapher";
    final int leftMargin = 150;
    final int bottomMargin = 50;

    Context context;
    Flight flight;
    Plane plane;
    List<Coordinate> envelope;

    int width;
    int height;

    Bitmap bmp;
    Canvas canvas;
    int minX;
    int maxX;
    int minY;
    int maxY;

    public Bitmap drawGraph(Context context, Flight flight, int size) {
        this.flight = flight;
        this.context = context;
        plane = Plane.readFromFile(context, flight.getPlane());
        envelope = plane.getCenterOfGravityEnvelope();

        width = size + leftMargin;
        height = size + bottomMargin;

        minX = Integer.MAX_VALUE;
        maxX = 0;
        minY = Integer.MAX_VALUE;
        maxY = 0;
        for(Coordinate coord : envelope) {
            minX = coord.getX() < minX ? Math.round(coord.getX()) : minX;
            maxX = coord.getX() > maxX ? Math.round(coord.getX()) : maxX;
            minY = coord.getY() < minY ? Math.round(coord.getY()) : minY;
            maxY = coord.getY() > maxY ? Math.round(coord.getY()) : maxY;
        }
        minX -= minX * .1;
        maxX += maxX * .1;
        maxY += maxY * .1;

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        bmp = Bitmap.createBitmap(width, height, conf);
        canvas = new Canvas(bmp);

        drawAxes();
        drawGrid();
        drawEnvelope();
        drawFlight();

        return bmp;
    }

    private void drawAxes() {
        Paint axesPaint = new Paint();
        axesPaint.setColor(Color.BLACK);
        axesPaint.setStrokeWidth(10);
        axesPaint.setStyle(Paint.Style.STROKE);

        Path axes = new Path();
        axes.moveTo(leftMargin, height - bottomMargin);
        axes.lineTo(leftMargin, 0);
        axes.moveTo(leftMargin, bmp.getHeight() - bottomMargin);
        axes.lineTo(width, bmp.getHeight() - bottomMargin);
        canvas.drawPath(axes, axesPaint);
    }

    private void drawGrid() {
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);

        Paint textStyle = new Paint();
        textStyle.setColor(Color.WHITE);
        textStyle.setTextSize(60);
        textStyle.setStrokeWidth(2);
        textStyle.setStyle(Paint.Style.STROKE);


        Path grid = new Path();
        textStyle.setTextAlign(Paint.Align.CENTER);
        for(int x = minX; x < maxX; x += (maxX - minX)/10) {
            grid.moveTo(convertX(x), height - bottomMargin);
            grid.lineTo(convertX(x), 0);
            canvas.drawText(Integer.toString(x), convertX(x), height, textStyle);
        }
        textStyle.setTextAlign(Paint.Align.LEFT);
        for(int y = minY; y < maxY; y += (maxY - minY)/10) {
            grid.moveTo(leftMargin, convertY(y));
            grid.lineTo(width, convertY(y));
            canvas.drawText(Integer.toString(y), 0, convertY(y), textStyle);
        }
        canvas.drawPath(grid, gridPaint);
    }

    private void drawEnvelope() {
        Paint envPaint = new Paint();
        envPaint.setColor(Color.WHITE);
        envPaint.setStrokeWidth(20);
        envPaint.setStyle(Paint.Style.STROKE);

        Path envPath = new Path();
        envPath.moveTo(convertX(envelope.get(0).getX()), convertY(envelope.get(0).getY()));
        for(Coordinate coord : envelope) {
            envPath.lineTo(convertX(coord.getX()), convertY(coord.getY()));
        }
        canvas.drawPath(envPath, envPaint);
    }

    private void drawFlight() {
        Paint flightPaint = new Paint();
        flightPaint.setColor(Color.RED);
        flightPaint.setStrokeWidth(20);
        flightPaint.setStyle(Paint.Style.STROKE);

        Path flightPath = new Path();
    }

    private int convertX(float x) {
        return Math.round((x-minX) * (((float)width - leftMargin)/((float)maxX - minX))) + leftMargin;
    }

    private int convertY(float y) {
        return height - (Math.round((y-minY) * (((float)height - bottomMargin)/((float)maxY - minY))) + bottomMargin);
    }
}

