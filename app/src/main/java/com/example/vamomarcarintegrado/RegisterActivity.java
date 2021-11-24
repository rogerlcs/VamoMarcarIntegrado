package com.example.vamomarcarintegrado;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etBirthDate =  findViewById(R.id.etBirthDate);
        etBirthDate.addTextChangedListener(Util.mask(etBirthDate, "##/##/####"));

        Button btnRegister =  findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNewLogin =  findViewById(R.id.etNewLogin);
                final String newLogin = etNewLogin.getText().toString();

                if(newLogin.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Campo de login não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!Validator.validateemail(newLogin)){
                    Toast.makeText(RegisterActivity.this, "Email inválido", Toast.LENGTH_LONG).show();
                    return;
                }


                EditText etNewPassword =  findViewById(R.id.etNewPassword);
                final String newPassword = etNewPassword.getText().toString();
                if(newPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Campo de senha não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText etName =  findViewById(R.id.etName);
                final String name = etName.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Campo de nome não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }



                String birthDate = etBirthDate.getText().toString();

                if(birthDate.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Campo de data de nascimento não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!Validator.validatedate(birthDate)){
                    Toast.makeText(RegisterActivity.this, "Data inválida", Toast.LENGTH_LONG).show();
                    return;
                }


                EditText etNewPasswordCheck =  findViewById(R.id.etNewPasswordCheck);
                String newPasswordCheck = etNewPasswordCheck.getText().toString();
                if(newPasswordCheck.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Campo de checagem de senha não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!newPassword.equals(newPasswordCheck)) {
                    Toast.makeText(RegisterActivity.this, "Senha não confere", Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String dtBirth = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(birthDate));
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "register.php", "POST", "UTF-8");
                            httpRequest.addParam("newLogin", newLogin);
                            httpRequest.addParam("newPassword", newPassword);
                            httpRequest.addParam("name", name);
                            httpRequest.addParam("birthDate", dtBirth);

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
                                            Toast.makeText(RegisterActivity.this, "Novo usuario registrado com sucesso", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    });
                                }
                                else {
                                    final String error = jsonObject.getString("error");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
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



}