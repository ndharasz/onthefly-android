package com.example.noah.onthefly.fragments;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.noah.onthefly.activities.ActivityLogin;

/**
 * Created by Brian Woodbury on 1/24/2017.
 */

public class FragmentPasswordReset extends AlertDialog {
    public FragmentPasswordReset(final Context context, String email) {
        super(context);
        setButton(BUTTON_NEUTRAL, "OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent activityLoginIntent = new Intent(context, ActivityLogin.class);
                context.startActivity(activityLoginIntent);
            }
        });
        setMessage("A password reset email has been sent to: " + email);
    }
}
