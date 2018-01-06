package com.sdtoolkit.anprbroadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.sdtoolkit.anpr.broadcast.BroadcastConstants;

/**
 * Class {@link GlobalResultReceiver} implements ...
 */

public class GlobalResultReceiver extends BroadcastReceiver {
    private static final String TAG = "ANPR-BCAST";

    private static Toast gToast;
    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra(BroadcastConstants.RESULT_VALUE);
        String country = intent.getStringExtra(BroadcastConstants.RESULT_VALUE);

        if (gToast != null) {
            // Cancel current toast just in case it is visible
            gToast.cancel();
        }

        gToast = Toast.makeText(context.getApplicationContext(), "Received results: " + result,
                Toast.LENGTH_SHORT);
        gToast.show();

        Log.d(TAG, "Received results:" + result);
    }
}
