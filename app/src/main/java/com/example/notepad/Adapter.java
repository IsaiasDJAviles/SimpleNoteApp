package com.example.notepad;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notepad.DataBase.Nota;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.NotaViewHolder> {

    private List<Nota> listaNotas = new ArrayList<>();
    private OnNotaClickListener listener;
    private int notaSeleccionadaPos = -1; // Para saber cuál está seleccionada

    // Interface para manejar clicks
    public interface OnNotaClickListener {
        void onNotaClick(Nota nota, int position);
        void onNotaDoubleClick(Nota nota, int position);
    }

    public void setOnNotaClickListener(OnNotaClickListener listener) {
        this.listener = listener;
    }

    // Método para actualizar la lista de notas
    public void setListaNotas(List<Nota> notas) {
        this.listaNotas = notas;
        notifyDataSetChanged();
    }

    // Método para obtener una nota por posición
    public Nota getNotaAt(int position) {
        return listaNotas.get(position);
    }

    // Método para obtener posición seleccionada
    public int getNotaSeleccionadaPos() {
        return notaSeleccionadaPos;
    }

    // Método para limpiar selección
    public void limpiarSeleccion() {
        notaSeleccionadaPos = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Nota notaActual = listaNotas.get(position);
        holder.tvTitulo.setText(notaActual.getTitulo());
        holder.tvFecha.setText(notaActual.getFechaCreacion());

        // Variables para detectar doble click y selección
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private static final int DOUBLE_CLICK_TIME_DELTA = 300;
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                int pos = holder.getAdapterPosition();

                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Doble click
                    if (listener != null) {
                        listener.onNotaDoubleClick(notaActual, pos);
                    }
                } else {
                    // Click simple - ACTUALIZAR SELECCIÓN
                    notaSeleccionadaPos = pos;
                    notifyDataSetChanged(); // Refrescar para mostrar selección
                    if (listener != null) {
                        listener.onNotaClick(notaActual, pos);
                    }
                }
                lastClickTime = clickTime;
            }
        });

        // Resaltar la nota seleccionada visualmente
        if (position == notaSeleccionadaPos) {
            holder.itemView.setBackgroundColor(0xFFE3F2FD); // Azul claro
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // Blanco
        }
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    // ViewHolder interno
    class NotaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitulo;
        private TextView tvFecha;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_nota);
            tvFecha = itemView.findViewById(R.id.tv_fecha_nota);
        }
    }
}