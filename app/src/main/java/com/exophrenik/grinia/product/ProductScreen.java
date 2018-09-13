package com.exophrenik.grinia.product;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.utilities.CartItem;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ProductScreen extends AppCompatActivity {

    //Interface elements.
    private TextView productNameBox;
    private TextView productPriceBox;
    private TextView productDescriptionBox;
    private TextView productQuantityBox;
    private Button addToCartButton;
    private Boolean onlineMode;
    private String username;


    private ArrayList<CartItem> cartList;
    private Intent nextScreen;
    private Bundle productBundle;

    // TODO The utility of this class is ready. I just need to polish the gui.

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_screen);

        productBundle = getIntent().getExtras();
        onlineMode = getIntent().getBooleanExtra("onlineMode", false);
        username = getIntent().getStringExtra("username");
        productNameBox = (TextView) findViewById(R.id.productName);
        productDescriptionBox = (TextView) findViewById(R.id.productDescription);
        productPriceBox = (TextView) findViewById(R.id.productPrice);
        productQuantityBox = (TextView) findViewById(R.id.productQuanity);
        addToCartButton = (Button) findViewById(R.id.addToCartButton);


        // If the product screen appears showing the default values there is an error
        productNameBox.setText(productBundle.getString("productName","Anavolika"));
        productDescriptionBox.setText(productBundle.getString("productDescription","Ta kalutera anavolika tou magaziou"));
        productPriceBox.setText(String.valueOf(productBundle.getDouble("productPrice",13.87)));


        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                readItemList();
                updateItemList();

                nextScreen = new Intent(getApplicationContext(),ScanScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
        });


    }

    private void readItemList() {

        File file = new File(getApplicationContext().getFilesDir(), "cartData" + username);

        // If the file exists read the arrayList from the file, otherwise create an empty arrayList
        if (file.exists()) {
            try {

                FileInputStream inputStream = new FileInputStream(file.getPath());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                cartList = (ArrayList<CartItem>) objectInputStream.readObject();
                if (cartList == null) Log.d("RED","cartList is null");
                objectInputStream.close();
            }
            catch (Exception e)
            {
                    Log.e("RED", e.getMessage());
            }
        }
        else
        {
            cartList = new ArrayList<CartItem>();
        }
    }


    private void updateItemList(){

        CartItem item = new CartItem();
        item.setBarcode(productBundle.getString("barcode","000000000"));
        item.setName(productBundle.getString("productName","Anavolika"));
        item.setDescription(productBundle.getString("productDescription","Ta kalutera anavolika tou magaziou"));
        item.setPriceOfUnit(productBundle.getDouble("productPrice",13.87));
        item.setQuantity(Integer.parseInt(productQuantityBox.getText().toString()));


        boolean itemExists = false;
        for (CartItem tmp : cartList){

            if (tmp.getBarcode().equals(item.getBarcode())){

                tmp.setQuantity(tmp.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }
        if (!itemExists){
            cartList.add(item);
        }
        File file = new File(getApplicationContext().getFilesDir(), "cartData" + username);
        // Write the cartList as a serializable object in the new file we just created
        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cartList);
            oos.close();
        }
        catch (Exception e){
            Log.d("RED",e.getMessage());
        }
    }

}
