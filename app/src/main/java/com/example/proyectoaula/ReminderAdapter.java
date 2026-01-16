package com.example.proyectoaula;

// ========= INICIO DE LA MODIFICACIÓN (IMPORTS) =========
import android.text.TextUtils; // Import para comprobar si un texto está vacío de forma segura
// ========= FIN DE LA MODIFICACIÓN (IMPORTS) =========
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private final OnItemDeleteListener deleteListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM, yyyy - hh:mm a", Locale.getDefault());

    public interface OnItemDeleteListener {
        void onDeleteClick(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminders, OnItemDeleteListener deleteListener) {
        this.reminders = reminders;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder currentReminder = reminders.get(position);
        holder.bind(currentReminder, deleteListener);
    }

    @Override
    public int getItemCount() {
        return reminders != null ? reminders.size() : 0;
    }

    public void updateData(List<Reminder> newReminders) {
        this.reminders = newReminders;
        notifyDataSetChanged();
    }

    // ========= INICIO DE LA MODIFICACIÓN (VIEWHOLDER) =========
    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDateTime;
        private final ImageButton buttonDelete;
        private final TextView textViewDescription; // <-- NUEVO: Vista para la descripción

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewReminderTitle);
            textViewDateTime = itemView.findViewById(R.id.textViewReminderDateTime);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            textViewDescription = itemView.findViewById(R.id.textViewReminderDescription); // <-- NUEVO: Enlazamos la vista del XML
        }

        public void bind(final Reminder reminder, final OnItemDeleteListener listener) {
            // Se asignan los datos a las vistas
            textViewTitle.setText(reminder.titulo);
            textViewDateTime.setText(dateFormat.format(reminder.timestamp));
            textViewDescription.setText(reminder.descripcion); // <-- NUEVO: Asignamos la descripción

            // Listener para el botón de eliminar
            buttonDelete.setOnClickListener(v -> listener.onDeleteClick(reminder));

            // ---- Lógica para mostrar/ocultar la descripción ----

            // 1. Ocultar la descripción por defecto si no tiene texto.
            // TextUtils.isEmpty comprueba si la descripción es nula o está vacía.
            if (TextUtils.isEmpty(reminder.descripcion)) {
                textViewDescription.setVisibility(View.GONE);
            } else {
                // Si tiene texto, la ocultamos al principio para que el usuario la muestre con un clic.
                // Esto es importante por el reciclaje de vistas del RecyclerView.
                textViewDescription.setVisibility(View.GONE);
            }

            // 2. Listener en todo el item para expandir/colapsar.
            itemView.setOnClickListener(v -> {
                // Solo hacemos algo si la descripción NO está vacía.
                if (!TextUtils.isEmpty(reminder.descripcion)) {
                    // Comprobamos si la descripción es visible actualmente.
                    boolean isVisible = textViewDescription.getVisibility() == View.VISIBLE;
                    // Cambiamos su visibilidad: si era visible, la ocultamos (GONE); si no, la mostramos (VISIBLE).
                    textViewDescription.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                }
            });
        }
    }
    // ========= FIN DE LA MODIFICACIÓN (VIEWHOLDER) =========
}
