package com.example.vamomarcarintegrado;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Data {
    String id;
    Calendar data;
    int votes;
    int votei;

    public Data(String id, Calendar data, int votes, int votei) {
        this.id = id;
        this.data = data;
        this.votes = votes;
        this.votei = votei;
    }

    public Data(String id, Calendar data, int votes) {
        this.id = id;
        this.data = data;
        this.votes = votes;
    }

    public void Vote(Context context, String idevento){
        Log.i("chamei a funcao", "vote");
        final String login = Config.getLogin(context);
        final String password = Config.getPassword(context);
        final String idusuario = Config.getId(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Executando a thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "vote.php", "POST", "UTF-8");
                httpRequest.setBasicAuth(login, password);
                httpRequest.addParam("idusuario", idusuario);
                httpRequest.addParam("idevento", idevento);
                httpRequest.addParam("iddata",id);



                try {
                    InputStream is = httpRequest.execute();
                    String result = Util.inputStream2String(is,"UTF-8");
                    Log.d("HTTP_REQUEST_RESULT", result);

                    httpRequest.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                InProgressViewModel vm = new ViewModelProvider((ViewModelStoreOwner) context, new InProgressViewModel.InProgressViewModelFactory(context, idevento)).get(InProgressViewModel.class);
                vm.refreshEvent();
            }
        });
    }

}
