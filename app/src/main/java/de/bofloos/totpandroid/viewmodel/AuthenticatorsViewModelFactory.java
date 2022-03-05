package de.bofloos.totpandroid.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.model.AccountDao;
import de.bofloos.totpandroid.model.AppDatabase;
import org.jetbrains.annotations.NotNull;

public class AuthenticatorsViewModelFactory implements ViewModelProvider.Factory {

    private final AccountDao accountDao;

    public AuthenticatorsViewModelFactory(Context ctx) {
        this.accountDao = AppDatabase.getInstance(ctx).getAccountRepository();
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AuthenticatorsViewModel(accountDao);
    }
}
