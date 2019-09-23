package com.example.pravinewa.foodhut;

import android.net.Uri;

public class UserPost {

    public String foodName, foodImage;

    public UserPost(){

    }

    public UserPost(String foodName, String foodImage){
        this.foodName = foodName;
        this.foodImage = foodImage;
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

}
