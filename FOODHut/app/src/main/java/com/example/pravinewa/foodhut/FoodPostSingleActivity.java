package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FoodPostSingleActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    ImageView backButton;

    DatabaseReference databaseReference;
    private String post_key = null;
    ImageView foodImage;
    TextView tvPriceItemDetails, tvNameItemDetails, tvStockItemDetails, tvExpiryDateItemDetails;
    TextView tvItemDescriptionItemDescripton;
    TextView tvNamePostedBy, tvContactPostedBy;
    TextView tvLocationLocation;
    DatabaseReference mDatabaseRef;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FloatingActionButton postToCartButton;
    EditText addStockNumber;
    String food_name, food_expiry_date, food_image, user_id, food_description, posted_user, posted_user_contact, food_category, food_location, food_status, food_currrent_date;
    ImageView reportIcon, callOwner;
    TextView reportBut,placeOrderTotalAmount;
    Long food_price;
    String phone;
    Long food_stock_no;
    LinearLayout quantityLinearLayout;

    String ReportView = "Gone";
    Button addButton, removeButton;
    long quantity = 1;

    Button btnBuy;
    EditText newAddressText;
    Button placeOrder;

    Vibrator vibrator;
    CheckBox iWishCheckBox;
    String shipped = "currentAddress";

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
        setContentView(R.layout.activity_food_post_single);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        tvPriceItemDetails = findViewById(R.id.tvpriceItemDetails);
        tvNameItemDetails = findViewById(R.id.tvNameItemDetails);
        tvStockItemDetails = findViewById(R.id.tvStockItemDetails);
        tvExpiryDateItemDetails = findViewById(R.id.tvExpireyDateItemDetails);
        postToCartButton = findViewById(R.id.postToCartButton);
        callOwner = findViewById(R.id.callOwner);
        quantityLinearLayout = findViewById(R.id.quantityLinearLayout);

        tvItemDescriptionItemDescripton = findViewById(R.id.tvItemDescriptionItemDescription);
        tvNamePostedBy = findViewById(R.id.tvNamePostedBy);
        tvContactPostedBy = findViewById(R.id.tvContactPostedBy);

        tvLocationLocation = findViewById(R.id.tvLocationLocation);
        foodImage = (ImageView) findViewById(R.id.postFoodImage);
        btnBuy = (Button)findViewById(R.id.buttonBuy);


        addButton = (Button)findViewById(R.id.increaseQuantityCart);
        removeButton = (Button)findViewById(R.id.decreaseQuantityCart);
        addStockNumber = (EditText) findViewById(R.id.stockNumberEditText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(15);
                quantity = quantity + 1;
                addStockNumber.setText(""+quantity);
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1) {
                    vibrator.vibrate(15);
                    quantity = quantity - 1;
                    addStockNumber.setText("" + quantity);
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        backButton = (ImageView) findViewById(R.id.backButtonPost);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });


        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog();
            }
        });

        if(sharedPref.loadDarkModeState())
        {
            btnBuy.setTextColor(Color.WHITE);
        }

