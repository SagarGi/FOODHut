package com.example.pravinewa.foodhut;

import android.net.Uri;

public class HistoryOrder {

    public String foodName, foodImage;
    long foodPrice, foodQuantity, foodTotalPrice;

    public HistoryOrder(){

    }

    public HistoryOrder(String foodName, String foodImage, long foodPrice, long foodQuantity, long foodTotalPrice){
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
        this.foodTotalPrice = foodTotalPrice;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public long getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(long foodPrice) {
        this.foodPrice = foodPrice;
    }

    public long getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(long foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public long getFoodTotalPrice() {
        return foodTotalPrice;
    }

    public void setFoodTotalPrice(long foodTotalPrice) {
        this.foodTotalPrice = foodTotalPrice;
    }
}