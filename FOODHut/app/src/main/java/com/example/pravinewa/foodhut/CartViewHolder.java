package com.example.pravinewa.foodhut;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartViewHolder extends RecyclerView.ViewHolder{
    public TextView foodName, foodExpiryDate, foodPrice, foodQuantity;
    public ImageView foodImg;
    public CheckBox cartChecked;

    public CartViewHolder(View itemView){
        super(itemView);

        foodImg = (ImageView)itemView.findViewById(R.id.food_cart_image);
        foodName = (TextView)itemView.findViewById(R.id.food_post_name_cart);
        foodExpiryDate = (TextView)itemView.findViewById(R.id.food_post_expiry_date_cart);
        foodPrice = (TextView)itemView.findViewById(R.id.food_post_price_cart);
        foodQuantity = (TextView)itemView.findViewById(R.id.food_post_quantity_cart);
        cartChecked = (CheckBox)itemView.findViewById(R.id.cartCheckedId);



    }

}