//        toolbar = (Toolbar) findViewById(R.id.postToolbarId);
//        toolbar.setTitle("Post");
//        toolbar.setTitleTextAppearance(this,R.style.ITCavantFont);
//        setSupportActionBar(toolbar);
//
//        if(getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//                finish();
//            }
//        });


        post_key = getIntent().getExtras().getString("post_id");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Post");

        mDatabaseRef.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                food_name = (String) dataSnapshot.child("itemName").getValue();
                food_description = (String) dataSnapshot.child("itemDescription").getValue();
                food_expiry_date = (String) dataSnapshot.child("itemExpiryDate").getValue();
                food_image = (String) dataSnapshot.child("imageUrl").getValue();
                posted_user = (String) dataSnapshot.child("username").getValue();
                posted_user_contact = (String) dataSnapshot.child("usercontactnumber").getValue();
                food_price = dataSnapshot.child("itemPrice").getValue(Long.class);
                food_stock_no =dataSnapshot.child("stockNo").getValue(Long.class);
                food_category = (String) dataSnapshot.child("itemCategory").getValue();
                food_status = (String) dataSnapshot.child("itemStatus").getValue();
                food_location = (String) dataSnapshot.child("useraddress").getValue();
                user_id = dataSnapshot.child("userID").getValue(String.class);
                food_currrent_date = (String) dataSnapshot.child("currentDate").getValue();

                try {
                    if (user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    postToCartButton.setVisibility(View.INVISIBLE);
                        callOwner.setVisibility(View.INVISIBLE);
                        quantityLinearLayout.setVisibility(View.INVISIBLE);
                        btnBuy.setText("Your Item");

                        if (btnBuy.getText().toString().trim() == ("Your Item")) {
                            btnBuy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                        reportIcon.setVisibility(View.INVISIBLE);
                        if (sharedPref.loadDarkModeState() == true) {
                            btnBuy.setTextColor(Color.WHITE);
                            postToCartButton.setImageResource(R.drawable.ic_edit_black_24dp);
                        } else {
                            postToCartButton.setImageResource(R.drawable.ic_edit_white_24dp);
                        }
                        postToCartButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    vibrator.vibrate(20);
                                    Intent sinlePostIntent = new Intent(FoodPostSingleActivity.this, ItemUpdateActivity.class);
                                    sinlePostIntent.putExtra("update_item", post_key);
                                    startActivity(sinlePostIntent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }catch (Exception e){

                                }
                            }
                        });
                    }
                }

                catch (NullPointerException e)
                {

                }
                try
                {
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    mDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String food_owner = (String) dataSnapshot.child("fullname").getValue();
                            String address = (String) dataSnapshot.child("address").getValue();
                            phone = (String) dataSnapshot.child("phone").getValue();
                            tvNamePostedBy.setText(food_owner + " (Uploader)");
                            tvContactPostedBy.setText(phone);
                            tvLocationLocation.setText(address);

                            try {
                                Picasso.get().load(food_image).into(foodImage);
                            }catch (Exception e){

                            }
                            if (food_status.equals("Donate")) {
                                tvPriceItemDetails.setText(String.valueOf(food_price));
                            } else {
                                tvPriceItemDetails.setText("Rs. " + food_price);
                            }
                            tvNameItemDetails.setText(food_name);
                            tvItemDescriptionItemDescripton.setText(food_description);
                            if (food_category.equals("Food")) {
                                tvStockItemDetails.setText("Available : " + food_stock_no + " Plates");
                            } else if (food_category.equals("Groceries")) {
                                tvStockItemDetails.setText("Available : " + food_stock_no + " Pieces");
                            } else {
                                tvStockItemDetails.setText("Available : " + food_stock_no + " Kg");
                            }
                            tvExpiryDateItemDetails.setText("Expiry : " + food_expiry_date);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch (NullPointerException e)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });




        callOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }catch (Exception e){

            }
            }
        });


        postToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(20);
                Cart cart = new Cart(
                            food_name,
                            food_expiry_date,
                            food_image,
                            food_price,
                            quantity
                    );
                try {
                    FirebaseDatabase.getInstance().getReference("Cart")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(post_key)
                            .setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make((CoordinatorLayout) findViewById(R.id.foodPostLayout), quantity + " item added to Cart!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }

                    });
                }catch (Exception e){

                }
                }
            });



        reportIcon = (ImageView) findViewById(R.id.reportMenuButtonPost);
        reportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReportView == "Gone") {
                    reportBut.setVisibility(View.VISIBLE);
                    ReportView = "Visible";
                } else if (ReportView == "Visible") {
                    reportBut.setVisibility(View.GONE);
                    ReportView = "Gone";
                }
            }
        });

        reportBut = (TextView) findViewById(R.id.reportButton);
        reportBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Admin admin = new Admin(
                        food_name,
                        food_image,
                        user_id
                );
                try {
                    FirebaseDatabase.getInstance().getReference("AdminReport")
                            .child(post_key)
                            .setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make((CoordinatorLayout) findViewById(R.id.foodPostLayout), "Reported Successfully!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                reportBut.setVisibility(View.GONE);
                                ReportView = "Gone";
                            } else {
                                Snackbar.make((CoordinatorLayout) findViewById(R.id.foodPostLayout), "Reported Failed!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                        }
                    });
                }catch (Exception e){

                }
            }
        });




    }
    public void openConfirmDialog()
    {

        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_checkout_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setView(dialogView);
//
        newAddressText = (EditText) dialogView.findViewById(R.id.newShippingAddress);
        iWishCheckBox = dialogView.findViewById(R.id.iwishChechBox);
        placeOrderTotalAmount = dialogView.findViewById(R.id.placeOrderTotalAmount);
        placeOrderTotalAmount.setText("Total Amount \t\t:\t\t\t\tRs." + (quantity * food_price));


        placeOrder = (Button) dialogView.findViewById(R.id.confirmPlaceOrder);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iWishCheckBox.isChecked())
                {
                    DatabaseReference  mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String currentUserName = (String) dataSnapshot.child("fullname").getValue();
                            final String currentuserContact = (String) dataSnapshot.child("phone").getValue();
                            final String currentUserAddress = (String) dataSnapshot.child("address").getValue();

                                try {

                                                        FirebaseDatabase.getInstance().getReference("Post").child(post_key).child("stockNo").setValue(food_stock_no-quantity);
                                                        HistoryOrder historyOrder = new HistoryOrder(
                                                                food_name,
                                                                food_image,
                                                                food_price,
                                                                quantity,
                                                                (food_price*quantity)
                                                        );
                                                        String history_id = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                                                        FirebaseDatabase.getInstance().getReference("History")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(history_id)
                                                                .setValue(historyOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getApplicationContext(),"Checkout Successfully",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        //owner info

                                                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                                        final String date = df.format(Calendar.getInstance().getTime());
                                                        String newAddresss = newAddressText.getText().toString().trim();
                                                        final String order_id = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                                                        if(shipped == "currentAddress"){
                                                            IntrestedItem intrested = new IntrestedItem(currentUserName, currentuserContact, food_name, date, quantity,(food_price*quantity),food_price,currentUserAddress);
                                                            FirebaseDatabase.getInstance().getReference("Order")
                                                                    .child(user_id)
                                                                    .child(order_id)
                                                                    .setValue(intrested).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(getApplicationContext(), "Your Item Has Been Confirmed!!!", Toast.LENGTH_SHORT).show();
                                                                    alertDialog.dismiss();

                                                                }
                                                            });

                                                        }else if(shipped == "newAddress"){
                                                            IntrestedItem intrested = new IntrestedItem(currentUserName, currentuserContact, food_name, date, quantity,(food_price*quantity),food_price,newAddresss);
                                                            FirebaseDatabase.getInstance().getReference("Order")
                                                                    .child(user_id)
                                                                    .child(order_id)
                                                                    .setValue(intrested).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(getApplicationContext(), "Your Item Has Been Confirmed!!!", Toast.LENGTH_SHORT).show();
                                                                    alertDialog.dismiss();

                                                                }
                                                            });
                                                        }




                                                    }catch (Exception e){

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });






                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Fill the CheckBox if you wish to proceed!!", Toast.LENGTH_SHORT).show();

                }

            }

        });
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.currentAddressButton:
                if(checked)
                {
                    newAddressText.setEnabled(false);
                    shipped = "currentAddress";
                }
                break;
            case R.id.newAddressButton:
                if(checked)
                {
                    newAddressText.setEnabled(true);
                    shipped = "newAddress";
                }
                break;
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
