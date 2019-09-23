package com.example.pravinewa.foodhut;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class IntrestedItemViewHolder extends RecyclerView.ViewHolder{

    TextView intrestedDescription,tvDear,tvTime;
    ImageView callToIntrestedUser;
    TextView quantity_Ordered,food_Price_odered_perunit,total_price_odered,tvShippingAddress;

    public IntrestedItemViewHolder(View itemView) {

        super(itemView);
        intrestedDescription = itemView.findViewById(R.id.intrestedDescription);
        callToIntrestedUser = itemView.findViewById(R.id.callToIntrestedUser);
        tvDear = itemView.findViewById(R.id.tvDear);
        tvTime = itemView.findViewById(R.id.tvTime);
        quantity_Ordered = itemView.findViewById(R.id.tvQuantity);
        food_Price_odered_perunit = itemView.findViewById(R.id.tvPricePerUnit);
        total_price_odered = itemView.findViewById(R.id.tvTotalPrice);
        tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);

    }
}
