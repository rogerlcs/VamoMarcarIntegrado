package com.example.vamomarcarintegrado;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter {
    List<User> users;
    Context context;

    public ParticipantAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.add_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);

        TextView tvNameAdd = holder.itemView.findViewById(R.id.tvNameAdd);
        tvNameAdd.setText(user.nome);


        ImageView imvPhotoAdd = holder.itemView.findViewById(R.id.imvPhotoAdd);
        if(user.img != null){
            imvPhotoAdd.setImageBitmap(user.img);
        }

        CheckBox ckbUser = holder.itemView.findViewById(R.id.ckbUser);
        ckbUser.setVisibility(View.INVISIBLE);
        ckbUser.setClickable(false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,PerfilActivity.class);
                i.putExtra("id", user.id);
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
