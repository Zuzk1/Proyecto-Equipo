package com.example.proyectoaula;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

// Importar Glide y PhotoView
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class calendar_academic_images extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_academic_images);

        // --- LÓGICA PARA LA FLECHA DE VOLVER ---
        ImageButton btnBack = findViewById(R.id.btnBackCalendar);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Encontramos las imágenes por su ID
        ImageView imagenEscolarizada = findViewById(R.id.calendario_escolarizada);
        ImageView imagenNoEscolarizada = findViewById(R.id.calendario_no_escolarizada);

        // Usar Glide para cargar las imágenes
        Glide.with(this).load(R.drawable.calendario_escolarizada).into(imagenEscolarizada);
        Glide.with(this).load(R.drawable.calendario_no_escolarizada).into(imagenNoEscolarizada);

        // Eventos de clic
        imagenEscolarizada.setOnClickListener(v -> showImageInDialog(R.drawable.calendario_escolarizada));
        imagenNoEscolarizada.setOnClickListener(v -> showImageInDialog(R.drawable.calendario_no_escolarizada));
    }

    private void showImageInDialog(int imageResId) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fullscreen_image, null);
        PhotoView photoView = dialogView.findViewById(R.id.dialog_imageview);

        Glide.with(this).load(imageResId).into(photoView);
        dialog.setContentView(dialogView);

        if (dialog.getWindow() != null) {
            // Establece el fondo de la ventana como transparente para que mande el color de tu XML
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // QUITA EL COLOR NEGRO DEL SISTEMA: Esto permite ver la transparencia real del XML
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        // 1. CERRAR AL TOCAR LA IMAGEN (Tap rápido para no chocar con el zoom)
        photoView.setOnPhotoTapListener((view, x, y) -> dialog.dismiss());

        // 2. CERRAR AL TOCAR EL FONDO (Cualquier parte que no sea la imagen)
        dialogView.setOnClickListener(v -> dialog.dismiss());
        photoView.setOnOutsidePhotoTapListener(view -> dialog.dismiss());

        dialog.show();
    }
}
