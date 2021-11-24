package com.example.vamomarcarintegrado;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddEventAdapter extends RecyclerView.Adapter {
    List<User> users;
    AddEventActivity context;

    public AddEventAdapter(List<User> users, AddEventActivity context) {
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

        CheckBox ckbUser = holder.itemView.findViewById(R.id.ckbUser);
        ckbUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AddEventViewModel vm = new ViewModelProvider(context).get(AddEventViewModel.class);
                if(isChecked){
                    Log.i("Teste caixa:", "Marquei " + user.nome);
                    vm.ids.add(user.id);
                    Log.i("Teste Lista:", "Marquei " + String.valueOf(vm.ids.size()));

                }
                else {
                    Log.i("Teste caixa:", "Desmarquei " + user.nome);
                    vm.ids.remove(user.id);
                    Log.i("Teste Lista:", "Marquei " + String.valueOf(vm.ids.size()));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
