package com.example.kanver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private ImageButton geriTusu;
    private CircleImageView profilResim;
    private TextView kullaniciAdi;
    private EditText mesajGirdi, adresBilgisi;
    private ImageView ekleAdres, gonder, konum;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> mMesaj = new ArrayList<>();
    private StringBuilder saat;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private void init(){
        geriTusu = findViewById(R.id.geriTusu);
        profilResim = findViewById(R.id.profilResim);
        kullaniciAdi = findViewById(R.id.kullaniciAdMesaj);
        mesajGirdi = findViewById(R.id.mesajGirisAlani);
        ekleAdres = findViewById(R.id.fotoEkle);
        gonder = findViewById(R.id.gonderBtn);
        adresBilgisi = findViewById(R.id.adresBilgisi);
        konum = findViewById(R.id.konum);

        recyclerView = findViewById(R.id.recyclerMesaj);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        saat = new StringBuilder();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();

        Date saatZaman = Calendar.getInstance().getTime();
        SimpleDateFormat saatFormat = new SimpleDateFormat("hh:mm");
        String saatt = saatFormat.format(saatZaman);
        saat.append(saatt);

        kullaniciBilgisi();

        geriDon();

        ChatListVeritabaninaKayit();

        AdresEkle();

        konumBilgisi();
    }

    private void konumBilgisi(){
        konum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location!=null){
                                Geocoder geocoder = new Geocoder(MessageActivity.this, Locale.getDefault());
                                try{
                                    List<Address> adresList = geocoder.getFromLocation(location.getLatitude(),
                                            location.getLongitude(), 1);
                                    mesajGirdi.setText(adresList.get(0).getLatitude() + " " +
                                            adresList.get(0).getLongitude() + " " +
                                            adresList.get(0).getAddressLine(0) + " " +
                                            adresList.get(0).getLocality() + " " +
                                            adresList.get(0).getCountryName());
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(MessageActivity.this, "Konum bulunamadı..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                else
                    ActivityCompat.requestPermissions(MessageActivity.this, new String []{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void AdresEkle(){
        ekleAdres.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                database.getReference("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Kullanici kullanici = snapshot.getValue(Kullanici.class);

                        if(!kullanici.getAdres().isEmpty()){
                            if(event.getAction()==MotionEvent.ACTION_DOWN){
                                adresBilgisi.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adresBilgisi.setVisibility(View.GONE);
                                    }
                                }, 2000);
                            }

                            if(event.getAction()==MotionEvent.ACTION_UP){
                                adresBilgisi.setVisibility(View.GONE);
                            }
                        }
                        else{
                            adresBilgisi.setText("Lütfen ayarlar bölümünden adres bilgisi ekleyin");
                            if(event.getAction()==MotionEvent.ACTION_DOWN){
                                adresBilgisi.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adresBilgisi.setVisibility(View.GONE);
                                    }
                                }, 2000);
                            }
                            if(event.getAction()==MotionEvent.ACTION_UP){
                                adresBilgisi.setVisibility(View.GONE);
                            }
                        }
                        mesajGirdi.setText(kullanici.getAdres());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }

        });
    }

    private void ChatListVeritabaninaKayit(){
        String receiveId = getIntent().getStringExtra("userId");

        DatabaseReference ref = database.getReference("ChatList");

        ref.child(auth.getCurrentUser().getUid()).child(receiveId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    ref.child(auth.getCurrentUser().getUid()).child(receiveId).child("id").setValue(receiveId);
                    ref.child(receiveId).child(auth.getCurrentUser().getUid()).child("id").setValue(auth.getCurrentUser().getUid());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void kullaniciBilgisi() {
        final String senderId = auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");

        chatAdapter = new ChatAdapter(this, mMesaj, receiveId);
        recyclerView.setAdapter(chatAdapter);

        kullaniciAdi.setText(userName);

        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId + senderId;

        database.getReference().child("Mesajlar").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMesaj.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    chat.setMesajId(snapshot1.getKey());

                    mMesaj.add(chat);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesajGirdisi = mesajGirdi.getText().toString();
                final Chat chat = new Chat(senderId, mesajGirdisi, saat.toString());

                mesajGirdi.setText("");

                database.getReference().child("Mesajlar").child(senderRoom).push()
                        .setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("Mesajlar").child(receiverRoom).push().
                                        setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });

                            }
                        });
            }
        });
    }

    private void geriDon(){
        geriTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MessageActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
    }

}