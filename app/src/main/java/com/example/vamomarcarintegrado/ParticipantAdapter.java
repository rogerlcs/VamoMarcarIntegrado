package com.example.vamomarcarintegrado;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantAdapter extends RecyclerView.Adapter {
    List<User> users;
    Context context;
    Event evento;

    public ParticipantAdapter(List<User> users, Context context, Event evento) {
        this.users = users;
        this.context = context;
        this.evento = evento;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.add_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);

        TextView tvNameAdd = holder.itemView.findViewById(R.id.tvNameAdd);
        tvNameAdd.setText(user.nome);


        ImageView imvPhotoAdd = holder.itemView.findViewById(R.id.imvPhotoAdd);
        if(user.img != null){
            imvPhotoAdd.setImageBitmap(user.img);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deseja tornar esse usuário um administrador?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String login = Config.getLogin(context);
                        final String password = Config.getPassword(context);

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "set_admin.php", "POST", "UTF-8");
                                httpRequest.addParam("idevento", evento.id);
                                httpRequest.addParam("idparticipante", user.id);
                                httpRequest.setBasicAuth(login, password);

                                try {
                                    InputStream is = httpRequest.execute();
                                    String result = Util.inputStream2String(is, "UTF-8");
                                    Log.i("Resultado:", result);
                                    httpRequest.finish();

                                    JSONObject jsonObject = new JSONObject(result);
                                    final int success = jsonObject.getInt("success");
                                    if(success == 1) {
                                        ((InProgressActivity)context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, user.nome + " agora é um administrador", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else {
                                        final String error = jsonObject.getString("error");
                                        ((InProgressActivity)context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                if(evento.admin == 1 && !context.getClass().getSimpleName().equalsIgnoreCase("EventActivity")){
                    alertDialog.show();
                }
                return true;
            }
        });

        CheckBox ckbUser = holder.itemView.findViewById(R.id.ckbUser);
        ckbUser.setVisibility(View.INVISIBLE);
        ckbUser.setClickable(false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,PerfilActivity.class);
                i.putExtra("id", user.id);
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
