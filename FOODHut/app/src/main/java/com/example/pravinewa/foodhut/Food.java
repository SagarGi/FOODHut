package com.example.pravinewa.foodhut;

public class Food {
    private String itemName;
    private String itemDescription;
    private long itemPrice;
    private String itemCategory;
    private String itemStatus;
    private String itemExpiryDate;
    private String imageUrl;
    private String currentDate;
    private long stockNo;
    private String userID;

    public Food() {

    }

    public Food(String itemName, String itemDescription, long itemPrice, String itemCategory, String itemStatus, String itemExpiryDate, String imageUrl, String currentDate, long stockNo, String userID) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemStatus = itemStatus;
        this.itemExpiryDate = itemExpiryDate;
        this.imageUrl = imageUrl;
        this.currentDate = currentDate;
        this.stockNo = stockNo;
        this.itemDescription = itemDescription;
        this.userID = userID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }



    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemExpiryDate() {
        return itemExpiryDate;
    }

    public void setItemExpiryDate(String itemExpiryDate) {
        this.itemExpiryDate = itemExpiryDate;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }



    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public long getStockNo() {
        return stockNo;
    }

    public void setStockNo(long stockNo) {
        this.stockNo = stockNo;
    }
}

