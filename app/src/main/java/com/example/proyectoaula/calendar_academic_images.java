package com.example.proyectoaula;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

// Importar Glide
import com.bumptech.glide.Glide;

public class calendar_academic_images extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usa el nombre correcto de tu layout principal
        setContentView(R.layout.activity_calendar_academic_images);

        // Encontramos las imágenes por su ID
        ImageView imagenEscolarizada = findViewById(R.id.calendario_escolarizada);
        ImageView imagenNoEscolarizada = findViewById(R.id.calendario_no_escolarizada);

        // Usar Glide para cargar las imágenes de forma eficiente
        Glide.with(this)
                .load(R.drawable.calendario_escolarizada)
                .into(imagenEscolarizada);

        Glide.with(this)
                .load(R.drawable.calendario_no_escolarizada)
                .into(imagenNoEscolarizada);


        // Asignamos el evento de clic a la primera imagen
        imagenEscolarizada.setOnClickListener(v -> {
            showImageInDialog(R.drawable.calendario_escolarizada);
        });

        // Asignamos el evento de clic a la segunda imagen
        imagenNoEscolarizada.setOnClickListener(v -> {
            showImageInDialog(R.drawable.calendario_no_escolarizada);
        });
    }

    // ----- FUNCIÓN CORREGIDA PARA EL FONDO SEMI-TRANSPARENTE -----
    private void showImageInDialog(int imageResId) {
        // 1. Crea un diálogo normal.
        final Dialog dialog = new Dialog(this);
        // 2. MUY IMPORTANTE: Le quitamos la barra de título que viene por defecto.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 3. "Infla" (carga) tu layout XML del diálogo.
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fullscreen_image, null);

        // 4. Encuentra el ImageView DENTRO de la vista del diálogo.
        ImageView fullscreenImageView = dialogView.findViewById(R.id.dialog_imageview);

        // 5. Usa Glide para cargar la imagen.
        Glide.with(this)
                .load(imageResId)
                .into(fullscreenImageView);

        // 6. Establece la vista como el contenido del diálogo.
        dialog.setContentView(dialogView);

        // 7. Configura la ventana del diálogo para el efecto deseado.
        if (dialog.getWindow() != null) {
            // HACE QUE EL FONDO SEA TRANSPARENTE
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // HACE QUE EL DIÁLOGO OCUPE TODA LA PANTALLA
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // 8. Hacemos que se cierre al tocar la imagen o el fondo.
        dialogView.setOnClickListener(v -> dialog.dismiss());

        // 9. Muestra el diálogo.
        dialog.show();
    }
}
