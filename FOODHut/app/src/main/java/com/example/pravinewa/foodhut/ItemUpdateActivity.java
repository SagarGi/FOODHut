package com.example.pravinewa.foodhut;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ItemUpdateActivity extends AppCompatActivity {
    String post_key = null;
    Toolbar toolbar;
    SharedPref sharedPref;
    Window window;
    ListView statusListView;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    EditText etItemName, etItemPrice, etItemCategory, etItemStatus, etItemDescription, etItemStock, etItemExpiryDate;
    ArrayAdapter<String> adapter;
    DatabaseReference mDatabaseRef;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    String food_name, food_expiry_date, food_description,food_category, food_status, food_image,user_id,food_currrent_date;
    Long food_price, food_stock;
    Button btnUpdateitem;
    String MyTag = "mytag";


    @SuppressLint("ClickableViewAccessibility")
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
        } else if(sharedPref.loadLightModeState()==false && sharedPref.loadDarkModeState()==false){
            setTheme(R.style.DefaultTheme);
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (Build.VERSION.SDK_INT >= 21) {
                window = this.getWindow();
                window.setStatusBarColor(this.getResources().getColor(R.color.default_status));
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);
        toolbar = (Toolbar) findViewById(R.id.EditItemToolbar);
        toolbar.setTitle("Edit Item");
        toolbar.setTitleTextAppearance(this, R.style.ITCavantFont);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        post_key = getIntent().getExtras().getString("update_item");
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemCategory = findViewById(R.id.etItemCategory);
        etItemStatus = findViewById(R.id.etItemStatus);
        etItemDescription = findViewById(R.id.etItemDescription);
        etItemExpiryDate = findViewById(R.id.etItemExpiryDate);
        etItemStock = findViewById(R.id.etItemStock);
        btnUpdateitem = findViewById(R.id.btnUpdateItem);


        onDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                if (month > 9 && dayOfMonth > 9) {
                    String date = year + "-" + month + "-" + dayOfMonth;
                    etItemExpiryDate.setText(date);
                } else if (month > 9 && dayOfMonth <= 9) {
                    String date = year + "-" + month + "-" + "0" + dayOfMonth;
                    etItemExpiryDate.setText(date);
                } else if (month <= 9 && dayOfMonth > 9) {
                    String date = year + "-" + "0" + month + "-" + dayOfMonth;
                    etItemExpiryDate.setText(date);
                } else {
                    String date = year + "-" + "0" + month + "-" + "0" + dayOfMonth;
                    etItemExpiryDate.setText(date);
                }
            }
        };


        etItemName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        etItemPrice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        etItemCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertSimpleListViewForCategory();
            }
        });

       etItemStatus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               alertSimpleListViewForStatus();
           }
       });


        etItemDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });


        etItemExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etItemExpiryDate.setFocusable(true);
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ItemUpdateActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        etItemStock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        //retrieve data to the edittext
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseRef.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                food_name = (String) dataSnapshot.child("itemName").getValue();
                food_description = (String) dataSnapshot.child("itemDescription").getValue();
                food_expiry_date = (String) dataSnapshot.child("itemExpiryDate").getValue();
                food_price = dataSnapshot.child("itemPrice").getValue(Long.class);
                food_stock = dataSnapshot.child("stockNo").getValue(Long.class);
                food_category = (String) dataSnapshot.child("itemCategory").getValue();
                food_status = (String) dataSnapshot.child("itemStatus").getValue();
                food_image = (String) dataSnapshot.child("imageUrl").getValue();
                user_id = dataSnapshot.child("userID").getValue(String.class);
                food_currrent_date = (String) dataSnapshot.child("currentDate").getValue();



                etItemName.setText(food_name);
                etItemPrice.setText(String.valueOf(food_price));
                etItemCategory.setText(food_category);
                etItemStatus.setText(food_status);
                etItemDescription.setText(food_description);
                etItemStock.setText(String.valueOf(food_stock));
                etItemExpiryDate.setText(food_expiry_date);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdateitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updated_name = etItemName.getText().toString().trim();
                String updated_price = etItemPrice.getText().toString();
                long long_price = Long.parseLong(updated_price);
                String updated_category = etItemCategory.getText().toString();
                String updated_status = etItemStatus.getText().toString();
                String updated_Descripton = etItemDescription.getText().toString();
                String updated_stock = etItemStock.getText().toString();
                long long_stock = Long.parseLong(updated_stock);
                String updated_expirydate = etItemExpiryDate.getText().toString();



                if(etItemDescription.length() > 150)
                {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ItemUpdateActivity.this);
                    builder.setMessage("Description too long!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.d(MyTag,"Updated value = " + updated_price);
                    Log.d(MyTag,"Updated long = " + long_price);
                    return;
                }

                //date validation

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                final String saveCurrentDate = currentDate.format(calendar.getTime());

                //date validation
