package com.example.vamomarcarintegrado;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InProgressActivity extends AppCompatActivity {
    long miliPrazoVotacao;
    String idevento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);


        Toolbar toolbar = findViewById(R.id.tbEventoSugestao);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        this.idevento = i.getStringExtra("id");

        RecyclerView rvDatasSugestoes = findViewById(R.id.rvDatasSugestoes);
        RecyclerView rvParticpSugestao = findViewById(R.id.rvParticpSugestao);

        InProgressViewModel vm = new ViewModelProvider(this, new InProgressViewModel.InProgressViewModelFactory(this,idevento)).get(InProgressViewModel.class);
        LiveData<Event> event = vm.getEvent();
        event.observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                miliPrazoVotacao = event.dateV.getTimeInMillis();
                Log.i("Id do evento", event.id);
                InProgressAdapter inProgressAdapter = new InProgressAdapter(event.dates,InProgressActivity.this, event.status_event, event.id);
                rvDatasSugestoes.setAdapter(inProgressAdapter);
                rvDatasSugestoes.setLayoutManager(new LinearLayoutManager(InProgressActivity.this));

                ParticipantAdapter participantAdapter = new ParticipantAdapter(event.users, InProgressActivity.this);
                rvParticpSugestao.setAdapter(participantAdapter);
                rvParticpSugestao.setLayoutManager(new LinearLayoutManager(InProgressActivity.this));

                TextView tvNomeSugestao = findViewById(R.id.tvNomeSugestao);
                tvNomeSugestao.setText(event.name);

                ImageView imvEventInProgress = findViewById(R.id.imvEventInProgress);
                imvEventInProgress.setImageBitmap(event.img);

                TextView tvDescEvS = findViewById(R.id.tvDescEvS);
                tvDescEvS.setText(event.description);

                ImageButton imageButton = findViewById(R.id.imbSugestao);


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


                ImageButton imbSugestao = findViewById(R.id.imbSugestao);
                imbSugestao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                InProgressActivity.this);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar mCalendar = Calendar.getInstance();
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, month);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                showHourPicker(mCalendar);
                            }
                        });
                        datePickerDialog.show();
                    }
                });


                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(InProgressActivity.this).setMessage("Deseja Marcar o Evento?").
                                setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        final String login = Config.getLogin(InProgressActivity.this);
                                        final String password = Config.getPassword(InProgressActivity.this);

                                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                                        //Executando a thread
                                        executorService.execute(new Runnable() {
                                            @Override
                                            public void run() {

                                                List<Data> dataLists = new ArrayList<>();
                                                List<String> datas = new ArrayList<>();
                                                final Data[] d = new Data[1];

                                                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_winning_dates.php", "GET", "UTF-8");
                                                httpRequest.setBasicAuth(login, password);
                                                httpRequest.addParam("idevento", idevento);

                                                try {
                                                    InputStream is = httpRequest.execute();
                                                    String result = Util.inputStream2String(is,"UTF-8");
                                                    Log.d("HTTP_REQUEST_RESULT", result);

                                                    httpRequest.finish();


                                                    JSONObject jsonObject = new JSONObject(result);

                                                    int success = jsonObject.getInt("success");
                                                    if(success == 1){
                                                        JSONArray jsonArrayDatas =jsonObject.getJSONArray("datas");
                                                        for (int i = 0; i < jsonArrayDatas.length(); i++){
                                                            JSONObject jData = jsonArrayDatas.getJSONObject(i);
                                                            String codigo = jData.getString("id");
                                                            String date = jData.getString("data");
                                                            int votos = jData.getInt("votos");



                                                            Timestamp tdate = Timestamp.valueOf(date);
                                                            Calendar calendarD =  Calendar.getInstance();
                                                            calendarD.setTime(tdate);
                                                            Data data = new Data(codigo, calendarD, votos);

                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                                            datas.add(sdf.format(calendarD.getTime()) + " - " + String.valueOf(votos));
                                                            dataLists.add(data);
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                final int[] d = new int[1];
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(InProgressActivity.this);
                                                                builder.setTitle("Escolha a data:");
                                                                final CharSequence[] datasChar = datas.toArray(new CharSequence[datas.size()]);
                                                                builder.setSingleChoiceItems(datasChar, -1, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        d[0] = which;
                                                                    }
                                                                });
                                                                builder.setPositiveButton("Marcar", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        marcar(dataLists.get(d[0]));
                                                                    }
                                                                });
                                                                AlertDialog alertDialog = builder.create();
                                                                alertDialog.show();
                                                            }

                                                        });

                                                    }
                                                    else {
                                                        final String error = jsonObject.getString("error");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(InProgressActivity.this, error, Toast.LENGTH_LONG).show();
                                                            }

                                                        });
                                                    }

                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                        AlertDialog marcar = null;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(InProgressActivity.this);

                                    }
                                }).setNegativeButton("Não",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                });


            }
            public void showHourPicker(Calendar c) {
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);


                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, 00);
                            if(c.getTimeInMillis() > miliPrazoVotacao){
                                sugerir(c);
                            }
                            else{
                                AlertDialog alertDialog = new AlertDialog.Builder(InProgressActivity.this)
                                        .setTitle("Data inválida")
                                        .setMessage("A data sugerida precisa ser após o prazo de votação")
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                                alertDialog.show();
                                
                            }

                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(InProgressActivity.this, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.show();
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

    public void marcar(Data d){
        final String login = Config.getLogin(InProgressActivity.this);
        final String password = Config.getPassword(InProgressActivity.this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //Criando a requisicao com o servidor
                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "marcar_evento.php", "POST", "UTF-8");

                //Adicionando os parametros do produto na requisicao
                httpRequest.addParam("idevento", idevento);
                httpRequest.addParam("iddata", d.id);
                httpRequest.setBasicAuth(login, password);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);
                    //Finalizando a requisicao
                    httpRequest.finish();

                    //Criando um JSONObject do resultado da requisicao
                    JSONObject jsonObject = new JSONObject(result);
                    //Obtendo o valor referente a chave success
                    int success = jsonObject.getInt("success");
                    //Execuntando o codigo na thread da interface
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(success == 1){
                                Toast.makeText(InProgressActivity.this, "Evento Marcado!",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                try {
                                    final String error = jsonObject.getString("error");
                                    Toast.makeText(InProgressActivity.this, error,Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sugerir(Calendar c){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String data = sdf.format(c.getTime());
        InProgressViewModel vm = new ViewModelProvider(this, new InProgressViewModel.InProgressViewModelFactory(this,this.idevento)).get(InProgressViewModel.class);

        final String login = Config.getLogin(InProgressActivity.this);
        final String password = Config.getPassword(InProgressActivity.this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "suggest.php", "POST", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("data", data);
                httpRequest.addParam("idevento", idevento);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();


                    JSONObject jsonObject = new JSONObject(result);

                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InProgressActivity.this, "Data sugerida com sucesso", Toast.LENGTH_LONG).show();
                                vm.refreshEvent();
                            }

                        });

                    }
                    else {
                        final String error = jsonObject.getString("error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InProgressActivity.this, error, Toast.LENGTH_LONG).show();
                            }

                        });
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}