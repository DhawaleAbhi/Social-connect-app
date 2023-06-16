package com.datingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.datingapp.activities.MainActivity;
import com.datingapp.activities.ProfileEditActivity;
import com.datingapp.auth.SignInActivity;
import com.datingapp.utils.Constant;
import com.datingapp.utils.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private final static int TIME_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(()->{
            HashMap<String,String> data = new UserPreferences(this).getData();
            if (data.get(Constant.phoneNumber) == null){
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }else {
                if (data.get(Constant.NAME) != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(), ProfileEditActivity.class));
                    finish();
                }
            }
        },TIME_SPLASH);
    }
}