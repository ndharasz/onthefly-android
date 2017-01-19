package com.example.noah.onthefly.activities; /**
 * Created by Alex on 1/19/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.example.noah.onthefly.R;

public class Legal {
    /**
     *  * This file provides simple End User License Agreement
     *  * It shows a simple dialog with the license text, and two buttons.
     *  * If user clicks on 'cancel' button, app closes and user will not be granted access to app.
     *  * If user clicks on 'accept' button, app access is allowed and this choice is saved in preferences
     *  * so next time this will not show, until next upgrade.
     *  
     */


        private String EULA_PREFIX = "EULA";
        private Activity mContext;

        public Legal(Activity context) {
            mContext = context;
        }



        private PackageInfo getPackageInfo() {
            PackageInfo info = null;
            try {
                info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return info;
            }

        public void show() {
            PackageInfo versionInfo = getPackageInfo();

            // The eulaKey changes every time you increment the version number in
            // the AndroidManifest.xml
            final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            boolean bAlreadyAccepted = prefs.getBoolean(eulaKey, false);
            if (bAlreadyAccepted == false) {

                String title = "End User License Agreement";

                String message = "This is where the legal documentation will go. Once the user presses enter, this message will no longer show";

                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {



                    public void onClick(
                        DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(eulaKey, true);
                        editor.commit();
                        dialogInterface.dismiss();

                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        }


                }).setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        mContext.finish();
                        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                       }



                });
             builder.create().show();
               }
            }
    }

