package com.datingapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.datingapp.R;
import com.datingapp.databinding.ActivityProfileEditBinding;
import com.datingapp.models.UserModel;
import com.datingapp.utils.Constant;
import com.datingapp.utils.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ProfileEditActivity extends AppCompatActivity {

    ActivityProfileEditBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private int mYear, mMonth, mDay;
    RadioGroup radioGroupGender,radioGroupMarital,radioGroupWorking, radioGroupHonest, radioGroupInterest;
    RadioButton radioButtonGender,radioButtonMarital, radioButtonWorking, radioButtonHonest, radioButtonInterest;
    int selectedGender, selectedMarital,selectedWorking, selectedHonest, selectedInterest;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference imageRef;
    String url = null;

    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userModel = (UserModel) getIntent().getSerializableExtra(Constant.USER);
        database = FirebaseDatabase.getInstance();
        imageRef = FirebaseStorage.getInstance().getReference()
                .child("image")
                .child(Objects.requireNonNull(auth.getUid()));
        reference = database.getReference().child(Constant.USER);

        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupMarital = (RadioGroup) findViewById(R.id.radioGroupMarital);
        radioGroupWorking = (RadioGroup) findViewById(R.id.radioGroupWorking);
        radioGroupHonest = (RadioGroup) findViewById(R.id.radioGroupHonest);
        radioGroupInterest = (RadioGroup) findViewById(R.id.radioGroupInterest);

        binding.imageViewUserProfileSelect.setOnClickListener(v -> {
            CropImage.activity()
                    .setAspectRatio(1,1).start(ProfileEditActivity.this);
        });
        binding.imageViewChooseDate.setOnClickListener(v -> {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n")
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) ->
                            binding.eDateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        if (userModel == null){
            binding.buttonSave.setOnClickListener(v -> {
                // get selected radio button from radioGroup
                selectedGender = radioGroupGender.getCheckedRadioButtonId();
                selectedMarital = radioGroupMarital.getCheckedRadioButtonId();
                selectedWorking = radioGroupWorking.getCheckedRadioButtonId();
                selectedHonest = radioGroupHonest.getCheckedRadioButtonId();
                selectedInterest = radioGroupInterest.getCheckedRadioButtonId();

                if (binding.eName.getText().toString().isEmpty()){
                    binding.eName.setError(Constant.EMPTY);
                }else if (binding.eEmailAddress.getText().toString().isEmpty()){
                    binding.eEmailAddress.setError(Constant.EMPTY);
                }else if (binding.eDateOfBirth.getText().toString().isEmpty()){
                    binding.eDateOfBirth.setError(Constant.EMPTY);
                }else if (selectedGender == -1){
                    Constant.setMessage(this, "Select your gender");
                }else {
                    radioButtonGender = findViewById(selectedGender);
                    radioButtonMarital = findViewById(selectedMarital);
                    radioButtonWorking = findViewById(selectedWorking);
                    radioButtonHonest = findViewById(selectedHonest);
                    radioButtonInterest = findViewById(selectedInterest);
                    getData(radioButtonGender.getText().toString());
                }
            });
        }else {
            Glide.with(getApplicationContext())
                    .load(userModel.getUrl())
                    .placeholder(R.drawable.logo)
                    .into(binding.imageViewUserProfileSelect);
            binding.eDateOfBirth.setText(userModel.getDate_of_birth());
            binding.eName.setText(userModel.getName());
            binding.eEmailAddress.setText(userModel.getEmail());

            try {
                if (radioGroupGender.getCheckedRadioButtonId() == -1){
                    if (userModel.getGender().equals("Male")){
                        radioButtonGender = findViewById(R.id.radioMale);
                        radioButtonGender.setChecked(true);
                    }else if(userModel.getGender().equals("Female")){
                        radioButtonGender = findViewById(R.id.radioFemale);
                        radioButtonGender.setChecked(true);
                    }
                }

                if (radioGroupInterest.getCheckedRadioButtonId() == -1){
                    if (userModel.getInterest().equals("Male")){
                        radioButtonInterest = findViewById(R.id.radioMaleInterest);
                        radioButtonInterest.setChecked(true);
                    }else if (userModel.getInterest().equals("Female")){
                        radioButtonInterest = findViewById(R.id.radioFemaleInterest);
                        radioButtonInterest.setChecked(true);
                    }
                }

                if (radioGroupMarital.getCheckedRadioButtonId() == -1){
                    if (userModel.getMarital_status().equals("Single")){
                        radioButtonMarital = findViewById(R.id.radioSingle);
                        radioButtonMarital.setChecked(true);
                    }else if (userModel.getMarital_status().equals("Married")){
                        radioButtonMarital = findViewById(R.id.radioMarried);
                        radioButtonMarital.setChecked(true);
                    }else if (userModel.getMarital_status().equals("Other")){
                        radioButtonMarital = findViewById(R.id.radioOtherMarried);
                        radioButtonMarital.setChecked(true);
                    }
                }

                if (radioGroupWorking.getCheckedRadioButtonId() == -1){
                    if (userModel.getWorking().equals("School")){
                        radioButtonWorking = findViewById(R.id.radioSchool);
                        radioButtonWorking.setChecked(true);
                    }else if (userModel.getWorking().equals("Job")){
                        radioButtonWorking = findViewById(R.id.radioJob);
                        radioButtonWorking.setChecked(true);
                    }else if (userModel.getWorking().equals("Other")){
                        radioButtonWorking = findViewById(R.id.radioOtherWork);
                        radioButtonWorking.setChecked(true);
                    }
                }

                if (radioGroupHonest.getCheckedRadioButtonId() == -1){
                    if (userModel.getHonest().equals("Smoke")){
                        radioButtonHonest = findViewById(R.id.radioSmoke);
                        radioButtonHonest.setChecked(true);
                    }else if (userModel.getHonest().equals("Drink")){
                        radioButtonHonest = findViewById(R.id.radioDrink);
                        radioButtonHonest.setChecked(true);
                    }else if (userModel.getHonest().equals("Both")){
                        radioButtonHonest = findViewById(R.id.radioBoth);
                        radioButtonHonest.setChecked(true);
                    }else if (userModel.getHonest().equals("None")){
                        radioButtonHonest = findViewById(R.id.radioNone);
                        radioButtonHonest.setChecked(true);
                    }
                }
            }catch (RuntimeException e){
                Log.d(Constant.TAG_PROFILE, "onCreate: "+e.getMessage());
            }

            if (userModel != null){
                binding.buttonSave.setOnClickListener(v -> {
                    // get selected radio button from radioGroup
                    selectedGender = radioGroupGender.getCheckedRadioButtonId();
                    selectedMarital = radioGroupMarital.getCheckedRadioButtonId();
                    selectedWorking = radioGroupWorking.getCheckedRadioButtonId();
                    selectedHonest = radioGroupHonest.getCheckedRadioButtonId();
                    selectedInterest = radioGroupInterest.getCheckedRadioButtonId();

                    if (binding.eName.getText().toString().isEmpty()){
                        binding.eName.setError(Constant.EMPTY);
                    }else if (binding.eEmailAddress.getText().toString().isEmpty()){
                        binding.eEmailAddress.setError(Constant.EMPTY);
                    }else if (binding.eDateOfBirth.getText().toString().isEmpty()){
                        binding.eDateOfBirth.setError(Constant.EMPTY);
                    }else if (selectedGender == -1){
                        Constant.setMessage(this, "Select your gender");
                    }else {
                        radioButtonGender = findViewById(selectedGender);
                        radioButtonMarital = findViewById(selectedMarital);
                        radioButtonWorking = findViewById(selectedWorking);
                        radioButtonHonest = findViewById(selectedHonest);
                        radioButtonInterest = findViewById(selectedInterest);
                        updateData(radioButtonGender.getText().toString());
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri image_uri = result.getUri();
                imageRef.child("IMG_"+auth.getUid())
                        .putFile(image_uri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                task
                                        .getResult()
                                        .getStorage()
                                        .getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Glide
                                                    .with(this)
                                                    .load(uri)
                                                    .centerCrop()
                                                    .into(binding.imageViewUserProfileSelect);
                                            url = uri.toString();
                                        })
                                        .addOnFailureListener(e -> {
                                            Constant.setMessage(ProfileEditActivity.this,e.getMessage());
                                            Log.d(Constant.TAG_PROFILE, "onFailure: "+e.getMessage());
                                        });
                            }else {
                                Constant.setMessage(ProfileEditActivity.this,Constant.FAILED);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Constant.setMessage(ProfileEditActivity.this,e.getMessage());
                        });
            }
        }
    }

    private void getData(String gender) {
        UserModel model = new UserModel();
        model.setName(binding.eName.getText().toString());
        model.setEmail(binding.eEmailAddress.getText().toString());
        model.setDate_of_birth(binding.eDateOfBirth.getText().toString());
        model.setGender(gender);
        model.setUrl(url);
        model.setCreated_by(new Date(System.currentTimeMillis()));
        model.setUpdated_to(new Date(System.currentTimeMillis()));
        model.setPhoneNumber(getIntent().getStringExtra(Constant.phoneNumber));
        model.setMarital_status((selectedMarital == -1)? "null" : radioButtonMarital.getText().toString());
        model.setWorking((selectedWorking == -1)? "null" : radioButtonWorking.getText().toString());
        model.setHonest((selectedHonest == -1)? "null" : radioButtonHonest.getText().toString());
        model.setInterest((selectedInterest == -1)? "null" : radioButtonInterest.getText().toString());
        String uid = user.getUid();
        reference.child(uid)
                .setValue(model)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (task.isSuccessful()){
                            new UserPreferences(this).setName(binding.eName.getText().toString());
                            Constant.setMessage(this, "Profile completely saved");
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }
                });

    }

    private void updateData(String gender) {
        HashMap<String, Object> model = new HashMap<>();
        model.put(Constant.NAME,binding.eName.getText().toString());
        model.put(Constant.EMAIL,binding.eEmailAddress.getText().toString());
        model.put(Constant.DOB,binding.eDateOfBirth.getText().toString());
        model.put(Constant.GENDER,gender);
        model.put(Constant.URL,url);
        model.put(Constant.CREATED_DATE,new Date(System.currentTimeMillis()));
        model.put(Constant.UPDATED_DATE,new Date(System.currentTimeMillis()));
        model.put(Constant.phoneNumber,getIntent().getStringExtra(Constant.phoneNumber));
        model.put(Constant.MARITAL_STATUS,(selectedMarital == -1)? "null" : radioButtonMarital.getText().toString());
        model.put(Constant.WORKING,(selectedWorking == -1)? "null" : radioButtonWorking.getText().toString());
        model.put(Constant.HONEST,(selectedHonest == -1)? "null" : radioButtonHonest.getText().toString());
        model.put(Constant.INTEREST,(selectedInterest == -1)? "null" : radioButtonInterest.getText().toString());
        String uid = user.getUid();
        reference.child(uid)
                .updateChildren(model)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (task.isSuccessful()){
                            new UserPreferences(this).setName(binding.eName.getText().toString());
                            Constant.setMessage(this, "Profile Updated");
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        HashMap<String, String> data = new UserPreferences(getApplicationContext()).getData();
        if (data.get(Constant.NAME) == null){
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.d(Constant.TAG_PROFILE, "onDataChange: "+snapshot.getKey());
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if (snap.getKey() != null){
                            Log.d(Constant.TAG_PROFILE, "onDataChange: "+snap.getKey());
                            if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(snap.getKey())){
                                new UserPreferences(ProfileEditActivity.this).setName(snap.child(Constant.NAME).getValue().toString());
                                new UserPreferences(ProfileEditActivity.this).setCredentials(getIntent().getStringExtra(Constant.phoneNumber));
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Constant.setMessage(getApplicationContext(),data.get(Constant.NAME));
        }
        super.onStart();
    }
}