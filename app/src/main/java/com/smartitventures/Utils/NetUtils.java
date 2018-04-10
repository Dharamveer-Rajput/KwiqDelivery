package com.smartitventures.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetUtils {


    public static boolean hasConnectivity(Context context){
        boolean hasConnectivity = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        hasConnectivity = info !=null && (info.isConnected());
        return hasConnectivity;
    }
}
