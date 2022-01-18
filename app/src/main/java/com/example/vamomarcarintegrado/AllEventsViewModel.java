package com.example.vamomarcarintegrado;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllEventsViewModel extends ViewModel {
    MutableLiveData<List<Event>> events;
    Context context;

    public AllEventsViewModel(Context context) {
        this.context = context;
    }

    public LiveData<List<Event>> getEvents(){
        if (events == null){
            Log.i("id do usuario", "cheguei aqui");
            events = new MutableLiveData<>();
            loadEvents();
        }
        return events;
    }

    public void refreshEvents(){
        loadEvents();
    }

    public void loadEvents() {
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        final String id = Config.getId(context);
        Log.i("id do usuario", id);
        Log.i("id do usuario", login);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                List<Event> eventLists = new ArrayList<>();


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_event.php", "GET", "UTF-8");
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
                        JSONArray jsonArray = jsonObject.getJSONArray("events");

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jEvent = jsonArray.getJSONObject(i);

                            String id = jEvent.getString("id");
                            String name = jEvent.getString("nome");
                            String description = jEvent.getString("descricao");
                            int status_event = jEvent.getInt("status_evento");
                            int status_invite = jEvent.getInt("status_convite");
                            String address = jEvent.getString("endereco");
                            String finalDate = jEvent.getString("data_marcada");
                            String localname = jEvent.getString("nome_local");
                            String dateS = jEvent.getString("prazo_sugestao");
                            String dateV= jEvent.getString("prazo_votacao");
                            int total = jEvent.getInt("total");

                            String imgBase64 = jEvent.getString("img");
                            String pureBase64Encoded = imgBase64.substring(imgBase64.indexOf(",") + 1);
                            Bitmap img = Util.base642Bitmap(pureBase64Encoded);

                            Timestamp tsS = Timestamp.valueOf(dateS);
                            Timestamp tsV = Timestamp.valueOf(dateV);

                            Calendar calendarS =  Calendar.getInstance();
                            calendarS.setTime(tsS);

                            Calendar calendarV = Calendar.getInstance();
                            calendarV.setTime(tsV);


                            Event event = new Event(id, name, description, calendarV, calendarS, finalDate, address, localname, status_event, status_invite, total, img);
                            eventLists.add(event);


                        }

                        events.postValue(eventLists);
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    static public class AllEventsViewModelFactory implements ViewModelProvider.Factory {

        Context context;

        public AllEventsViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            return (T) new AllEventsViewModel(context);
        }
    }

}
