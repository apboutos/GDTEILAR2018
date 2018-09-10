package com.exophrenik.grinia.scan;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.exophrenik.grinia.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by exophrenik on 7/5/2018.
 */

public class ScanIntentService extends IntentService {

    private HttpURLConnection connection;
    private String barcodeInformation;
    private boolean connectionError = false;
    private boolean barcodeResult = false;
    private String productName;
    private String productDescription;
    private double productPrice;

    public ScanIntentService() {
        super("ScanIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            //serverSimulation(intent);

            //formatBarcodeInformationInJSON(intent);

            createConnectionToServer(intent);

            //sendBarcodeInformationToServer();

            readBarcodeResultFromServer();

        }
        catch (Exception e) {
            Log.e("DEBUG",e.getMessage(),e);
            connectionError = true;
        }
        finally {
            broadcastResponse();
            connection.disconnect();
        }


    }

    //TODO This function simulates the server's response and must be deleted when the
    //server is implemented
    private void serverSimulation(Intent intent){

        if (intent.getStringExtra("submittedBarcode").equals("123456789012"))
        {
            barcodeResult = true;
            productName = "Patates";
            productDescription = "Oi patates einai polu kales";
            productPrice = 12.3;
        }
        else if (intent.getStringExtra("submittedBarcode").equals("671860013624"))
        {
            barcodeResult = true;
            productName = "Ntomates";
            productDescription = "Oi ntomates einai sapies";
            productPrice = 22.3;
        }
        else{
            barcodeResult = false;
        }




    }

    /*
    private void formatBarcodeInformationInJSON(Intent intent) throws Exception{
        String submittedBarcode = intent.getStringExtra("submittedBarcode");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("barcode", submittedBarcode);
        barcodeInformation = jsonObject.toString();

    }*/

    private void createConnectionToServer(Intent intent)throws Exception {

        String submittedBarcode = intent.getStringExtra("submittedBarcode");
        connection = (HttpURLConnection) new URL(getResources().getString(R.string.scan_URL) + "?barcode=" + submittedBarcode).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.setRequestProperty("Accept","application/json: charset=UTF-8");
        connection.setRequestMethod("GET");
        Log.d("DEBUG","Request method " + connection.getRequestMethod());
    }

    /*
    private void sendBarcodeInformationToServer() throws Exception{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(barcodeInformation);
        writer.flush();
        Log.e("DEBUG","LOG: " + barcodeInformation); // Prints the sent JSON object.
        writer.close();
        Log.d("DEBUG","Response Message " + connection.getResponseMessage());
    }*/

    private void readBarcodeResultFromServer() throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        Log.e("DEBUG","LOG: " + stringBuilder.toString()); // Prints the received unparsed JSON object.
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        barcodeResult = jsonObject.getBoolean("barcodeResult");
        productName = jsonObject.getString("productName");
        productDescription = jsonObject.getString("productDescription");
        productPrice = jsonObject.getDouble("productPrice");

    }

    private void broadcastResponse(){

        Intent barcodeRespondIntent = new Intent();
        barcodeRespondIntent.putExtra("barcodeResult",barcodeResult);
        barcodeRespondIntent.putExtra("connectionError", connectionError);
        barcodeRespondIntent.putExtra("productName",productName);
        barcodeRespondIntent.putExtra("productDescription",productDescription);
        barcodeRespondIntent.putExtra("productPrice",productPrice);
        barcodeRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        barcodeRespondIntent.setAction(ScanScreen.ScanResponseReceiver.SERVER_SCAN_RESPONSE);
        sendBroadcast(barcodeRespondIntent);

    }

}
