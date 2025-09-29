package com.example.notepad.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Nota.class}, version = 1, exportSchema = false)
public abstract class database extends RoomDatabase {

    // Método para obtener el DAO
    public abstract notasDAO notaDao();

    // Instancia única de la base de datos (patrón Singleton)
    private static database INSTANCIA;

    // Método para obtener la instancia de la base de datos
    public static database obtenerBaseDatos(Context context) {
        if (INSTANCIA == null) {
            synchronized (database.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            database.class,
                            "mi_base_de_notas" // Nombre de tu base de datos
                    ).build();
                }
            }
        }
        return INSTANCIA;
    }
}