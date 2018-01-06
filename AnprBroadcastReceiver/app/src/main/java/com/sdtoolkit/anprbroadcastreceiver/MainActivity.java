package com.sdtoolkit.anprbroadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sdtoolkit.anpr.broadcast.BroadcastConstants;
import com.sdtoolkit.anpr.api.AnprResult;
import com.sdtoolkit.anpr.storage.AnprContentEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_INSTALL_SDTANPR = 0x4563;

    private TextView mTvNotifications;

    private BroadcastReceiver mAnprBcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBcastIntent(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isAnprServiceIsInstalled()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please install SD-TOOLKIT ANPR Serivce");
            builder.setCancelable(false);
            builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.sdtoolkit.anprservice"));
                    startActivityForResult(intent, REQUEST_INSTALL_SDTANPR);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        mTvNotifications = findViewById(R.id.tv_notifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter anprIntentFilter = new IntentFilter();
        anprIntentFilter.addAction(BroadcastConstants.RESULT_BCAST_INTENT_ACTION);
        registerReceiver(mAnprBcastReceiver, anprIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mAnprBcastReceiver);
    }

    private void onBcastIntent(final Intent intent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String country = "";
                float confidence = 0f;
                Date captureTime = new Date();
                double locationLati = 0;
                double locationLongi = 0;

                if (intent.hasExtra(BroadcastConstants.RESULT_VALUE)) {
                    result = intent.getStringExtra(BroadcastConstants.RESULT_VALUE);
                }
                if (intent.hasExtra(BroadcastConstants.RESULT_COUNTRY)) {
                    country = intent.getStringExtra(BroadcastConstants.RESULT_COUNTRY);
                }
                if (intent.hasExtra(BroadcastConstants.RESULT_CONFIDENCE)) {
                    confidence = intent.getFloatExtra(BroadcastConstants.RESULT_CONFIDENCE, 0f);
                }
                if (intent.hasExtra(BroadcastConstants.RESULT_TIMESTAMP)) {
                    SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                            BroadcastConstants.TIME_STAMP_FORMAT);

                    try {
                        captureTime = timeStampFormat.parse(
                                intent.getStringExtra(BroadcastConstants.RESULT_TIMESTAMP));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (intent.hasExtra(BroadcastConstants.RESULT_GPS_LATI)) {
                    locationLati = intent.getDoubleExtra(BroadcastConstants.RESULT_GPS_LATI, 0.0);
                }
                if (intent.hasExtra(BroadcastConstants.RESULT_GPS_LONGI)) {
                    locationLongi = intent.getDoubleExtra(BroadcastConstants.RESULT_GPS_LONGI, 0.0);
                }

                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");

                mTvNotifications.append(String.format("%s - Result: %s[%s] (%.2f) {%f:%f} \r\n",
                        df.format(captureTime),
                        result,
                        country,
                        confidence,
                        locationLati,
                        locationLongi
                        ));
            }
        });
    }

    private boolean isAnprServiceIsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(
                    "com.sdtoolkit.anprservice", 0);

            if (info != null && info.enabled) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
