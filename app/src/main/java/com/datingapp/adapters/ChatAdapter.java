package com.datingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datingapp.R;
import com.datingapp.models.ChatModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    Context context;
    List<ChatModel> list;

    public ChatAdapter(Context context, List<ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_layout,parent,false));
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel model = list.get(position);
        holder.textContent.setText(model.getMsg());
//        try {
//            holder.textDate.setText(String.valueOf(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(model.getDate())));
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }

        if (model.isUserId()){
            holder.textContent.setGravity(Gravity.END);
            holder.textContent.setTextColor(Color.parseColor("#404040"));
            holder.itemView.setBackgroundColor(Color.parseColor("#f4f4f4"));
        }else {
            holder.textContent.setGravity(Gravity.START);
            holder.textContent.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemView.setBackgroundColor(Color.parseColor("#2DB4CB"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textContent, textDate;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            textContent = itemView.findViewById(R.id.textChatContent);
            textDate = itemView.findViewById(R.id.textChatDate);
        }
    }
}
