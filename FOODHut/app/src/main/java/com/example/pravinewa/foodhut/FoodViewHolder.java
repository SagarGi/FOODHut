package com.example.pravinewa.foodhut;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodViewHolder extends RecyclerView.ViewHolder {

    public TextView foodName, foodExpiryDate, tvStock, tvPrice;
    public ImageView foodImg;
    public  ImageView ivStatusImage;


    public FoodViewHolder(View itemView){
        super(itemView);

        foodImg = (ImageView)itemView.findViewById(R.id.food_post_image);
        foodName = (TextView)itemView.findViewById(R.id.food_post_name);
        foodExpiryDate = (TextView)itemView.findViewById(R.id.food_post_expiry_date);
        tvStock = (TextView)itemView.findViewById(R.id.food_stock);
        tvPrice = (TextView)itemView.findViewById(R.id.food_post_price);
        ivStatusImage =(ImageView)itemView.findViewById(R.id.ivStatusImage);

    }
}
