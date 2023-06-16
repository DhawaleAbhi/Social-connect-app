package com.datingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.datingapp.R;
import com.datingapp.adapters.ChatAdapter;
import com.datingapp.databinding.ActivityChatBinding;
import com.datingapp.models.ChatModel;
import com.datingapp.models.MatchObjects;
import com.datingapp.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    List<ChatModel> list = new ArrayList<>();
    ChatAdapter adapter;
    String currentUserId, matchId;
    DatabaseReference reference,referenceChat;
    MatchObjects objects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        objects = (MatchObjects) getIntent().getSerializableExtra("dataUser");
        Log.d(Constant.TAG_CHAT, "onCreate: "+objects.getUserId());
        matchId = objects.getUserId();
        referenceChat = reference.child(Constant.USER)
                .child(currentUserId)
                .child(Constant.CONNECTIONS)
                .child("matches")
                .child(matchId)
                .child("chat_id");
        getChatId();
        binding.recyclerViewChat.setHasFixedSize(false);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ChatAdapter(getApplicationContext(), getUserChats());
        binding.recyclerViewChat.setAdapter(adapter);


        binding.textViewChat.setText(objects.getName());
        binding.buttonSendImage.setOnClickListener(v -> {
            if (binding.eTextWrite.getText().toString().isEmpty()){
                binding.eTextWrite.setError(Constant.EMPTY);
            }else {
                sendMessage();
            }
        });

    }

    private void sendMessage(){
        DatabaseReference ref = reference.child("chat").push();

        HashMap<String, String> map = new HashMap<>();
        map.put("createByUser", currentUserId);
        map.put("msg", binding.eTextWrite.getText().toString());
        map.put("createdAt", String.valueOf(new Date()));
        ref.setValue(map);
        binding.eTextWrite.setText("");
    }

    private void getChatId(){
        referenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String chatId = snapshot.getKey();
                    assert chatId != null;
                    reference.child("chat").child(chatId);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatMessage() {
        reference.child("chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot snap : snapshot.getChildren()){
                                String message = "";
                                String createdByUser = "";
                                String date = "";
                                Log.d(Constant.TAG_CHAT, "onDataChangeChat: "+snap.child("createByUser").getValue().toString());

                                if (snap.child("msg").getValue() != null){
                                    message = Objects.requireNonNull(snap.child("msg").getValue()).toString();
                                }

                                if (snap.child("createByUser").getValue() != null){
                                    createdByUser = Objects.requireNonNull(snap.child("createByUser").getValue()).toString();
                                }

                                if (snap.child("createByUser").getValue() != null){
                                    date = Objects.requireNonNull(snap.child("createdAt").getValue()).toString();
                                }

                                if (message != null && createdByUser != null){
                                    boolean currentUserBoolean = false;
                                    if (createdByUser.equals(currentUserId)){
                                        currentUserBoolean = true;
                                    }
                                    ChatModel model = new ChatModel(currentUserBoolean,message,date);
                                    list.add(model);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private List<ChatModel> getUserChats(){
        return list;
    }
}