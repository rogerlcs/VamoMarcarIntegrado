package com.example.vamomarcarintegrado;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditPerfilActivity extends AppCompatActivity {
    static int PHOTO_PICKER_REQUEST = 1;
    String iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        Intent i = getIntent();
        String id = i.getStringExtra("id");
        iduser = id;

        EditPerfilViewModel vm = new ViewModelProvider(this, new EditPerfilViewModel.EditPerfilViewModelFactory(id,this)).get(EditPerfilViewModel.class);
        LiveData<User> userLiveData = vm.getUser();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                EditText tvNomeEdit = findViewById(R.id.tvNomeEdit);
                tvNomeEdit.setText(user.nome);

                EditText tvBioEdit = findViewById(R.id.tvBioEdit);
                if(user.bio.equalsIgnoreCase("null")){
                    tvBioEdit.setText("");
                }
                else tvBioEdit.setText(user.bio);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                EditText tvDtNascEdit = findViewById(R.id.tvDtNascEdit);
                tvDtNascEdit.setText(sdf.format(user.dtNasc.getTime()));
                tvDtNascEdit.addTextChangedListener(Util.mask(tvDtNascEdit, "##/##/####"));

                ImageView imvPhotoEdit = findViewById(R.id.imvPhotoEdit);
                String currentPath = vm.getCurrentPhotoPath();

                if(currentPath != ""){
                    Bitmap bitmap = Util.getBitmap(currentPath, imvPhotoEdit.getMaxWidth(), imvPhotoEdit.getMaxHeight());
                    imvPhotoEdit.setImageBitmap(bitmap);
                }
                else if(user.img != null){
                    imvPhotoEdit.setImageBitmap(user.img);
                }
                imvPhotoEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        i.setType("image/*");
                        startActivityForResult(i, PHOTO_PICKER_REQUEST);
                    }
                });

                LiveData<ArrayList<String>> estadosLiveData = vm.getEstados();
                estadosLiveData.observe(EditPerfilActivity.this, new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> strings) {
                        Spinner sEstado = findViewById(R.id.sEstado);
                        ArrayAdapter<String> estadosAdapter = new ArrayAdapter<String>(EditPerfilActivity.this, android.R.layout.simple_spinner_item, strings);
                        estadosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sEstado.setAdapter(estadosAdapter);

                        if(!user.estado.equalsIgnoreCase("null")){
                            sEstado.setSelection(estadosAdapter.getPosition(user.estado));
                        }

                        Button btnEditPerfil = findViewById(R.id.btnEditPerfil);
                        btnEditPerfil.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setEnabled(false);

                                EditText etNameAdd = findViewById(R.id.tvNomeEdit);
                                String name = etNameAdd.getText().toString();
                                if(name.isEmpty()){
                                    Toast.makeText(EditPerfilActivity.this, "O campo nome não foi preenchido", Toast.LENGTH_LONG).show();
                                    v.setEnabled(true);
                                    return;
                                }

                                String birthDate = tvDtNascEdit.getText().toString();

                                if(birthDate.isEmpty()) {
                                    Toast.makeText(EditPerfilActivity.this, "Campo de data de nascimento não preenchido", Toast.LENGTH_LONG).show();
                                    v.setEnabled(true);
                                    return;
                                }

                                if(!Validator.validatedate(birthDate)){
                                    Toast.makeText(EditPerfilActivity.this, "Data inválida", Toast.LENGTH_LONG).show();
                                    v.setEnabled(true);
                                    return;
                                }

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    String dtBirth = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(birthDate));
                                    final String login = Config.getLogin(EditPerfilActivity.this);
                                    final String password = Config.getPassword(EditPerfilActivity.this);
                                    int idestado = sEstado.getSelectedItemPosition();

                                    EditPerfilViewModel vm = new ViewModelProvider(EditPerfilActivity.this, new EditPerfilViewModel.EditPerfilViewModelFactory(id,EditPerfilActivity.this)).get(EditPerfilViewModel.class);
                                    String photoPath = vm.getCurrentPhotoPath();
                                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "update_user.php", "POST", "UTF-8");
                                            httpRequest.addParam("bio", tvBioEdit.getText().toString());
                                            httpRequest.addParam("name", name);
                                            httpRequest.addParam("birthDate", dtBirth);
                                            if(photoPath != ""){
                                                Log.i("Teste de imagem: ", photoPath);
                                                httpRequest.addFile("img",new File(photoPath));
                                            }
                                            Log.i("Id do estado: ", String.valueOf(idestado + 1));

                                            httpRequest.addParam("idestado",String.valueOf(idestado + 1));
                                            httpRequest.setBasicAuth(login, password);

                                            try {
                                                InputStream is = httpRequest.execute();
                                                String result = Util.inputStream2String(is, "UTF-8");
                                                Log.i("Resultado:", result);
                                                httpRequest.finish();

                                                JSONObject jsonObject = new JSONObject(result);
                                                final int success = jsonObject.getInt("success");
                                                if(success == 1) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(EditPerfilActivity.this, "Perfil editado com sucesso", Toast.LENGTH_LONG).show();
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        }
                                                    });
                                                }
                                                else {
                                                    final String error = jsonObject.getString("error");
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(EditPerfilActivity.this, error, Toast.LENGTH_LONG).show();
                                                            setResult(RESULT_CANCELED);
                                                            finish();
                                                        }
                                                    });
                                                }
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



                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Uri photoPath = data.getData();
                String currentPhotoPath = Util.getPathFromUri(this,photoPath);
                ImageView imvPhoto = findViewById(R.id.imvPhotoEdit);
                Bitmap bitmap = Util.getBitmap(currentPhotoPath, imvPhoto.getWidth(), imvPhoto.getHeight());
                imvPhoto.setImageBitmap(bitmap);
                File f = new File(currentPhotoPath);
                EditPerfilViewModel vm = new ViewModelProvider(this, new EditPerfilViewModel.EditPerfilViewModelFactory(iduser,this)).get(EditPerfilViewModel.class);
                vm.setCurrentPhotoPath(currentPhotoPath);
            }
        }
    }
}