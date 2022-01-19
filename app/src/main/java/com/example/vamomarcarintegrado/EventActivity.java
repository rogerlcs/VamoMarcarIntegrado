package com.example.vamomarcarintegrado;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        Toolbar toolbar = findViewById(R.id.tbEventoMarcado);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String idevento = i.getStringExtra("id");

        RecyclerView rvParticp = findViewById(R.id.rvParticp);

        EventViewModel vm = new ViewModelProvider(this, new EventViewModel.EventViewModelFactory(this, idevento)).get(EventViewModel.class);
        LiveData<Event> event = vm.getEvent();
        event.observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                Log.i("Id do evento", event.id);

                ParticipantAdapter participantAdapter = new ParticipantAdapter(event.users, EventActivity.this);
                rvParticp.setAdapter(participantAdapter);
                rvParticp.setLayoutManager(new LinearLayoutManager(EventActivity.this));

                TextView tvNome = findViewById(R.id.tvNome);
                tvNome.setText(event.name);

                ImageView imEvento = findViewById(R.id.imEvento);
                imEvento.setImageBitmap(event.img);

                TextView tvDescEv = findViewById(R.id.tvDescEv);
                tvDescEv.setText(event.description);

                TextView tvHoraData = findViewById(R.id.tvHoraData);
                tvHoraData.setText("Hora e Data: "+ event.getData() + "  " + event.getHour());

                ImageView imMapView = findViewById(R.id.imMapView);
                imMapView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("geo:0,0?q=" + event.local));
                        Log.i("Click:", "clickei");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });

            }
        });
    }
}