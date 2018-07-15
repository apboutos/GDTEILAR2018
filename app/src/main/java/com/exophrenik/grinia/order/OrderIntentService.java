package com.exophrenik.grinia.order;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.utilities.CartItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OrderIntentService extends IntentService{

    private HttpURLConnection connection;
    private boolean connectionError = false;
    private boolean orderResult = false;
    private String orderInformation;
    private ArrayList<CartItem> cartList;

    public OrderIntentService() {
        super("OrderIntentService");
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        Log.d("RED","Hellow world");
        try {

            formatOrderInformationInJSON(intent);

            createConnectionToServer();

            sendOrderInformationToServer();

            readOrderResultFromServer();

        }
        catch (Exception e) {
            Log.d("RED",e.getMessage(),e);
            connectionError = true;
        }
        finally {
            broadcastResponse();
            connection.disconnect();
        }
    }

    private void formatOrderInformationInJSON(Intent intent) throws Exception{

        cartList = (ArrayList<CartItem>) intent.getSerializableExtra("cartList");

        JSONObject jsonObject = new JSONObject();
        JSONObject infoObject = new JSONObject();
        //TODO fix username
        infoObject.put("username","exophrenik");
        infoObject.put("firstName",intent.getStringExtra("firstName"));
        infoObject.put("middleName",intent.getStringExtra("middleName"));
        infoObject.put("lastName",intent.getStringExtra("lastName"));
        infoObject.put("address",intent.getStringExtra("address"));
        infoObject.put("postalCode",intent.getStringExtra("postalCode"));
        infoObject.put("creditCard",intent.getStringExtra("creditCard"));
        infoObject.put("expirationDate",intent.getStringExtra("expirationDate"));
        infoObject.put("cvs",intent.getStringExtra("cvs"));
        jsonObject.put("0",infoObject);

        int index = 1;
        for (CartItem item : cartList){

            JSONObject tmpArray = new JSONObject();
            tmpArray.put("barcode",item.getBarcode());
            tmpArray.put("quantity",item.getQuantity());
            jsonObject.put(String.valueOf(index),tmpArray);
            index++;
    }

        orderInformation = jsonObject.toString();
        Log.d("RED","JSON Object =" + orderInformation);

    }

    private void createConnectionToServer()throws Exception {

        connection = (HttpURLConnection) new URL(getResources().getString(R.string.order_URL)).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.setRequestProperty("Accept","application/json: charset=UTF-8");
        connection.setRequestMethod("POST");
        Log.d("RED","Request method " + connection.getRequestMethod());
    }

    private void sendOrderInformationToServer() throws Exception{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(orderInformation);
        writer.flush();
        Log.d("RED","LOG: " + orderInformation); // Prints the sent JSON object.
        writer.close();
        Log.d("RED","Response Message " + connection.getResponseMessage());
    }

    private void readOrderResultFromServer() throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        Log.d("RED","LOG: " + stringBuilder.toString()); // Prints the received unparsed JSON object.
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        orderResult = jsonObject.getBoolean("orderResult");

    }

    private void broadcastResponse(){

        Intent orderRespondIntent = new Intent();
        orderRespondIntent.putExtra("orderResult",orderResult);
        orderRespondIntent.putExtra("connectionError", connectionError);
        orderRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        orderRespondIntent.setAction(OrderScreen.OrderResponseReceiver.SERVER_ORDER_RESPONSE);
        sendBroadcast(orderRespondIntent);

    }
}
