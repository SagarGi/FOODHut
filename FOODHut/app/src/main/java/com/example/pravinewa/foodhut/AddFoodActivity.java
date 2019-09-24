package com.example.pravinewa.foodhut;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class AddFoodActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    EditText etItemName, etItemPrice, etDescription, etStockNo, etEcpiryDate;
    Spinner spinnerAddCategory;
    Spinner spinnerAddStatus;
    ImageView ivFoodImage;
    TextView tvAttachImage, tvChooseDate;
    Button btnPost;
    ProgressBar progressBar;
    String status, category, photoUrl;
    public static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    Uri downloadURL;
    private String attachedImage = "attached";
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    StorageReference mStorageRef;
    DatabaseReference databaseReference;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    public String personName, personContactNumber, personAddress;
    String post_id;
    public final String TAG = "MyTag: ";



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
        setContentView(R.layout.activity_add_food);

        toolbar = (Toolbar) findViewById(R.id.addFoodToolbarId);
        toolbar.setTitle("Post Food");
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

        spinnerAddCategory = findViewById(R.id.spinnerAddCategory);
        spinnerAddStatus = findViewById(R.id.spinnerAddStatus);
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etDescription = findViewById(R.id.etDescription);
        etStockNo = findViewById(R.id.etStockNo);
        etEcpiryDate = findViewById(R.id.etExpiryDate);
        progressBar = findViewById(R.id.progressBar);
        tvAttachImage = findViewById(R.id.tvAttachImage);
        tvChooseDate = findViewById(R.id.tvChooseDate);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        btnPost = findViewById(R.id.btnPost);

        databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                personName = user.getFullname();
                personAddress = user.getAddress();
                personContactNumber = user.getPhone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error occured", Toast.LENGTH_SHORT).show();
            }
        });

       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                attachFile.setText("Attach Image");
                attachedImage = "attached";

            }
        });*/
       progressBar.setVisibility(View.GONE);
       selectCategory();
       selectStatus();
       attachButtonClicked();
       chooseDateButtonClicked();
       postButtonClicked();

       if(sharedPref.loadDarkModeState() == true)
       {
           spinnerAddCategory.setBackground(getResources().getDrawable(R.drawable.spinner_background_white));
           spinnerAddStatus.setBackground(getResources().getDrawable(R.drawable.spinner_background_white));

       }
       else if(sharedPref.loadLightModeState() == true)
       {
           spinnerAddCategory.setBackground(getResources().getDrawable(R.drawable.spinner_background_black));
           spinnerAddStatus.setBackground(getResources().getDrawable(R.drawable.spinner_background_black));
       }
       else
       {
           spinnerAddCategory.setBackground(getResources().getDrawable(R.drawable.spinner_backgroung));
           spinnerAddStatus.setBackground(getResources().getDrawable(R.drawable.spinner_backgroung));
       }


        onDateSetListener = new DatePickerDialog.OnDateSetListener()

        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                if(month > 9 && dayOfMonth > 9)
                {
                    String date = year + "-" + month + "-" + dayOfMonth;
                    etEcpiryDate.setText(date);
                }
                else if(month > 9 && dayOfMonth <= 9)
                {
                    String date = year + "-" + month + "-" + "0" + dayOfMonth;
                    etEcpiryDate.setText(date);
                }
                else if(month <= 9 && dayOfMonth > 9)
                {
                    String date = year + "-" + "0" + month + "-" + dayOfMonth;
                    etEcpiryDate.setText(date);
                }
                else
                {
                    String date = year + "-" + "0" + month + "-" + "0" + dayOfMonth;
                    etEcpiryDate.setText(date);
                }
            }
        };


    }

    // required functions
    public void postButtonClicked()
    {

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String itemName = etItemName.getText().toString().trim();
                final String itemPrice = etItemPrice.getText().toString().trim();
                final String itemStockno = etStockNo.getText().toString().trim();
                final String itemDescription = etDescription.getText().toString().trim();
                final String itemEcpirydate = etEcpiryDate.getText().toString().trim();

                //validation
                if(itemName.isEmpty() && itemPrice.isEmpty() && itemStockno.isEmpty() && itemDescription.isEmpty() && category.equals("Category") && status.equals("Status") && itemEcpirydate.isEmpty())
                {
                    buildDialog(AddFoodActivity.this, "Fields cannot be left Empty").show();
                    return;
                }

                if(itemName.isEmpty())
                {
                    etItemName.setError("Food Name Required!!");
                    etItemName.requestFocus();
                    return;
                }

                if(itemName.length() > 12)
                {
                    etItemName.setError("Name too long!!");
                    etItemName.requestFocus();
                    return;
                }

                if(itemPrice.isEmpty())
                {
                    etItemPrice.setError("Price Required!!");
                    etItemPrice.requestFocus();
                    return;
                }
                if(itemPrice.length() > 3)
                {
                    etItemPrice.setError("Invalid Price!!");
                    etItemPrice.requestFocus();
                    return;
                }

                if(itemDescription.isEmpty())
                {
                    etDescription.setError("Field Cannot be empty!!");
                    etDescription.requestFocus();
                    return;
                }
                if(itemDescription.length() < 10)
                {
                    etDescription.setError("Description too short!!");
                    etDescription.requestFocus();
                    return;
                }
                if(itemDescription.length() > 150)
                {
                    etDescription.setError("Description too long!!");
                    etDescription.requestFocus();
                    return;
                }

                if(itemStockno.isEmpty())
                {
                    etStockNo.setError("Field cannot be empty!!");
                    etStockNo.requestFocus();
                    return;
                }

                if(itemEcpirydate.isEmpty())
                {
                    etEcpiryDate.setError("Expiry Date is Required");
                    etEcpiryDate.requestFocus();
                    return;
                }

                if(category.equals("Category"))
                {
                    buildDialog(AddFoodActivity.this, "Please Select Category!!").show();
                    return;
                }

                if(status.equals("Status"))
                {

                    buildDialog(AddFoodActivity.this, "Please Select Status!!").show();
                    return;

                }

                if(imageUri == null)
                {
                    buildDialog(AddFoodActivity.this, "Please Select Image!!").show();
                    return;
                }

                final String userID = mUser.getUid();
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                final String saveCurrentDate = currentDate.format(calendar.getTime());

                //date validation
                final long item_Price = Integer.parseInt(itemPrice);
                final long item_Stockno = Integer.parseInt(itemStockno);



                Log.d(TAG, "edate = " + itemEcpirydate);
                String expiry_year = (((Character.toString(itemEcpirydate.charAt(0))).concat(Character.toString(itemEcpirydate.charAt(1)))).concat(Character.toString(itemEcpirydate.charAt(2)))).concat(Character.toString(itemEcpirydate.charAt(3)));
                int e_year = Integer.parseInt(expiry_year);
                String current_year = (((Character.toString(saveCurrentDate.charAt(0))).concat(Character.toString(saveCurrentDate.charAt(1)))).concat(Character.toString(saveCurrentDate.charAt(2)))).concat(Character.toString(saveCurrentDate.charAt(3)));
                int c_year = Integer.parseInt(current_year);

                String expiry_month = (Character.toString(itemEcpirydate.charAt(5))).concat(Character.toString(itemEcpirydate.charAt(6)));
                int e_month = Integer.parseInt(expiry_month);
                Log.d(TAG, "e month = " + e_month);
                String current__month = (Character.toString(saveCurrentDate.charAt(5))).concat(Character.toString(saveCurrentDate.charAt(6)));
                int c_month = Integer.parseInt(current__month);

                String expiry_day = (Character.toString(itemEcpirydate.charAt(8))).concat(Character.toString(itemEcpirydate.charAt(9)));
                int e_day = Integer.parseInt(expiry_day);
                Log.d(TAG, "e day = " + e_day);

                String current_day = (Character.toString(saveCurrentDate.charAt(8))).concat(Character.toString(saveCurrentDate.charAt(9)));
                int c_day = Integer.parseInt(current_day);
                Log.d(TAG, "c day = " + c_day);


                 if (e_year <= c_year && e_month <= c_month && e_day <= c_day ) {
                     buildDialog(AddFoodActivity.this, "Invalid Date!!").show();
                     return;


                } else {
//                            Toast.makeText(getApplicationContext(), "Validation Complete!!", Toast.LENGTH_SHORT).show();
                    final String uid = mUser.getUid();

                    if (status.equals("For Sale")) {
                        progressBar.setVisibility(View.VISIBLE);
                        final StorageReference filePath = mStorageRef.child(UUID.randomUUID().toString());
                        UploadTask uploadTask = filePath.putFile(imageUri);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Uploading Photo Error", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Uploading Photo Succes", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadURL = task.getResult();
                                    photoUrl = downloadURL.toString();
                                    Food food = new Food(
                                            itemName,
                                            itemDescription,
                                            item_Price,
                                            category,
                                            status,
                                            itemEcpirydate,
                                            photoUrl,
                                            saveCurrentDate,
                                            item_Stockno,
                                            uid

                                    );
                                    final UserPost userPost = new UserPost(
                                            itemName,
                                            photoUrl
                                    );
                                    post_id = databaseReference.push().getKey();

                                    FirebaseDatabase.getInstance().getReference("Post")
                                            .child(post_id)
                                            .setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("UserPost")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(post_id)
                                                        .setValue(userPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                            tvAttachImage.setText("Attach Image");
                                                            attachedImage = "attached";
                                                            Toast.makeText(AddFoodActivity.this, "Data Stored Succesfully", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        } else {
                                                            Toast.makeText(AddFoodActivity.this, "Data Registered Failed Inside", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(AddFoodActivity.this, "Data Registered Failed", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        final StorageReference filePath = mStorageRef.child(UUID.randomUUID().toString());
                        UploadTask uploadTask = filePath.putFile(imageUri);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Uploading Photo Error", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Uploading Photo Succes", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadURL = task.getResult();
                                    photoUrl = downloadURL.toString();
                                    Food food = new Food(
                                            itemName,
                                            itemDescription,
                                            0,
                                            category,
                                            status,
                                            itemEcpirydate,
                                            photoUrl,
                                            saveCurrentDate,
                                            item_Stockno,
                                            uid

                                    );
                                    final UserPost userPost = new UserPost(
                                            itemName,
                                            photoUrl
                                    );

                                    post_id = databaseReference.push().getKey();


                                    FirebaseDatabase.getInstance().getReference("Post")
                                            .child(post_id)
                                            .setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("UserPost")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(post_id)
                                                        .setValue(userPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                            tvAttachImage.setText("Attach Image");
                                                            attachedImage = "attached";
                                                            Toast.makeText(AddFoodActivity.this, "Data Stored Succesfully", Toast.LENGTH_SHORT).show();
                                                            finish();

                                                        } else {
                                                            Toast.makeText(AddFoodActivity.this, "Data Registered Failed Inside", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(AddFoodActivity.this, "Data Registered Failed", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });





                                } else {
                                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }


            }// up to here






        });
    }


    public void selectCategory()
    {
        List<String> listCategory = new ArrayList<String>();
        listCategory.add("Category");
        listCategory.add("Fruits");
        listCategory.add("Vegetables");
        listCategory.add("Groceries");
        listCategory.add("Food");



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddFoodActivity.this,android.R.layout.simple_spinner_item,listCategory)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.RED);
                }
                else {
                    if(sharedPref.loadDarkModeState() == true)
                    {
                        tv.setTextColor(Color.WHITE);
                    }
                    else
                    {
                        tv.setTextColor(Color.BLACK);

                    }
                }
                return view;
            }
        };

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddCategory.setAdapter(arrayAdapter);
        spinnerAddCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    // Notify the selected item text
