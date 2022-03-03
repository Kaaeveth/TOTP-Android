package de.bofloos.totpandroid.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * SQlite Datenbank Anbindung mittels Room
 */
@Database(entities = {Account.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AccountDao getAccountRepository();

    private static AppDatabase db;

    /** Stellt die Datenbank als Singleton zur Verfügung
     * @param cxt - Der Kontext der Anwendung / Activity (kann über getApplicationContext() erhalten werden
     * */
    public static AppDatabase getInstance(Context cxt){
        if(db == null)
            db = Room.databaseBuilder(cxt, AppDatabase.class, "otp").build();
        return db;
    }
}
