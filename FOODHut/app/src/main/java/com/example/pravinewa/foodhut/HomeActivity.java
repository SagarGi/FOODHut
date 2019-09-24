package com.example.pravinewa.foodhut;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HomeActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Toolbar toolbar;
    Window window;
    String sortView = "hide";
    RelativeLayout sortLayout;
    private RadioGroup radioGroup;
    FloatingActionButton floatingActionButton;
    Button defaultBut, fruitBut, vegetableBut, cookBut, fridgeBut, itemForDonation;
    private Toast backToast;
    private long backPressedTime;
    Vibrator vibrator;
    String statusInOut = "empty";
    String signInTitle = "Sign In";
    String signOutTitle = "Sign Out";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    FirebaseUser mUser;
    FirebaseRecyclerOptions<Food> recyclerOptions;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> recyclerAdapter;

    private boolean isSpeakButtonLongPressed = false;
    private boolean isMovedPressed = false;

    SwipeRefreshLayout swipeRefreshLayout;
    private static final String[] path = {"Recent", "Price", "Expiration Date"};
    CoordinatorLayout homeId;

    String layouts = "grid";
    Boolean NetworkStatus = false;
    public int cart_count = 0;
    Boolean lightTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        View decorView = getWindow().getDecorView();

        if (sharedPref.loadDarkModeState() == true || sharedPref.loadLightModeState() == true) {
            if (sharedPref.loadLightModeState() == true) {
                setTheme(R.style.LightTheme);
                lightTheme = true;
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
        setContentView(R.layout.activity_home);
        homeId = (CoordinatorLayout) findViewById(R.id.homeView);

        try {
            if (!isNetworkConnected(HomeActivity.this)) {
                NetworkStatus = false;
                buildDialog(HomeActivity.this).show();
            } else {
                NetworkStatus = true;
            }
        } catch (Exception e) {

        }

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.keepSynced(false);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshHome);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databaseReference.keepSynced(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                                .setQuery(databaseReference, Food.class).build();
                        viewPostRecent();
                        onResume();
                    }
                }, 1500);
            }
        });


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        recyclerView = (RecyclerView) findViewById(R.id.food_post_list);
        recyclerView.setHasFixedSize(true);


        toolbar = (Toolbar) findViewById(R.id.homeToolbarId);
        toolbar.setTitle("FOODHut");
        toolbar.setTitleTextAppearance(this, R.style.ITCavantFont);
        setSupportActionBar(toolbar);

        sortLayout = (RelativeLayout) findViewById(R.id.sortViewDisplay);


        radioGroup = (RadioGroup) findViewById(R.id.sortRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                switch (index) {
                    case 0:
                        try {
                            recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                                    .setQuery(databaseReference, Food.class).build();
                            viewPostRecent();
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Recent", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            onResume();
                            break;
                        } catch (Exception e) {

                        }
                    case 1:
                        try {
                            recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                                    .setQuery(databaseReference.orderByChild("itemExpiryDate").startAt("2019-08-27").endAt("2030-08-27"), Food.class).build();
                            viewPost();
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Expiry Date", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            onResume();
                            break;
                        } catch (Exception e) {

                        }
                    case 2:
                        try {
                            ;
                            recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                                    .setQuery(databaseReference.orderByChild("itemPrice").startAt(1), Food.class).build();
                            viewPostPrice();
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Price", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            onResume();
                            break;
                        } catch (Exception e) {

                        }
                }

            }
        });


        floatingActionButton = (FloatingActionButton) findViewById(R.id.floationgButtonAddBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (NetworkStatus) {
                        if (mUser != null && mUser.isEmailVerified()) {
                            openAddFoodActivity();

                        } else {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            vibrator.vibrate(20);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    } else {
                        buildDialog(HomeActivity.this).show();
                    }
                } catch (Exception e) {

                }
            }
        });

        defaultBut = (Button) findViewById(R.id.defaultButton);
        fruitBut = (Button) findViewById(R.id.fruitsButton);
        vegetableBut = (Button) findViewById(R.id.vegetableButton);
        cookBut = (Button) findViewById(R.id.cookFoodButton);
        fridgeBut = (Button) findViewById(R.id.fridgeItemButton);
        itemForDonation = (Button) findViewById(R.id.itemForDonation);
        defaultBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openDefaultCategory();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference, Food.class).build();
                    viewPostRecent();
                    onResume();
                } catch (Exception e) {

                }
            }
        });
        vegetableBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openVegetableCategory();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference.orderByChild("itemCategory").equalTo("Vegetables"), Food.class).build();
                    viewPost();
                    onResume();
                } catch (Exception e) {

                }
            }
        });
        fruitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openFruitCategory();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference.orderByChild("itemCategory").equalTo("Fruits"), Food.class).build();
                    viewPost();
                    onResume();
                } catch (Exception e) {

                }
            }
        });
        cookBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openCookCategory();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference.orderByChild("itemCategory").equalTo("Food"), Food.class).build();
                    viewPost();
                    onResume();
                } catch (Exception e) {

                }
            }
        });
        fridgeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openFridgeCategory();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference.orderByChild("itemCategory").equalTo("Groceries"), Food.class).build();
                    viewPost();
                    onResume();
                } catch (Exception e) {

                }
            }
        });

        itemForDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    openDonation();
                    recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                            .setQuery(databaseReference.orderByChild("itemStatus").equalTo("Donate"), Food.class).build();
                    viewPost();
                    onResume();
                } catch (Exception e) {

                }
            }
        });

        recyclerOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(databaseReference, Food.class).build();
        viewPostRecent();

        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int Size = (int) dataSnapshot.getChildrenCount();
                        cart_count = Size;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } catch (Exception e) {

        }

    }

    public void viewPostRecent() {


        recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(FoodViewHolder holder, int position, Food model) {

                final String post_key = getRef(position).getKey();
                try {
                    Picasso.get().load(model.getImageUrl()).into(holder.foodImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext(), "Error on load image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                }
                String foodStatus = model.getItemStatus();
                long bestPrice = model.getItemPrice();

                //delete item automatically


                String exp_date = model.getItemExpiryDate();
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                final String saveCurrentDate = currentDate.format(calendar.getTime());
                String expiry_year = (((Character.toString(exp_date.charAt(0))).concat(Character.toString(exp_date.charAt(1)))).concat(Character.toString(exp_date.charAt(2)))).concat(Character.toString(exp_date.charAt(3)));
                int e_year = Integer.parseInt(expiry_year);
                String current_year = (((Character.toString(saveCurrentDate.charAt(0))).concat(Character.toString(saveCurrentDate.charAt(1)))).concat(Character.toString(saveCurrentDate.charAt(2)))).concat(Character.toString(saveCurrentDate.charAt(3)));
                int c_year = Integer.parseInt(current_year);

                String expiry_month = (Character.toString(exp_date.charAt(5))).concat(Character.toString(exp_date.charAt(6)));
                int e_month = Integer.parseInt(expiry_month);
                String current__month = (Character.toString(saveCurrentDate.charAt(5))).concat(Character.toString(saveCurrentDate.charAt(6)));
                int c_month = Integer.parseInt(current__month);

                String expiry_day = (Character.toString(exp_date.charAt(8))).concat(Character.toString(exp_date.charAt(9)));
                int e_day = Integer.parseInt(expiry_day);

                String current_day = (Character.toString(saveCurrentDate.charAt(8))).concat(Character.toString(saveCurrentDate.charAt(9)));
                int c_day = Integer.parseInt(current_day);
                // Automatic Delete
                try {
                    if (e_year == c_year && e_month == c_month && e_day == c_day) {


                        holder.foodExpiryDate.setText("Expired");
                        holder.foodExpiryDate.setTextColor(Color.parseColor("#ff5034"));

                    } else {
                        holder.foodExpiryDate.setText(model.getItemExpiryDate());
                    }
                } catch (Exception e) {

                }

                // delete automatically  after 5 days
                try {
                    int delete_day = e_day + 5;
                    if (e_year == c_year && e_month == c_month && delete_day == c_day) {
//                    DatabaseReference postDeleteCart = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                        DatabaseReference postDeletePost = FirebaseDatabase.getInstance().getReference("Post").child(post_key);
                        DatabaseReference postDeleteUserPost = FirebaseDatabase.getInstance().getReference("UserPost").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                        DatabaseReference postDeleteAdminReport = FirebaseDatabase.getInstance().getReference("AdminReport").child(post_key);

//                    postDeleteCart.removeValue();
                        postDeletePost.removeValue();
                        postDeleteAdminReport.removeValue();
                        postDeleteUserPost.removeValue();

                    }
                } catch (Exception e) {

                }


                String food_category = model.getItemCategory();

                if (foodStatus.equals("For Sale")) {
                    if (bestPrice < 80) {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.best_price);
                    } else {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.on_sale_big);
                    }

                } else {
                    holder.tvPrice.setText(String.valueOf(model.getItemPrice()));

                    holder.ivStatusImage.setImageResource(R.drawable.donate);
                }
                holder.foodName.setText(model.getItemName());

                if (food_category.equals("Food")) {
                    holder.tvStock.setText(model.getStockNo() + " Plates");
                } else if (food_category.equals("Groceries")) {
                    holder.tvStock.setText(model.getStockNo() + " Pieces");

                } else {
                    holder.tvStock.setText(model.getStockNo() + " Kg");

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser == null || !mUser.isEmailVerified()) {
                            try {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(intent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        } else {
                            try {
                                Intent sinlePostIntent = new Intent(HomeActivity.this, FoodPostSingleActivity.class);
                                sinlePostIntent.putExtra("post_id", post_key);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(sinlePostIntent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        }

                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.food_post_linear, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }


    public void viewPost() {


        recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(FoodViewHolder holder, int position, Food model) {

                final String post_key = getRef(position).getKey();
                try {
                    Picasso.get().load(model.getImageUrl()).into(holder.foodImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext(), "Error on load image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                }
                String foodStatus = model.getItemStatus();
                long bestPrice = model.getItemPrice();

                String food_category = model.getItemCategory();

                if (foodStatus.equals("For Sale")) {
                    if (bestPrice < 80) {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.best_price);
                    } else {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.on_sale_big);
                    }

                } else {
                    holder.tvPrice.setText("Rs." + String.valueOf(model.getItemPrice()));

                    holder.ivStatusImage.setImageResource(R.drawable.donate);
                }
                holder.foodName.setText(model.getItemName());
                holder.foodExpiryDate.setText(model.getItemExpiryDate());
                if (food_category.equals("Food")) {
                    holder.tvStock.setText(model.getStockNo() + " Plates");
                } else if (food_category.equals("Groceries")) {
                    holder.tvStock.setText(model.getStockNo() + " Pieces");

                } else {
                    holder.tvStock.setText(model.getStockNo() + " Kg");

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser == null || !mUser.isEmailVerified()) {
                            try {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(intent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        } else {
                            try {
                                Intent sinlePostIntent = new Intent(HomeActivity.this, FoodPostSingleActivity.class);
                                sinlePostIntent.putExtra("post_id", post_key);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(sinlePostIntent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        }

                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.food_post_linear, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }


    public void viewPostPrice() {


        recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(FoodViewHolder holder, int position, Food model) {

                final String post_key = getRef(position).getKey();
                try {
                    Picasso.get().load(model.getImageUrl()).into(holder.foodImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext(), "Error on load image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                }
                String foodStatus = model.getItemStatus();
                long bestPrice = model.getItemPrice();
                String food_category = model.getItemCategory();

                if (foodStatus.equals("For Sale")) {
                    if (bestPrice < 80) {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.best_price);
                    } else {
                        holder.tvPrice.setText("Rs. " + model.getItemPrice());
                        holder.ivStatusImage.setImageResource(R.drawable.on_sale_big);
                    }

                } else {
                    holder.tvPrice.setText("Rs." + String.valueOf(model.getItemPrice()));

                    holder.ivStatusImage.setImageResource(R.drawable.donate);
                }
                holder.foodName.setText(model.getItemName());
                holder.foodExpiryDate.setText(model.getItemExpiryDate());
                if (food_category.equals("Food")) {
                    holder.tvStock.setText(model.getStockNo() + " Plates");
                } else if (food_category.equals("Groceries")) {
                    holder.tvStock.setText(model.getStockNo() + " Pieces");

                } else {
                    holder.tvStock.setText(model.getStockNo() + " Kg");

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUser == null || !mUser.isEmailVerified()) {
                            try {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(intent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        } else {
                            try {
                                Intent sinlePostIntent = new Intent(HomeActivity.this, FoodPostSingleActivity.class);
                                sinlePostIntent.putExtra("post_id", post_key);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(sinlePostIntent);
                                HomeActivity.this.finish();
                            } catch (Exception e) {

                            }
                        }

                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.food_post_linear, viewGroup, false);
                return new FoodViewHolder(view);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.cartButton);
        try {
            if (lightTheme) {
                menuItem.setIcon(Converter.convertLayoutToImage(HomeActivity.this, cart_count, R.drawable.ic_shopping_cart_black_24dp));
            } else {
                menuItem.setIcon(Converter.convertLayoutToImage(HomeActivity.this, cart_count, R.drawable.ic_shopping_cart_white_24dp));
            }
        } catch (Exception e) {

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.cartButton:
                try {
                    if (NetworkStatus) {
                        openCartActivity();
                    } else {
                        buildDialog(HomeActivity.this).show();
                    }
                    break;
                } catch (Exception e) {

                }
            case R.id.profileButton:
                try {
                    if (NetworkStatus) {
                        openProfileActivity();
                    } else {
                        buildDialog(HomeActivity.this).show();
                    }
                    break;
                } catch (Exception e) {

                }
            case R.id.sortViewButton:
                try {
                    if (NetworkStatus) {
                        openSortView();
                    } else {
                        buildDialog(HomeActivity.this).show();
                    }
                    break;
                } catch (Exception e) {

                }
            case R.id.settingButton:
                try {
                    openSettingActivity();
                    break;
                } catch (Exception e) {

                }
            case R.id.aboutButton:
                try {
                    openAboutUsActivity();
                    break;
                } catch (Exception e) {

                }
            case R.id.signInOut:
                try {
                    if (NetworkStatus) {
                        openSignInOut();
                    } else {
                        buildDialog(HomeActivity.this).show();
                    }
                    break;
                } catch (Exception e) {

                }

        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        invalidateOptionsMenu();

        MenuItem signInOutStatus = menu.findItem(R.id.signInOut);
        if (mUser != null && mUser.isEmailVerified()) {
            statusInOut = "signIn";
            menu.findItem(R.id.signInOut).setTitle(signOutTitle);
        } else if (firebaseAuth.getCurrentUser() == null) {
            statusInOut = "signOut";
            menu.findItem(R.id.signInOut).setTitle(signInTitle);
        } else {
            statusInOut = "signOut";
            menu.findItem(R.id.signInOut).setTitle(signInTitle);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void openSettingActivity() {
        try {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            HomeActivity.this.finish();
        } catch (Exception e) {

        }
    }

    public void openProfileActivity() {
        try {
            if (mUser == null || !mUser.isEmailVerified()) {
                openSignInOut();

                Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                vibrator.vibrate(20);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            } else {

                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            }
        } catch (Exception e) {

        }
    }

    public void openCartActivity() {
        try {
            if (mUser == null || !mUser.isEmailVerified()) {
                Toast.makeText(this, "Please Sign in to Add item to cart!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                vibrator.vibrate(20);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();

            } else {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            }

        }catch (Exception e){

        }
    }

    public void openAboutUsActivity() {
        try {
            Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            HomeActivity.this.finish();
        } catch (Exception e) {

        }
    }

    public void openSortView() {

        vibrator.vibrate(20);
        if (sortView == "hide") {
            sortLayout.setVisibility(View.VISIBLE);
            overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            sortView = "show";
        } else if (sortView == "show") {
            sortLayout.setVisibility(View.GONE);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            sortView = "hide";
        }
    }

    public void openSignInOut() {
        if (statusInOut == "signOut") {
            try {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            } catch (Exception e) {

            }
        } else if (statusInOut == "signIn") {
            try {
                firebaseAuth.signOut();
                restartApp();
            } catch (Exception e) {

            }
        }
    }

    public void openAddFoodActivity() {
        if (!mUser.isEmailVerified()) {
            try {
                openSignInOut();
                Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                vibrator.vibrate(20);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            } catch (Exception e) {

            }
        } else {

            try {
                Intent intent = new Intent(getApplicationContext(), AddFoodActivity.class);
                vibrator.vibrate(20);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                HomeActivity.this.finish();
            } catch (Exception e) {

            }
        }

    }


    public void openDefaultCategory() {
        if (sharedPref.loadDarkModeState() == true) {

            defaultBut.setBackground(getDrawable(R.drawable.button_rev_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#ffffff"));

        } else {
            defaultBut.setBackground(getDrawable(R.drawable.button_rev_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Recent", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    public void openFruitCategory() {
        if (sharedPref.loadDarkModeState() == true) {

            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_rev_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#ffffff"));

        } else {
            fruitBut.setBackground(getDrawable(R.drawable.button_rev_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Fruit", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    public void openDonation() {
        if (sharedPref.loadDarkModeState() == true) {


            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_rev_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));


        } else {
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_rev_design));
            itemForDonation.setTextColor(Color.parseColor("#ffffff"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Donation", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void openVegetableCategory() {
        if (sharedPref.loadDarkModeState() == true) {

            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_rev_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#ffffff"));

        } else {
            vegetableBut.setBackground(getDrawable(R.drawable.button_rev_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Vegetable", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void openCookCategory() {
        if (sharedPref.loadDarkModeState() == true) {

            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            cookBut.setBackground(getDrawable(R.drawable.button_rev_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));

        } else {
            cookBut.setBackground(getDrawable(R.drawable.button_rev_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Food", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void openFridgeCategory() {
        if (sharedPref.loadDarkModeState() == true) {

            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#ffffff"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#ffffff"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#ffffff"));
            fridgeBut.setBackground(getDrawable(R.drawable.button_rev_design));
            fridgeBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));

        } else {
            fridgeBut.setBackground(getDrawable(R.drawable.button_rev_design));
            fridgeBut.setTextColor(Color.parseColor("#ffffff"));
            fruitBut.setBackground(getDrawable(R.drawable.button_design));
            fruitBut.setTextColor(Color.parseColor("#000000"));
            vegetableBut.setBackground(getDrawable(R.drawable.button_design));
            vegetableBut.setTextColor(Color.parseColor("#000000"));
            cookBut.setBackground(getDrawable(R.drawable.button_design));
            cookBut.setTextColor(Color.parseColor("#000000"));
            defaultBut.setBackground(getDrawable(R.drawable.button_design));
            defaultBut.setTextColor(Color.parseColor("#000000"));
            itemForDonation.setBackground(getDrawable(R.drawable.button_design));
            itemForDonation.setTextColor(Color.parseColor("#000000"));
        }
        Snackbar.make((CoordinatorLayout) findViewById(R.id.homeView), "Sort by Groceries", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    public void restartApp() {
        Intent i = getIntent();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(i);
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;

        } else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("No Internet Connection");
        builder.setCancelable(true);
        builder.setMessage("You need to have Mobile data or Wifi to Access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

    //double back pressed showing toast
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            finishAffinity();
            super.onBackPressed();
            System.exit(1);

        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to Exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}
