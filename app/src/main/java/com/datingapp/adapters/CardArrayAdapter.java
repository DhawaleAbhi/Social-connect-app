package com.datingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.datingapp.R;
import com.datingapp.models.Cards;

import java.util.List;

public class CardArrayAdapter extends ArrayAdapter<Cards> {
    Context context;

    public CardArrayAdapter(Context context, int resouceId, List<Cards> items) {
        super(context,resouceId,items);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Cards cards = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        }

        ImageView itemImage = convertView.findViewById(R.id.imageViewItem);
        TextView itemName = convertView.findViewById(R.id.textViewItem);

        itemName.setText(cards.getName());
        Glide.with(getContext())
                .load(cards.getUrl())
                .placeholder(R.drawable.logo)
                .into(itemImage);

        return convertView;
    }
}
