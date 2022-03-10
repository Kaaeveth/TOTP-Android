package de.bofloos.totpandroid.model;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.io.Serializable;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AccountRepository extends Serializable {

    @Query("select * from Account")
    LiveData<List<Account>> getAllAccounts();

    @Query("select * from Account where label = :label")
    LiveData<Account> getAccountByLabel(String label);

    @Query("select count(*) > 0 from Account where label = :label")
    boolean accountExists(String label);

    @Delete
    void deleteAccount(Account acc);

    @Insert(onConflict = REPLACE)
    void insertAccount(Account acc);

    @Update
    void updateAccount(Account acc);
}
