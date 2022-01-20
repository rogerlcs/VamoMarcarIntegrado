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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditPerfilViewModel extends ViewModel {
    String id;
    String currentPhotoPath = "";

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    MutableLiveData<ArrayList<String>> estados;
    Context context;
    MutableLiveData<User> user;

    public EditPerfilViewModel(String id, Context context) {
        this.id = id;
        this.context = context;
    }

    public LiveData<ArrayList<String>> getEstados(){
        if(estados == null){
            estados = new MutableLiveData<ArrayList<String>>();
            loadEstados();
        }
        return estados;
    }

    public LiveData<User> getUser(){
        if(this.user == null){
            user = new MutableLiveData<User>();
            loadUser();
        }
        return user;
    }

    public void loadEstados(){
        ArrayList<String> arrayList = new ArrayList<>();
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_estados.php", "GET", "UTF-8");
                httpRequest.setBasicAuth(login, password);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();


                    JSONObject jsonObject = new JSONObject(result);

                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        JSONArray jsonArray = jsonObject.getJSONArray("estados");
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jEstado = jsonArray.getJSONObject(i);
                            String nome = jEstado.getString("nome");

                            arrayList.add(nome);
                        }
                        estados.postValue(arrayList);
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void loadUser(){
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        Log.i("id do usuario", id);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {


                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_user_info.php", "GET", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("idusuario", id);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();


                    JSONObject jsonObject = new JSONObject(result);

                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        JSONArray jsonArray = jsonObject.getJSONArray("user");
                        JSONObject jUSer = jsonArray.getJSONObject(0);
                        String id = jUSer.getString("codigo");
                        String name = jUSer.getString("nome");
                        String bio = jUSer.getString("bio");
                        String dtNasc = jUSer.getString("data_nascimento");
                        String estado = jUSer.getString("estado");
                        String imgBase64 = jUSer.getString("img");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(sdf.parse(dtNasc));

                        if(imgBase64.equalsIgnoreCase("null")){
                            User user1 = new User(id, name, bio, calendar, estado);
                            user.postValue(user1);
                        }
                        else {
                            String pureBase64Encoded = imgBase64.substring(imgBase64.indexOf(",") + 1);
                            Bitmap img = Util.base642Bitmap(pureBase64Encoded);
                            User user1 = new User(id, name, bio, calendar, estado, img);
                            user.postValue(user1);
                        }
                    }

                } catch (IOException | JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    static public class EditPerfilViewModelFactory implements ViewModelProvider.Factory {

        Context context;
        String id;

        public EditPerfilViewModelFactory(String id, Context context) {
            this.context = context;
            this.id = id;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            return (T) new EditPerfilViewModel(id, context);
        }
    }
}
