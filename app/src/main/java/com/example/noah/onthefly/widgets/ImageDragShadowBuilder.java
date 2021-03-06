package com.example.noah.onthefly.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

/**
 * Created by Alex on 2/25/17.
 */


    public class ImageDragShadowBuilder extends View.DragShadowBuilder {
        public static final String TAG = "ImageDragShadowBuilder";
        View greyBox;

        public ImageDragShadowBuilder(View view) {
            super(view);
            greyBox = view;
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            greyBox.draw(canvas);
            greyBox.setBackgroundColor(Color.BLUE);
            greyBox.setAlpha((float)(0.2));
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            View v = getView();
            int height = v.getHeight();
            int width = v.getWidth();
            shadowSize.set(width, height);
            shadowTouchPoint.set((width / 2), (height / 2));
            Log.d(TAG, "Shadow at: " + shadowTouchPoint.x + " " + shadowTouchPoint.y );
            if (shadowSize.y > v.getHeight()) {
                greyBox.setAlpha(0);
            }
        }
    }


