package com.example.notepad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Import para Room Database
import com.example.notepad.DataBase.Nota;
import com.example.notepad.DataBase.notasDAO;
import com.example.notepad.DataBase.database;

public class AgregarNota extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etTitulo, etContenido;
    private Button btnGuardar;

    // Variables para base de datos
    private database database;
    private notasDAO dao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_nota);

        inicializarVistas();
        configurarToolbar();
        inicializarBaseDatos();
        configurarBotonGuardar();
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbar);
        etTitulo = findViewById(R.id.etTitulo);
        etContenido = findViewById(R.id.etContenido);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);

        // Mostrar botón de back
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

    private void configurarBotonGuardar() {
        btnGuardar.setOnClickListener(v -> {
            guardarNota();
        });
    }

    private void guardarNota() {
        // Obtener valores de los campos
        String titulo = etTitulo.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();

        // VALIDACIÓN: El contenido no puede estar vacío
        if (contenido.isEmpty()) {
            Toast.makeText(this, "El contenido de la nota no puede estar vacío", Toast.LENGTH_SHORT).show();
            etContenido.requestFocus();
            return;
        }

        // Si el título está vacío, generar uno automático
        if (titulo.isEmpty()) {
            titulo = generarTituloAutomatico(contenido);
        }

        // Obtener fecha y hora actual del sistema
        String fechaHora = obtenerFechaHoraActual();

        // Crear objeto Nota
        Nota nuevaNota = new Nota();
        nuevaNota.setTitulo(titulo);
        nuevaNota.setContenido(contenido);
        nuevaNota.setFechaCreacion(fechaHora);

        // Guardar en base de datos (en segundo plano)
        executor.execute(() -> {
            // Insertar en la base de datos
            dao.insertarNota(nuevaNota);

            // Actualizar UI en hilo principal
            runOnUiThread(() -> {
                Toast.makeText(this, "Nota guardada exitosamente", Toast.LENGTH_SHORT).show();
                finish(); // Cerrar esta actividad y volver a MainActivity
            });
        });
    }

    private String generarTituloAutomatico(String contenido) {
        // Generar título con las primeras palabras del contenido
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

    private String obtenerFechaHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Manejar el botón de back en la toolbar
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Regresar a MainActivity sin guardar
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