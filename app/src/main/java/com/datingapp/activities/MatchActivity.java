package com.datingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.datingapp.R;
import com.datingapp.adapters.MatchAdapter;
import com.datingapp.databinding.ActivityMatchBinding;
import com.datingapp.models.MatchObjects;
import com.datingapp.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchActivity extends AppCompatActivity {

    ActivityMatchBinding binding;
    MatchAdapter adapter;
    private List<MatchObjects> list = new ArrayList<>();
    DatabaseReference reference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference().child(Constant.USER);

        getUserMatchId();

        binding.recyclerViewMatch.setHasFixedSize(true);
        binding.recyclerViewMatch.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchAdapter(this, getDatasetMatch());
        binding.recyclerViewMatch.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private void getUserMatchId() {
        reference.child(currentUserId)
                .child(Constant.CONNECTIONS)
                .child(Constant.MATCH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot snap:snapshot.getChildren()){
                                fetchMatchInformation(snap.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchMatchInformation(String key) {
        reference.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String userId = snapshot.getKey();
                            String name = "";
                            String imageUrl = "";
                            if (snapshot.child(Constant.NAME).getValue() != null){
                                name = Objects.requireNonNull(snapshot.child(Constant.NAME).getValue()).toString();
                            }
                            if (snapshot.child(Constant.URL).getValue() != null){
                                imageUrl = Objects.requireNonNull(snapshot.child(Constant.URL).getValue()).toString();
                            }

                            MatchObjects obj = new MatchObjects(userId,name,imageUrl);
                            list.add(obj);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private List<MatchObjects> getDatasetMatch() {

        return  list;
    }
}