package com.example.pravinewa.foodhut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.VIBRATOR_SERVICE;

public class CartActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    Vibrator vibrator;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<Cart> recyclerOptions;
    FirebaseRecyclerAdapter<Cart, CartViewHolder> recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    Button checkOut;
    long foodPriceCart;
    //    Button addButton, removeButton;
//    TextView quantityCart;
    long totalPrice = 0;
    Button placeOrder;
    TextView totPlaceOrder;
    ProgressBar progressBar;
    ArrayList<String> cartlist;
    ArrayList<String> notFounded;
    RadioGroup radioGroup;
    RadioButton currentAddress, newAddress;
    EditText newAddressText;
    CheckBox iWishCheckBox;
    String shipped = "currentAddress";
    Boolean Found = true;

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
        setContentView(R.layout.activity_cart);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshCart);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 2000);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        cartlist = new ArrayList<>();
        notFounded = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.food_cart_list);
        recyclerView.setHasFixedSize(true);

        checkOut = (Button) findViewById(R.id.checkoutButton);

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chk = checkOut.getText().toString();
//                Toast.makeText(getApplicationContext(),chk,Toast.LENGTH_SHORT).show();
                try {
                    if (chk.equals("Checkout (Rs. 00.00)")) {
                        Snackbar.make((CoordinatorLayout) findViewById(R.id.cartLayout), "Please select item first!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        showPlaceOrderDialog(totalPrice);
                    }
                } catch (Exception e) {

                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.cartToolbarId);
        toolbar.setTitle("Cart");
        toolbar.setTitleTextAppearance(this, R.style.ITCavantFont);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCart);
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

        recyclerOptions = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(databaseReference, Cart.class).build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(final CartViewHolder holder, int position, Cart model) {

                final String post_key = getRef(position).getKey();
                notFounded.add(post_key);
                final int pos = position;
                try {
                    Picasso.get().load(model.getFoodImage()).into(holder.foodImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getApplicationContext(), "Error on load image Cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                }
                final String foodNameC = model.getFoodName();
                final String foodExpiryC = model.getFoodExpiry();
                final long food_price = model.getFoodPrice();
                final long food_quantity = model.getFoodQuantity();

                holder.foodQuantity.setText("" + food_quantity);
                holder.foodName.setText(foodNameC);
                holder.foodExpiryDate.setText(foodExpiryC);
                holder.foodPrice.setText("Rs. " + food_price);
                foodPriceCart = food_price;
                final long totalThisPrice = food_price * food_quantity;


                holder.cartChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase.getInstance().getReference("Post").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(post_key)) {
                                    try {
                                        if (holder.cartChecked.isChecked()) {

                                            cartlist.add(post_key);
                                            totalPrice = totalPrice + totalThisPrice;
                                            checkOut.setText("Checkout (Rs. " + totalPrice + ")");

                                        } else if (!holder.cartChecked.isChecked()) {
                                            cartlist.remove(post_key);
                                            totalPrice = totalPrice - totalThisPrice;
                                            checkOut.setText("Checkout (Rs. " + totalPrice + ")");
                                        }
                                    } catch (Exception e) {

                                    }
                                } else {
                                    try {
                                        holder.cartChecked.setChecked(false);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                        builder.setMessage("This items has been deleted by Owner, Press OK to remove from Cart.")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        DatabaseReference postDeleteCart = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                                                        postDeleteCart.removeValue();
                                                        Snackbar.make((CoordinatorLayout) findViewById(R.id.cartLayout), "Removed from cart Sucessfully!!", Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();

                                                    }
                                                });

                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    } catch (Exception e) {

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });


