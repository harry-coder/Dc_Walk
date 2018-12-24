package com.dc_walk.CustomClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.se.omapi.SEService;

import com.dc_walk.CustomInterfaces.OnConnectivityListener;
import com.dc_walk.Home_Activity;


/**
 * Created by Harpreet on 15/02/2018.
 */

public class InternetConnectionCheck extends BroadcastReceiver {

    public static OnConnectivityListener connectivityListener;


    public InternetConnectionCheck()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected= Home_Activity.checkInternetConnection(context);

        if(connectivityListener!=null)
        {
            connectivityListener.isConnectivityAvailable ( isConnected );
        }

    }




}
