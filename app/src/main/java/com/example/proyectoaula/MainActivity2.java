package com.example.proyectoaula;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

    }

    public void alHacerClick(View view) {
        switch (view.getId()){
            case R.id.btnRojo:
                Toast.makeText(this, "Alarmas", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnVerde:
                Toast.makeText(this, "Pendientes", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}