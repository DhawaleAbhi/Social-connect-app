package com.datingapp.utils;

import android.content.Context;
import android.widget.Toast;

public class Constant {
    public static final String TAG_MAIN = "mainActivity";
    public static final String TAG_CHAT = "chatActivity";
    public static final String TAG_SIGN_IN = "signInActivity";
    public static final String TAG_PROFILE = "profileActivity";
    public static final int REQUEST_PERMISSION_CODE = 100;
    public static final String permission_verified = "Permission Verified";
    public static final String permission_unverified = "Permission Denied";
    public static final String phoneNumber = "phoneNumber";
    public static final String USER = "users";
    public static final String CONNECTIONS = "connections";
    public static final String MATCH = "matches";
    public static final String EMPTY = "Empty";
    public static final String FAILED = "Failed";

    //    DB Fields
    public static final String GENDER = "gender";
    public static final String HONEST = "honest";
    public static final String WORKING = "working";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String INTEREST = "interest";
    public static final String CREATED_DATE = "created_by";
    public static final String UPDATED_DATE = "updated_to";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String DOB = "date_of_birth";
    public static final String URL = "url";

    public static void setMessage(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
