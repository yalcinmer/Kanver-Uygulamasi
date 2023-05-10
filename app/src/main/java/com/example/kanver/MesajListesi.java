package com.example.kanver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MesajListesi extends AppCompatActivity {

    private ImageButton geriTusu;
    private EditText textArama;
    private ImageView aramaButon;

    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private KullaniciAdapter adapter;
    private ArrayList<Kullanici> mUsers;
    private ArrayList<ChatList> usersList;


    public void init(){
        textArama = findViewById(R.id.textAramaMesajListesi);
        aramaButon=findViewById(R.id.aramaButonMesajListesi);
        geriTusu = findViewById(R.id.geriTusuMesajListesi);

        recyclerView = findViewById(R.id.recyclerMesajListesi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersList = new ArrayList<>();

        mUsers = new ArrayList<>();
        adapter = new KullaniciAdapter(this, mUsers, true);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_listesi);

        init();

        Liste();

        AramaYap();

        geriDon();
    }

    private void Liste(){
        if (textArama.getText().toString().equals("")) {

            database.getReference().child("ChatList").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ChatList chatlist = snapshot1.getValue(ChatList.class);
                        usersList.add(chatlist);
                    }
                    chatList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void chatList(){
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(textArama.getText().toString().equals("")){
                    mUsers.clear();

                    for(DataSnapshot snapshots : snapshot.getChildren()){
                        Kullanici kullanici = snapshots.getValue(Kullanici.class);

                       for(ChatList chatlist : usersList){

                           if(kullanici.getId().equals(chatlist.getId())){
                               mUsers.add(kullanici);
                           }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void AramaYap(){
        textArama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                Arama(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void Arama(String s){
        Query sorgu = database.getReference().child("Users").orderByChild("ad").startAt(s).endAt(s+"\uf8ff");
        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUsers.clear();

                for(DataSnapshot snapshots : snapshot.getChildren()){
                    Kullanici kullanici = snapshots.getValue(Kullanici.class);

                    for(ChatList chatlist : usersList){

                        if(kullanici.getId().equals(chatlist.getId())){
                            mUsers.add(kullanici);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void geriDon(){
        geriTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MesajListesi.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void online(final String durum){
        DatabaseReference dbr = database.getReference("Users").child(currentUser.getUid());
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("durum", durum);

        dbr.updateChildren(hashMap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        online("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        online("online");
    }
}