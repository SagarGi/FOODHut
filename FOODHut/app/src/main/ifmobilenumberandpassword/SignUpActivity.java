package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    ProgressBar progressBar;

    private EditText userFullName;
    private EditText userEmailId;
    private EditText userAddress;
    private EditText userMobileNumber;
    private EditText userPassword;
    private EditText userConPassword;
    private PinView pinView;

    FirebaseAuth firebaseAuth;

    Button nextButton, verifyButton, signUpButton, backFirstButton;
    ScrollView firstPage, secondPage, lastPage;

    String codeSent;

    String verifiedMobileNumber = "notVerified";


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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("en");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //edittext field
        userMobileNumber = (EditText) findViewById(R.id.userMobNumberSignUp);
        userPassword = (EditText) findViewById(R.id.userPasswordSignUp);
        userConPassword = (EditText) findViewById(R.id.userConfirmPasswordSignUp);
        userFullName = (EditText) findViewById(R.id.userFullNameSignUp);
        userEmailId = (EditText) findViewById(R.id.userEmailSignUp);
        userAddress = (EditText) findViewById(R.id.userAddressSignUp);
        pinView = (PinView) findViewById(R.id.emailVerifyPin);


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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        firstPage = (ScrollView) findViewById(R.id.signUpFirstPage);
        secondPage = (ScrollView) findViewById(R.id.signUpSecondPage);
        lastPage = (ScrollView) findViewById(R.id.signUpLastPage);

        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVeificationCode();
            }
        });

        verifyButton = (Button) findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignUpCode();
            }
        });

        signUpButton = (Button) findViewById(R.id.signUps_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });

        backFirstButton = (Button) findViewById(R.id.back_first_button);
        backFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstPage.setVisibility(View.VISIBLE);
                secondPage.setVisibility(View.GONE);
                lastPage.setVisibility(View.GONE);
            }
        });
    }

    private void verifySignUpCode() {

        String code = pinView.getText().toString().trim();
        if (code.isEmpty()) {
            pinView.setError("Pin Number Required");
            pinView.requestFocus();
            return;
        }

        if (code.length() != 6) {
            pinView.setError("Pin must be 6 digit");
            pinView.requestFocus();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signUpWithMobileNumber(credential);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void sendVeificationCode() {

        String frontNumb = "+977 ";
        String mobNumber = userMobileNumber.getText().toString();
        String Password = userPassword.getText().toString().trim();
        String cPassword = userConPassword.getText().toString().trim();
        String completeMobileNumber = frontNumb + mobNumber.substring(0, 3) + "-" + mobNumber.substring(3, 10);

        if (mobNumber.isEmpty()) {
            userEmailId.setError("Mobile Number Required");
            userEmailId.requestFocus();
            return;
        }

        if (mobNumber.length() != 10) {
            userEmailId.setError("Mobile Number should be 10 digit");
            userEmailId.requestFocus();
            return;
        }

        if (Password.isEmpty()) {
            userPassword.setError("Password Required");
            userPassword.requestFocus();
            return;
        }

        if (cPassword.isEmpty()) {
            userConPassword.setError("Confirmation Password Required");
            userConPassword.requestFocus();
            return;
        }

        if (Password.length() < 6) {
            userPassword.setError("Password should more than 6 digit");
            userPassword.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                completeMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        firstPage.setVisibility(View.GONE);
        secondPage.setVisibility(View.VISIBLE);
        lastPage.setVisibility(View.GONE);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), "Verification complete", Toast.LENGTH_SHORT).show();
            signUpWithMobileNumber(phoneAuthCredential);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            Toast.makeText(getApplicationContext(), "Code Sent", Toast.LENGTH_SHORT).show();

        }
    };


    private void signUpWithMobileNumber(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Correct Code", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            verifiedMobileNumber = "Verified";
                            firstPage.setVisibility(View.GONE);
                            secondPage.setVisibility(View.GONE);
                            lastPage.setVisibility(View.VISIBLE);

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect Code", Toast.LENGTH_SHORT).show();
                                pinView.setText("");
                                progressBar.setVisibility(View.GONE);
                                verifiedMobileNumber = "notVerified";
                                firstPage.setVisibility(View.VISIBLE);
                                secondPage.setVisibility(View.GONE);
                                lastPage.setVisibility(View.GONE);
                            }

                        }
                    }
                });
    }

    //on button clicked
    public void registerUser() {

        if (verifiedMobileNumber == "Verified") {
            final FirebaseUser user = firebaseAuth.getCurrentUser();

            String mobNumb = userMobileNumber.getText().toString().trim();
            String domain = "@foodhut.com";
            final String mobNumbID = mobNumb + domain;
            String Password = userPassword.getText().toString().trim();

            final String fullname = userFullName.getText().toString().trim();
            final String email = userEmailId.getText().toString().trim();
            final String number = userMobileNumber.getText().toString().trim();
            final String address = userAddress.getText().toString().trim();

            if (fullname.isEmpty()) {
                userFullName.setError("Name Required");
                userFullName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                userEmailId.setError("Email Required");
                userEmailId.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userEmailId.setError("Enter valid email address");
                userEmailId.requestFocus();
                return;
            }

            if (address.isEmpty()) {
                userAddress.setError("Address Required");
                userAddress.requestFocus();
                return;
            }

            //showing progress bar
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(mobNumbID, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        User user = new User(
                                fullname,
                                email,
                                number,
                                address
                        );
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Data Stored Succesfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Data Registered Failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(SignUpActivity.this, "Number must be Verified First", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
