package com.example.kanver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class KullaniciAdapter extends RecyclerView.Adapter<KullaniciAdapter.MyViewHolder> {

    ArrayList<Kullanici> mList;
    Context context;
    boolean durum;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public KullaniciAdapter(Context context, ArrayList<Kullanici> mList, Boolean durum){
        this.context = context;
        this.mList = mList;
        this.durum = durum;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.kullanici_item, parent, false) ;
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Kullanici kullanici = mList.get(position);
        holder.kullaniciAdi.setText("Ad: "+ kullanici.getAd());
        holder.kanGrubu.setText("Aranan kan grubu: "+ kullanici.getKanGrubu());
        holder.sehir.setText("Şehir: " + kullanici.getSehir());

        if(kullanici.getKanDurumu().equals("Acil")){
            holder.kanDurumu.setVisibility(View.VISIBLE);
        }

        if(kullanici.kanGrubu.equals("")){
            holder.kanGrubu.setText("Kan vermek istiyorum..");
        }

        if(durum){
            if(kullanici.getDurum().equals("online")){
                holder.online.setVisibility(View.VISIBLE);
                holder.offline.setVisibility(View.GONE);
            }
            else{
                holder.online.setVisibility(View.GONE);
                holder.offline.setVisibility(View.VISIBLE);
            }
        }

        holder.mesajGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId",kullanici.getId());
                intent.putExtra("userName", kullanici.getAd());
                context.startActivity(intent);
            }
        });

        if(!kullanici.getId().equals(auth.getCurrentUser().getUid())){
            holder.mesajGonder.setVisibility(View.VISIBLE);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
        else{
            holder.mesajGonder.setVisibility(View.GONE);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context).setTitle("Kan Bilgisini Sil..").
                            setMessage("Oluşturduğunuz kan ihtiyaç bilgisi silinsin mi?").
                            setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    HashMap<String, Object> hashMap = new HashMap();
                                    hashMap.put("kanGrubu", "");
                                    hashMap.put("kanDurumu", "");

                                    database.getReference("Users").child(auth.getCurrentUser().getUid()).updateChildren(hashMap).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView kullaniciAdi, kanGrubu, sehir, kanDurumu;
        CircleImageView online, offline;
        ImageView mesajGonder;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            kullaniciAdi = itemView.findViewById(R.id.kullaniciAdii);
            kanGrubu = itemView.findViewById(R.id.kanGrubuu);
            sehir = itemView.findViewById(R.id.adresii);
            mesajGonder = itemView.findViewById(R.id.kanMesajGonderimi);
            online = itemView.findViewById(R.id.online);
            offline = itemView.findViewById(R.id.ofline);
            kanDurumu = itemView.findViewById(R.id.acil);
        }
    }
}
