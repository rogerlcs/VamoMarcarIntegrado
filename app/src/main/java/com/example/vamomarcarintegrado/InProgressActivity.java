package com.example.vamomarcarintegrado;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class InProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);


        Toolbar toolbar = findViewById(R.id.tbEventoSugestao);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        RecyclerView rvDatasSugestoes = findViewById(R.id.rvDatasSugestoes);
        RecyclerView rvParticpSugestao = findViewById(R.id.rvParticpSugestao);

        InProgressViewModel vm = new ViewModelProvider(this, new InProgressViewModel.InProgressViewModelFactory(this,id)).get(InProgressViewModel.class);
        LiveData<Event> event = vm.getEvent();
        event.observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                Log.i("Id do evento", event.id);
                InProgressAdapter inProgressAdapter = new InProgressAdapter(event.dates,InProgressActivity.this, event.status_event, event.id);
                rvDatasSugestoes.setAdapter(inProgressAdapter);
                rvDatasSugestoes.setLayoutManager(new LinearLayoutManager(InProgressActivity.this));

                ParticipantAdapter participantAdapter = new ParticipantAdapter(event.users, InProgressActivity.this);
                rvParticpSugestao.setAdapter(participantAdapter);
                rvParticpSugestao.setLayoutManager(new LinearLayoutManager(InProgressActivity.this));

                TextView tvNomeSugestao = findViewById(R.id.tvNomeSugestao);
                tvNomeSugestao.setText(event.name);

                TextView tvDescEvS = findViewById(R.id.tvDescEvS);
                tvDescEvS.setText(event.description);

                ImageButton imageButton = findViewById(R.id.imButton);


                TextView tvCronometro = findViewById(R.id.tvCronometro);

                event.startTime(InProgressActivity.this, tvCronometro);
                FloatingActionButton floatingActionButton = findViewById(R.id.fabMarcar);

                if(event.status_event != 2){
                    floatingActionButton.setClickable(false);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    Log.i("Texto cronometro: ", tvCronometro.getText().toString());
                }
                else{
                    floatingActionButton.setClickable(true);
                    floatingActionButton.setVisibility(View.VISIBLE);
                    Log.i("Texto cronometro: ", tvCronometro.getText().toString());
                }





                TextView tvLocalEvS = findViewById(R.id.tvLocalEvS);
                tvLocalEvS.setText("Local: " + event.local);


                /*floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(SugestaoActivity.this).setMessage("Deseja Marcar o Evento?").
                                setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(SugestaoActivity.this, ResultActivity.class);
                                        i.putExtra("index",position);
                                        startActivity(i);
                                    }
                                }).setNegativeButton("Não",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                });


            }*/

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tbevento, menu);
        return true;
    }
}