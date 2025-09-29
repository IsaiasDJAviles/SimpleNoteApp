package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notepad.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Imports para Room Database
import com.example.notepad.DataBase.Nota;
import com.example.notepad.DataBase.notasDAO;
import com.example.notepad.DataBase.database;

public class MainActivity extends AppCompatActivity implements Adapter.OnNotaClickListener {

    private ActivityMainBinding binding;

    // Variables para RecyclerView
    private Adapter adapter;
    private List<Nota> listaNotas;

    // Variables para Room Database
    private database database;
    private notasDAO dao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar componentes
        inicializarVistas();
        configurarRecyclerView();
        inicializarBaseDatos();
        configurarBotones();
    }

    private void inicializarVistas() {
        listaNotas = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
    }

    private void configurarRecyclerView() {
        adapter = new Adapter();
        adapter.setOnNotaClickListener(this);

        binding.recyclerViewNotas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNotas.setAdapter(adapter);
        binding.recyclerViewNotas.setHasFixedSize(true);
    }

    private void inicializarBaseDatos() {
        database = database.obtenerBaseDatos(this);
        dao = database.notaDao();
    }

    private void configurarBotones() {
        // Botón Agregar Nota
        binding.btnAgregarNota.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarNota.class);
            startActivity(intent);
        });

        // Botón Eliminar Nota
        binding.btnEliminarNota.setOnClickListener(v -> {
            eliminarNotaSeleccionada();
        });
    }

    private void cargarNotas() {
        executor.execute(() -> {
            // Cargar notas desde Room Database
            List<Nota> notas = dao.obtenerTodasLasNotas();

            // Actualizar UI en hilo principal
            runOnUiThread(() -> {
                listaNotas.clear();
                listaNotas.addAll(notas);
                adapter.setListaNotas(listaNotas);
                actualizarContador();
            });
        });
    }

    private void actualizarContador() {
        binding.tvCounter.setText("Notas: " + listaNotas.size());
    }

    private void eliminarNotaSeleccionada() {
        int posicionSeleccionada = adapter.getNotaSeleccionadaPos();

        // Validar que hay una nota seleccionada
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, "Selecciona una nota para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (posicionSeleccionada >= listaNotas.size()) {
            Toast.makeText(this, "Error: Nota no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        Nota notaAEliminar = listaNotas.get(posicionSeleccionada);

        // Mostrar Snackbar de confirmación
        Snackbar.make(binding.recyclerViewNotas,
                        "¿Eliminar '" + notaAEliminar.getTitulo() + "'?",
                        Snackbar.LENGTH_LONG)
                .setAction("CONFIRMAR", v -> {
                    eliminarNotaConfirmada(notaAEliminar);
                })
                .show();
    }

    private void eliminarNotaConfirmada(Nota nota) {
        executor.execute(() -> {
            // Eliminar de Room Database
            dao.eliminarNota(nota);

            // Actualizar UI
            runOnUiThread(() -> {
                adapter.limpiarSeleccion();
                cargarNotas(); // Recargar lista
                Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Implementación de la interface OnNotaClickListener
    @Override
    public void onNotaClick(Nota nota, int position) {
        // Click simple - seleccionar nota
        // El adapter ya maneja el resaltado visual
    }

    @Override
    public void onNotaDoubleClick(Nota nota, int position) {
        // Doble click - abrir para editar
        Intent intent = new Intent(MainActivity.this, EditarNota.class);
        intent.putExtra(EditarNota.EXTRA_ID, nota.getId());
        intent.putExtra(EditarNota.EXTRA_TITULO, nota.getTitulo());
        intent.putExtra(EditarNota.EXTRA_CONTENIDO, nota.getContenido());
        intent.putExtra(EditarNota.EXTRA_FECHA, nota.getFechaCreacion());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar notas cada vez que volvemos a esta actividad
        cargarNotas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}