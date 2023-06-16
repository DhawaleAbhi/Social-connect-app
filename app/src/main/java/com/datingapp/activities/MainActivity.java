package com.datingapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.datingapp.R;
import com.datingapp.adapters.CardArrayAdapter;
import com.datingapp.auth.SignInActivity;
import com.datingapp.models.Cards;
import com.datingapp.models.UserModel;
import com.datingapp.utils.Constant;
import com.datingapp.utils.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Cards cards[];
    private List<Cards> rowItemList;
    private CardArrayAdapter arrayAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userSex, userOppositeSex;
    private ImageView imageViewGoProfile,imageViewGoMatch,imageViewGoExit;

    private UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(Constant.USER);
        imageViewGoExit = findViewById(R.id.imageViewGoExit);
        imageViewGoProfile = findViewById(R.id.imageViewGoProfile);
        imageViewGoMatch = findViewById(R.id.imageViewGoMatch);
        checkUserPreferences();
        rowItemList = new ArrayList<>();
        arrayAdapter = new CardArrayAdapter(this, R.layout.item,rowItemList);

        imageViewGoProfile
                .setOnClickListener(v ->
                    startActivity(new Intent(getApplicationContext(), ProfileEditActivity.class)
                            .putExtra(Constant.USER,model)));

        imageViewGoMatch.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MatchActivity.class)));

        imageViewGoExit.setOnClickListener(v ->{
                new UserPreferences(getApplicationContext()).clear();
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });

        SwipeFlingAdapterView flingAdapterView = findViewById(R.id.frame);
        flingAdapterView.setAdapter(arrayAdapter);
        flingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItemList.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                reference
                        .child(userId)
                        .child(Constant.CONNECTIONS)
                        .child("nope")
                        .child(Objects.requireNonNull(auth.getUid()))
                        .setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                reference.child(userId)
                        .child(Constant.CONNECTIONS)
                        .child("yeps")
                        .child(Objects.requireNonNull(auth.getUid()))
                        .setValue(true);
                
                isConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
//                Constant.setMessage(MainActivity.this, Constant.EMPTY);
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = flingAdapterView.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingAdapterView.setOnItemClickListener((itemPosition, dataObject) -> Constant.setMessage(MainActivity.this, "Clicked!"));

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference dbReference = reference.child(Objects.requireNonNull(auth.getUid()))
                .child(Constant.CONNECTIONS).child("yeps")
                .child(userId);

        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Constant.setMessage(MainActivity.this, "new connection");
                    String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
//                    reference.child(snapshot.getKey())
//                            .child(Constant.CONNECTIONS)
//                            .child("matches")
//                            .child(auth.getUid())
//                            .setValue(true);

                    reference.child(snapshot.getKey())
                            .child(Constant.CONNECTIONS)
                            .child("matches")
                            .child(auth.getUid())
                            .child("chat_id")
                            .setValue(key);

//                    reference.child(auth.getUid())
//                            .child(Constant.CONNECTIONS)
//                            .child("matches")
//                            .child(snapshot.getKey())
//                            .setValue(true);

                    reference.child(auth.getUid())
                            .child(Constant.CONNECTIONS)
                            .child("matches")
                            .child(snapshot.getKey())
                            .child("chat_id")
                            .setValue(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkUserPreferences(){
        reference
                .child(auth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    model = snapshot.getValue(UserModel.class);
                                    Log.d(Constant.TAG_MAIN, "onDataChange: "+model.getName());
                                    if (snapshot.child(Constant.GENDER).getValue() != null){
                                        userSex = snapshot.child(Constant.GENDER).getValue().toString();
                                        switch (userSex){
                                            case "Male":
                                                userOppositeSex = snapshot.child(Constant.INTEREST).getValue().toString();
                                                break;
                                            case "Female":
                                                userOppositeSex = snapshot.child(Constant.INTEREST).getValue().toString();
                                                break;
                                        }
                                        getUserOppositeUserSex();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
//        reference.child(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        if (snapshot.exists()){
//                            Toast.makeText(MainActivity.this, snapshot.child("gender").getValue()+"", Toast.LENGTH_SHORT).show();
//                            Log.d(Constant.TAG_MAIN, "onChildAdded: "+snapshot.child("email"));
////                            if (snapshot.child(Constant.GENDER).getValue() != null){
////                                userSex = snapshot.child(Constant.GENDER).getValue().toString();
////                                userOppositeSex = "Female";
////                                Constant.setMessage(MainActivity.this,userSex);
////                                switch (userSex){
////                                    case "Male":
////                                        userOppositeSex = snapshot.child(Constant.INTEREST).getValue().toString();
////                                        break;
////                                    case "Female":
////                                        userOppositeSex = snapshot.child(Constant.INTEREST).getValue().toString();
////                                        break;
////                                }
////                                getUserOppositeUserSex();
////                            }
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//
//                });

        /** reference.child("Female")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getKey().equals(uid)){
                            userSex = "Female";
                            userOppositeSex = "Male";
                            getUserOppositeUserSex();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
    }

    public void getUserOppositeUserSex(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()){

                    Log.d(Constant.TAG_MAIN, "onDataChangeValues: "+s.getKey());

                    if (s.exists() && !snapshot
                                    .child(s.getKey())
                                    .child(Constant.CONNECTIONS)
                                    .child("nope")
                                    .hasChild(Objects.requireNonNull(auth.getUid())) &&
                                    !snapshot
                                            .child(s.getKey())
                                            .child(Constant.CONNECTIONS)
                                            .child("yeps")
                                            .hasChild(Objects.requireNonNull(auth.getUid())) && s.child(Constant.GENDER).getValue().toString().equals(userOppositeSex)){
                                        rowItemList.add(new Cards(s.getKey(),s.child("name").getValue().toString(),s.child("url").getValue().toString()));
                                        arrayAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        reference.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        for (DataSnapshot s : snapshot.getChildren()){
//                            Log.d(Constant.TAG_MAIN, "onChildAdded: "+s);
//                            if (s.exists() && !snapshot
//                                    .child(s.getKey())
//                                    .child(Constant.CONNECTIONS)
//                                    .child("nope")
//                                    .hasChild(Objects.requireNonNull(auth.getUid())) &&
//                                    !s
//                                            .child(s.getKey())
//                                            .child(Constant.CONNECTIONS)
//                                            .child("yeps")
//                                            .hasChild(Objects.requireNonNull(auth.getUid())) &&
//                                    s.child(s.getKey())
//                                            .child(Constant.CONNECTIONS)
//                                            .child(Constant.GENDER)
//                                            .getValue().toString().equals(userOppositeSex)){
////                                rowItemList.add(new Cards(snapshot.getKey(),snapshot.child("name").getValue().toString(),snapshot.child("url").getValue().toString()));
////                                arrayAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

}