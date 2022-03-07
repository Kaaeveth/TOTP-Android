package de.bofloos.totpandroid.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.model.AccountRepository;
import de.bofloos.totpandroid.model.AppDatabase;
import org.jetbrains.annotations.NotNull;

public class AuthenticatorsViewModelFactory implements ViewModelProvider.Factory {

    private final AccountRepository accountRepository;

    public AuthenticatorsViewModelFactory(Context ctx) {
        this.accountRepository = AppDatabase.getInstance(ctx).getAccountRepository();
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new AuthenticatorsViewModel(accountRepository);
    }
}
