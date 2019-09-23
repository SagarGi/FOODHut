package com.example.pravinewa.foodhut;

public class CartCheckOut {
    private String mPostId;
    private long mQuantity;

    public  CartCheckOut(){}

    public CartCheckOut(String mPostId, long mQuantity){
        this.mPostId = mPostId;
        this.mQuantity = mQuantity;
    }

    public String getmPostId() {
        return mPostId;
    }

    public void setmPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    public long getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(long mQuantity) {
        this.mQuantity = mQuantity;
    }
}
