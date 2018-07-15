package com.exophrenik.grinia.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by exophrenik on 4/10/2017.
 */

public class Network {

    private Network(){

    }

    public static boolean isNetworkAvailable(AppCompatActivity activity) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                Log.d("DEBUG", "No network has been found");
                return false;
            } else if (!activeNetworkInfo.isConnected()) {
                Log.d("DEBUG", "Network found but no connection is established");
                return false;
            } else {
                return true;
            }
        }
        catch (Exception e){
            Log.e("DEBUG","NETWORK ERROR: " + e.getMessage(),e);
            return false;
        }
    }


    public enum LoginResult {
        OK,
        WRONG_CREDENTIALS,
        HOST_UNREACHABLE;
    }

}
