package com.datingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datingapp.R;
import com.datingapp.activities.ChatActivity;
import com.datingapp.models.MatchObjects;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private Context context;
    private List<MatchObjects> list;

    public MatchAdapter(Context context, List<MatchObjects> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MatchAdapter.MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MatchViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.match_items_layout,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.MatchViewHolder holder, int position) {
        MatchObjects objects = list.get(position);
        holder.textView.setText(objects.getName());
        Glide.with(context)
                .load(objects.getUrl())
                .placeholder(R.drawable.logo)
                .into(holder.imageUser);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageUser;
        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewUserName);
            imageUser = itemView.findViewById(R.id.imageViewUserPicture);

            itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context.getApplicationContext(), ChatActivity.class)
                        .putExtra("dataUser",list.get(getAdapterPosition())));
            });
        }

    }
}
