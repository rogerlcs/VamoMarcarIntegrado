package com.example.vamomarcarintegrado;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PerfilActivity extends AppCompatActivity {
    static int EDIT_PERFIL_RESULT = 1;
    String idperfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        Toolbar toolbar = findViewById(R.id.tbPerfil);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        String iduser = Config.getId(this);
        idperfil = id;



        PerfilViewModel vm = new ViewModelProvider(this, new PerfilViewModel.PerfilViewModelFactory(id,this)).get(PerfilViewModel.class);
        LiveData<User> userLiveData = vm.getUser();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                TextView tvNomePerfil = findViewById(R.id.tvNomePerfil);
                tvNomePerfil.setText(user.nome);

                TextView tvBioPerfil = findViewById(R.id.tvBioPerfil);
                tvBioPerfil.setText(user.bio);

                TextView tvDtNasc = findViewById(R.id.tvDtNasc);
                tvDtNasc.setText(user.getDay() + " de " + user.getMonthName() + " de " + user.getYear());

                if(user.img != null){
                    ImageView imvPerfil = findViewById(R.id.imvPerfil);
                    imvPerfil.setImageBitmap(user.img);
                }

                TextView tvLocalPerfil = findViewById(R.id.tvLocalPerfil);
                tvLocalPerfil.setText(user.estado);

                ImageButton imbEditPerfil = findViewById(R.id.imbEditPerfil);
                Button btnLogout = findViewById(R.id.btnLogout);

                Log.i("ids", id + "," + iduser);
                if (id.equalsIgnoreCase(iduser)){
                    Log.i("ids", "São iguais");
                    imbEditPerfil.setVisibility(View.VISIBLE);
                    imbEditPerfil.setClickable(true);
                    btnLogout.setVisibility(View.VISIBLE);
                    btnLogout.setClickable(true);

                }
                else{
                    Log.i("ids", "Não são iguais");
                    imbEditPerfil.setVisibility(View.INVISIBLE);
                    imbEditPerfil.setClickable(false);
                    btnLogout.setVisibility(View.INVISIBLE);
                    btnLogout.setClickable(false);
                }
                imbEditPerfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PerfilActivity.this, EditPerfilActivity.class);
                        intent.putExtra("id",user.id);
                        startActivityForResult(intent, EDIT_PERFIL_RESULT);
                    }
                });


                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.setLogin(PerfilActivity.this, "");
                        Config.setPassword(PerfilActivity.this, "");
                        Config.setId(PerfilActivity.this, "");
                        Intent i = new Intent(PerfilActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_PERFIL_RESULT){
            if(resultCode == RESULT_OK){
                PerfilViewModel vm = new ViewModelProvider(this, new PerfilViewModel.PerfilViewModelFactory(idperfil,this)).get(PerfilViewModel.class);
                vm.refreshUser();
            }
        }
    }
}