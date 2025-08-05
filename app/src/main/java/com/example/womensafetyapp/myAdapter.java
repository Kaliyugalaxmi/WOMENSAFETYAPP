package com.example.womensafetyapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myViewHolder> {
    Context context;
    List<item> items;

    public myAdapter(Context context, List<item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new myViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int i) {
        holder.imageview.setImageResource(items.get(i).getImage());
        holder.num.setText(items.get(i).getName());
        holder.txt.setText(items.get(i).getName1());
        holder.imageview2.setImageResource(items.get(i).getImage1());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
