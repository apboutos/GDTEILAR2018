package com.exophrenik.grinia.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.utilities.CartItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<CartItem> implements View.OnClickListener {

    private ArrayList<CartItem> cartList;
    private Context context;
    private String username;

    public CustomAdapter(ArrayList<CartItem> cartList, Context context,String username) {
        super(context, R.layout.cart_list_item,cartList);
        this.cartList = cartList;
        this.context = context;
        this.username = username;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        CartItem cartItem = (CartItem) getItem(position);

        v.getId();
        if (v.getId() == R.id.removeItemButton)
        {
            Log.d("RED","This is item no" + position);
        }

    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent){

        View cartItemView = convertView;
        if (cartItemView == null){
            cartItemView = LayoutInflater.from(context).inflate(R.layout.cart_list_item,parent,false);
        }

        CartItem currentItem = getItem(position);

        TextView productName = (TextView) cartItemView.findViewById(R.id.itemNameView);
        EditText productQuantity = (EditText) cartItemView.findViewById(R.id.itemQuantityView);
        TextView productPrice = (TextView) cartItemView.findViewById(R.id.itemPriceView);
        Button removeItem = (Button) cartItemView.findViewById(R.id.removeItemButton);

        productName.setText(currentItem.getName());
        productPrice.setText(Double.toString(currentItem.getPriceOfUnit()*currentItem.getQuantity()));
        productQuantity.setText(String.valueOf(currentItem.getQuantity()));


        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("RED", "This is item no " + position);
                cartList.remove(position);
                notifyDataSetChanged();
                updateCartListFile();
            }
        });

        productQuantity.setOnFocusChangeListener(new CustomOnFocusListener(productQuantity,position,productPrice,currentItem));


        return cartItemView;
    }


    private void updateCartListFile(){

        File file = new File(context.getFilesDir(), "cartData" + username);
        if (file.exists()) {
            file.delete();
        }
        try{
            FileOutputStream fos = new FileOutputStream(file.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cartList);
            oos.close();
            fos.close();
        }
        catch (Exception e){
            Log.d("RED",e.getMessage());
        }

    }

    public class CustomOnFocusListener implements View.OnFocusChangeListener {

        EditText productQuantity;
        TextView productPrice;
        int position;
        CartItem currentItem;
        public CustomOnFocusListener(EditText productQuantity, int position, TextView productPrice, CartItem currentItem){
            this.productQuantity = productQuantity;
            this.position = position;
            this.productPrice = productPrice;
            this.currentItem = currentItem;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {


            if (productQuantity.getText().toString().equals("") || productQuantity.getText().toString() == null){
                productQuantity.setText("0");
            }
            if (cartList.size() > position){
                if (hasFocus){

                    cartList.get(position).setQuantity(Integer.valueOf(productQuantity.getText().toString()));
                    updateCartListFile();
                    productPrice.setText(Double.toString(currentItem.getPriceOfUnit()*currentItem.getQuantity()));
                }
                if (!hasFocus){

                    cartList.get(position).setQuantity(Integer.valueOf(productQuantity.getText().toString()));
                    updateCartListFile();
                    productPrice.setText(Double.toString(currentItem.getPriceOfUnit()*currentItem.getQuantity()));
                }
            }
        }


    }



}