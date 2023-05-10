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
import android.widget.Toast;

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
import java.util.Locale;

public class KanIhtiyacListesi extends AppCompatActivity {

    private ImageButton geriTusu;
    private EditText textArama;
    private ImageView aramaButon;

    private RecyclerView recyclerView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private KullaniciAdapter adapter;
    private ArrayList<Kullanici> list;

    public void init(){
        textArama = findViewById(R.id.textArama);
        aramaButon=findViewById(R.id.aramaButon);
        geriTusu = findViewById(R.id.geriTusuu);

        recyclerView = findViewById(R.id.recyclerViewKan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new KullaniciAdapter(this, list, true);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kan_ihtiyac_listesi);

        init();

        KullaniciListele();

        AramaYap();

        geriDon();

    }

    private void KullaniciListele(){
        db.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(textArama.getText().toString().equals("")){
                    list.clear();

                    for(DataSnapshot snapshots : snapshot.getChildren()){
                        Kullanici kullanici = snapshots.getValue(Kullanici.class);
                        if(!kullanici.getKanGrubu().isEmpty()){
                            if(kullanici.getId().equals(currentUser.getUid())){
                                list.add(kullanici);
                            }
                        }
                    }

                    for(DataSnapshot snapshots : snapshot.getChildren()){
                        Kullanici kullanici = snapshots.getValue(Kullanici.class);
                        if(!kullanici.getKanGrubu().isEmpty()){
                            if(!kullanici.getId().equals(currentUser.getUid())){
                                list.add(kullanici);
                            }
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
        Query sorgu = db.getReference().child("Users").orderByChild("sehir").startAt(s).endAt(s+"\uf8ff");
        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot snapshots : snapshot.getChildren()){
                    Kullanici kullanici = snapshots.getValue(Kullanici.class);
                    if(kullanici.getId().equals(currentUser.getUid())){
                        if(!kullanici.getKanGrubu().isEmpty()){
                            list.add(kullanici);
                        }
                    }
                }

                for(DataSnapshot snapshots : snapshot.getChildren()){
                    Kullanici kullanici = snapshots.getValue(Kullanici.class);
                    if(!kullanici.getId().equals(currentUser.getUid())){
                        if(!kullanici.getKanGrubu().isEmpty()){
                            list.add(kullanici);
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
                Intent intent = new Intent(KanIhtiyacListesi.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void online(final String durum){
        DatabaseReference dbr = db.getReference().child("Users").child(currentUser.getUid());
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