package com.example.womensafetyapp;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class helpline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_helpline);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        List<item> items=new ArrayList<item>();
        items.add(new item(R.drawable.nationalhelpline,"112","National helpline", R.drawable.phone));
        items.add(new item(R.drawable.ambulance,"109","Ambulance", R.drawable.phone));
        items.add(new item(R.drawable.medic,"102","Pregnancy medic", R.drawable.phone));
        items.add(new item(R.drawable.fireservice,"108","Fire services", R.drawable.phone));
        items.add(new item(R.drawable.police,"100","Police", R.drawable.phone));
        items.add(new item(R.drawable.womenhelpline,"1091","Women helpline", R.drawable.phone));
        items.add(new item(R.drawable.child,"1098","Child helpline", R.drawable.phone));
        items.add(new item(R.drawable.accident,"1073","Road accident", R.drawable.phone));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new myAdapter(getApplicationContext(),items));
    }
}