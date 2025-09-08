package com.example.notepad;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Note {
    private String id;
    private String content;
    private long timestamp;

    public Note(String id, String content, long timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Note(String id, String content) {
        this(id, content, System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

public class NotesViewModel extends ViewModel {

    private MutableLiveData<List<Note>> notes = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Integer> notesCount = new MutableLiveData<>(0);

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public LiveData<Integer> getNotesCount() {
        return notesCount;
    }

    public void addNote(String noteContent) {
        if (noteContent != null && !noteContent.trim().isEmpty()) {
            List<Note> currentNotes = notes.getValue();
            if (currentNotes == null) {
                currentNotes = new ArrayList<>();
            }

            Note newNote = new Note(
                    UUID.randomUUID().toString(),
                    noteContent.trim()
            );

            List<Note> updatedNotes = new ArrayList<>(currentNotes);
            updatedNotes.add(newNote);

            notes.setValue(updatedNotes);
            notesCount.setValue(updatedNotes.size());
        }
    }

    public void clearAllNotes() {
        notes.setValue(new ArrayList<>());
        notesCount.setValue(0);
    }

    public void loadNotes(String notesData) {
        if (notesData != null && !notesData.isEmpty()) {
            List<Note> notesList = new ArrayList<>();
            String[] noteStrings = notesData.split("\\|##\\|");

            for (String noteString : noteStrings) {
                String[] parts = noteString.split("\\|#\\|");
                if (parts.length >= 2) {
                    String id = parts[0];
                    String content = parts[1];
                    long timestamp = System.currentTimeMillis();

                    if (parts.length >= 3) {
                        try {
                            timestamp = Long.parseLong(parts[2]);
                        } catch (NumberFormatException e) {
                            // Usar timestamp actual si no se puede parsear
                        }
                    }

                    notesList.add(new Note(id, content, timestamp));
                }
            }

            notes.setValue(notesList);
            notesCount.setValue(notesList.size());
        } else {
            notes.setValue(new ArrayList<>());
            notesCount.setValue(0);
        }
    }

    public String getNotesAsString() {
        List<Note> currentNotes = notes.getValue();
        if (currentNotes == null || currentNotes.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentNotes.size(); i++) {
            Note note = currentNotes.get(i);
            sb.append(note.getId())
                    .append("|#|")
                    .append(note.getContent())
                    .append("|#|")
                    .append(note.getTimestamp());

            if (i < currentNotes.size() - 1) {
                sb.append("|##|");
            }
        }

        return sb.toString();
    }

    public String getFormattedNotesText() {
        List<Note> currentNotes = notes.getValue();
        if (currentNotes == null || currentNotes.isEmpty()) {
            return "No hay notas guardadas";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentNotes.size(); i++) {
            Note note = currentNotes.get(i);
            sb.append(i + 1).append(". ").append(note.getContent());

            if (i < currentNotes.size() - 1) {
                sb.append("\n\n");
            }
        }

        return sb.toString();
    }
}