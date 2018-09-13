package com.exophrenik.grinia.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.order.OrderScreen;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.utilities.CartItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class CartScreen extends AppCompatActivity {

    private ArrayList<CartItem> cartList;
    private ListView cartListView;
    private Button orderButton;
    private Button cancelButton;
    private CustomAdapter adapter;
    private boolean onlineMode;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);

        onlineMode = getIntent().getBooleanExtra("onlineMode",false);
        username = getIntent().getStringExtra("username");
        cartListView = (ListView) findViewById(R.id.cartListView);
        orderButton  = (Button) findViewById(R.id.orderButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        readCartListFromFile();




        adapter = new CustomAdapter(cartList,getApplicationContext(),username);
        cartListView.setAdapter(adapter);

        orderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), OrderScreen.class);
                nextScreen.putExtra("cartList",cartList);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(getApplicationContext(), ScanScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
        });

    }

    private void readCartListFromFile(){

        File file = new File(getApplicationContext().getFilesDir(), "cartData" + username);
        // If the file exists read the cartList from the file otherwise create a new empty arrayList
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file.getPath());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                cartList = (ArrayList<CartItem>) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                Log.d("RED","CartList exists");
            }
            catch (Exception e) {
                Log.d("RED", e.getMessage());
            }
        }
        else {
            cartList = new ArrayList<CartItem>();
            Log.d("RED","CartList doesnt exist.");
        }
    }
}
