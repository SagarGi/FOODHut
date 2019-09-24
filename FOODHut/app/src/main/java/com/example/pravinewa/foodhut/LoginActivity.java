package com.example.pravinewa.foodhut;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private SharedPreferences sharedPreferences;
    //use for remember me i.e store id and password for later use
    private static final String PREFS_NAME = "Prefsfile";

    Window window;
    Toolbar toolbar;
    ProgressBar progressBar;
    Button btnSigninLogin,btnSignUpLogin;
    EditText etEmailLogin, etPasswordLogin;
    private CheckBox rememberme;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView passwordImage;
    TextView forgetPassword, detailPassword, topicPassword;
    EditText emailForgotPassword;
    Button sendForgotPassword;
    String emailForPassword;
    FirebaseAuth firebaseAuth;
    String password = "Invisible";
    ImageView show_password;
    int progressStatus = 0;

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
        btnSignUpLogin = (Button)findViewById(R.id.btnSignUpLogin);
        btnSigninLogin = (Button)findViewById(R.id.btnSigninLogin);
        etEmailLogin = (EditText)findViewById(R.id.etEmailLogin);
        etPasswordLogin = (EditText)findViewById(R.id.etPasswordLogin);
        forgetPassword = findViewById(R.id.forgetPassword);
        topicPassword = findViewById(R.id.topicPassword);
        rememberme = (CheckBox) findViewById(R.id.rememberMeBox);

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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        //firebase database user authentication
        sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        btnSignUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        if(sharedPref.loadDarkModeState() == true)
        {
            forgetPassword.setTextColor(Color.parseColor("#ff5034"));

        }

        if(sharedPref.loadDarkModeState() == true) {
            show_password = (ImageView) findViewById(R.id.password_show_button);
            show_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        etPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordLogin.setSelection(etPasswordLogin.length());
                        password = "Visible";
                        show_password.setImageResource(R.drawable.visible_eye_white);
                    } else if (password == "Visible") {
                        etPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordLogin.setSelection(etPasswordLogin.length());
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
                        etPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordLogin.setSelection(etPasswordLogin.length());
                        password = "Visible";
                        show_password.setImageResource(R.drawable.visible_eye_black);
                    } else if (password == "Visible") {
                        etPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordLogin.setSelection(etPasswordLogin.length());
                        password = "Invisible";
                        show_password.setImageResource(R.drawable.invisible_eye_black);
                    }
                }
            });
        }

        getPreferenceData();
        signInButtonClicked();
        forgetPasswordClick();
    }

    public void signInButtonClicked()

    {
        btnSigninLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    //remember me code
    private void getPreferenceData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_email")){
            String u = sp.getString("pref_email","not found");
            etEmailLogin.setText(u.toString());
        }
        if(sp.contains("pref_password")){
            String p = sp.getString("pref_password","not found");
            etPasswordLogin.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            boolean b = sp.getBoolean("pref_check",false);
            rememberme.setChecked(b);
        }
    }

    //login user
    public void loginUser()
    {
        final String emailLogin = etEmailLogin.getText().toString().trim();
        final String passwordLogin = etPasswordLogin.getText().toString().trim();


        if(emailLogin.isEmpty() && passwordLogin.isEmpty())
        {
            buildDialog(LoginActivity.this,"Please enter email and password!!");
            return;
        }

        if (emailLogin.isEmpty()) {
            etEmailLogin.setError("Enter your email!!");
            etEmailLogin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()) {
            etEmailLogin.setError("Invalid Email Address");
            etEmailLogin.requestFocus();
            return;
        }

        if (passwordLogin.isEmpty()) {
            etPasswordLogin.setError("Enter Your Password!!");
            etPasswordLogin.requestFocus();
            return;
        }
        if (passwordLogin.length() < 6) {
            etPasswordLogin.setError("Password should be at least 6 character!!");
            etPasswordLogin.requestFocus();
            return;
        }

        if(rememberme.isChecked()){
            boolean boolischecked = rememberme.isChecked();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_email",etEmailLogin.getText().toString());
            editor.putString("pref_password",etPasswordLogin.getText().toString());
            editor.putBoolean("pref_check",boolischecked);
            editor.apply();

        }else {
            sharedPreferences.edit().clear().apply();
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(emailLogin,passwordLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user.isEmailVerified()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        LoginActivity.this.finish();

                    } else {
                        progressBar.setVisibility(View.GONE);

                        Snackbar.make((CoordinatorLayout) findViewById(R.id.loginLayout), "Please Verify your Email First!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make((CoordinatorLayout) findViewById(R.id.loginLayout), "Authentication failed. Please check Email/Password!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }

            }
        });
    }

    public void forgetPasswordClick()
    {
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
    private void showForgotPasswordDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setView(dialogView);

        emailForgotPassword = (EditText) dialogView.findViewById(R.id.emailForgetPassword);
        sendForgotPassword = (Button) dialogView.findViewById(R.id.sendForgotPassword);
        detailPassword = (TextView) dialogView.findViewById(R.id.detailPassword);
        topicPassword = (TextView) dialogView.findViewById(R.id.topicPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        sendForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailForgotPassword.getText().toString().equals(""))
                {
                    emailForgotPassword.setError("Enter you email!!");
                    emailForgotPassword.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailForgotPassword.getText().toString()).matches()) {
                    emailForgotPassword.setError("Invalid Email Address");
                    emailForgotPassword.requestFocus();
                    return;
                }
                else
                {

                    firebaseAuth.sendPasswordResetEmail(emailForgotPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                emailForgotPassword.setVisibility(View.GONE);
                                sendForgotPassword.setText("Ok");
                                topicPassword.setText("Reset Link Sent");
                                detailPassword.setText("We have sent a reset password email to " + emailForgotPassword.getText().toString() + ".  Please click the reset " +
                                        "password link to reset your new password. Thank You!!");



                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Error!!", Toast.LENGTH_LONG).show();

                            }

                            sendForgotPassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(sendForgotPassword.getText().toString().equals("Ok"))

                                    {

                                        alertDialog.dismiss();
                                        Snackbar.make((CoordinatorLayout) findViewById(R.id.loginLayout), "Reset Link Sent!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

       /* btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });*/


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

}
