package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    TextView fullname, email, mobNumber, address;
    String profileUrl;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mUser;
    StorageReference storageReference;
    CircleImageView profileImage;
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        sharedPref = new SharedPref(this);
        View decorView = getWindow().getDecorView();

        if (sharedPref.loadDarkModeState() == true || sharedPref.loadLightModeState() == true) {
            if (sharedPref.loadLightModeState() == true) {
                setTheme(R.style.LightTheme);
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (Build.VERSION.SDK_INT >= 21) {
                    window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.light_status));
                }

            } else if (sharedPref.loadDarkModeState() == true) {
                setTheme(R.style.DarkTheme);
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (Build.VERSION.SDK_INT >= 21) {
                    window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.dark_status));
                }
            }
        } else if (sharedPref.loadLightModeState() == false && sharedPref.loadDarkModeState() == false) {
            setTheme(R.style.DefaultTheme);
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(this.getResources().getColor(R.color.default_status));
            }
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_profile);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarProfile);
        viewPager = (ViewPager) findViewById(R.id.viewpager_profile);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new InCompleteFragment(), "");
        adapter.AddFragment(new CompleteFragment(), "");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (sharedPref.loadDarkModeState() == true) {
            tabLayout.getTabAt(0).setIcon(R.drawable.iconschecklist80white);
            tabLayout.getTabAt(1).setIcon(R.drawable.iconstaskcompleted80white);
        } else {
            tabLayout.getTabAt(0).setIcon(R.drawable.iconschecklist80black);
            tabLayout.getTabAt(1).setIcon(R.drawable.iconstaskcompleted80black);
        }


        toolbar = (Toolbar) findViewById(R.id.profileToolbarId);
        toolbar.setTitle("Profile");
        toolbar.setTitleTextAppearance(this, R.style.ITCavantFont);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");

        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        fullname = (TextView) findViewById(R.id.fullNameProfile);
        email = (TextView) findViewById(R.id.emailProfile);
        mobNumber = (TextView) findViewById(R.id.mobNumberProfile);
        address = (TextView) findViewById(R.id.addressProfile);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                email.setText(user.getEmail());
                mobNumber.setText(user.getPhone());
                address.setText(user.getAddress());
                String profile_Image = (String) dataSnapshot.child("profileUrl").getValue();
                if (profile_Image.equals("null")) {
                        Bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.blank_profile);
                        profileImage.setImageBitmap(Icon);
                } else {

                    Picasso.get().load(profile_Image).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.editProfileButton:
                openEditProfile();
                break;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void openEditProfile() {
        try {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            startActivity(intent);
            ProfileActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}