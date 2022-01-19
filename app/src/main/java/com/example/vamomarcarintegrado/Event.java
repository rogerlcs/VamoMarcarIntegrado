package com.example.vamomarcarintegrado;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Event {
    String id;
    String name;
    int admin;
    String description;
    Calendar dateV;
    Calendar dateS;
    String finalDate;
    String local;
    String localName;
    List<Data> dates;
    List<User> users;
    int status_event;
    int total;
    int status_invite;
    Bitmap img;

    public Event(String id, String name, String description, Calendar dateV, Calendar dateS, String finalDate, String local, String localName, List<Data> dates, List<User> users, int status_event, Bitmap img) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateV = dateV;
        this.dateS = dateS;
        this.finalDate = finalDate;
        this.local = local;
        this.localName = localName;
        this.dates = dates;
        this.users = users;
        this.status_event = status_event;
        this.img = img;
    }

    public Event(String id, String name, String description, Calendar dateV, Calendar dateS, String finalDate, String local, String localName, List<Data> dates, List<User> users, int status_event, Bitmap img, int admin) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateV = dateV;
        this.dateS = dateS;
        this.finalDate = finalDate;
        this.local = local;
        this.localName = localName;
        this.dates = dates;
        this.users = users;
        this.status_event = status_event;
        this.img = img;
        this.admin = admin;
    }




    public Event(String id, String name, String description, Calendar dateV, Calendar dateS, String finalDate, String local, String localName, int status_event, int status_invite, int total, Bitmap img) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateV = dateV;
        this.dateS = dateS;
        this.finalDate = finalDate;
        this.local = local;
        this.localName = localName;
        this.status_event = status_event;
        this.status_invite = status_invite;
        this.total = total;
        this.img = img;
    }

    public void setStatusEvent(int status, Context context){
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "set_status_event.php", "POST", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("id", id);
                httpRequest.addParam("status", String.valueOf(status));

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                AllEventsViewModel vm = new ViewModelProvider((ViewModelStoreOwner) context, new AllEventsViewModel.AllEventsViewModelFactory(context)).get(AllEventsViewModel.class);
                vm.refreshEvents();

            }
        });
    }

    public void setStatusinvite(Context context, int status){
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        final String idusuario = Config.getId(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "set_status_invite.php", "POST", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("idusuario", idusuario);
                httpRequest.addParam("idevento", id);
                httpRequest.addParam("status", String.valueOf(status));

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                AllEventsViewModel vm = new ViewModelProvider((ViewModelStoreOwner) context, new AllEventsViewModel.AllEventsViewModelFactory(context)).get(AllEventsViewModel.class);
                vm.refreshEvents();
            }
        });
    }

    public String getData()  {
        Timestamp timestamp = Timestamp.valueOf(finalDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(calendar.getTime());

    }

    public String getHour()  {
       Timestamp timestamp = Timestamp.valueOf(finalDate);
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(timestamp);
       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
       return simpleDateFormat.format(calendar.getTime());
    }

    public void startTime(Context context, TextView tvDate){
        Calendar now = Calendar.getInstance();
        Log.i("Função", "chamei starttime");
        if(status_event == 0){
            Log.i("Função", "chamei starttime");
            long dateS = this.dateS.getTimeInMillis() - now.getTimeInMillis();
            CountDownTimer countDownTimer = new CountDownTimer(dateS,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hora = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    long min = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hora));
                    long sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hora) - TimeUnit.MINUTES.toMillis(min));
                    tvDate.setText("Sugestão: " + hora + ":" + min + ":" + sec);
                }

                @Override
                public void onFinish() {
                    setStatusEvent(1, context);
                }
            };
            countDownTimer.start();
        }
        else if(status_event == 1){
            long dateV = this.dateV.getTimeInMillis() - now.getTimeInMillis();
            CountDownTimer countDownTimer = new CountDownTimer(dateV,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hora = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    long min = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hora));
                    long sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hora) - TimeUnit.MINUTES.toMillis(min));
                    tvDate.setText("Votação: " + hora + ":" + min + ":" + sec);
                }

                @Override
                public void onFinish() {
                    tvDate.setText("Votação encerrada!");
                    setStatusEvent(2, context);

                }
            };
            countDownTimer.start();
        }
        else if(status_event == 2){
            tvDate.setText("Votação encerrada!");
        }
    }

    public String getParticipants(Context context) {
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_participants.php", "GET", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("id", id);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();


                    JSONObject jsonObject = new JSONObject(result);

                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        JSONArray jsonArray = jsonObject.getJSONArray("participantes");
                        JSONObject jtotal = jsonArray.getJSONObject(0);
                        total = jtotal.getInt("total");
                        }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return String.valueOf(total);
    }
}


