package com.exophrenik.grinia.cart;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.utilities.CartItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<CartItem> implements View.OnClickListener {

    private ArrayList<CartItem> cartList;
    private Context context;

    public CustomAdapter(ArrayList<CartItem> cartList, Context context) {
        super(context, R.layout.cart_list_item,cartList);
        this.cartList = cartList;
        this.context = context;

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
        final TextView productQuantity = (TextView) cartItemView.findViewById(R.id.itemQuantityView);
        TextView productPrice = (TextView) cartItemView.findViewById(R.id.itemPriceView);
        Button removeItem = (Button) cartItemView.findViewById(R.id.removeItemButton);

        productName.setText(currentItem.getName());
        productPrice.setText(Double.toString(currentItem.getPriceOfUnit()*currentItem.getQuantity()));
        productQuantity.setText(String.valueOf(currentItem.getQuantity()));
        /*//TODO Implement the edit button for quntity functionality
        productQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
        });*/


        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("RED", "This is item no " + position);
                cartList.remove(position);
                notifyDataSetChanged();
                updateCartListFile();
            }
        });

        return cartItemView;
    }


    private void updateCartListFile(){

        File file = new File(context.getFilesDir(), "cartData");
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



}