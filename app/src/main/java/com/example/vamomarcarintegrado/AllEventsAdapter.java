package com.example.vamomarcarintegrado;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllEventsAdapter extends RecyclerView.Adapter {
    Context context;
    List<Event> events;

    public AllEventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v;
        switch (viewType){
            case 0:
                v = layoutInflater.inflate(R.layout.invite_item,parent,false);
                return new MyViewHolder(v);
            case 1:
                v = layoutInflater.inflate(R.layout.event_item, parent, false);
                return new MyViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Event event = events.get(position);
        TextView tvTitle;
        TextView tvDescription;
        TextView tvLocation;
        TextView tvParticipants;
        TextView tvDate;


        switch (getItemViewType(position)){
            case 1:
                Log.i("Nome do evento: ", event.name);
                Log.i("Convite", String.valueOf(event.status_invite));
                tvTitle = holder.itemView.findViewById(R.id.tvTitle);
                tvTitle.setText(event.name);

                tvDescription = holder.itemView.findViewById(R.id.tvDescription);
                tvDescription.setText(event.description);

                tvParticipants = holder.itemView.findViewById(R.id.tvParticipants);
                tvParticipants.setText(event.total + " Participantes");

                tvDate = holder.itemView.findViewById(R.id.tvDate);

                tvLocation = holder.itemView.findViewById(R.id.tvLocation);
                tvLocation.setText(event.local);

                if(event.status_event == 3){
                    tvDate.setText(event.getData() + "    " + event.getHour());
                }

                else{
                    event.startTime(context, tvDate);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, InProgressActivity.class);
                            i.putExtra("id", event.id);
                            context.startActivity(i);
                        }
                    });
                }

                break;
            case 0:
                tvTitle = holder.itemView.findViewById(R.id.tvTitleI);
                tvTitle.setText(event.name);

                tvDescription = holder.itemView.findViewById(R.id.tvDescI);
                tvDescription.setText(event.description);

                tvParticipants = holder.itemView.findViewById(R.id.tvParticipantsI);
                tvParticipants.setText(event.total + " Participantes");

                tvDate = holder.itemView.findViewById(R.id.tvDateI);
                tvDate.setText("");

                tvLocation = holder.itemView.findViewById(R.id.tvLocationI);
                tvLocation.setText(event.local);

                Button btnAccept = holder.itemView.findViewById(R.id.btnAccept);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event.setStatusinvite(context,1);
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public int getItemViewType(int position) {
        return events.get(position).status_invite;
    }
}
