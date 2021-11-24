package com.example.vamomarcarintegrado;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventActivity extends AppCompatActivity {
    static int PHOTO_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = findViewById(R.id.tbCriarEvento);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        AddEventViewModel vm = new ViewModelProvider(this).get(AddEventViewModel.class);
        Uri currentPath = vm.getCurrentPhotoPath();
        ImageView imvPhoto = findViewById(R.id.imvPhoto);

        if(currentPath != null){
            imvPhoto.setImageURI(currentPath);
        }

        EditText etDateVote = findViewById(R.id.etDateVote);
        etDateVote.addTextChangedListener(Util.mask(etDateVote, "##/##/####"));

        EditText etDateSuggest = findViewById(R.id.etDateSuggest);
        etDateSuggest.addTextChangedListener(Util.mask(etDateSuggest, "##/##/####"));


        imvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.setType("image/*");
                startActivityForResult(i, PHOTO_PICKER_REQUEST);
            }
        });


        ImageButton imbSearch = findViewById(R.id.imbSearch);
        imbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                EditText etNameSearch = findViewById(R.id.etNameSearch);
                LiveData<List<User>> users = vm.getUsers(etNameSearch.getText().toString(), AddEventActivity.this);
                users.observe(AddEventActivity.this, new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        AddEventAdapter addEventAdapter = new AddEventAdapter(users, AddEventActivity.this);
                        RecyclerView rvAddEvent = findViewById(R.id.rvAddEvent);
                        rvAddEvent.setAdapter(addEventAdapter);
                        rvAddEvent.setLayoutManager(new LinearLayoutManager(AddEventActivity.this));
                    }
                });

            }
        });

        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                EditText etNameAdd = findViewById(R.id.etNameAdd);
                String name = etNameAdd.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(AddEventActivity.this, "O campo nome não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etDescription = findViewById(R.id.etDescription);
                String description = etDescription.getText().toString();
                if(description.isEmpty()){
                    Toast.makeText(AddEventActivity.this, "O campo descrição não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etLocal = findViewById(R.id.etLocal);
                String local = etLocal.getText().toString();
                if(local.isEmpty()){
                    Toast.makeText(AddEventActivity.this, "O campo local não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                Uri currentPhotoPath = vm.currentPhotoPath;
                if(currentPhotoPath == null){
                    Toast.makeText(AddEventActivity.this, "O campo foto  não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etDateVote = findViewById(R.id.etDateVote);
                String dateVote = etDateVote.getText().toString();

                if(dateVote.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Campo prazo de votação não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                if(!Validator.validatedate(dateVote)){
                    Toast.makeText(AddEventActivity.this, "Data de votação inválida", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etDateSuggest = findViewById(R.id.etDateSuggest);
                String dateSuggest = etDateSuggest.getText().toString();

                if(dateSuggest.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Campo prazo de sugestão não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                if(!Validator.validatedate(dateSuggest)){
                    Toast.makeText(AddEventActivity.this, "Data de sugestão inválida", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }
                if(vm.ids.size() == 0){
                    Toast.makeText(AddEventActivity.this, "Adicione pessoas", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String dtVote = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(dateVote));
                    String dtSuggest = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(dateSuggest));
                    String ids = TextUtils.join(",", vm.ids);

                    final String login = Config.getLogin(AddEventActivity.this);
                    final String password = Config.getPassword(AddEventActivity.this);

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    //Executando a thread
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            //Criando a requisicao com o servidor
                            HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "create_event.php", "POST", "UTF-8");

                            //Adicionando os parametros do produto na requisicao
                            httpRequest.addParam("nome", name);
                            httpRequest.addParam("local", local);
                            httpRequest.addParam("descricao", description);
                            httpRequest.addParam("prazov", dtVote);
                            httpRequest.addParam("prazos", dtSuggest);
                            httpRequest.addParam("ids", ids);
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
                                            Toast.makeText(AddEventActivity.this, "Evento adicionado com sucesso",Toast.LENGTH_LONG).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(AddEventActivity.this, "Evento não foi adicionado com sucesso",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Uri photoPath = data.getData();
                ImageView imvPhoto = findViewById(R.id.imvPhoto);
                imvPhoto.setImageURI(photoPath);
                File f = new File(photoPath.toString());
                Log.i("arquivo de imagem", f.getAbsolutePath());
                AddEventViewModel vm = new ViewModelProvider(this).get(AddEventViewModel.class);
                vm.setCurrentPhotoPath(photoPath);
            }
        }
    }
}