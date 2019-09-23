package com.example.pravinewa.foodhut;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {


    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.foodhut1,
            R.drawable.buy1,
            R.drawable.sell1,
            R.drawable.dontate1
    };

    public String[] slide_heading = {
            "FOODHut",
            "Buy",
            "Sell",
            "Donate"
    };


    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout)o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_scr,container,false);

        ImageView slide_img= (ImageView) view.findViewById(R.id.sliderImage);
        TextView slide_head = (TextView) view.findViewById(R.id.sliderHeading);

        slide_img.setImageResource(slide_images[position]);
        slide_head.setText(slide_heading[position]);
        container.addView(view);

        return view;
    };

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