//                Log.d(TAG, "edate = " + updated_expirydate);
                String expiry_year = (((Character.toString(updated_expirydate.charAt(0))).concat(Character.toString(updated_expirydate.charAt(1)))).concat(Character.toString(updated_expirydate.charAt(2)))).concat(Character.toString(updated_expirydate.charAt(3)));
                int e_year = Integer.parseInt(expiry_year);
                String current_year = (((Character.toString(saveCurrentDate.charAt(0))).concat(Character.toString(saveCurrentDate.charAt(1)))).concat(Character.toString(saveCurrentDate.charAt(2)))).concat(Character.toString(saveCurrentDate.charAt(3)));
                int c_year = Integer.parseInt(current_year);

                String expiry_month = (Character.toString(updated_expirydate.charAt(5))).concat(Character.toString(updated_expirydate.charAt(6)));
                int e_month = Integer.parseInt(expiry_month);
//                Log.d(TAG, "e month = " + e_month);
                String current__month = (Character.toString(saveCurrentDate.charAt(5))).concat(Character.toString(saveCurrentDate.charAt(6)));
                int c_month = Integer.parseInt(current__month);

                String expiry_day = (Character.toString(updated_expirydate.charAt(8))).concat(Character.toString(updated_expirydate.charAt(9)));
                int e_day = Integer.parseInt(expiry_day);
//                Log.d(TAG, "e day = " + e_day);

                String current_day = (Character.toString(saveCurrentDate.charAt(8))).concat(Character.toString(saveCurrentDate.charAt(9)));
                int c_day = Integer.parseInt(current_day);
//                Log.d(TAG, "c day = " + c_day);

                if (e_year <= c_year && e_month <= c_month && e_day <= c_day ) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ItemUpdateActivity.this);
                    builder.setMessage("Invalid Date!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                    return;

                }
                if(updated_name.equals(food_name ) && long_price == food_price && long_stock == food_stock &&  updated_category.equals(food_category )&&
                        updated_status.equals(food_status ) && updated_Descripton.equals(food_description  )&& updated_expirydate.equals(food_expiry_date))
                {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ItemUpdateActivity.this);
                    builder.setMessage("Nothing Changed!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.d(MyTag,"Updated value = " + updated_price);
                    Log.d(MyTag,"Updated long = " + long_price);
                    return;
                }
                else
                {
                    if(updated_status.equals("Donate"))
                    {
                        Food food = new Food(updated_name, updated_Descripton, 0, updated_category, updated_status, updated_expirydate, food_image, food_currrent_date,long_stock, user_id);
                        FirebaseDatabase.getInstance().getReference("Post")
                                .child(post_key)
                                .setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Successfull", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }

                    else
                    {

                        Food food = new Food(updated_name, updated_Descripton, long_price , updated_category, updated_status, updated_expirydate, food_image, food_currrent_date, long_stock, user_id);
                        FirebaseDatabase.getInstance().getReference("Post")
                                .child(post_key)
                                .setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Successfull", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }



                }


            }

        });

    }

    public void alertSimpleListViewForStatus() {

        /*
         * WebView is created programatically here.
         *
         * @Here are the list of items to be shown in the list
         */
        final CharSequence[] items = { "For Sale", "Donate"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemUpdateActivity.this);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                // will toast your selection
              etItemStatus.setText(items[item]);
                dialog.dismiss();

            }
        }).show();
    }
    public void alertSimpleListViewForCategory() {

        /*
         * WebView is created programatically here.
         *
         * @Here are the list of items to be shown in the list
         */
        final CharSequence[] items = { "Fruits", "Vegetables", "Food", "Groceries"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ItemUpdateActivity.this);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                // will toast your selection
                etItemCategory.setText(items[item]);
                dialog.dismiss();

            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        ItemUpdateActivity.this.finish();
    }


}