//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            Intent sinlePostIntent = new Intent(CartActivity.this, FoodPostSingleActivity.class);
//                            sinlePostIntent.putExtra("post_id", post_key);
//                            startActivity(sinlePostIntent);
//                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                            CartActivity.this.finish();
//                        } catch (Exception e) {
//
//                        }
//                    }
//                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);

                            vibrator.vibrate(30);
                            Bundle bundle = new Bundle();
                            bundle.putString("post_id", post_key);
                            DeleteDialogCart deleteDialogCart = new DeleteDialogCart();
                            deleteDialogCart.setCancelable(false);
                            deleteDialogCart.setArguments(bundle);
                            deleteDialogCart.show(getSupportFragmentManager(), "dialog");
                        } catch (Exception e) {

                        }

                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(CartActivity.this).inflate(R.layout.food_cart_linear, viewGroup, false);
                return new CartViewHolder(view);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
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


    public void showPlaceOrderDialog(long total) {

        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_checkout_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setView(dialogView);
        totPlaceOrder = (TextView) dialogView.findViewById(R.id.placeOrderTotalAmount);
        totPlaceOrder.setText("Total Amount : Rs. " + total);
        newAddressText = (EditText) dialogView.findViewById(R.id.newShippingAddress);
        iWishCheckBox = dialogView.findViewById(R.id.iwishChechBox);


        placeOrder = (Button) dialogView.findViewById(R.id.confirmPlaceOrder);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iWishCheckBox.isChecked()) {
                    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String currentUserName = (String) dataSnapshot.child("fullname").getValue();
                            final String currentuserContact = (String) dataSnapshot.child("phone").getValue();
                            final String currentUserAddress = (String) dataSnapshot.child("address").getValue();


                            for (int i = 0; i < cartlist.size(); i++) {
                                final String pKey = cartlist.get(i);

                                try {
                                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Post").child(pKey);
                                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Food food = dataSnapshot.getValue(Food.class);
                                            final long PostQuant = food.getStockNo();
                                            final String ownerId = food.getUserID();
//                                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(ownerId);
//                                    String userAddress = userReference.child("address").toString();

                                            final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pKey);
                                            databaseReference2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {


                                                        //owner info

                                                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                                        final String date = df.format(Calendar.getInstance().getTime());
                                                        String newAddresss = newAddressText.getText().toString().trim();


                                                        final String order_id = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                                                        if (shipped == "currentAddress") {
                                                            Cart cart = dataSnapshot.getValue(Cart.class);
                                                            long cartQuant = cart.getFoodQuantity();
                                                            long totalQuant = PostQuant - cartQuant;
                                                            String foodName = cart.getFoodName();
                                                            String foodImage = cart.getFoodImage();
                                                            long foodPrice = cart.getFoodPrice();
                                                            long totalPrice = foodPrice * cartQuant;
                                                            FirebaseDatabase.getInstance().getReference("Post").child(pKey).child("stockNo").setValue(totalQuant);
                                                            HistoryOrder historyOrder = new HistoryOrder(
                                                                    foodName,
                                                                    foodImage,
                                                                    foodPrice,
                                                                    cartQuant,
                                                                    totalPrice
                                                            );
                                                            String history_id = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                                                            FirebaseDatabase.getInstance().getReference("History")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child(history_id)
                                                                    .setValue(historyOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    Toast.makeText(getApplicationContext(), "Checkout Successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            IntrestedItem intrested = new IntrestedItem(currentUserName, currentuserContact, foodName, date, cartQuant, totalPrice, foodPrice, currentUserAddress);
                                                            FirebaseDatabase.getInstance().getReference("Order")
                                                                    .child(ownerId)
                                                                    .child(order_id)
                                                                    .setValue(intrested).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Snackbar.make((CoordinatorLayout) findViewById(R.id.cartLayout), "Your Item Has Been Confirmed!!", Snackbar.LENGTH_LONG)
                                                                            .setAction("Action", null).show();
                                                                    alertDialog.dismiss();

                                                                }
                                                            });
                                                            databaseReference2.removeValue();
                                                            cartlist.clear();

                                                        } else if (shipped == "newAddress") {

                                                            if (newAddresss.isEmpty()) {
                                                                newAddressText.setError("Price Required!!");
                                                                newAddressText.requestFocus();
                                                                return;
                                                            } else {
                                                                Cart cart = dataSnapshot.getValue(Cart.class);
                                                                long cartQuant = cart.getFoodQuantity();
                                                                long totalQuant = PostQuant - cartQuant;
                                                                String foodName = cart.getFoodName();
                                                                String foodImage = cart.getFoodImage();
                                                                long foodPrice = cart.getFoodPrice();
                                                                long totalPrice = foodPrice * cartQuant;
                                                                FirebaseDatabase.getInstance().getReference("Post").child(pKey).child("stockNo").setValue(totalQuant);
                                                                HistoryOrder historyOrder = new HistoryOrder(
                                                                        foodName,
                                                                        foodImage,
                                                                        foodPrice,
                                                                        cartQuant,
                                                                        totalPrice
                                                                );
                                                                String history_id = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                                                                FirebaseDatabase.getInstance().getReference("History")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .child(history_id)
                                                                        .setValue(historyOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        Toast.makeText(getApplicationContext(), "Checkout Successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                IntrestedItem intrested = new IntrestedItem(currentUserName, currentuserContact, foodName, date, cartQuant, totalPrice, foodPrice, newAddresss);
                                                                FirebaseDatabase.getInstance().getReference("Order")
                                                                        .child(ownerId)
                                                                        .child(order_id)
                                                                        .setValue(intrested).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Snackbar.make((CoordinatorLayout) findViewById(R.id.cartLayout), "Your Item Has Been Confirmed!!", Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                        alertDialog.dismiss();

                                                                    }
                                                                });
                                                                databaseReference2.removeValue();
                                                                cartlist.clear();
                                                            }
                                                        }


                                                    } catch (Exception e) {

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } catch (Exception e) {

                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please Fill the CheckBox if you wish to proceed!!", Toast.LENGTH_SHORT).show();

                }

            }

        });


    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.currentAddressButton:
                if (checked) {
                    try {
                        newAddressText.setText("");
                        newAddressText.setEnabled(false);
                        shipped = "currentAddress";
                    } catch (Exception e) {

                    }
                }
                break;
            case R.id.newAddressButton:
                if (checked) {
                    try {
                        newAddressText.setEnabled(true);
                        shipped = "newAddress";
                    } catch (Exception e) {

                    }
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.historyButton:
                try {
                    openHistory();
                    break;
                } catch (Exception e) {

                }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void openHistory() {
        Intent intent = new Intent(getApplicationContext(), HistoryOrderActivity.class);
        startActivity(intent);
        CartActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        cartlist.clear();
        notFounded.clear();
        finish();
    }
}