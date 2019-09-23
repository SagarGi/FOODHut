package com.example.pravinewa.foodhut;

public class IntrestedItem {

    String intrestedUserName, intrestedUserContact, intrestedFoodName,intrestedTime, intrestedAddress;
    long quantity, total_price, item_price_per_unit;

    public IntrestedItem()
    {

    }

    public IntrestedItem(String intrestedUserName, String intrestedUserContact, String intrestedFoodName, String intrestedTime, long quantity , long total_price, long item_price_per_unit, String intrestedAddress)
    {
        this.intrestedUserName = intrestedUserName;
        this.intrestedUserContact = intrestedUserContact;
        this.intrestedFoodName = intrestedFoodName;
        this.intrestedTime = intrestedTime;
        this.quantity = quantity;
        this.total_price = total_price;
        this.item_price_per_unit = item_price_per_unit;
        this.intrestedAddress = intrestedAddress;
    }

    public String getIntrestedUserName() {
        return intrestedUserName;
    }

    public void setIntrestedUserName(String intrestedUserName) {
        this.intrestedUserName = intrestedUserName;
    }

    public String getIntrestedUserContact() {
        return intrestedUserContact;
    }

    public void setIntrestedUserContact(String intrestedUserContact) {
        this.intrestedUserContact = intrestedUserContact;
    }

    public String getIntrestedFoodName() {
        return intrestedFoodName;
    }

    public void setIntrestedFoodName(String intrestedFoodName) {
        this.intrestedFoodName = intrestedFoodName;
    }

    public String getIntrestedTime() {
        return intrestedTime;
    }

    public void setIntrestedTime(String intrestedTime) {
        this.intrestedTime = intrestedTime;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTotal_price() {
        return total_price;
    }

    public void setTotal_price(long total_price) {
        this.total_price = total_price;
    }

    public long getItem_price_per_unit() {
        return item_price_per_unit;
    }

    public void setItem_price_per_unit(long item_price_per_unit) {
        this.item_price_per_unit = item_price_per_unit;
    }

    public String getIntrestedAddress() {
        return intrestedAddress;
    }

    public void setIntrestedAddress(String intrestedAddress) {
        this.intrestedAddress = intrestedAddress;
    }
}
