package com.example.kanver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class KanBilgileri extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button kaydet;
    private Spinner spinner, spinnerAcil;
    private EditText adres;
    private ImageButton geriTusu;
    private FirebaseAuth auth;

    //Seçilen kan grubu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0) {
            Toast.makeText(this, "Seçili Kan Grubu: " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }else{

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kan_bilgileri);

        spinner = (Spinner) findViewById(R.id.spinnerKanGrubu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.kanGrubu_array,
                R.layout.spinner_item_colored);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinnerAcil = (Spinner) findViewById(R.id.spinnerKanAciliyet);
        ArrayAdapter<CharSequence> adapterAcil = ArrayAdapter.createFromResource(this, R.array.kanAciliyet_array,
                R.layout.spinner_item_colored);
        adapterAcil.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerAcil.setAdapter(adapterAcil);


        kaydet = findViewById(R.id.btnKaydet);
        adres = findViewById(R.id.inputAddress);
        geriTusu = findViewById(R.id.geriTusuKanBilgi);

        auth = FirebaseAuth.getInstance();

        //Adresin veri tabanından alınması
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kullanici kullanici = snapshot.getValue(Kullanici.class);
                adres.setText(kullanici.getAdres());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ButonTikla();

        geriTusuTikla();
    }

    private void geriTusuTikla(){
        geriTusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KanBilgileri.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void ButonTikla(){
        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnerString = null;
                spinnerString = spinner.getSelectedItem().toString();
                int nPos = spinner.getSelectedItemPosition();

                if(nPos==0){
                    spinnerString = "";
                }

                String spinnerStringAcil = null;
                spinnerStringAcil = spinnerAcil.getSelectedItem().toString();
                int nPosition = spinnerAcil.getSelectedItemPosition();

                String adress = adres.getText().toString();

                if(TextUtils.isEmpty(adress)){
                    Toast.makeText(KanBilgileri.this,"Adres alanı boş olamaz!",Toast.LENGTH_LONG).show();
                }
                else if(nPos == 0){
                    Toast.makeText(KanBilgileri.this, "Lütfen kan grubunu seçin!", Toast.LENGTH_SHORT).show();
                }
                else if(nPosition == 0){
                    Toast.makeText(KanBilgileri.this, "Lütfen kan aciliyet durumunu belirtin!", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, Object> hashMap = new HashMap();
                    hashMap.put("adres", adress);
                    hashMap.put("kanGrubu", spinnerString);
                    hashMap.put("kanDurumu", spinnerStringAcil);

                    FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).updateChildren(hashMap).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(KanBilgileri.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(KanBilgileri.this, "Kaydetme işlemi başarılı!", Toast.LENGTH_SHORT).show();
                            adres.setText("");
                        }
                    });
                }
            }
        });
    }




}