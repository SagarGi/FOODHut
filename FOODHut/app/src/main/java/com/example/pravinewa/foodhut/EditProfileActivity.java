package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    Button saveProfile;
    EditText updateName, updateContact, updateAddress;

    CircleImageView imageView;
    public static final int GALLERY_REQUEST = 1;
    private Uri profileImageUri = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    String updateProfileUrl,photoUrl;
    Uri downloadURL;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser mPost;
    CircleImageView profileImage;
    String oldEmail;
    String updatedName, updatedAddress, updatedContact;
    String profileUri;


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
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.profileToolbarId);
        toolbar.setTitle("Edit Profile");
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
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        profileImage = (CircleImageView)findViewById(R.id.profile_image);

        updateName =(EditText)findViewById(R.id.updateName);
        updateContact = (EditText)findViewById(R.id.updateContact);
        updateAddress = (EditText)findViewById(R.id.updateAddress);

        imageView = (CircleImageView) findViewById(R.id.profile_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GALLERY_REQUEST);

            }
        });

        saveProfile = (Button) findViewById(R.id.saveProfileButton);


        storageReference= FirebaseStorage.getInstance().getReference("ProfileImages");

         databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final String profile_Image = (String) dataSnapshot.child("profileUrl").getValue();
                if(profile_Image.equals("null"))
                {
                    try {
                        Bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.blank_profile);
                        profileImage.setImageBitmap(Icon);
                    }catch (Exception e){

                    }
                }
                else
                {
                    try {
                        Picasso.get().load(profile_Image).into(profileImage);
                    }catch (Exception e){

                    }
                }

                String  oldName = (String)dataSnapshot.child("fullname").getValue();
                String  oldAddress = (String)dataSnapshot.child("address").getValue();
                String  oldContact = (String)dataSnapshot.child("phone").getValue();
                final String  oldemail = (String)dataSnapshot.child("email").getValue();



                updateProfileUrl = user.getProfileUrl();

                //save profile button
                saveProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(profileImageUri != null)
                        {
                            uploadProfile();
                        }
                        else
                        {
                            updatedName = updateName.getText().toString().trim();
                            updatedContact = updateContact.getText().toString().trim();
                            updatedAddress = updateAddress.getText().toString().trim();

                            User user = new User(updatedName,oldemail,updatedContact,updatedAddress,profile_Image);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {

                                        Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_SHORT).show();
                                        try {
                                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            EditProfileActivity.this.finish();

                                        }catch (Exception e){

                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        }

                    }
                });

                //set old information
                updateName.setText(oldName);
                updateName.setSelection(oldName.length());
               updateAddress.setText(oldAddress);
                updateContact.setText(oldContact);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && data != null) {
            profileImageUri = data.getData();
            imageView.setImageURI(profileImageUri);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        EditProfileActivity.this.finish();
    }


    //update profile method
    public void uploadProfile() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    updatedName = updateName.getText().toString().trim();
                    updatedContact = updateContact.getText().toString().trim();
                    updatedAddress = updateAddress.getText().toString().trim();
                    oldEmail = user.getEmail();
                    updateProfileUrl = user.getProfileUrl();
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });




        //for User child
        final StorageReference filePath = storageReference.child(UUID.randomUUID().toString());
        UploadTask uploadTask = filePath.putFile(profileImageUri);

                       uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uploading Photo Error", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Image Uploading", Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.GONE);

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
                            User user = new User(updatedName,oldEmail,updatedContact,updatedAddress,photoUrl);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {

                                        Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        EditProfileActivity.this.finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_SHORT).show();

                        }
            }
        });
    }
}
