package com.example.pravinewa.foodhut;

import android.net.Uri;

public class Cart {

    public String foodName, foodExpiry, foodImage;
    long foodPrice, foodQuantity;

    public Cart(){

    }

    public Cart(String foodName, String foodExpiry, String foodImage, long foodPrice, long foodQuantity){
        this.foodName = foodName;
        this.foodExpiry = foodExpiry;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodExpiry() {
        return foodExpiry;
    }

    public void setFoodExpiry(String foodExpiry) {
        this.foodExpiry = foodExpiry;
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
}