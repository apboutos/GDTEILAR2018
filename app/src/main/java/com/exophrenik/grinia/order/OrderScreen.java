package com.exophrenik.grinia.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.login.LoginScreen;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.server.ServerSimulationService;
import com.exophrenik.grinia.utilities.CartItem;
import com.exophrenik.grinia.utilities.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
    private TextView priceLabel;
    private TextView priceBox;
    private Button completeOrderButton;
    private Button cancelButton;
    private ArrayList<CartItem> cartList;
    private boolean onlineMode;
    private IntentFilter filter;
    private OrderResponseReceiver receiver;

    public class OrderResponseReceiver extends BroadcastReceiver {

        public static final String SERVER_ORDER_RESPONSE = "server";

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("RED","red");
            if (intent.getBooleanExtra("connectionError",false)){

                showInfo("Server does not respond");

            }
            else if(!intent.getBooleanExtra("orderResult",false)){
                showInfo("Error: Your order was not submitted");
            }
            else {
                Toast registerComplete = Toast.makeText(getApplicationContext(),R.string.order_complete_message,Toast.LENGTH_SHORT);
                registerComplete.setGravity(Gravity.CENTER,0,0);
                registerComplete.show();

                Intent nextScreen = new Intent(getApplicationContext(),ScanScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_screen);

        cartList = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartList");
        onlineMode = getIntent().getBooleanExtra("onlineMode",false);
        username = getIntent().getStringExtra("username");

        firstNameBox = (TextView) findViewById(R.id.orderFirstNameBox);
        middleNameBox = (TextView) findViewById(R.id.orderMiddleNameBox);
        lastNameBox = (TextView) findViewById(R.id.orderLastNameBox);
        addressBox = (TextView) findViewById(R.id.orderAddressBox);
        postalCodeBox = (TextView) findViewById(R.id.orderPostalBox);
        creditCardBox = (TextView) findViewById(R.id.orderCreditCardNumberBox);
        cvsBox = (TextView) findViewById(R.id.orderCVSBox);
        expirationDateBox = (TextView) findViewById(R.id.orderCreditExpirationBox);
        priceLabel = (TextView) findViewById(R.id.orderTotalPriceLabelBox);
        priceBox = (TextView) findViewById(R.id.orderTotalPriceBox);
        completeOrderButton = (Button) findViewById(R.id.orderCompleteButton);
        cancelButton = (Button) findViewById(R.id.orderCancelButton);

        priceLabel.setText(R.string.order_price_sum);
        Double tmp = 0.00;
        for (CartItem tmpItem : cartList){
            tmp = tmp + tmpItem.getQuantity() * tmpItem.getPriceOfUnit();
        }
        priceBox.setText(String.valueOf(tmp));

        /* Create and register the login receiver with the appropriate filter */
        filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(LoginScreen.LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        receiver = new OrderScreen.OrderResponseReceiver();
        registerReceiver(receiver,filter);


        completeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderToServer();


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), ScanScreen.class);
                nextScreen.putExtra("username",username);
                nextScreen.putExtra("onlineMode",onlineMode);
                startActivity(nextScreen);
            }
        });


        readProfileData();

    }

    private void readProfileData(){

        File file = new File(getFilesDir(),"profile" + username);
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
            Intent  orderIntentService;
            if(onlineMode ){
                orderIntentService = new Intent(OrderScreen.this, OrderIntentService.class);
                Log.d("RED","online");
            }
            else{
                orderIntentService = new Intent(OrderScreen.this, ServerSimulationService.class);
                Log.d("RED","offline");
            }
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
            orderIntentService.putExtra("action","order");
            startService(orderIntentService);
        }
        catch (Exception e){
            Log.d("RED","Error: " + e.getMessage());
        }
    }


    private void showInfo(String message){
        Toast registerComplete = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
        registerComplete.setGravity(Gravity.CENTER,0,0);
        registerComplete.show();
    }

}