//                    Toast.makeText
//                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
//                            .show();
                }
//                spinnerAddCategory.setSelection(position);
                category = spinnerAddCategory.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void selectStatus()
    {
        List<String> listCategory = new ArrayList<String>();
        listCategory.add("Status");
        listCategory.add("For Sale");
        listCategory.add("Donate");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddFoodActivity.this,android.R.layout.simple_spinner_item,listCategory)
        {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.RED);
                }
                else {
                    if(sharedPref.loadDarkModeState() == true)
                    {
                        tv.setTextColor(Color.WHITE);
                    }
                   else
                    {
                        tv.setTextColor(Color.BLACK);

                    }
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddStatus.setAdapter(arrayAdapter);
        spinnerAddStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    // Notify the selected item text
//                    Toast.makeText
//                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
//                            .show();
                }
//                spinnerAddStatus.setSelection(position);
                status = spinnerAddStatus.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void attachButtonClicked()
    {
        tvAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (attachedImage) {

                    case "attached":
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);
                        break;
                    case "removed":
                        imageUri = null;
                        ivFoodImage.setImageResource(R.drawable.defaultimage);
                        tvAttachImage.setText("Attach Image");
                        attachedImage = "attached";
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && data != null) {
            imageUri = data.getData();
            tvAttachImage.setText("Remove Image");
            attachedImage = "removed";
            ivFoodImage.setImageURI(imageUri);
//            Toast.makeText(getApplicationContext(), "Image attached" + imageUri, Toast.LENGTH_SHORT).show();
        }

    }

    public void chooseDateButtonClicked()
    {
        tvChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddFoodActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
    }

    public android.app.AlertDialog.Builder buildDialog(Context context, String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Empty Fields");
        builder.setCancelable(true);
        builder.setMessage(msg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        tvAttachImage.setText("Attach Image");
        attachedImage = "attached";

    }



}
