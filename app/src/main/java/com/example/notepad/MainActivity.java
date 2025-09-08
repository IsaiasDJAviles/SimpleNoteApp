package com.example.notepad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notepad.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NotesViewModel notesViewModel;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "NotesPreferences";
    private static final String KEY_NOTES_DATA = "notes_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar ViewModel
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Cargar datos guardados
        loadNotesFromPreferences();

        // Configurar observadores
        setupObservers();

        // Configurar listeners de botones
        setupClickListeners();
    }

    private void setupObservers() {
        // Observar el contador de notas
        notesViewModel.getNotesCount().observe(this, count -> {
            binding.tvCounter.setText("Notas: " + count);
        });

        // Observar las notas y actualizar el TextView de listado
        notesViewModel.getNotes().observe(this, notes -> {
            binding.tvNotes.setText(notesViewModel.getFormattedNotesText());
        });
    }

    private void setupClickListeners() {
        // Botón para guardar nota
        binding.btnSave.setOnClickListener(v -> {
            String noteContent = binding.etNote.getText().toString();
            if (!noteContent.trim().isEmpty()) {
                notesViewModel.addNote(noteContent);
                binding.etNote.setText("");
                saveNotesToPreferences();
                Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor escribe una nota", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para limpiar notas
        binding.btnClear.setOnClickListener(v -> {
            notesViewModel.clearAllNotes();
            saveNotesToPreferences();
            Toast.makeText(this, "Notas eliminadas", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadNotesFromPreferences() {
        String notesData = sharedPreferences.getString(KEY_NOTES_DATA, "");
        if (notesData == null) {
            notesData = "";
        }
        notesViewModel.loadNotes(notesData);
    }

    private void saveNotesToPreferences() {
        String notesData = notesViewModel.getNotesAsString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTES_DATA, notesData);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Guardar datos cuando la actividad se pausa
        saveNotesToPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Guardar datos cuando la actividad se destruye
        saveNotesToPreferences();
    }
}