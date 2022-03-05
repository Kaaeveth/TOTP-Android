package de.bofloos.totpandroid.model;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.io.Serializable;
import java.util.List;

@Dao
public interface AccountDao extends Serializable {

    @Query("select * from Account")
    LiveData<List<Account>> getAllAccounts();

    @Query("select * from Account where label = :label")
    LiveData<Account> getAccountByLabel(String label);

    @Query("select count(*) > 0 from Account where label = :label")
    boolean accountExists(String label);

    @Delete
    void deleteAccount(Account acc);

    @Insert
    void insertAccount(Account acc);

    @Update
    void updateAccount(Account acc);
}
