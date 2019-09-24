package com.example.pravinewa.foodhut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    ProgressBar progressBar;
    EditText etFullNameSignUp, etEmailSignUp, etPasswordSignUp, etContactNumberSignup,etAddressSignUp;
    Button btnSignUp;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Boolean isRegister = false;
    String profileUrl = "null";
    String password = "Invisible";
    ImageView show_password;
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
        setContentView(R.layout.activity_sign_up);
        toolbar = (Toolbar) findViewById(R.id.signUpToolbarId);
        toolbar.setTitle("Sign Up");
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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        etEmailSignUp = findViewById(R.id.etEmailSignUp);
        etFullNameSignUp = findViewById(R.id.etFullNameSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etContactNumberSignup = findViewById(R.id.etContactNumberSignUp);
        etAddressSignUp = findViewById(R.id.etAddressSignUp);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        show_password = (ImageView) findViewById(R.id.password_show_button);
        if(sharedPref.loadDarkModeState() == true) {
            show_password = (ImageView) findViewById(R.id.password_show_button);
            show_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        etPasswordSignUp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordSignUp.setSelection(etPasswordSignUp.length());
                        password = "Visible";
                        show_password.setImageResource(R.drawable.visible_eye_white);
                    } else if (password == "Visible") {
                        etPasswordSignUp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordSignUp.setSelection(etPasswordSignUp.length());
                        password = "Invisible";
                        show_password.setImageResource(R.drawable.invisible_eye_white);
                    }
                }
            });
        }
        else
        {
            show_password = (ImageView) findViewById(R.id.password_show_button);
            show_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        etPasswordSignUp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordSignUp.setSelection(etPasswordSignUp.length());
                        password = "Visible";
                        show_password.setImageResource(R.drawable.visible_eye_black);
                    } else if (password == "Visible") {
                        etPasswordSignUp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordSignUp.setSelection(etPasswordSignUp.length());
                        password = "Invisible";
                        show_password.setImageResource(R.drawable.invisible_eye_black);
                    }
                }
            });
        }

        signupButtonClicked();

    }


    //
    public void signupButtonClicked()
    {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }


    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    //user signup method
    public void registerUser() {
        final String fullname = etFullNameSignUp.getText().toString().trim();
        final String email = etEmailSignUp.getText().toString().trim();
        String password = etPasswordSignUp.getText().toString().trim();
        final String contact = etContactNumberSignup.getText().toString().trim();
        final String address = etAddressSignUp.getText().toString().trim();

        if(fullname.isEmpty() && email.isEmpty() && password.isEmpty() && contact.isEmpty() && address.isEmpty())
        {
            buildDialog(SignUpActivity.this, "Fields cannot be left empty!!");
            return;
        }

        if (fullname.isEmpty()) {
            etFullNameSignUp.setError("Enter Full Name!!");
            etFullNameSignUp.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmailSignUp.setError("Enter your email!!");
            etEmailSignUp.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailSignUp.setError("Invalid Email Address");
            etEmailSignUp.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPasswordSignUp.setError("Enter Your Password!!");
            etPasswordSignUp.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPasswordSignUp.setError("Password should be at least 6 character!!");
            etPasswordSignUp.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            etContactNumberSignup.setError("Enter Contact Number!!");
            etContactNumberSignup.requestFocus();
            return;
        }

        if (contact.length() != 10) {
            etContactNumberSignup.setError("Contact Number Should be of 10 Digits!!");
            etContactNumberSignup.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            try {
                                User user = new User(
                                        fullname,
                                        email, contact, address, profileUrl
                                );

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            sendEmailVerification();
                                        } else {
                                            //display a failure message
                                        }
                                    }
                                });
                            }catch (Exception e){

                            }
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //send email verification

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

    public void sendEmailVerification() {
        try {
            final FirebaseUser user = mAuth.getCurrentUser();
            user.sendEmailVerification()
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                Snackbar.make((CoordinatorLayout) findViewById(R.id.signUpLayout), "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                SignUpActivity.this.finish();


                            } else {
                                progressBar.setVisibility(View.GONE);

                                Snackbar.make((CoordinatorLayout) findViewById(R.id.signUpLayout), "Failed to send verification email.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    });
        }catch (Exception e){

        }
    }
}
