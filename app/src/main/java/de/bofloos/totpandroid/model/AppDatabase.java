package de.bofloos.totpandroid.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * SQlite Datenbank Anbindung mittels Room
 *
 * Room erlaubt keine Zugriffe auf die Datenbank vom UI/Main-Thread aus.
 * {@link de.bofloos.totpandroid.util.EventQueue} stellt eine Nachrichtenschleife für Zugriffe bereit.
 */
@Database(entities = {Account.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountRepository getAccountRepository();

    private static AppDatabase db;

    /**
     * Stellt die Datenbank als Singleton zur Verfügung
     * @param cxt - Der Kontext der Anwendung / Activity (kann über getApplicationContext() erhalten werden)
     * */
    public static AppDatabase getInstance(Context cxt){
        if(db == null)
            db = Room.databaseBuilder(cxt, AppDatabase.class, "otp").fallbackToDestructiveMigration().build();
        return db;
    }
}
