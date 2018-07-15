package com.exophrenik.grinia.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.utilities.CartItem;

import java.util.ArrayList;

public class OrderListCustomAdapter extends ArrayAdapter<CartItem> {


    private TextView itemDescription;
    private ArrayList<CartItem> cartList;
    private Context context;

    public OrderListCustomAdapter(ArrayList<CartItem> cartList, Context context){
        super(context, R.layout.order_cart_list_item);
        this.cartList = cartList;
        this.context = context;
    }


    @Override
    public View getView (final int position, View convertView, ViewGroup parent){

        View cartItemView = convertView;
        if (cartItemView == null){
            cartItemView = LayoutInflater.from(context).inflate(R.layout.order_cart_list_item,parent,false);

        }

        CartItem currentItem = getItem(position);
        itemDescription = (TextView) cartItemView.findViewById(R.id.orderItem_itemDescription);
        itemDescription.setText(currentItem.getName());
        return cartItemView;
    }


}
