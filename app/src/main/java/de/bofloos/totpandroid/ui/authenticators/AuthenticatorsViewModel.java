package de.bofloos.totpandroid.ui.authenticators;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.bofloos.totpandroid.model.Account;

import java.util.List;

public class AuthenticatorsViewModel extends ViewModel {

    LiveData<List<Account>> accounts;
}
