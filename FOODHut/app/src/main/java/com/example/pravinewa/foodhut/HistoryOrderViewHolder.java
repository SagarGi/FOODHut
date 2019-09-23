package com.example.pravinewa.foodhut;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryOrderViewHolder extends RecyclerView.ViewHolder{
        public TextView foodName, foodPrice, foodQuantity, foodTotalPrice;
        public ImageView foodImg;

        public HistoryOrderViewHolder(View itemView){
            super(itemView);

            foodImg = (ImageView)itemView.findViewById(R.id.food_history_image);
            foodName = (TextView)itemView.findViewById(R.id.food_post_name_history);
            foodPrice = (TextView)itemView.findViewById(R.id.food_post_price_history);
            foodQuantity = (TextView)itemView.findViewById(R.id.food_post_quantity_history);
            foodTotalPrice = (TextView) itemView.findViewById(R.id.food_total_price_history);


        }

    }
