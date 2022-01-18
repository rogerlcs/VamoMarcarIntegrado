package com.example.vamomarcarintegrado;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class InProgressAdapter extends RecyclerView.Adapter {

    List<Data> datas;
    Context context;
    int status;
    String id;

    public InProgressAdapter(List<Data> datas, Context context, int status, String idevento) {
        this.datas = datas;
        this.context = context;
        this.status = status;
        this.id = idevento;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.date_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        TextView tvDatavotacao = holder.itemView.findViewById(R.id.tvDataVotacao);
        ImageButton imbVotacao = holder.itemView.findViewById(R.id.imbVoto);
        Data data = datas.get(position);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataformatada = sdf.format(data.data.getTime());
        String hora = time.format(data.data.getTime());

        switch (status){
            case 0:
                imbVotacao.setVisibility(View.INVISIBLE);
                imbVotacao.setClickable(false);
                tvDatavotacao.setText(dataformatada + ", " + hora);
                break;
            case 2:
                imbVotacao.setVisibility(View.INVISIBLE);
                imbVotacao.setClickable(false);
                tvDatavotacao.setText(dataformatada + ", " + hora + " - " + data.votes);
                break;
            case 1:
                imbVotacao.setVisibility(View.VISIBLE);
                imbVotacao.setClickable(true);
                if(data.votei == 0){
                    imbVotacao.setColorFilter(Color.argb(255, 133, 132, 132));
                }
                else {
                    imbVotacao.setColorFilter(Color.argb(255, 0, 0, 0));
                }
                imbVotacao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.Vote(context, id);
                        Log.i("data votei", String.valueOf(id));

                    }
                });
                tvDatavotacao.setText(dataformatada + ", " + hora + " - " + data.votes);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
