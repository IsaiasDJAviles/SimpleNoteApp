package com.example.notepad.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface notasDAO {

    @Insert
    void insertarNota(Nota nota);

    @Update
    void actualizarNota(Nota nota);

    @Delete
    void eliminarNota(Nota nota);

    @Query("SELECT * FROM Notas ORDER BY fechaCreacion DESC")
    List<Nota> obtenerTodasLasNotas();

    @Query("SELECT * FROM Notas WHERE id = :id")
    Nota obtenerNotaPorId(int id);

    @Query("SELECT * FROM Notas WHERE titulo LIKE '%' || :busqueda || '%'")
    List<Nota> buscarNotasPorTitulo(String busqueda);
}