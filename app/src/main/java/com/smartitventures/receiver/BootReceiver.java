package com.smartitventures.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smartitventures.service.LocationService;

/**
 * Created by dharamveer on 3/2/18.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start your intent service


            Intent i = new Intent(context, LocationService.class);
            context.startService(i);

        }

    }
}
