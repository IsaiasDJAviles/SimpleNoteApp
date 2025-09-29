package com.example.notepad.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notas") // Puedes cambiar el nombre de la tabla
public class Nota {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titulo;
    private String contenido;
    private String fechaCreacion;

    // Constructor vacío (obligatorio para Room)
    public Nota() {
    }

    // Constructor con parámetros (opcional, pero útil)
    public Nota(String titulo, String contenido, String fechaCreacion) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters (OBLIGATORIOS)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
