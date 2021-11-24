package com.example.vamomarcarintegrado;

import android.content.Context;
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

public class InProgressViewModel extends ViewModel {
    MutableLiveData<Event> event;
    Context context;
    String id;

    public InProgressViewModel(Context context, String id) {
        this.context = context;
        this.id = id;
    }


    public LiveData<Event> getEvent(){
        if (event == null){
            event = new MutableLiveData<Event>();
            loadEvent();
        }
        return event;
    }
    public void refreshEvent(){
        loadEvent();
    }

    private void loadEvent() {
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        final String idusuario = Config.getId(context);
        Log.i("id do evento", id);
        Log.i("id do usuario", idusuario);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                List<Data> dataLists = new ArrayList<>();
                List<User> userList = new ArrayList<>();


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_event_details.php", "GET", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("idusuario", idusuario);
                httpRequest.addParam("idevento", id);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();


                    JSONObject jsonObject = new JSONObject(result);

                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        JSONArray jsonArray = jsonObject.getJSONArray("event");
                        JSONObject jEvent = jsonArray.getJSONObject(0);
                        String idevent = jEvent.getString("id");
                        String name = jEvent.getString("nome");
                        String description = jEvent.getString("descricao");
                        int status_event = jEvent.getInt("status_evento");
                        String address = jEvent.getString("endereco");
                        String finalDate = jEvent.getString("data_marcada");
                        String localname = jEvent.getString("nome_local");
                        String dateS = jEvent.getString("prazo_sugestao");
                        String dateV= jEvent.getString("prazo_votacao");

                        JSONArray jsonArrayDatas =jEvent.getJSONArray("datas");

                        for (int i = 0; i < jsonArrayDatas.length(); i++){
                            JSONObject jData = jsonArrayDatas.getJSONObject(i);
                            String codigo = jData.getString("codigo");
                            String date = jData.getString("data");
                            int votos = jData.getInt("votos");
                            int votei = jData.getInt("votei");

                            Log.i("Votei", String.valueOf(votei));


                            Timestamp tdate = Timestamp.valueOf(date);
                            Calendar calendarD =  Calendar.getInstance();
                            calendarD.setTime(tdate);
                            Data data = new Data(codigo, calendarD, votos, votei);



                            dataLists.add(data);
                        }

                        JSONArray jsonArrayUsers =jEvent.getJSONArray("usuarios");

                        for (int i = 0; i < jsonArrayUsers.length(); i++){
                            JSONObject jUser = jsonArrayUsers.getJSONObject(i);
                            String codigo = jUser.getString("codigo");
                            String nome = jUser.getString("nome");

                            User user = new User(codigo, nome);

                            userList.add(user);
                        }


                        Timestamp tsS = Timestamp.valueOf(dateS);
                        Timestamp tsV = Timestamp.valueOf(dateV);

                        Calendar calendarS =  Calendar.getInstance();
                        calendarS.setTime(tsS);

                        Calendar calendarV = Calendar.getInstance();
                        calendarV.setTime(tsV);



                        Event event1 = new Event(idevent, name, description, calendarV, calendarS, finalDate, address, localname, dataLists, userList, status_event);
                        Log.d("HTTP_REQUEST_RESULT", event1.id);

                        event.postValue(event1);
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    static public class InProgressViewModelFactory implements ViewModelProvider.Factory {

        Context context;
        String id;

        public InProgressViewModelFactory(Context context, String id) {
            this.context = context;
            this.id = id;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            return (T) new InProgressViewModel(context, id);
        }
    }

}
