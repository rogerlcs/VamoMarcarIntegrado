package com.example.vamomarcarintegrado;

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
import android.widget.TextView;

public class PerfilActivity extends AppCompatActivity {

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
}