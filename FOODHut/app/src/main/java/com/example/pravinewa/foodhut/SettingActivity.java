package com.example.pravinewa.foodhut;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Window window;
    Toolbar toolbar;
    CardView appearanceBut;
    CardView accountBut;
    RadioGroup radioGroup;
    RadioButton defaultBut, lightBut, darkBut;
    String appearanceHide = "hide";
    LinearLayout appearanceView;
    String accountHide = "hide";
    String generalHide = "hide";
    RelativeLayout accountView;
    LinearLayout generalView;
    String password = "Invisible";
    ImageView show_password_old;
    ImageView show_password_new;

    private FirebaseAuth firebaseAuth;

    CardView signInOutBut;

    String statusInOut = "empty";
    TextView signInOutText;
    Button change_password;
    EditText old_password, new_password;

    FirebaseUser firebaseUser;

    ProgressBar progressBar;
    Boolean NetworkStatus = false;
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
        setContentView(R.layout.activity_setting);

        if (!isNetworkConnected(SettingActivity.this)) {
            NetworkStatus = false;
        } else {
            NetworkStatus = true;
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBarSetting);
//        show_password_old = findViewById(R.id.password_show_old);
//        show_password_new = findViewById(R.id.password_show_new);

        toolbar = (Toolbar) findViewById(R.id.settingToolbarId);
        toolbar.setTitle("Setting");
        toolbar.setTitleTextAppearance(this, R.style.ITCavantFont);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        signInOutText = (TextView) findViewById(R.id.settingSignInOut);

        if (firebaseAuth.getCurrentUser() != null) {
            statusInOut = "signIn";
            signInOutText.setText("Sign Out");
        } else if (firebaseAuth.getCurrentUser() == null) {
            statusInOut = "signOut";
            signInOutText.setText("Sign In");
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        old_password = (EditText) findViewById(R.id.userOldPassword);
        new_password = (EditText) findViewById(R.id.userPasswordReset);

        change_password = (Button) findViewById(R.id.changePassword_button);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = old_password.getText().toString();
                final String newPass = new_password.getText().toString();


                if (oldPass.isEmpty()) {
                    old_password.setError("Password Required");
                    old_password.requestFocus();
                    return;
                }
                if (newPass.isEmpty()) {
                    new_password.setError("Password Required");
                    new_password.requestFocus();
                    return;
                }
                if (oldPass.length() < 6) {
                    old_password.setError("Password should more than 6 digit");
                    old_password.requestFocus();
                    return;
                }
                if (newPass.length() < 6) {
                    new_password.setError("Password should more than 6 digit");
                    new_password.requestFocus();
                    return;
                }

                String user_email = firebaseUser.getEmail();
//                Toast.makeText(getApplicationContext(),"Email : "+user_email,Toast.LENGTH_SHORT).show();

                AuthCredential credential = EmailAuthProvider.getCredential(user_email,oldPass);
                progressBar.setVisibility(View.VISIBLE);
                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(),"Password Changed Successfully!!",Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Snackbar.make((CoordinatorLayout) findViewById(R.id.settingLayout), "Password changed Failed!!", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                progressBar.setVisibility(View.GONE);

                                            }
                                        }
                                    });

                                }else{
                                    Snackbar.make((CoordinatorLayout) findViewById(R.id.settingLayout), "Old Password Didn't Match!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    progressBar.setVisibility(View.GONE);

                                }
                            }
                        });
            }
        });
        accountBut = (CardView) findViewById(R.id.accountButton);
        accountBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStatus){
                    openAccountActivity();
                }else{
                    buildDialog(SettingActivity.this).show();
                }
            }
        });

        appearanceBut = (CardView) findViewById(R.id.appearanceButton);
        appearanceBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppearanceActivity();
            }
        });

        signInOutBut = (CardView) findViewById(R.id.settingButton);
        signInOutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStatus){
                    openSignInOut();
                }else{
                    buildDialog(SettingActivity.this).show();
                }
            }
        });

        appearanceView = (LinearLayout) findViewById(R.id.differentMode);
        accountView = (RelativeLayout) findViewById(R.id.accountSettingMode);


        defaultBut = (RadioButton) findViewById(R.id.defaultMode);
        lightBut = (RadioButton) findViewById(R.id.lightMode);
        darkBut = (RadioButton) findViewById(R.id.darkMode);

        if(sharedPref.loadLightModeState()==true && sharedPref.loadDarkModeState()==false){
            defaultBut.setChecked(false);
            lightBut.setChecked(true);
            darkBut.setChecked(false);
        }
        if(sharedPref.loadDarkModeState()==true && sharedPref.loadLightModeState()==false){
            defaultBut.setChecked(false);
            lightBut.setChecked(false);
            darkBut.setChecked(true);
        }
        if(sharedPref.loadLightModeState()==false && sharedPref.loadDarkModeState()==false){
            defaultBut.setChecked(true);
            lightBut.setChecked(false);
            darkBut.setChecked(false);
        }
        radioGroup = (RadioGroup) findViewById(R.id.appearanceMode);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                switch (index) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Default Theme Applied", Toast.LENGTH_SHORT).show();
                        sharedPref.setDarkModeState(false);
                        sharedPref.setLightModeState(false);
                        restartApp();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Light Theme Applied", Toast.LENGTH_SHORT).show();
                        sharedPref.setLightModeState(true);
                        sharedPref.setDarkModeState(false);
                        restartApp();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Dark Theme Applied", Toast.LENGTH_SHORT).show();
                        sharedPref.setDarkModeState(true);
                        sharedPref.setLightModeState(false);
                        restartApp();
                        break;


                }

            }
        });

        if(sharedPref.loadDarkModeState() == true) {
            show_password_old = (ImageView) findViewById(R.id.password_show_old);
            show_password_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        old_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        old_password.setSelection(old_password.length());
                        password = "Visible";
                        show_password_old.setImageResource(R.drawable.visible_eye_white);
                    } else if (password == "Visible") {
                        old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        old_password.setSelection(old_password.length());
                        password = "Invisible";
                        show_password_old.setImageResource(R.drawable.invisible_eye_white);
                    }
                }
            });
        }
        else
        {
            show_password_old = (ImageView) findViewById(R.id.password_show_old);
            show_password_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        old_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        old_password.setSelection(old_password.length());
                        password = "Visible";
                        show_password_old.setImageResource(R.drawable.visible_eye_black);
                    } else if (password == "Visible") {
                        old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        old_password.setSelection(old_password.length());
                        password = "Invisible";
                        show_password_old.setImageResource(R.drawable.invisible_eye_black);
                    }
                }
            });
        }


        if(sharedPref.loadDarkModeState() == true) {
            show_password_new = (ImageView) findViewById(R.id.password_show_new);
            show_password_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        new_password.setSelection(old_password.length());
                        password = "Visible";
                        show_password_new.setImageResource(R.drawable.visible_eye_white);
                    } else if (password == "Visible") {
                        new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        new_password.setSelection(old_password.length());
                        password = "Invisible";
                        show_password_new.setImageResource(R.drawable.invisible_eye_white);
                    }
                }
            });
        }
        else
        {
            show_password_new = (ImageView) findViewById(R.id.password_show_new);
            show_password_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password == "Invisible") {
                        new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        new_password.setSelection(old_password.length());
                        password = "Visible";
                        show_password_new.setImageResource(R.drawable.visible_eye_black);
                    } else if (password == "Visible") {
                        new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        new_password.setSelection(old_password.length());
                        password = "Invisible";
                        show_password_new.setImageResource(R.drawable.invisible_eye_black);
                    }
                }
            });
        }


    }

    public void openAccountActivity() {

        if(firebaseAuth.getCurrentUser() != null) {
            if (accountHide == "hide") {
                accountView.setVisibility(View.VISIBLE);
                accountHide = "show";
            } else if (accountHide == "show") {
                accountView.setVisibility(View.GONE);
                accountHide = "hide";
            }
        }else{
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

    public void openAppearanceActivity() {

        if (appearanceHide == "hide") {
            appearanceView.setVisibility(View.VISIBLE);
            appearanceHide = "show";
        } else if (appearanceHide == "show") {
            appearanceView.setVisibility(View.GONE);
            appearanceHide = "hide";
        }


    }


    public void openSignInOut() {
        if (statusInOut == "signOut") {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (statusInOut == "signIn") {
            firebaseAuth.signOut();
            restartApp();

        }
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
        builder.setMessage("You need to have Mobile data or Wifi to Access this.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}