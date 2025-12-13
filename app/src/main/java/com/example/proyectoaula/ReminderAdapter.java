package com.example.proyectoaula;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoaula.Reminder;

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

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDateTime;
        private final ImageButton buttonDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewReminderTitle);
            textViewDateTime = itemView.findViewById(R.id.textViewReminderDateTime);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(final Reminder reminder, final OnItemDeleteListener listener) {
            // ----- ¡AQUÍ ESTÁ LA CORRECCIÓN FINAL Y CRUCIAL! -----
            // Accedemos a las variables directamente, que es lo estándar en entidades de Room.
            textViewTitle.setText(reminder.titulo);
            textViewDateTime.setText(dateFormat.format(reminder.timestamp));

            buttonDelete.setOnClickListener(v -> {
                listener.onDeleteClick(reminder);
            });
        }
    }
}
