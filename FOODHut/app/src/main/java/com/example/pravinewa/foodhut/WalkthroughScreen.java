package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WalkthroughScreen extends AppCompatActivity {

    ViewPager sliderPager;
    LinearLayout dotLayout;

    TextView [] dots;

    Window window;
    SliderAdapter sliderAdapter;

    int positionNum;
    Button getStarted;

    Animation btnAnimIn;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

                View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (Build.VERSION.SDK_INT >= 21) {
                    window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.white));
                }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough_screen);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //ater this activity is launch we need to check sharedpreference

        if(restorePrefData()){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

        sliderPager = (ViewPager) findViewById(R.id.sliderWalkThrough);
        dotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        getStarted = (Button) findViewById(R.id.gettingStarted);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    vibrator.vibrate(25);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent);

                    savePrefsData();
                    finish();
                }catch (Exception e){

                }
            }
        });

        btnAnimIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);

        sliderAdapter = new SliderAdapter(this);
        sliderPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        sliderPager.addOnPageChangeListener(viewListener);
    }

    private boolean restorePrefData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = preferences.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    public void addDotsIndicator(int position){
        dots = new TextView[4];
        dotLayout.removeAllViews();

        for (int i = 0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(getResources().getColor(R.color.light_status));

            dotLayout.addView(dots[i]);

        }

        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.default_status));
        }

        if(position == 3){
            loadLastButton();
        }else{
            hideButton();
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
                addDotsIndicator(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    public void loadLastButton(){

        getStarted.setVisibility(View.VISIBLE);
        getStarted.setAnimation(btnAnimIn);
    }

    public void savePrefsData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }

    public void hideButton(){
        getStarted.setVisibility(View.INVISIBLE);
    }
}
