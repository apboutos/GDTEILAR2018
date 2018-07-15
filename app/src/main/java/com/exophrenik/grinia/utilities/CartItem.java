package com.exophrenik.grinia.utilities;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by exophrenik on 9/5/2018.
 */

public class CartItem  implements Serializable{

    private String barcode;
    private String name;
    private String description;
    private double priceOfUnit;
    private double totalPrice;
    private int quantity;

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriceOfUnit(double priceOfUnit) {
        this.priceOfUnit = priceOfUnit;
    }

    public void setTotalPrice(){

        totalPrice = quantity * priceOfUnit;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPriceOfUnit() {
        return priceOfUnit;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
