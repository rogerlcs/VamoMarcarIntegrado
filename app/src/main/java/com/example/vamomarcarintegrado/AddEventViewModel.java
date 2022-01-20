package com.example.vamomarcarintegrado;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventViewModel extends ViewModel {
    String currentPhotoPath = "";
    MutableLiveData<List<User>> users;
    List<String> ids;

    public LiveData<List<User>> getUsers(String nome, Context context) {

        if(users == null){
            users = new MutableLiveData<List<User>>();
        }
        if(ids == null){
            ids = new ArrayList<>();
        }
        List<User> users1 = new ArrayList<>();
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        final String idusuario = Config.getId(context);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "get_users.php", "GET", "UTF-8");
                httpRequest.addParam("nome", nome);
                httpRequest.setBasicAuth(login, password);

                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is, "UTF-8");
                    Log.i("usuarios:", result);
                    httpRequest.finish();

                    JSONObject jsonObject = new JSONObject(result);
                    final int success = jsonObject.getInt("success");
                    if(success == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("users");
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jUser = jsonArray.getJSONObject(i);
                            String id = jUser.getString("id");
                            String name = jUser.getString("nome");
                            String imgBase64 = jUser.getString("img");

                            User user;

                            if(imgBase64.equalsIgnoreCase("null")){
                                user = new User(id, name);
                            }
                            else {
                                String pureBase64Encoded = imgBase64.substring(imgBase64.indexOf(",") + 1);
                                Bitmap img = Util.base642Bitmap(pureBase64Encoded);
                                user = new User(id, name, img);
                            }

                            if(!idusuario.equalsIgnoreCase(id)){
                                users1.add(user);
                            }
                        }
                        Log.i("usuarios:", String.valueOf(users1.size()));
                        users.postValue(users1);
                    }
                    users.postValue(users1);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return users;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }
}
