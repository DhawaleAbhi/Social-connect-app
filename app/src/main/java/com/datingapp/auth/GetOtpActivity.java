package com.datingapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.chaos.view.PinView;
import com.datingapp.R;
import com.datingapp.activities.ProfileEditActivity;
import com.datingapp.utils.Constant;
import com.datingapp.utils.UserPreferences;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GetOtpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private PinView pinView;
    private String sentOtp, code,phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);
        auth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra(Constant.phoneNumber);
        pinView = findViewById(R.id.firstPinView);
        verifyPhoneNumber(phoneNumber);

        findViewById(R.id.buttonVerified)
                .setOnClickListener(v -> {
                    code = Objects.requireNonNull(pinView.getText()).toString().trim();
                    if (!code.isEmpty() | code.length() == 6){
                        pinView.setText(code);
                        verifyCode(code);
                    }else {
                        Constant.setMessage(GetOtpActivity.this,Constant.EMPTY);
                    }
                });
    }

    private void verifyPhoneNumber(String phone){
        PhoneAuthOptions option = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(GetOtpActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        sentOtp = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String sms = phoneAuthCredential.getSmsCode();
                        if (sms != null) {
                            pinView.setText(sms);
                            verifyCode(sms);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Constant.setMessage(GetOtpActivity.this,e.getMessage());
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(option);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentOtp,code);
        authenticateUser(credential);
    }

    public void authenticateUser(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnSuccessListener(authResult -> saveData())
                .addOnFailureListener(e -> Constant.setMessage(GetOtpActivity.this,e.getMessage()));
    }

    private void saveData() {
        Constant.setMessage(this, "Verified");
        new UserPreferences(getApplicationContext()).setCredentials(phoneNumber);
        startActivity(new Intent(getApplicationContext(), ProfileEditActivity.class)
                .putExtra(Constant.phoneNumber,phoneNumber));
        finish();
    }
}