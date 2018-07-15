package com.exophrenik.grinia.retrieval;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.HttpURLConnection;

/**
 * Created by exophrenik on 9/10/2017.
 * //TODO
 */

public class RerieveIntentService extends IntentService {

    private HttpURLConnection connection;

    public RerieveIntentService(){
        super("RetrieveIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try{

        }
        catch (Exception e)
        {
            Log.e("DEBUG",e.getMessage(),e);
        }
    }
}
