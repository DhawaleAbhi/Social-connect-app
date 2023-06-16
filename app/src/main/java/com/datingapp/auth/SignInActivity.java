package com.datingapp.auth;

import static com.datingapp.utils.Constant.REQUEST_PERMISSION_CODE;
import static com.datingapp.utils.Constant.TAG_SIGN_IN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.datingapp.R;
import com.datingapp.utils.Constant;
import com.datingapp.utils.UserPreferences;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private EditText ePhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ePhoneNumber = findViewById(R.id.ePhoneNumber);
        getPermissions();

        HashMap<String, String> data = new UserPreferences(getApplicationContext()).getData();
        if (data.get(Constant.phoneNumber) != null){
            Constant.setMessage(this,data.get(Constant.phoneNumber));
        }
        findViewById(R.id.buttonGetOtp)
                .setOnClickListener(v -> sentOTP(ePhoneNumber.getText().toString(), "+91"));
    }
    //    TODO Permissions
    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(
                SignInActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                    SignInActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constant.setMessage(SignInActivity.this,Constant.permission_verified);
            }else {
                Constant.setMessage(SignInActivity.this,Constant.permission_unverified);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void sentOTP(String phone, String s) {
        String phoneNumber = s + phone;
        Log.d(TAG_SIGN_IN, "sentOTP: "+phoneNumber);
        startActivity(new Intent(getApplicationContext(), GetOtpActivity.class)
                .putExtra(Constant.phoneNumber, phoneNumber));
    }
}