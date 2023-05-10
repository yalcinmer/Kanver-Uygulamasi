package com.example.kanver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AyarlarActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userAddress, userSehir;
    private ImageButton geriTusu;

    private String currentUserId;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private void Init(){
        updateAccountSettings = (Button) findViewById(R.id.text_update_settings_button);
        userName = (EditText) findViewById(R.id.text_user_name);
        userAddress = (EditText) findViewById(R.id.text_user_address);
        userSehir = (EditText) findViewById(R.id.text_sehir);
        geriTusu = (ImageButton) findViewById(R.id.geriTusuAyarlar);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        Init();

        geriDon();

        //Verileri getir
        database.getReference("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);

                userName.setText(kullanici.getAd());
                userSehir.setText(kullanici.getSehir());
                userAddress.setText(kullanici.getAdres());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = userName.getText().toString();
                String usersehir = userSehir.getText().toString();
                String useradres = userAddress.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(AyarlarActivity.this,"Kullanıcı adı boş olamaz!",Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(usersehir)){
                    Toast.makeText(AyarlarActivity.this,"Şehir boş olamaz!",Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(useradres)) {
                    Toast.makeText(AyarlarActivity.this, "Adres boş olamaz!", Toast.LENGTH_LONG).show();
                }
                else{
                    HashMap<String, Object> hashmap = new HashMap<>();
                    hashmap.put("ad", username.toLowerCase());
                    hashmap.put("sehir", usersehir.toLowerCase());
                    hashmap.put("adres", useradres);

                    database.getReference().child("Users").child(currentUserId).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AyarlarActivity.this, "Başarıyla güncellendi..", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    private void geriDon(){
        geriTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AyarlarActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}