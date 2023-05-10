package com.example.kanver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button btnKanAl, btnKanVer;
    private ImageView imageMessage;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private Toolbar toolbar;


    public void init(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        btnKanVer = (Button) findViewById(R.id.btnKanVer);
        btnKanAl = (Button) findViewById(R.id.btnKanAl);
        imageMessage = (ImageView) findViewById(R.id.btnMessage);
        toolbar = (Toolbar) findViewById(R.id.tbMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MesajListesi.class);
                startActivity(intent);
            }
        });

        btnKanAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, KanIhtiyacListesi.class);
                startActivity(mainIntent);
                finish();

            }
        });

        btnKanVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, KanBilgileri.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.settings_option:
                Intent profilIntent = new Intent(MainActivity.this, AyarlarActivity.class);
                startActivity(profilIntent);
                break;
            case R.id.main_logout_option :
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}