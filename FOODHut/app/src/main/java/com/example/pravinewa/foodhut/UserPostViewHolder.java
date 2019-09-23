package com.example.pravinewa.foodhut;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserPostViewHolder extends RecyclerView.ViewHolder {

    public TextView foodName;
    public ImageView foodImg;

    public UserPostViewHolder(View itemView){
        super(itemView);

        foodImg = (ImageView)itemView.findViewById(R.id.food_user_post_image);
        foodName = (TextView)itemView.findViewById(R.id.food_user_post_name);

    }
}