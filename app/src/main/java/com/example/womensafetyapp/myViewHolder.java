package com.example.womensafetyapp;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class myViewHolder extends RecyclerView.ViewHolder{

    ImageView imageview,imageview2;
    TextView num,txt;
    public myViewHolder(@NonNull View itemView) {
        super(itemView);
        imageview=itemView.findViewById(R.id.imageview);
        imageview2=itemView.findViewById(R.id.imageview2);
        num=itemView.findViewById(R.id.num);
        txt=itemView.findViewById(R.id.txt);

    }
}
