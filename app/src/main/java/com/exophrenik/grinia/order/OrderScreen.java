package com.exophrenik.grinia.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.product.ProductScreen;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.utilities.CartItem;
import com.exophrenik.grinia.utilities.Profile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OrderScreen extends AppCompatActivity {

    private String username;
    private TextView firstNameBox;
    private TextView middleNameBox;
    private TextView lastNameBox;
    private TextView addressBox;
    private TextView postalCodeBox;
    private TextView creditCardBox;
    private TextView expirationDateBox;
    private TextView cvsBox;
    private ListView itemList;
    private TextView priceLabel;
    private TextView priceBox;
    private Button completeOrderButton;
    private Button cancelButton;
    private ArrayList<CartItem> cartList;

    public class OrderResponseReceiver extends BroadcastReceiver {

        public static final String SERVER_ORDER_RESPONSE = "server";

        @Override
        public void onReceive(Context context, Intent intent) {

                Log.d("RED","red");
                if (intent.getBooleanExtra("connectionError",false) == false){

                    showInfo("Server does not respond");

                }
                if(intent.getBooleanExtra("orderResult",false) == false){
                    showInfo("Error: Your order was not submitted");
                }
                else {
                    showInfo("Thank you, your order has been submmitted.");
                }

        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        cartList = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartList");

        firstNameBox = (TextView) findViewById(R.id.orderFirstNameBox);
        middleNameBox = (TextView) findViewById(R.id.orderMiddleNameBox);
        lastNameBox = (TextView) findViewById(R.id.orderLastNameBox);
        addressBox = (TextView) findViewById(R.id.orderAddressBox);
        postalCodeBox = (TextView) findViewById(R.id.orderPostalBox);
        creditCardBox = (TextView) findViewById(R.id.orderCreditCardNumberBox);
        cvsBox = (TextView) findViewById(R.id.orderCVSBox);
        expirationDateBox = (TextView) findViewById(R.id.orderCreditExpirationBox);
        itemList = (ListView) findViewById(R.id.orderItemList);
        priceLabel = (TextView) findViewById(R.id.orderTotalPriceLabelBox);
        priceBox = (TextView) findViewById(R.id.orderTotalPriceBox);
        completeOrderButton = (Button) findViewById(R.id.orderCompleteButton);
        cancelButton = (Button) findViewById(R.id.orderCancelButton);

        priceLabel.setText("Total Price");
        Double tmp = 0.00;
        for (CartItem tmpItem : cartList){
            tmp = tmp + tmpItem.getQuantity() * tmpItem.getPriceOfUnit();
        }
        priceBox.setText(String.valueOf(tmp));

        completeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderToServer();
                //Log.d("RED","Im here");
                //Intent nextScreen = new Intent(getApplicationContext(), ScanScreen.class);
                //startActivity(nextScreen);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), ScanScreen.class);
                startActivity(nextScreen);
            }
        });


        readProfileData();
        itemList.setAdapter(new OrderListCustomAdapter(cartList,getApplicationContext()));

    }

    private void readProfileData(){

        File file = new File (getFilesDir(),"profile");
        Profile profile = new Profile();
        if(file.exists()){

            try{

                FileInputStream fis = new FileInputStream(file.getPath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                profile = (Profile) ois.readObject();
                ois.close();
                fis.close();

            }
            catch (Exception e){
                Log.d("RED", e.getMessage());
            }
        }

        firstNameBox.setText(profile.getFirstName());
        middleNameBox.setText(profile.getMiddleName());
        lastNameBox.setText(profile.getLastName());
        addressBox.setText(profile.getAddress());
        postalCodeBox.setText(profile.getPostalCode());
        creditCardBox.setText(profile.getCreditCartNumber());
        expirationDateBox.setText(profile.getCreditExpirationDate());
        cvsBox.setText(profile.getCreditCV());

    }

    private void sendOrderToServer(){
        try {
            Intent orderIntentService = new Intent(OrderScreen.this, OrderIntentService.class);
            orderIntentService.putExtra("username",username);
            orderIntentService.putExtra("firstName",firstNameBox.getText().toString());
            orderIntentService.putExtra("middleName",middleNameBox.getText().toString());
            orderIntentService.putExtra("lastName",lastNameBox.getText().toString());
            orderIntentService.putExtra("address",addressBox.getText().toString());
            orderIntentService.putExtra("postalCode",postalCodeBox.getText().toString());
            orderIntentService.putExtra("creditCard",creditCardBox.getText().toString());
            orderIntentService.putExtra("expirationDate",expirationDateBox.getText().toString());
            orderIntentService.putExtra("cvs",cvsBox.getText().toString());
            orderIntentService.putExtra("cartList",cartList);
            startService(orderIntentService);
        }
        catch (Exception e){
            Log.d("RED",e.getMessage());
        }
    }


    private void showInfo(String message){
        Toast.makeText(this.getApplicationContext(),message,Toast.LENGTH_LONG);
    }



}
