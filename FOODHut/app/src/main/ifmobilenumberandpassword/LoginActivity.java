package com.example.pravinewa.foodhut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private SharedPreferences sharedPreferences;
    //use for remember me i.e store id and password for later use
    private static final String PREFS_NAME = "Prefsfile";

    Window window;
    Toolbar toolbar;
    private Button signUpButton;
    private Button loginButton;
    ProgressBar progressBar;

    private EditText userMobNumberLogin;
    private EditText userPasswordLogin;
    private CheckBox rememberme;

    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceUsers;

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
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.loginToolbarId);
        toolbar.setTitle("Sign In");
        toolbar.setTitleTextAppearance(this,R.style.ITCavantFont);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //firebase database user authentication
        firebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        userMobNumberLogin = (EditText) findViewById(R.id.userMobNumberLogin);
        userPasswordLogin = (EditText) findViewById(R.id.userPasswordLogin);
        rememberme = (CheckBox) findViewById(R.id.rememberMeBox);


        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        signUpButton = (Button)findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        getPreferenceData();
    }


    //remember me code
    private void getPreferenceData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_email")){
            String u = sp.getString("pref_email","not found");
            userMobNumberLogin.setText(u.toString());
        }
        if(sp.contains("pref_password")){
            String p = sp.getString("pref_password","not found");
            userPasswordLogin.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            boolean b = sp.getBoolean("pref_check",false);
            rememberme.setChecked(b);
        }
    }

    //empty and other validation checking
    public void userLogin(){
        String domain = "@foodhut.com";
        String email = userMobNumberLogin.getText().toString().trim();
        String emailID = email+domain;
        String password = userPasswordLogin.getText().toString().trim();
        String firstTwoNumber = email.substring(0,2);

        if(email.isEmpty()){
            userMobNumberLogin.setError("Mobile Number Required");
            userMobNumberLogin.requestFocus();
            return;
        }

        if(email.length() != 10){
            userMobNumberLogin.setError("Mobile Number should be 10 digit");
            userMobNumberLogin.requestFocus();
            return;
        }
//
//        if(firstTwoNumber != "98"){
//            userEmailLogin.setError("Enter valid Mobile Number");
//            userEmailLogin.requestFocus();
//            return;
//        }

        if(password.isEmpty()){
            userPasswordLogin.setError("Password Required");
            userPasswordLogin.requestFocus();
            return;
        }

        if(password.length() <6){
            userPasswordLogin.setError("Password should more than 6 digit");
            userPasswordLogin.requestFocus();
            return;
        }

        if(rememberme.isChecked()){
            boolean boolischecked = rememberme.isChecked();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_email",userMobNumberLogin.getText().toString());
            editor.putString("pref_password",userPasswordLogin.getText().toString());
            editor.putBoolean("pref_check",boolischecked);
            editor.apply();

        }else {
            sharedPreferences.edit().clear().apply();
        }

        //visible progress bar after button clicked
        progressBar.setVisibility(View.VISIBLE);
        userMobNumberLogin.setEnabled(false);
        userPasswordLogin.setEnabled(false);
        rememberme.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(emailID,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            userMobNumberLogin.setEnabled(true);
                            userPasswordLogin.setEnabled(true);
                            rememberme.setEnabled(true);
                        }else{
                            Toast.makeText(LoginActivity.this,"Mobile Number and Password incorrect",Toast.LENGTH_SHORT).show();
                            userMobNumberLogin.setEnabled(true);
                            userPasswordLogin.setEnabled(true);
                            rememberme.setEnabled(true);
                        }
                    }
                });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

}
