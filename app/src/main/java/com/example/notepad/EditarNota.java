package com.example.notepad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.notepad.DataBase.Nota;
import com.example.notepad.DataBase.database;
import com.example.notepad.DataBase.notasDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditarNota extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etTitulo, etContenido;
    private TextView tvFechaCreacion;
    private Button btnGuardar;

    // Variables para la nota
    private int notaId;
    private Nota notaActual;

    // Variables para base de datos
    private database database;
    private notasDAO dao;
    private ExecutorService executor;

    // Constantes para recibir datos del Intent
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITULO = "extra_titulo";
    public static final String EXTRA_CONTENIDO = "extra_contenido";
    public static final String EXTRA_FECHA = "extra_fecha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_nota);

        inicializarVistas();
        configurarToolbar();
        inicializarBaseDatos();
        cargarDatosNota();
        configurarBotonGuardar();
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbar);
        etTitulo = findViewById(R.id.etTitulo);
        etContenido = findViewById(R.id.etContenido);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void inicializarBaseDatos() {
        database = database.obtenerBaseDatos(this);
        dao = database.notaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    private void cargarDatosNota() {
        // Obtener datos del Intent
        if (getIntent().hasExtra(EXTRA_ID)) {
            notaId = getIntent().getIntExtra(EXTRA_ID, -1);
            String titulo = getIntent().getStringExtra(EXTRA_TITULO);
            String contenido = getIntent().getStringExtra(EXTRA_CONTENIDO);
            String fecha = getIntent().getStringExtra(EXTRA_FECHA);

            // Cargar en los campos
            etTitulo.setText(titulo);
            etContenido.setText(contenido);
            tvFechaCreacion.setText("Creada: " + fecha);

            // Crear objeto nota actual
            notaActual = new Nota();
            notaActual.setId(notaId);
            notaActual.setTitulo(titulo);
            notaActual.setContenido(contenido);
            notaActual.setFechaCreacion(fecha);
        } else {
            Toast.makeText(this, "Error al cargar la nota", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void configurarBotonGuardar() {
        btnGuardar.setOnClickListener(v -> {
            guardarCambios();
        });
    }

    private void guardarCambios() {
        // Obtener valores editados
        String nuevoTitulo = etTitulo.getText().toString().trim();
        String nuevoContenido = etContenido.getText().toString().trim();

        // VALIDACIÓN: El contenido no puede estar vacío
        if (nuevoContenido.isEmpty()) {
            Toast.makeText(this, "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
            etContenido.requestFocus();
            return;
        }

        // Si el título está vacío, generar uno automático
        if (nuevoTitulo.isEmpty()) {
            nuevoTitulo = generarTituloAutomatico(nuevoContenido);
        }

        // Actualizar el objeto nota
        notaActual.setTitulo(nuevoTitulo);
        notaActual.setContenido(nuevoContenido);
        // La fecha de creación se mantiene igual

        // Actualizar en base de datos
        executor.execute(() -> {
            dao.actualizarNota(notaActual);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                finish(); // Volver a MainActivity
            });
        });
    }

    private String generarTituloAutomatico(String contenido) {
        String[] palabras = contenido.split("\\s+");
        StringBuilder titulo = new StringBuilder("Nota: ");

        int maxPalabras = Math.min(3, palabras.length);
        for (int i = 0; i < maxPalabras; i++) {
            titulo.append(palabras[i]).append(" ");
        }

        String tituloFinal = titulo.toString().trim();
        if (tituloFinal.length() > 30) {
            tituloFinal = tituloFinal.substring(0, 27) + "...";
        }

        return tituloFinal;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        // Preguntar si quiere descartar cambios
        // Por simplicidad, solo cerramos
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}