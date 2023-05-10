package com.example.kanver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter {

    int Mesaj_sag=1;
    int Mesaj_sol=2;

    private Context mcontext;
    private ArrayList<Chat> mchat;
    private String recId;


    public ChatAdapter(Context mcontext, ArrayList<Chat> mchat){
        this.mcontext=mcontext;
        this.mchat=mchat;
    }

    public ChatAdapter(Context mcontext, ArrayList<Chat> mchat, String recId){
        this.mcontext=mcontext;
        this.mchat=mchat;
        this.recId=recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Mesaj_sag){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.sag, parent, false);
            return new SenderViewHolder(view);
        }
        else{
            View view =LayoutInflater.from(mcontext).inflate(R.layout.sol, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = mchat.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(mcontext).setTitle("Mesajı Sil").
                        setMessage("Mesajı silmek istediğinizden emin misiniz?").
                        setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;

                                database.getReference().child("Mesajlar").child(senderRoom).child(chat.getMesajId()).setValue(null);
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


        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMesaj.setText(chat.getMesaj());
            ((SenderViewHolder)holder).senderSaat.setText(chat.getTime());
        }
        else{
            ((ReceiverViewHolder)holder).receiverMesaj.setText(chat.getMesaj());
            ((ReceiverViewHolder)holder).receiverSaat.setText(chat.getTime());
        }

    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMesaj, receiverSaat;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMesaj = itemView.findViewById(R.id.mesajAl);
            receiverSaat = itemView.findViewById(R.id.tarihAl);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMesaj, senderSaat;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMesaj = itemView.findViewById(R.id.mesajGon);
            senderSaat = itemView.findViewById(R.id.tarihGon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mchat.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return Mesaj_sag;
        }
        else{
            return Mesaj_sol;
        }

    }
}