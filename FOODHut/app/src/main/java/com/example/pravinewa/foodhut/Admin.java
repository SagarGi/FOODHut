package com.example.pravinewa.foodhut;

import android.net.Uri;

public class Admin {

    public String foodName, foodImage, userID;

    public Admin(){

    }

    public Admin(String foodName, String foodImage, String userID){
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.userID = userID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}